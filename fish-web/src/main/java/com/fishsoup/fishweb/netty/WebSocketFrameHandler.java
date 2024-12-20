package com.fishsoup.fishweb.netty;

import com.alibaba.fastjson2.JSON;
import com.fishsoup.fishweb.domain.ChatUser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.Objects;

import static com.fishsoup.fishweb.enums.OnlineStatusEnum.OFFLINE;
import static com.fishsoup.fishweb.netty.NettyServerHandler.chatClientMap;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
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
        if (Objects.equals(chatUser.getToUsername(), OFFLINE.name())) {
            chatClientMap.remove(chatUser.getUsername());
            chatClientMap.forEach((key, channel)
                -> channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser))));
            return;
        }
        chatClientMap.get(chatUser.getUsername()).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser)));
        if (chatClientMap.containsKey(chatUser.getToUsername())) {
            // 如果用户在线则直接同步消息
            chatClientMap.get(chatUser.getToUsername()).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(chatUser)));
            return;
        }
        // todo 若用户不在线, 则将消息存入redis
    }
}
