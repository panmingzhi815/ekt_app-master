package com.hbtl.utils;

import android.util.Log;

import com.hbtl.app.CoamApplicationLoader;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2017/11/20.
 */

public class HmacUtil {
    private static final String LOG_TAG = "HmacUtil";
    //private static final String REGISTER_HMAC_KEY = "12a9cc3f-1fd9-48a3-1fd9-1fd9d027ac2";
    //private static final String REGISTER_HMAC_KEY = "abcdefghijklmnop";

    public String stringToSign(String data) {
        String aesKey = CoamApplicationLoader.getInstance().appRunInfo.aesKey;
        if (aesKey == null) return null;

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(aesKey.getBytes("UTF-8"), mac.getAlgorithm());
            mac.init(secret);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            //return Base64.encodeToString(rawHmac, Base64.NO_WRAP);
            return toHexString(rawHmac);
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "Hash algorithm SHA-1 is not supported", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "Encoding UTF-8 is not supported", e);
        } catch (InvalidKeyException e) {
            Log.e(LOG_TAG, "Invalid key", e);
        }

        return null;
    }

    // 转化成 16 进制表示...
    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }
}
