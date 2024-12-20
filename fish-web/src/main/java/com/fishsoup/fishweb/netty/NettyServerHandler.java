package com.fishsoup.fishweb.netty;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.fishweb.domain.ChatUser;
import com.fishsoup.fishweb.domain.User;
import com.fishsoup.fishweb.util.JWTUtils;
import com.fishsoup.fishweb.util.StringUtils;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.AccessDeniedException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.fishsoup.fishweb.enums.OnlineStatusEnum.OFFLINE;
import static com.fishsoup.fishweb.enums.OnlineStatusEnum.ONLINE;
import static com.fishsoup.fishweb.util.JWTUtils.ISSUER_FISH;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    public static ConcurrentHashMap<String, Channel> chatClientMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端: {} 已连接", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("客户端: {} 已断开", ctx.channel().remoteAddress());
        Iterator<Map.Entry<String, Channel>> iterator = chatClientMap.entrySet().iterator();
        String username = "";
        while (iterator.hasNext()) {
            Map.Entry<String, Channel> entry = iterator.next();
            Channel channel = entry.getValue();
            if (channel == ctx.channel()) {
                username = entry.getKey();
                // 以断线的通道是发布了数据的
                iterator.remove();
                break;
            }
        }
        if (!StringUtils.hasText(username)) {
            return;
        }
        ChatUser chatUser = new ChatUser().setUsername(username).setToUsername(OFFLINE.name());
        chatClientMap.forEach((key, channel)
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
            throw new AccessDeniedException("访问令牌解析异常, 请重新登录");
        }
        if (!Objects.equals(ISSUER_FISH, claims.getIssuer())) {
            throw new AccessDeniedException("访问令牌签发者错误, 请重新登录");
        }

        String subject = claims.getSubject();
        // 为性能考虑此处的用户权限不进行数据库查询
        User user = JSON.parseObject(subject, User.class);
        chatClientMap.put(user.getUsername(), ctx.channel());
        ChatUser chatUser = new ChatUser().setUsername(user.getUsername()).setToUsername(ONLINE.name());
        chatClientMap.forEach((username, channel)
            -> channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser))));
        // todo 同步历史消息
        super.channelRead(ctx, msg);
    }
}
