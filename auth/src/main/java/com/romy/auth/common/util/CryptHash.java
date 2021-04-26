package com.romy.auth.common.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class CryptHash {

	/**
	 * make a new random salt
	 *
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested password
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 */
	public static String getNewSalt(String strTmp) throws NoSuchAlgorithmException, IOException {
		/*
		 * http://docs.oracle.com/javase/1.4.2/docs/api/
		 * Random Number Generation (RNG) Algorithms
		 * The algorithm names in this section can be specified when generating
		 * an instance of SecureRandom.
		 * SHA1PRNG: The name of the pseudo-random number generation
		 * (PRNG) algorithm supplied by the SUN provider.
		 * This implementation follows the IEEE P1363 standard,
		 * Appendix G.7: "Expansion of source bits",
		 * and uses SHA-1 as the foundation of the PRNG.
		 * It computes the SHA-1 hash over a true-random seed value concatenated
		 * with a 64-bit counter which is incremented by 1 for each operation.
		 * From the 160-bit SHA-1 output, only 64 bits are used.
		 */
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");  // Uses a secure Random not a simple Random
		byte[] bSalt = new byte[8]; // Salt generation 64 bits long
		random.nextBytes(bSalt);

		return byteToBase64(bSalt) ;
	}
	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 *
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return String The digested password
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 *         IOException
	 *             doesn't encoding/decoding
	 */
	public static String getHash(String password, String strSalt, int ITERATION_NUMBER) throws NoSuchAlgorithmException, IOException {

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();

		
		byte[] salt = null;
		salt = base64ToByte(strSalt);

		digest.update(salt);

		byte[] input = null;
		input = digest.digest(password.getBytes("UTF-8"));

		/* ----------------------------------------------------------------------
		 *      https://www.owasp.org/index.php/Hashing_Java
		 *      A minimum of 1000 operations is recommended in RSA PKCS5 standard.
		 *      The stored password looks like this :
		 *           Hash(hash(hash(hash(??╈?╈??.hash(password||salt)))))))))))))))
		 * ---------------------------------------------------------------------- */
		for(int i=0; i<ITERATION_NUMBER;i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return byteToBase64(input);
	}

	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 *
	 * @param data
	 *            String The base64 representation
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String data) throws IOException {
		
		Decoder decoder = Base64.getDecoder();
		return decoder.decode(data);
	}


	/**
	 * From a byte[] returns a base 64 representation
	 *
	 * @param data
	 *            byte[]
	 * @return String
	 * @throws IOException
	 */
	public static String byteToBase64(byte[] data) throws IOException {
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(data);
	}
	
}
