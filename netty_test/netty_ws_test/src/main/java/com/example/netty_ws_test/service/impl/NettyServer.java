package com.example.netty_ws_test.service.impl;

import java.net.InetSocketAddress;

import com.example.netty_ws_test.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/3/31.
 */
@Component
public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    /**
     * webSocket协议名
     */
    private static final String WEBSOCKET_PROTOCOL = "WebSocket";

    /**
     * 端口号
     */
    @Value("${webSocket.netty.port:58080}")
    private int port;

    /**
     * webSocket路径
     */
    @Value("${webSocket.netty.path:/webSocket}")
    private String webSocketPath;

    @Autowired
    private WebSocketHandler webSocketHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    /**
     * 启动
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        // bossGroup辅助客户端的tcp连接请求, workGroup负责与客户端之前的读写操作
        bootstrap.group(bossGroup,workGroup);
        // 设置NIO类型的channel
        bootstrap.channel(NioServerSocketChannel.class);
        // 设置监听端口
        bootstrap.localAddress(new InetSocketAddress(port));
        // 连接到达时会创建一个通道
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 流水线管理通道中的处理程序（Handler），用来处理业务
                // webSocket协议本身是基于http协议的，所以这边也要使用http编解码器
                ch.pipeline().addLast(new HttpServerCodec());

                /*
                A {@link ChannelHandler} that adds support for writing a large data stream asynchronously neither spending
                a lot of memory nor getting {@link OutOfMemoryError}.
                 */
                ch.pipeline().addLast(new ChunkedWriteHandler());
                /*
                {@link ChannelHandler} that aggregates an {@link HttpMessage}  and its
                following {@link HttpContent}s into a single {@link FullHttpRequest} or
                {@link FullHttpResponse} (depending on if it used to handle requests or responses)
                It is useful when you don't want to take care of HTTP messages whose transfer encoding is 'chunked'.
                Be aware that you need to have the {@link HttpResponseEncoder} or {@link HttpRequestEncoder} before
                the {@link HttpObjectAggregator} in the {@link ChannelPipeline}.
                 */
                ch.pipeline().addLast(new HttpObjectAggregator(8192));
                /*
                说明：
                1、对应webSocket，它的数据是以帧（frame）的形式传递
                2、浏览器请求时 ws://localhost:58080/xxx 表示请求的uri
                3、核心功能是将http协议升级为ws协议，保持长连接
                */
                ch.pipeline().addLast(new WebSocketServerProtocolHandler(webSocketPath, WEBSOCKET_PROTOCOL, true, 65536 * 10));
                // 自定义的handler，处理业务逻辑
                ch.pipeline().addLast(webSocketHandler);

            }
        });
        // 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
        ChannelFuture channelFuture = bootstrap.bind().sync();
        log.info("Server started and listen on:{}",channelFuture.channel().localAddress());
        // 对关闭通道进行监听
        channelFuture.channel().closeFuture().sync();
        log.info("Netty channel close.");
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkGroup() {
        return workGroup;
    }
}
