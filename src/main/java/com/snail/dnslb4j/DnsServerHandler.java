package com.snail.dnslb4j;

import com.alibaba.fastjson.JSONObject;
import com.snail.dnslb4j.util.Cfg;
import com.snail.dnslb4j.util.ConnectionManager;
import com.snail.dnslb4j.util.DnsPacket;
import com.snail.dnslb4j.util.Log;
import com.snail.dnslb4j.util.Misc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import jodd.exception.ExceptionUtil;

public class DnsServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	@Override
	public void messageReceived(ChannelHandlerContext ctx0, DatagramPacket packet0) throws InterruptedException {
		try {
			String sender = packet0.sender().getAddress().getHostAddress() + ":" + packet0.sender().getPort();
			Log.logger().info("revceived from <-" + sender);

			JSONObject dest = new JSONObject();
			dest.put("ip", Cfg.config("dns_ip"));
			dest.put("port", Cfg.config("dns_port"));
			dest.put("timeout", Cfg.config("timeout"));
			DnsPacket dnsPacket = new DnsPacket();
			dnsPacket.setQueryInfo(dest).setQueryPacket(Misc.byteBuf2bytes(packet0.content().copy()));

			ConnectionManager.send(dnsPacket, new SimpleChannelInboundHandler<Object>() {
				@Override
				protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
					DnsPacket replyDnsPacket = (DnsPacket) msg;
					DatagramPacket packet = new DatagramPacket(replyDnsPacket.getReplyPacket(), packet0.sender());
					ctx0.writeAndFlush(packet);
					ctx.channel().close();
					Log.logger().info("reply to ->" + sender);
				}
			});
		} catch (Exception e) {
			Log.logger().error(ExceptionUtil.exceptionChainToString(e));
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Log.logger().error(ExceptionUtil.exceptionChainToString(cause));
		ctx.close();
	}
}
