/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snail.dnslb4j.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 *
 * @author pengmeng
 */
public class Misc {

	public static byte[] byteBuf2bytes(ByteBuf buf) {
		byte[] bytes = new byte[buf.readableBytes()];
		int readerIndex = buf.readerIndex();
		buf.getBytes(readerIndex, bytes);
		return bytes;
	}

	public static ByteBuf bytes2ByteBuf(byte[] bytes) {
		return Unpooled.copiedBuffer(bytes);
	}
}
