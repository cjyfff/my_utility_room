package com.cjyfff;

import com.cjyfff.handler.EchoServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**
 * Created by cjyfff on 18-2-19.
 */
public class EchoServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public EchoServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }

        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(2048));
        p.addLast(new EchoServerHandler());
    }
}
