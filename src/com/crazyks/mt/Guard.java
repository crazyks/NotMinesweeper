package com.crazyks.mt;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;

public class Guard {
	private final Cipher encryptCipher;
	private final Cipher decryptCipher;
	private final MessageDigest md;

	public Guard(byte[] bytes) throws Exception {
		Key key = getKey(bytes);

		encryptCipher = Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);

		decryptCipher = Cipher.getInstance("DES");
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
		
		md = MessageDigest.getInstance("MD5");
	}

	private Key getKey(byte[] arrBTmp) throws Exception {
		byte[] arrB = new byte[8];
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		return new javax.crypto.spec.SecretKeySpec(arrB, "DES");
	}

	public byte[] encrypt(byte[] arrB) throws Exception {
		return encryptCipher.doFinal(arrB);
	}

	public byte[] decrypt(byte[] arrB) throws Exception {
		return decryptCipher.doFinal(arrB);
	}
	
	public byte[] md5(byte[] arrB) throws Exception {
		md.update(arrB);
		return md.digest();
	} 

}