package com.hbtl.utils;

import android.content.Context;
import android.util.Base64;

import com.hbtl.config.CoamBuildVars;

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import timber.log.Timber;

/**
 * @author Mr.Zheng
 * @date 2014年8月22日 下午1:44:23
 * https://github.com/zhengxiaopeng/AndroidUtils/blob/006c207c2bad47fc08eb4705796a7d7fa6be46fa/library/src/main/java/com/rocko/android/common/security/RSAHelper.java
 */

//    针对java后端进行的RSA加密,android客户端进行解密,结果是部分乱码的问题:
//    注意两点,编码问题和客户端使用的算法问题
//    即:都使用UTF-8编码,Base64使用一致,
//    另外,使用下面的代码在后端和移动端解密只有一点不同:
//    移动端使用:
//    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//    后端使用:
//    Cipher cipher = Cipher.getInstance("RSA");
//    其他地方都不需要改动
//    关于加密填充方式:之前以为上面这些操作就能实现rsa加解密,以为万事大吉了,呵呵,这事还没完,悲剧还是发生了,Android这边加密过的数据,服务器端死活解密不了,原来android系统的RSA实现是"RSA/None/NoPadding",而标准JDK实现是"RSA/None/PKCS1Padding" ,这造成了在android机上加密后无法在服务器上解密的原因,所以在实现的时候这个一定要注意.
//   [Android数据加密之Rsa加密](http://www.cnblogs.com/whoislcj/p/5470095.html)
//    [RSA的公钥和私钥到底哪个才是用来加密和哪个用来解密?](https://www.zhihu.com/question/25912483)

public final class RsaUtils {
    /**
     * KeyFactory.getInstance("RSA")
     * Cipher.getInstance("RSA/ECB/PKCS1Padding")
     * 获取 KeyFactory 时 getInstance 要传 ["RSA"] 不能传 ["RSA/ECB/PKCS1Padding"],否则会报 NoSuchAlgorithmException 错误
     * Cipher.getInstance 加解密传入的参数要一致,如果是与PHP交互加解密, Cipher.getInstance要传["RSA/ECB/PKCS1Padding"]
     */
    private static String RSA = "RSA";
    private static String RsaPadding = "RSA/ECB/PKCS1Padding";
    //private static String RsaPadding = "RSA/None/NoPadding";
    //private static String RsaPadding = "RSA/ECB/NoPadding";

    /**
     * 随机生成RSA密钥对(默认65555555555555 密钥长度为1024)
     *
     * @return
     */
    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(1024);
    }

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength 密钥长度,范围:512～2048<br>
     *                  一般1024
     * @return
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥加密 - 标准...
     * 每次加密的字节数,不能超过密钥的长度值减去11
     *
     * @param data      需加密数据的byte数据
     * @param publicKey 公钥
     * @return 加密后的byte型数据
     */
    public static byte[] encryptData(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RsaPadding);
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 传入编码数据并返回编码结果
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密 - 标准...
     *
     * @param encryptedData 经过encryptedData()加密返回的byte数据
     * @param privateKey    私钥
     * @return
     */
    public static byte[] decryptData(byte[] encryptedData, PrivateKey privateKey) {
        try {
            //Cipher cipher = Cipher.getInstance(RSA);
            Cipher cipher = Cipher.getInstance(RsaPadding);

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encryptedData);
        } catch (Exception e) {
            return null;
        }
    }

    // 使用公钥加密 - 标准......
    public static String encryptDataToStr(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RsaPadding);
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 传入编码数据并返回编码结果
            // 加密
            byte[] encryptByte = cipher.doFinal(data);
            return new String(Base64.encode(encryptByte, Base64.DEFAULT));
            //return new String(encryptByte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 用私钥解密 - 标准...
    public static String decryptDataToStr(byte[] encryptedData, PrivateKey privateKey) {
        try {
            // [android 客户端 RSA加密 要注意的问题](http://www.cnblogs.com/qianyukun/p/4998150.html)
            // 注意这里,移动端使用 ["RSA/ECB/PKCS1Padding"] 否则解码会出现乱码...
            //Cipher cipher = Cipher.getInstance(RSA);
            Cipher cipher = Cipher.getInstance(RsaPadding);

            cipher.init(Cipher.DECRYPT_MODE, privateKey);

//            byte[] decryptByte = cipher.doFinal(encryptedData);
            byte[] decryptByte = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
            return new String(decryptByte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 使用私钥加密 - 非标准...
    public static String encryptDataToStr(byte[] data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RsaPadding);
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            // 传入编码数据并返回编码结果
            // 加密
            byte[] encryptByte = cipher.doFinal(data);
            return new String(Base64.encode(encryptByte, Base64.DEFAULT));
            //return new String(encryptByte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 使用公钥解密 - 非标准...
    public static String decryptDataToStr(byte[] encryptedData, PublicKey publicKey) {
        try {
            // [android 客户端 RSA加密 要注意的问题](http://www.cnblogs.com/qianyukun/p/4998150.html)
            // 注意这里,移动端使用 ["RSA/ECB/PKCS1Padding"] 否则解码会出现乱码...
            //Cipher cipher = Cipher.getInstance(RSA);
            Cipher cipher = Cipher.getInstance(RsaPadding);

            cipher.init(Cipher.DECRYPT_MODE, publicKey);

//            byte[] decryptByte = cipher.doFinal(encryptedData);
            byte[] decryptByte = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
            return new String(decryptByte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过公钥byte[](publicKey.getEncoded())将公钥还原,适用于RSA算法
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 通过私钥byte[]将公钥还原,适用于RSA算法
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 使用N、e值还原公钥
     *
     * @param modulus
     * @param publicExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(String modulus, String publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 使用N、d值还原私钥
     *
     * @param modulus
     * @param privateExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKey(String modulus, String privateExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(privateExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decode(publicKeyStr.getBytes(), Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从字符串中加载私钥<br>
     * 加载时使用的是PKCS8EncodedKeySpec(PKCS#8编码的Key指令).
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = Base64.decode(privateKeyStr, Base64.DEFAULT);
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            Timber.e("||||||||||||||||||||||||-InvalidKeySpecException:" + e.toString());
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param publicCaKf 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public static PublicKey loadPublicKey(Context context, String publicCaKf) throws Exception {
        InputStream pub_s = null;
        String publicCaKey = null;
        try {
            //InputStream pub_s = readCaIs(context, publicCaKf);
            //String publicCaKey = readKey(pub_s);
            pub_s = context.getAssets().open(publicCaKf);
            publicCaKey = readKey(pub_s);
            pub_s.close();

            return loadPublicKey(publicCaKey);
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        } finally {
            try {
                if (pub_s != null) pub_s.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @return 是否成功
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey(Context context, String privateCaKf) throws Exception {
        InputStream pri_s = null;
        String privateCaKey = null;
        try {
            //InputStream pri_s = readCaIs(context, privateCaKf);
            //String privateCaKey = readKey(pri_s);
            pri_s = context.getAssets().open(privateCaKf);
            privateCaKey = readKey(pri_s);
            pri_s.close();

            return loadPrivateKey(privateCaKey);
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        } finally {
            try {
                if (pri_s != null) pri_s.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 读取密钥信息
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static String readKey(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine = null;
        StringBuilder sb = new StringBuilder();
        while ((readLine = br.readLine()) != null) {
            if (readLine.charAt(0) == '-') {
                continue;
            } else {
                sb.append(readLine);
                sb.append('\r');
            }
        }

        return sb.toString();
    }

    /**
     * 打印公钥信息
     *
     * @param publicKey
     */
    public static void printPublicKeyInfo(PublicKey publicKey) {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
        System.out.println("----------RSAPublicKey----------");
        System.out.println("Modulus.length=" + rsaPublicKey.getModulus().bitLength());
        System.out.println("Modulus=" + rsaPublicKey.getModulus().toString());
        System.out.println("PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());
        System.out.println("PublicExponent=" + rsaPublicKey.getPublicExponent().toString());
    }

    /**
     * 打印私钥信息
     *
     * @param privateKey
     */
    public static void printPrivateKeyInfo(PrivateKey privateKey) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
        System.out.println("----------RSAPrivateKey ----------");
        System.out.println("Modulus.length=" + rsaPrivateKey.getModulus().bitLength());
        System.out.println("Modulus=" + rsaPrivateKey.getModulus().toString());
        System.out.println("PrivateExponent.length=" + rsaPrivateKey.getPrivateExponent().bitLength());
        System.out.println("PrivatecExponent=" + rsaPrivateKey.getPrivateExponent().toString());
    }

    /**
     * 读取 ca 文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容,失败返回null
     */
//    public static String readCa(Context context, String fileName) {
//        InputStream is = null;
//        String content = null;
//        try {
//            is = context.getAssets().open(fileName);
//            if (is != null) {
//
//                byte[] buffer = new byte[1024];
//                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//                while (true) {
//                    int readLength = is.read(buffer);
//                    if (readLength == -1) break;
//                    arrayOutputStream.write(buffer, 0, readLength);
//                }
//                is.close();
//                arrayOutputStream.close();
//                content = new String(arrayOutputStream.toByteArray());
//
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//            content = null;
//        } finally {
//            try {
//                if (is != null) is.close();
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//        }
//        return content;
//    }

    // [关于Android Assets读取文件为File对象](http://www.cnblogs.com/spring87/p/5990156.html)
    public static InputStream readCaIs(Context context, String fileName) {
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return is;
    }

    // 测试 rsa 加密解密...
    public static void testRsaCa() {
        try {
            Timber.i("-----------------------------------------");
            // 最长不能超过 117 位,否则加密会报错...
            String testStr = "123456789|123456789|123456789|123456789|123456789|123456789|123456789|123456789|123456789|123456789|123456789|.......";

            // 直接从字符串获取 公钥 和 私钥 ...
            PublicKey publicKey = RsaUtils.loadPublicKey(CoamBuildVars.publicCaKey);
            PrivateKey privateKey = RsaUtils.loadPrivateKey(CoamBuildVars.privateCaKey);

            // 从文件中加载公钥和私钥...
            //InputStream pri_s = readCaIs(CoamApplicationLoader.appContextInstance, CoamBuildVars.publicCaKf);
            //InputStream pub_s = readCaIs(CoamApplicationLoader.appContextInstance, CoamBuildVars.privateCaKf);
            //PublicKey publicKey = loadPublicKey(CoamApplicationLoader.appContextInstance, CoamBuildVars.publicCaKf);
            //PrivateKey privateKey = loadPrivateKey(CoamApplicationLoader.appContextInstance, CoamBuildVars.privateCaKf);
            //pri_s.close();
            //pub_s.close();

            String afterEncrypt = RsaUtils.encryptDataToStr(testStr.getBytes("UTF-8"), publicKey);
            String afterDecrypt = RsaUtils.decryptDataToStr(afterEncrypt.getBytes("UTF-8"), privateKey);
            //输出加密后的内容,与服务器端校验字符串是否一致,防止通过url编码后服务器端没有解码导致解密出现问题
            Timber.i("TTTTTTTTTT[testStr: " + testStr + "]");
            Timber.i("afterEncrypt:" + afterEncrypt);
            Timber.i("afterDecrypt:" + afterDecrypt);

            //String _testStr = "fr24vyqNf4TDXMA2";
            String _testStr = "7GlNR17MAbU+PippC6OmW5KDieKGUkZ3rsgs2P+DMP1GUZAYbb4hT5B/36Jlhxe2uFyn10NFdIsopebzXRblDVvuyNXsq4XwHX89Tp9w6/U=";
            PublicKey _publicKey = RsaUtils.loadPublicKey(CoamBuildVars._publicCaKey);
            String _afterDecrypt = RsaUtils.decryptDataToStr(_testStr.getBytes("UTF-8"), publicKey);
            Timber.i("TTTTTTTTTT[_testStr: " + _testStr + "]");
            Timber.i("_afterDecrypt:" + _afterDecrypt);


            testVerifySign();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String ALGORITHM = "RSA";
    private static final String PRIVATE_KEY_PATH = "D:\\rsa_private.isa";
    private static final String PUBLIC_KEY_PATH = "D:\\rsa_public.isa";

    /**
     * 测试签名
     * @throws Exception
     */
//    public void testSign() throws Exception {
//        byte[] sign = this.sign("Hello World");
//        String result = Base64Util.getEncoder().encodeToString(sign);
//        System.out.println(result);
//    }

    /**
     * 私钥签名
     * @param data
     * @return
     * @throws Exception
     */
//    private byte[] sign(String data) throws Exception {
//        //读取储存的私钥字节数组
//        byte[] privateKeyCode = MediaStore.Files.readAllBytes(Paths.get(PRIVATE_KEY_PATH));
//        //包装私钥字节数组为一个KeySpec
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyCode);
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
//        //通过KeyFactory生成私钥
//        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
//        Signature signature = Signature.getInstance("MD5withRSA");//签名的算法
//        //通过私钥初始化Signature,签名时用
//        signature.initSign(privateKey);
//        //指定需要进行签名的内容
//        signature.update(data.getBytes());
//        //签名
//        byte[] result = signature.sign();
//        return result;
//    }

    /**
     * 测试公钥验签
     *
     * @throws Exception
     */
    public static void testVerifySign() throws Exception {
        Timber.i("[###############################################################][验签]...");
        //String data = "Hello World";
        String data = "tlnkw8LcpNAj1Sxn1LNJ|1510912648";
        String sign = "176FA00C6BEE6702076E9F21A88E5E89AAC3CAEA";
        byte[] signer = sign.getBytes();
        //byte[] sign = this.sign(data);
        Signature signature = Signature.getInstance("MD5withRSA");

//        byte[] publicKeyCode = MediaStore.Files.readAllBytes(Paths.get(PUBLIC_KEY_PATH));
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyCode);
//        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
//        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        PublicKey publicKey = RsaUtils.loadPublicKey(CoamBuildVars._publicCaKey);

        //以验签的方式初始化Signature
        signature.initVerify(publicKey);
        //指定需要验证的签名
        signature.update(data.getBytes());
        //进行验签,返回验签结果
        boolean result = signature.verify(signer);
        Assert.assertTrue(result);
    }
}
