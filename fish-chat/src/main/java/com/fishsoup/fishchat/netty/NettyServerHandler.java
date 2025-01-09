package com.fishsoup.fishchat.netty;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.fishchat.domain.ChatUser;
import com.fishsoup.util.CollectionUtils;
import com.fishsoup.util.JWTUtils;
import com.fishsoup.util.StringUtils;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.fishsoup.enums.OnlineStatusEnum.OFFLINE;
import static com.fishsoup.enums.OnlineStatusEnum.ONLINE;
import static com.fishsoup.util.JWTUtils.ISSUER_FISH;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RedissonClient redissonClient;

    public final static Map<String, Set<Channel>> channelMap = new ConcurrentHashMap<>();

    NettyServerHandler(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        RBucket<String> ipBucket = redissonClient.getBucket("fish:userIp:" + ctx.channel().remoteAddress().toString());
        String username = ipBucket.get();
        if (!StringUtils.hasText(username)) {
            return;
        }
        ipBucket.delete();

        for (Map.Entry<String, Set<Channel>> next : channelMap.entrySet()) {
            if (Objects.equals(next.getKey(), username)) {
                next.getValue().removeIf(channel -> Objects.equals(ctx.channel(), channel));
                ctx.channel().close();
                break;
            }
        }

        RBucket<Integer> bucket = redissonClient.getBucket("fish:chat:" + username);
        Integer num = bucket.get();
        if (num != null && --num > 0) {
            bucket.set(num);
            return;
        }
        bucket.delete();

        ChatUser chatUser = new ChatUser().setUsername(username).setToUsername(OFFLINE.name());
        channelMap.forEach((key, channels)
            -> channels.forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser)))));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof TextWebSocketFrame frame)) {
            return;
        }
        String message = frame.text();
        if (!message.startsWith("Bearer ")) {
            super.channelRead(ctx, msg);
            return;
        }
        String jwt = message.substring("Bearer ".length());
        Claims claims;
        try {
            claims = JWTUtils.parseJWT(jwt);
        } catch (Exception e) {
            throw new RuntimeException("访问令牌解析异常, 请重新登录");
        }
        if (!Objects.equals(ISSUER_FISH, claims.getIssuer())) {
            throw new RuntimeException("访问令牌签发者错误, 请重新登录");
        }

        ChatUser user = JSON.parseObject(claims.getSubject(), ChatUser.class);
        RBucket<String> ipBucket = redissonClient.getBucket("fish:userIp:" + ctx.channel().remoteAddress().toString());
        String rIp = ipBucket.get();
        RBucket<Integer> bucket = redissonClient.getBucket("fish:chat:" + user.getUsername());
        Integer num = bucket.get();
        if (num == null) {
            bucket.set(1);
            ipBucket.set(user.getUsername());
        } else if (!StringUtils.hasText(rIp)) {
            ipBucket.set(user.getUsername());
            // ip未登录过才能加1
            bucket.set(num + 1);
        }

        Set<Channel> channels = channelMap.get(user.getUsername());
        if (CollectionUtils.isEmpty(channels)) {
            Set<Channel> channelList = new HashSet<>();
            channelList.add(ctx.channel());
            channelMap.put(user.getUsername(), channelList);
        } else {
            channels.add(ctx.channel());
        }

        ChatUser chatUser = new ChatUser().setUsername(user.getUsername()).setToUsername(ONLINE.name());
        channelMap.forEach((key, list)
            -> list.forEach(channel -> channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser)))));
        // 同步历史消息
        RBucket<String> chatBucket = redissonClient.getBucket("fish:chatContent:" + chatUser.getUsername());
        String content = chatBucket.get();
        if (!StringUtils.hasText(content)) {
            super.channelRead(ctx, msg);
            return;
        }
        String[] split = content.split("<///>");
        for (String c : split) {
            ctx.writeAndFlush(new TextWebSocketFrame(c));
        }
        chatBucket.delete();
        super.channelRead(ctx, msg);
    }
}
