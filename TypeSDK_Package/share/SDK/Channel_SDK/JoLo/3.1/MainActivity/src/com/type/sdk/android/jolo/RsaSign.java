package com.type.sdk.android.jolo;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.jolo.joloalipay.Base64;

/**
 * RSA工具类
 * 
 */
public class RsaSign {

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * 支付内容加密，强烈建议放到服务器端
	 * @param content ：加密的明文
	 * @param privateKey ： CP用工具openssl生成的私钥
	 * @return
	 */
	public static String sign(String content, String privateKey) {
		String charset = "utf-8";
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(charset));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * 用户合法性校验，建议放在服务器端操作
	 * @param content ：用户信息，校验字符串，明文（在onActivityResult中接收到的）
	 * @param sign ：用户帐号信息签名(聚乐公钥验签)，密文，CP对该密文用公钥进行校验（在onActivityResult中接收到的签名字符串）
	 * @param publicKey ：SDK提供给CP的公钥
	 * @return
	 */
	public static boolean doCheck(String content, String sign, String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(publicKey);
			PublicKey pubKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes("utf-8"));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
