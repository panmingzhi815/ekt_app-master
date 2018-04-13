package com.hbtl.utils;

/**
 * Created by Administrator on 2017/11/17.
 */

import android.util.Base64;
import android.widget.Toast;

import com.hbtl.app.CoamApplicationLoader;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

/**
 * Created by shoewann on 16-9-23.
 * [Android中的AES加密解密技术](https://shoewann0402.github.io/2016/11/03/AES-in-Android/)
 * [Android AES 加密、解密](http://kongqw.com/2017/08/04/2017-08-04-Android-AES-%E5%8A%A0%E5%AF%86%E3%80%81%E8%A7%A3%E5%AF%86/)
 */

// [AES-PHP-TESTER](https://p.coam.co/aes.php)
public class AesUtils {

    private final static String Cipher_AES_CBC = "AES/CBC/PKCS5Padding";
    //private final static String Cipher_AES_CBC = "AES/CBC/PKCS7Padding";
    //private final static String Cipher_AES_CBC = "AES/CBC/ZeroPadding"; // 不可用,报错[java.security.NoSuchAlgorithmException: No provider found for AES/CBC/ZeroPadding]...
    //private final static String Cipher_AES_CBC = "AES/CBC/NoPadding";// 不可用,报错...
    private final static String Cipher_AES_ECB = "AES";
    private final static int Cipher_AES_128 = 128;
    private final static int Cipher_AES_192 = 192;
    private final static int Cipher_AES_256 = 256;

    // 设定 AES 加解密钥信息...
    private final static String aesWay = Cipher_AES_ECB;// AES_Way 加密方式...
    private final static int aesType = Cipher_AES_128;// AES_Type 加密方式...
    private static String aesKey = null;
    private static String aesIv = null;

    // 参考单例设计模式
    private static volatile AesUtils Instance = null;  // <<< 这里添加了 volatile

    public static AesUtils getInstance() {
        AesUtils inst = Instance;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (AesUtils.class) {
                inst = Instance;
                if (inst == null) {
                    inst = new AesUtils();
                    Instance = inst;
                }
            }
        }
        return inst;  // <<< 注意这里返回的是临时变量
    }

    // 加载加密 Key:Iv...
    public static void loadAesKiv() {
        //String aesWay = Cipher_AES_ECB;
        //int aesType = Cipher_AES_128;

        String qrAesKey = CoamApplicationLoader.getInstance().appRunInfo.aesKey;//"abcdefghijklmnop";
        String qrAesIv = CoamApplicationLoader.getInstance().appRunInfo.aesIv;//"000000";
        qrAesKey = "abcdefghijklmnop";//16
        //qrAesKey = "abcdefghijklmnop00000000";//24
        //qrAesKey = "abcdefghijklmnopabcdefghijklmnop";//32
        qrAesIv = "0000000011111111";// 初始向量 iv 只能是 16 位...

        if (qrAesKey == null) {
            Toast.makeText(CoamApplicationLoader.getInstance().getApplicationContext(), "抱歉,[qrAesKey: " + qrAesKey + "]格式非法,即将退出...", Toast.LENGTH_LONG).show();
            Timber.e("抱歉,[qrAesKey: " + qrAesKey + "]格式非法,即将退出...");
            return;
        }

        // 加密方式 [ECB|CBC]...
        switch (aesWay) {
            case Cipher_AES_ECB:

                break;
            case Cipher_AES_CBC:
                if (qrAesIv == null) {
                    Toast.makeText(CoamApplicationLoader.getInstance().getApplicationContext(), "抱歉,[qrAesIv: " + qrAesIv + "]格式非法,即将退出...", Toast.LENGTH_LONG).show();
                    Timber.e("抱歉,[qrAesIv: " + qrAesIv + "]格式非法,即将退出...");
                    return;
                }
                break;
        }

        // 加密位长 [128|192|256]...
        switch (aesType) {
            case Cipher_AES_128:
                // 判断Key是否为16位
                if (qrAesKey.length() != 16) {
                    Timber.e("Key长度不是[16]位...");
                    return;
                }
                break;
            case Cipher_AES_192:
                // 判断Key是否为24位
                if (qrAesKey.length() != 24) {
                    Timber.e("Key长度不是[24]位...");
                    return;
                }
                break;
            case Cipher_AES_256:
                // 判断Key是否为16位
                if (qrAesKey.length() != 32) {
                    Timber.e("Key长度不是[32]位...");
                    return;
                }
                break;
        }

        // 设置 AES 密钥...
        aesKey = qrAesKey;
        aesIv = qrAesIv;
    }

    public static String encrypt(String encryptContent) throws Exception {

        // 加载加密 Key:Iv...
        loadAesKiv();

        byte[] rawAesKey = getRawKey(aesKey.getBytes("UTF-8"));
        byte[] rawAesIv = getRawKey(aesIv.getBytes("UTF-8"));
        byte[] rawAesInfo = encryptContent.getBytes("UTF-8");

        SecretKeySpec skeySpec = new SecretKeySpec(rawAesKey, "AES");

        Cipher cipher = Cipher.getInstance(aesWay);
        // 初始化向量...
        IvParameterSpec iv = null;
        switch (aesWay) {
            case Cipher_AES_CBC:
                iv = new IvParameterSpec(rawAesIv);
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                break;
            case Cipher_AES_ECB:
                iv = new IvParameterSpec(new byte[cipher.getBlockSize()]);// 应与 encrypt 保持一致,否则解码会造成部分乱码...
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                break;
            default:
                ///Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                throw new Exception("UnKnown Cipher_AES_XXX error...");
        }

        byte[] encrypted = cipher.doFinal(rawAesInfo);

        // 使用 Base64 安全编码...
        String encoded = Base64.encodeToString(encrypted, Base64.DEFAULT);

        return encoded;
    }

    public static String decrypt(String encryptedContent) throws Exception {

        // 加载加密 Key:Iv...
        loadAesKiv();

        byte[] rawAesKey = getRawKey(aesKey.getBytes());
        byte[] rawAesIv = getRawKey(aesIv.getBytes());

        // 使用 Base64 安全编码...
        byte[] encrypted = Base64.decode(encryptedContent, Base64.DEFAULT);

        SecretKeySpec skeySpec = new SecretKeySpec(rawAesKey, "AES");

        Cipher cipher = Cipher.getInstance(aesWay);
        // 初始化向量...
        IvParameterSpec iv = null;
        switch (aesWay) {
            case Cipher_AES_CBC:
                iv = new IvParameterSpec(rawAesIv);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
                break;
            case Cipher_AES_ECB:
                iv = new IvParameterSpec(new byte[cipher.getBlockSize()]);// 应与 encrypt 保持一致,否则解码会造成部分乱码...
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                break;
            default:
                ///Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                throw new Exception("UnKnown Cipher_AES_XXX error...");
        }

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {

        // 暂不处理,直接返回key...
        if (true) return seed;

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);

        // 加密位长 [128|192|256]...
        switch (aesType) {
            case Cipher_AES_128:
                kgen.init(128, sr);
                break;
            case Cipher_AES_192:
                kgen.init(192, sr);
                break;
            case Cipher_AES_256:
                kgen.init(256, sr);
                break;
        }

        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    public static void test() throws Exception {
        // 原始串...
        String qrCodeInfo = "tlnkw8LcpNAj1Sxn1LNJ|1510912648|176FA00C6BEE6702076E9F21A88E5E89AAC3CAEA";
        qrCodeInfo = "X-I-X";
        Timber.d("[#AES#]加密原始串[qrCodeInfo:" + qrCodeInfo + "]");
        // encrypt （加密）
        String encryptQrCodeInfo = AesUtils.encrypt(qrCodeInfo);
        Timber.d("[#AES#]加密后[encryptQrCodeInfo:" + encryptQrCodeInfo + "]");
        // decrypt（解密）
        String decryptQrCodeInfo = AesUtils.decrypt(encryptQrCodeInfo);
        Timber.d("[#AES#]解密后[decryptQrCodeInfo:" + decryptQrCodeInfo + "]");

        // decrypt（直接解密测试）...

        String _qrCodeInfo = null;
        // AES_128_ECB
        //_qrCodeInfo = "7GlNR17MAbU+PippC6OmW5KDieKGUkZ3rsgs2P+DMP1GUZAYbb4hT5B/36Jlhxe2uFyn10NFdIsopebzXRblDVvuyNXsq4XwHX89Tp9w6/U=";
        // AES_192_ECB
        //_qrCodeInfo = "mAp9JsVprbYuhLxG8IZyB/SbkdZ1ioQyTrraNPy8R2pPtxYG24assbCfH/eqFj4yWfUeT69p7SocLde1zRuHhSBdOVcBZDeHZpUf/QiGZno=";
        // AES_256_ECB
        //_qrCodeInfo = "Dglz9N/lGRb1Bt+fcIwO2Xtzoyzf979kvz5MmI68y5xajLgzXp8dBw5B4VKqX5UEcgBvH3rxk8XKvaJE9pM5E5G6nnUa9qmxn9+0VB2NisA=";

        // AES_128_CBC
        _qrCodeInfo = "oSUIVLZEuxeYKD0glq/HC0c5FtRzTkYA487LkLhw+vOofROt7sEPqSON6YFa+zJyZEw3Uf2eM79MqO1VWtFHC2i3RT5CINm4GOE6V9BNR8U=";
        // AES_192_CBC
        _qrCodeInfo = "b0LbHOWVIyYBTeNdC86iVsNiDQcOxt2Dipoba5hf4TUSZJZZlOKXmB3rejA0UWuS9bOOh2TPeWYGWEtf8JlHL0gkwA6FOdXQtuhsMo4xwWE=";
        // AES_256_CBC
        _qrCodeInfo = "DMTZiYAAYRE6jPx0GhgbQ2C7bYoOpRd4AI6wReZx5zrMk4k+p6BDLDcW1YhdwBOh0ed1x8V7A+hGjnSOjbNk413bqM4s8HqNj5VJKxU0s8g=";

        // ekatong@00
        _qrCodeInfo = "7GlNR17MAbU+PippC6OmW+dfWM8HVWsrhP+lI1tCGiGdacA+RJVz99gfkRLxuModRs5Zyz6ufLQuLN9r8/49//6oBVs6bckqOgjXPAaH5Sg=";

        decryptQrCodeInfo = AesUtils.decrypt(_qrCodeInfo);
        Timber.d("[#AES#]->解密测试[" + decryptQrCodeInfo + "]");
    }
}