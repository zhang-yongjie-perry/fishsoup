package com.fishsoup.fishchat.netty;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.enums.YesNoEnum;
import com.fishsoup.fishchat.domain.ChatUser;
import com.fishsoup.util.JWTUtils;
import com.fishsoup.util.StringUtils;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.fishsoup.enums.OnlineStatusEnum.OFFLINE;
import static com.fishsoup.enums.OnlineStatusEnum.ONLINE;
import static com.fishsoup.util.JWTUtils.ISSUER_FISH;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RedissonClient redissonClient;

    public final static Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    NettyServerHandler(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        Iterator<Map.Entry<String, Channel>> iterator = channelMap.entrySet().iterator();
        String username = "";
        while (iterator.hasNext()) {
            Map.Entry<String, Channel> next = iterator.next();
            if (Objects.equals(next.getValue(), ctx.channel())) {
                username = next.getKey();
                iterator.remove();
                break;
            }
        }

        if (StringUtils.hasText(username)) {
            return;
        }

        RBucket<Channel> bucket = redissonClient.getBucket("fish:chat:" + username);
        bucket.delete();

        ChatUser chatUser = new ChatUser().setUsername(username).setToUsername(OFFLINE.name());
        channelMap.forEach((key, channel)
            -> channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser))));
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

        RBucket<String> bucket = redissonClient.getBucket("fish:chat:" + user.getUsername());
        bucket.set(YesNoEnum.YES.getCode());
        channelMap.put(user.getUsername(), ctx.channel());

        ChatUser chatUser = new ChatUser().setUsername(user.getUsername()).setToUsername(ONLINE.name());
        channelMap.forEach((key, channel)
            -> channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser))));
        // todo 同步历史消息
        super.channelRead(ctx, msg);
    }
}
