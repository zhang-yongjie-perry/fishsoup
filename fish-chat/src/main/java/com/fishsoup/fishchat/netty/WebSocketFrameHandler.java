package com.fishsoup.fishchat.netty;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.fishchat.domain.ChatUser;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.Objects;

import static com.fishsoup.enums.OnlineStatusEnum.OFFLINE;
import static com.fishsoup.fishchat.netty.NettyServerHandler.channelMap;


public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final RedissonClient redissonClient;

    WebSocketFrameHandler(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WebSocketFrame webSocketFrame) {
        if (!(webSocketFrame instanceof TextWebSocketFrame frame)) {
            return;
        }
        String message = frame.text();
        if (message.startsWith("Bearer ")) {
            return;
        }

        ChatUser chatUser = JSON.parseObject(message, ChatUser.class);
        if (channelMap.get(chatUser.getUsername()) == null) {
            // 如果请求不是令牌, 且map中没有放入, 则断开该连接
            channelHandlerContext.channel().close();
            return;
        }

        RBucket<Channel> bucketSelf = redissonClient.getBucket("fish:chat:" + chatUser.getUsername());

        if (Objects.equals(chatUser.getToUsername(), OFFLINE.name())) {
            bucketSelf.delete();
            channelMap.forEach((key, channel)
                -> channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser))));
            return;
        }
        // 给自己的客户端发消息
        channelMap.get(chatUser.getUsername()).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser)));

        // 给别人的客户端发消息
        if (channelMap.containsKey(chatUser.getToUsername())) {
            // 如果用户在线则直接同步消息
            channelMap.get(chatUser.getToUsername()).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser)));
            return;
        }
        // todo 若用户不在线, 则将消息存入redis
    }
}
