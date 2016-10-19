package com.snail.dnslb4j;

import com.snail.dnslb4j.util.Cfg;
import com.snail.dnslb4j.util.Log;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public final class DnsServer {
	
	public static void main(String[] args) throws Exception {
		if (args.length == 1) {
			Cfg.setPrePath(args[0]);
		} else {
			Cfg.setPrePath("development");
		}
		io.netty.util.internal.logging.InternalLoggerFactory.setDefaultFactory(new io.netty.util.internal.logging.JdkLoggerFactory());
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			String listenIp = Cfg.config("listen_ip");
			int listenPort = Cfg.configInt("listen_port");
			b.group(group)
				.channel(NioDatagramChannel.class)
				.handler(new DnsServerHandler());
			Log.logger().info("listen on " + listenIp + ":" + String.valueOf(listenPort));
			b.bind(listenIp, listenPort).sync().channel().closeFuture().await();
		} finally {
			group.shutdownGracefully();
		}
	}
}
