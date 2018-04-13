package com.hbtl.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {
    public static boolean isPhoneNum(String phoneNum) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(17[0,7])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNum);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
