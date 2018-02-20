package com.cjyfff.handler;

import com.alibaba.fastjson.JSON;
import com.cjyfff.vo.CommentDetailReqVo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import java.util.concurrent.*;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by cjyfff on 18-2-19.
 */
@Sharable                                        //1
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };

    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    private final static ExecutorService workerThreadService =
            newBlockingExecutorsUseCallerRun(Runtime.getRuntime().availableProcessors() * 2);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;

            if ("/detail".equals(req.uri())) {
                workerThreadService.execute(new Runnable() {
                    @Override
                    public void run() {

                        ByteBuf jsonBuf = req.content();
                        String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);

                        CommentDetailReqVo reqVo = JSON.parseObject(jsonStr, CommentDetailReqVo.class);


                        boolean keepAlive = HttpUtil.isKeepAlive(req);
                        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
                        response.headers().set(CONTENT_TYPE, "text/plain");
                        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

                        if (!keepAlive) {
                            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                        } else {
                            response.headers().set(CONNECTION, KEEP_ALIVE);
                            ctx.writeAndFlush(response);
                        }
                    }
                });

            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private static ExecutorService newBlockingExecutorsUseCallerRun(int size) {
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            if (! executor.isShutdown()) {
                                executor.getQueue().put(r);
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
}
