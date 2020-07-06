package com.android.utils;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


/**
 * 账单计划主表
 *
 * @author s
 */
public class RSA {

    // 签名算法
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    // 加密算法
    private static final String ENCRYPT_ALGORITHM = "RSA";
    // 解密算法
    private static final String DECRYPT_ALGORITHM = "RSA";
    private static final String CHARSET = "UTF-8";
    // 2048位rsa单次最大加密长度
    private static final int MAX_ENCRYPT_BLOCK = 234;
    // 2048位rsa单次最大解密长度
    private static final int MAX_DECRYPT_BLOCK = 256;

    private static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;

    static {
        // 对方base64后公钥字符串,用于验签和加密
        String pubKeyStr = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4HFxMLUANCo3DcUD9VAoHC8iBdhb5aeHrGzkvDI/om0FtArRh3Y4dqyahXXJ7/0jqnVgEb2i95U3mEdR+JxBA0TRZ3y+0k4iRAI25UGRPQQLWxd8pK6H+POc191M72vezzRooY8V2zs/pqCQDVscBN/8IJ6fIbci2VViPDBonp9+F1VMPy2bcRF1FiqgPGC6TSDlfYmLcG8ifQKB5bifTHFf1y4xix9C8znL9PcQfWOkJJpGPGemHLZF3jeLpt2ql5kertINJXKDTu/Ou2xt4gRVNqb08EfoaDNDN4DKHjFHYDf9s+2GF5qOWueVI7JN8eA14tLW6MinWNGeG8pddwIDAQAB";
        // 己方base64后私钥字符串,用于签名和解密
        String priKeyStr = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDgcXEwtQA0KjcNxQP1UCgcLyIF2Fvlp4esbOS8Mj+ibQW0CtGHdjh2rJqFdcnv/SOqdWARvaL3lTeYR1H4nEEDRNFnfL7STiJEAjblQZE9BAtbF3ykrof485zX3Uzva97PNGihjxXbOz+moJANWxwE3/wgnp8htyLZVWI8MGien34XVUw/LZtxEXUWKqA8YLpNIOV9iYtwbyJ9AoHluJ9McV/XLjGLH0LzOcv09xB9Y6QkmkY8Z6YctkXeN4um3aqXmR6u0g0lcoNO7867bG3iBFU2pvTwR+hoM0M3gMoeMUdgN/2z7YYXmo5a55Ujsk3x4DXi0tboyKdY0Z4byl13AgMBAAECggEAIfZol2M8RaiBri6625smhqVHM7U6qrTAHgJYVnYQFQcETus4K5RjFOonc7yQMyWOCRQ71lNo3qgQwpVAPSt/1vvJ6CwDQBWcr8faALT8QkxjX4RdUxop7y0F9dBB/RTIoXgfwRrCxurfRdqUEcFQqxgtsc3u7I4m0tZNyCfXHPFi+sWkdZc0jCZN+iSH5qWq9wHQRaKoq6eALJAMqMRxEJsFJaV+Qo6l3ucgkV++EW027ZTg7cTwZvq0Pc2X+qJ3boftSV58vseZIQ+R5HAJMmUCXLVt8HbxmtbDinNipBzbnezN2ePnL792Z3+CRi+K9My4k19vJghW5i2J6fSPEQKBgQDwOy7S9me/7P5YlxeZvXRzUE+tEUemJcwgZ91lb5PG7M2sW3bHP8xtcpoUKEOb8GVRghIx6wUcuLd66EHCWir3r1M+86esECt4yi304oaMix6FfdwTuaculPnN/YoRF45FBRQlp7d+rbdq1DPhijRovQ2eiW3vLEBXxTq7U6JomQKBgQDvLPVrszAa++iDt097Moy5EbTrt5XPV2Jze6XKHmYObWwNTLfOCgqsDmvyvxbAVkkmMCC0tgMmCJwtOPpppuGt2QiEppjwUc2WXPw33NLWt/rUm6dRGFnJWFbMhlMBoOprHapXjV26s+FGtkemIeYnzFxTjvv7jLzYOU49/ydwjwKBgBzAuKX/YAOUtgycaPK5vkpe/mpUeyLjuz7Vr6YIaMOSbDuI5vqKJEmlQdPPzefIKhhLXSokWwRJB4zqZ5R/fk0O6wPJ3fO2K73VVRLOWhRQQmLv4Xtq6RumMQ/6nJ/XFewk43huToQW/rEGnP8Nr6ApoIutHz9VKI5YyOLK69FhAoGBAM8/EdolBsYbCjs4GDMUdkPXSAIIrlNf0PtoZRxiguR1hG9xzsNGiEDWrktFsZ0wJ2pjwbNVFB1c0JePC82IwX021t66zUNQXuiv4g0116we98ZKqgznIMYLOgs3Sa3blcUDi5sZ7+HcBJSVVjhqn1hVCJuPBmHObcSew2GNOsuZAoGAR3EVmEcn9gZW4WxsQ+oXeEQNTPFIUmiRfllLv+0ayp1F5yL8Aq/cf+e8tRRVTGZY+4EgiDx3KfTZ637Nl2qfQXPvfLjDdFNIUa0o82jlE2OGe6ZLBPn2Ze0ee7wzZd7Hewq6ujolkSTt73yt3oN8ErJkVdzf69yTJMGOpNCVsno=";
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 用于验签和加密
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(pubKeyStr, Base64.DEFAULT)));
            // 用于签名和解密
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(priKeyStr, Base64.DEFAULT)));
        } catch (Exception e) {
            //日志记录
            e.printStackTrace();
        }
    }

    /**
     * 加签
     *
     * @param param 要加签内容
     * @return 加签后的内容
     */
    public static String sign(String param) {
        try {
            if (privateKey == null) {
                throw new RuntimeException("私钥未初始化");
            }
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(param.getBytes(CHARSET));
            return Base64.encodeToString(signature.sign(), Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException("签名异常", e);
        }
    }

    /**
     * 验签
     *
     * @param param 加签后的内容
     * @param sign  验签密钥
     * @return 验签结果
     */
    public static boolean veriSign(String param, String sign) {
        try {
            if (publicKey == null) {
                throw new RuntimeException("公钥未初始化");
            }
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(param.getBytes(CHARSET));
            return signature.verify(Base64.decode(sign, Base64.DEFAULT));
        } catch (Exception e) {
            throw new RuntimeException("验签失败", e);
        }
    }

    /**
     * 加密
     *
     * @param param 要加密的内容
     * @return 加密后的内容
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String encrypt(String param) {
        if (publicKey == null) {
            throw new RuntimeException("公钥未初始化");
        }
        if (param == null || param.length() == 0) {
            throw new IllegalArgumentException("待加密数据为空");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] data = param.getBytes(CHARSET);
            int inputLen = data.length;

            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen > MAX_ENCRYPT_BLOCK + offSet) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException("加密异常", e);
        }
    }


    /**
     * 解密
     *
     * @param param 加密的密文
     * @return 解密后的内容
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String decrypt(String param) {
        if (privateKey == null) {
            throw new RuntimeException("私钥未初始化");
        }
        if (param == null || param.length() == 0) {
            throw new IllegalArgumentException("待解密数据为空");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Cipher cipher = Cipher.getInstance(DECRYPT_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] data = Base64.decode(param, Base64.DEFAULT);
            int inputLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen > MAX_DECRYPT_BLOCK + offSet) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                i++;
                out.write(cache, 0, cache.length);
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            return new String(out.toByteArray(), CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密处理异常", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void main(String[] args) throws Exception {
//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//        keyPairGen.initialize(2048);
//        KeyPair keyPair = keyPairGen.generateKeyPair();
//        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//        System.out.println("privateKey: " + Base64Utils.encodeToString(privateKey.getEncoded()));
//        System.out.println("publicKey:  " + Base64Utils.encodeToString(publicKey.getEncoded()));

        /*
          加密和签名规则

          1. 双方各自生成2048位的RSA密钥对，把公钥进行base64位加密后，交给对方进行解密和验签
          2. 请求方把请求的明文JSON内容体转成String字符串后，用对方的公钥进行RSA加密，用本方的私钥进行数字签名，把密文和签字封装成如下格式发送给对方：
              {“data”:”数据密文”,”sign”:”签名结果”}
          3. 接收方接收数据密文后，用本方的私钥进行解密，用对方的公钥进行验签。
          4. 交易接收方的返回结果会用对方的公钥进行加密和用本方的私钥进行签名，返回格式同为：
              {“data”:”数据密文”,”sign”:”签名结果”}
         */
        String param = "test";
        String sign = sign(param);
        System.out.println("sign = " + sign);
        String enc = encrypt(param);
        System.out.println("enc = " + enc);

        String dec = decrypt(enc);
        System.out.println("dec = " + dec);
        boolean f = veriSign(dec, sign);
        System.out.println("f = " + f);
    }
}
