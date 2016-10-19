package com.snail.dnslb4j.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DESPlus {

	private Cipher encryptCipher = null;
	private Cipher decryptCipher = null;

	/**
	 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
	 * hexStr2ByteArr(String strIn) 互为可逆的转换过程
	 *
	 * @param arrB 需要转换的byte数组
	 * @return 转换后的字符串
	 * @ 本方法不处理任何异常，所有异常全部抛出
	 */
	public static String byteArr2HexStr(byte[] arrB) {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuilder sb = new StringBuilder(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[]
	 * arrB) 互为可逆的转换过程
	 *
	 * @param strIn 需要转换的字符串
	 * @return 转换后的byte数组
	 * @ 本方法不处理任何异常，所有异常全部抛出
	 * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
	 */
	public static byte[] hexStr2ByteArr(String strIn) {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		if (iLen % 2 == 1) {
			iLen--;
		}
		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			try {
				arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
			} catch (Exception e) {
			}

		}
		return arrOut;
	}

	/**
	 * 指定密钥构造方法
	 *
	 * @param strKey 指定的密钥
	 * @
	 */
	public DESPlus(String strKey) {
		try {
			Security.addProvider(new com.sun.crypto.provider.SunJCE());
			Key key = getKey(strKey);

			encryptCipher = Cipher.getInstance("DES");
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);

			decryptCipher = Cipher.getInstance("DES");
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			Logger.getLogger(DESPlus.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	/**
	 * 加密字节数组
	 *
	 * @param arrB 需加密的字节数组
	 * @return 加密后的字节数组
	 * @
	 */
	public byte[] encrypt(byte[] arrB) {
		try {
			return encryptCipher.doFinal(arrB);
		} catch (IllegalBlockSizeException | BadPaddingException ex) {
			Logger.getLogger(DESPlus.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * 加密字符串
	 *
	 * @param strIn 需加密的字符串
	 * @return 加密后的字符串
	 * @
	 */
	public String encrypt(String strIn) {
		return byteArr2HexStr(encrypt(strIn.getBytes()));
	}

	/**
	 * 解密字节数组
	 *
	 * @param arrB 需解密的字节数组
	 * @return 解密后的字节数组
	 * @
	 */
	public byte[] decrypt(byte[] arrB) {
		try {
			return decryptCipher.doFinal(arrB);
		} catch (IllegalBlockSizeException | BadPaddingException ex) {
		}
		return null;
	}

	/**
	 * 解密字符串
	 *
	 * @param strIn 需解密的字符串
	 * @return 解密后的字符串
	 * @
	 */
	public String decrypt(String strIn) {
		byte[] de = decrypt(hexStr2ByteArr(strIn));
		return de == null ? null : new String(de);
	}

	/**
	 * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
	 *
	 * @param arrBTmp 构成该字符串的字节数组
	 * @return 生成的密钥
	 * @throws java.lang.Exception
	 */
	private Key getKey(String arrBTmp) {
		String ekey = MD5(arrBTmp).substring(0, 8);
		// 生成密钥
		Key key = new javax.crypto.spec.SecretKeySpec(ekey.getBytes(), "DES");

		return key;
	}

	public static String MD5(String message) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(message.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			return null;
		}

	}

	public static void main(String[] args) {
		String test = "Hellow中文呢 Word!";
		DESPlus des = new DESPlus("testpass");//自定义密钥  
		System.out.println("加密前的字符：" + test);
		System.out.println("加密后的字符：" + des.encrypt(test));
		System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));
	}
}
