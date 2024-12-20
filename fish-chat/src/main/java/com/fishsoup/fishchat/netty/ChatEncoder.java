package com.fishsoup.fishchat.netty;

import com.fishsoup.fishchat.domain.ChatUser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ChatEncoder extends MessageToMessageEncoder<ChatUser> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ChatUser chatUser, List<Object> list) throws Exception {
        list.add(chatUser);
    }
}
