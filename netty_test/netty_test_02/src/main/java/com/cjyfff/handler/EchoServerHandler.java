package com.cjyfff.handler;

import com.alibaba.fastjson.JSON;
import com.cjyfff.vo.CommentDetailReqVo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("netty-test-pool-%d").build();

        // 1、使用LinkedBlockingQueue，就可以有一个缓冲过程。不过LinkedBlockingQueue需要指定一个容量，不然过多任务堆积，
        // 会消耗大量的资源，请求也不能在短时间内得到相应
        // 2、使用SynchronousQueue，当任务达到最大线程数时，生产线程再插入任务会阻塞，继而会阻塞NioEvenLoop，考虑到请求已经多到
        // 达到线程池最大线程数，让请求阻塞其实也是一种合理的选择。
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(size * 2),
            namedThreadFactory,
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
