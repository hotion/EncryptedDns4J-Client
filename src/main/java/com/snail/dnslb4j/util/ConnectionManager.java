package com.snail.dnslb4j.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import jodd.exception.ExceptionUtil;

public class ConnectionManager {

	public static void send(Object object, SimpleChannelInboundHandler<Object> callback) {
		String host = Cfg.config("server_ip");
		int port = Cfg.configInt("server_port");
		String dest = host + ":" + String.valueOf(port);
		NioEventLoopGroup workGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(
					new ObjectDecoder(1024 * 1024, ClassResolvers
						.cacheDisabled(getClass().getClassLoader())));
				ch.pipeline().addLast(new ObjectEncoder());
				ch.pipeline().addLast(callback);
				Log.logger().debug("reply from : " + dest);
			}
		});

		// 发起异步链接操作
		ChannelFuture future;
		try {
			Log.logger().debug("request to : " + dest);
			future = bootstrap.connect(Cfg.config("server_ip"), Cfg.configInt("server_port")).sync();
			future.channel().writeAndFlush(object);
			if (!future.channel().closeFuture().sync().await(Cfg.configInt("timeout"))) {
				Log.logger().info("request to : " + dest + " timeout (" + Cfg.config("timeout") + "ms)");
			}
		} catch (InterruptedException e) {
			Log.logger().error(ExceptionUtil.exceptionChainToString(e));
		} finally {
			workGroup.shutdownGracefully();
		}
	}
}
