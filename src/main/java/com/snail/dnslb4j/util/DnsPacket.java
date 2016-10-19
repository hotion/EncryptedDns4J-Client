package com.snail.dnslb4j.util;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.Serializable;

public class DnsPacket implements Serializable {

	private byte[] queryPacket;
	private byte[] replyPacket;
	private String queryInfo;
	private static final DESPlus DES = new DESPlus(Cfg.config("encrypt_key"));

	public ByteBuf getQueryPacket() {
		return Unpooled.copiedBuffer(DES.decrypt(queryPacket));
	}

	public DnsPacket setQueryPacket(byte[] aQueryPacket) {
		queryPacket = DES.encrypt(aQueryPacket);
		return this;
	}

	public ByteBuf getReplyPacket() {
		return Unpooled.copiedBuffer(DES.decrypt(replyPacket));
	}

	public DnsPacket setReplyPacket(byte[] aReplyPacket) {
		replyPacket = DES.encrypt(aReplyPacket);
		return this;
	}

	public JSONObject getQueryInfo() {
		return JSONObject.parseObject(DES.decrypt(queryInfo));
	}

	public DnsPacket setQueryInfo(JSONObject aQueryInfo) {
		queryInfo = DES.encrypt(aQueryInfo.toJSONString());
		return this;
	}

}
