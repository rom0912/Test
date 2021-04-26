package com.romy.auth.common.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSA {

	private String publicKeyModulus = "";
	private String publicKeyExponent = "";
	private String privateKey = null;
	
	public static final String secretKey = "sEcreTrOMauTHKeyrom1RoMY2aUth1@3";
	public static final String encryptAlgorithm = "AES/CBC/PKCS5Padding";
	
	public static final byte[] ivBytes = { 0x00, 0x00, 0x01, 0x02, 0x00, 0x03, 0x00, 0x04, 0x05, 0x06, 0x07, 0x00, 0x00, 0x08, 0x00, 0x09 };

	public static RSA getEncKey() {

		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} // RSA키 제네레이터 생성
		generator.initialize(2048); // 키 사이즈

		KeyPair keyPair = generator.genKeyPair();

		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

		PublicKey publicKey = keyPair.getPublic(); // 공개키
		PrivateKey privateKey = keyPair.getPrivate(); // 개인키

		RSAPublicKeySpec publicSpec;
		try {
			publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
		String publicKeyModulus = publicSpec.getModulus().toString(16);
		String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
		
		String strPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

		RSA rsa = new RSA();
		rsa.setPrivateKey(strPrivateKey);
		rsa.setPublicKeyExponent(publicKeyExponent);
		rsa.setPublicKeyModulus(publicKeyModulus);

		return rsa;
	}
	
	public static boolean dec(String privateKey, String encString) throws Exception {
		boolean result = false;

		if (privateKey == null) {
			throw new RuntimeException("암호화 비밀키 정보를 찾을 수 없습니다.");
		}
		try {
			decryptRsa(privateKey, encString);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	public static String decryptRsa(String stringPrivateKey, String securedValue) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		
		byte[] encryptedBytes = hexToByteArray(securedValue);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
		return decryptedValue;
	}

	public static byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() % 2 != 0) {
			return new byte[] {};
		}

		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
			bytes[(int) Math.floor(i / 2)] = value;
		}
		return bytes;
	}

	public String getPublicKeyModulus() {
		return publicKeyModulus;
	}

	public void setPublicKeyModulus(String publicKeyModulus) {
		this.publicKeyModulus = publicKeyModulus;
	}

	public String getPublicKeyExponent() {
		return publicKeyExponent;
	}

	public void setPublicKeyExponent(String publicKeyExponent) {
		this.publicKeyExponent = publicKeyExponent;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public static String encrypt(String data) throws Exception {

        byte[] textBytes = data.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");

        Cipher cipher = Cipher.getInstance(encryptAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        return Base64.getEncoder().encodeToString(cipher.doFinal(textBytes));
    }
	
    public static String decrypt(String data) throws Exception {

		byte[] textBytes = Base64.getDecoder().decode(data);
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance(encryptAlgorithm);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		return new String(cipher.doFinal(textBytes), "UTF-8");
	}
	
}
