package com.hbtl.config;

import android.os.Environment;

import java.io.File;

public class CoamBuildVars {
    // Token 最大可刷新间隔-两分钟
    public static final int EKT_TOKEN_REFRESH_TS = 2 * 60 * 1000;
    // Token最大可用时长7天(检测6天)...
    public static final int EKT_TOKEN_CREATE_TS = 6 * 24 * 60 * 60 * 1000;

    // EKT 管理员授权登录密码...
    public static final String EKT_ADMIN_AUTH_PASSWORD = "666666";

    public static final String SAVE_DIR = "coam_save_dir";

    public static final String IS_FIRST_LAUNCH = "is_first_launch";

    // Web-Api
    //public static String AMAP_WEB_API_KEY = "d4b97ff20797efdd990fd957113a188d";
    //public static String AMAP_API_VERSION = "20150326";

    public static final String ACTION_DOWNLOAD_PROGRESS = "my_download_progress";
    public static final String ACTION_DOWNLOAD_SUCCESS = "my_download_success";
    public static final String ACTION_DOWNLOAD_FAIL = "my_download_fail";

    public static final String USER_IMAGE = "image";
    public static final String USER_NICK = "nick";
    public static final String USER_INVITE_REASON = "reason";

    public static final String SP_CONFIG = "config"; //SharedPreference配置文件
    public static final String SY_ACCOUNT = "sy_account"; //腾旅账号
    public static final String SY_PASSWORD = "sy_password"; //腾旅密码

    // tester android-11
    public static String publicCaKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAxXrXnsQDgFV95xPMo3QM\n" +
            "5Z4VioT9Cv1HTSy1fK7DzirGOLPbfZ3d58SJGVZGwA/GG5VGDDUFPWmUUjZeioIb\n" +
            "6x8WSyDIow27Uav8bwsGKh2MgmtpSAe4M9Spu6gYRaSycVT1zkikYSqvNElcv+ZG\n" +
            "SmdQxu7AYu4XkyolJ4aUloBLSe04PfKsIVyAObIjEM78/gjSnP+KCHBLZLhr2rWR\n" +
            "NpiXlHsKRdGHZzhwsKcguxWNQfdLyJCo+fT0DJnLfvGIUKBgebdfxKm2NCtLuIGV\n" +
            "MbLOYfFWkWVgV7kFQ7jk4l5+K90TOru+6QWgokusDdTogrhGJzsSSqpzH2LvfBpS\n" +
            "Lz7qA/J1rJDLMQbmeAgf7N8VeTTEM72UjJNrabe1DTwghyhqbOElD96IGjev0Wlw\n" +
            "8y4JZRDr0Vqe8/dI+8c86Go11RWX6iLL3NtWbJYAbct3yV0zKobIZAfGrhbCm/zJ\n" +
            "63pdyYm+5RoFCgnLwIE9QUO+DoTusdoyhgDfxBoV48wdVdqjUDGgfxcJPPI202Fk\n" +
            "a6uVyg9+lWSWpbz5tpVifzS6lJZdlRAkYf7olN1LzDw14VQhYZSMEtui1MtKJG56\n" +
            "K7NNNKtml+lv88GxrGs2oNn+61MYlxRRbAD3Shx7Bu+08ymzOngn1CVjzhdUtVUi\n" +
            "N0FEPzUB6u8qOJDvdCdDSjcCAwEAAQ==";
    public static String privateCaKey = "MIIJRAIBADANBgkqhkiG9w0BAQEFAASCCS4wggkqAgEAAoICAQDFeteexAOAVX3n\n" +
            "E8yjdAzlnhWKhP0K/UdNLLV8rsPOKsY4s9t9nd3nxIkZVkbAD8YblUYMNQU9aZRS\n" +
            "Nl6KghvrHxZLIMijDbtRq/xvCwYqHYyCa2lIB7gz1Km7qBhFpLJxVPXOSKRhKq80\n" +
            "SVy/5kZKZ1DG7sBi7heTKiUnhpSWgEtJ7Tg98qwhXIA5siMQzvz+CNKc/4oIcEtk\n" +
            "uGvatZE2mJeUewpF0YdnOHCwpyC7FY1B90vIkKj59PQMmct+8YhQoGB5t1/EqbY0\n" +
            "K0u4gZUxss5h8VaRZWBXuQVDuOTiXn4r3RM6u77pBaCiS6wN1OiCuEYnOxJKqnMf\n" +
            "Yu98GlIvPuoD8nWskMsxBuZ4CB/s3xV5NMQzvZSMk2tpt7UNPCCHKGps4SUP3oga\n" +
            "N6/RaXDzLgllEOvRWp7z90j7xzzoajXVFZfqIsvc21ZslgBty3fJXTMqhshkB8au\n" +
            "FsKb/Mnrel3Jib7lGgUKCcvAgT1BQ74OhO6x2jKGAN/EGhXjzB1V2qNQMaB/Fwk8\n" +
            "8jbTYWRrq5XKD36VZJalvPm2lWJ/NLqUll2VECRh/uiU3UvMPDXhVCFhlIwS26LU\n" +
            "y0okbnors000q2aX6W/zwbGsazag2f7rUxiXFFFsAPdKHHsG77TzKbM6eCfUJWPO\n" +
            "F1S1VSI3QUQ/NQHq7yo4kO90J0NKNwIDAQABAoICAQCFYoXFPQxIYHZb4Cm2taoI\n" +
            "ZhCSsWThSVYhsHMdNkYXORkOL52vaTP7WepobLf4/i6HbvoTQLS7WFbQeNy+nUcS\n" +
            "NrsuH4SlbZ76eTpWHpIZXEANz1MbTeuDRr8me1F5qNbGaeZlYPd2kggpPuwgyf7s\n" +
            "PoB2/ciAieTuh+b0nZg7V4iC9HGqlgDhv8L0NXgt4EFNrNPlK00migLCkzZ2UXE1\n" +
            "+TdZGQpFQlM8ywiCqUAYXWQ+DDTp0ZvwyR0WbcLfiXDTVKAVVmCfSypAboYEp7II\n" +
            "EYgI66PXLSw31EbUCUzAAacjJX3Zi/pVFnH7bTVXWll6AdGQqfue5yY/KuorXvPm\n" +
            "uZjAnqJ6L6b003HGnA4vIaQ5YVeAksGA42+cbNVQpOnslvlDI18JM2FXwym6mouH\n" +
            "muX4rJk1BuBDEtNxT860T9hXXOr9cOo+Sd6FFplGo14+QRs7M/51x3DfNrVYqLnj\n" +
            "mPkgwlQnQTnX/vpuf5diepTabfE0TcAmWPPuAwNQ5t859OfvcSFwNNZN6hUnDbRq\n" +
            "gEBPKnn6n5ihhmaxLu6LjW+FKSEbkKjybRsiNkwM9l9GteDQ61DU8IcTwkMnwpeg\n" +
            "b6F4lsG4J+iAzE3d8RjX/1X8gFdzNKqH+4nTGwlWgCYySsFWGXsu3y/QFTF+kMqK\n" +
            "aOhrHAcz/c6dzb5MOzcOEQKCAQEA6YfAcwNLxc260o/8kJp0Vee11l/8sZy7U47U\n" +
            "VoSrPeMh3yS4hlRi5xrXhi7KCauNVoBWyjB92l24NTgb4ZxV/zbB1mgMgmzp69j2\n" +
            "D7Hjl4gunvOn3yqxvQzW0wl18IWyLYJljDndZXlK8qw3VB+Ykcf0VgtVtkmXLU0X\n" +
            "OFqEhKjEv224tWCj4AJoMfIwBMqgzO6pYTpQmSAqKlxzpOHlZ/IdiEKgo7PWz1Kc\n" +
            "2wnGXivBQc5o1jvj6JvuooV62Uk7RDS7zvANM2wJ9q6X/k2GW5aMsmU6xvO7oIgB\n" +
            "eVg0Tv5Xt6PlUiPXBjQ+e+Brh8MV7MCcOiI3UHyrmLXId0LuJQKCAQEA2HsbbHW9\n" +
            "Oy2NXqT11EeVYjEoZHtcJoBB1O1SKhxx/PnN402sUu/4m85O0tpejHx1gBtrgpPC\n" +
            "ch/vFuQleTQOD1z/ztQbajf5hCMljZdvm5+9ieyrdD1lRYRsW75VhCNNynzKTHGZ\n" +
            "0+Jz9igHVM0NUsjz3tm5QZM/h9uA7IEQ9jBU5ADbu5UJEA5v+2O71NNMsvwfCKPk\n" +
            "yMK161+4B1j+lwoIIRKcgiiPOOY7dL2yEOhJhN6KDURlTcEGSHdu5WhwyoF1nBUc\n" +
            "ySRyEGsbXwynA27zrrsixSNSKur7q0+ZYHsYZ2LfPz8CoBQGhw2yA6KLRTT/Ds0O\n" +
            "cftQZ9o2hbUCKwKCAQAaOBGVWCNju+tjxKK4oX66Pl+eB/zY/B3UDc8tP4vyUNP2\n" +
            "t6q3+jV4eP0Fpa8tUsN1mMclwDgBElCiOQB4XhMRqlYs+lWdNxk0T5GgxXomAzYo\n" +
            "xnZsieCWtyBPZVqGfJw6m89G5SBESk/t2wQDP+DjTS3Tk0Y2RyDgBy26N/903XXp\n" +
            "gCoiwwzjE6IbKXPWteFD4O/HaMKvznqtct/Q/UPCXd6O89wRRRmzATV3KivJWuRB\n" +
            "vSjsFKFL9mXefVoDl0M+LyV+4Fi7E0AzH/sUpCOXmNmHFfJ5b9f6+AoiU5Lb45Z5\n" +
            "mb8fb8KugaMaSWIJRg/qGt3vcPQA515ksNQm/665AoIBAQCQFcA2/Faq4LLLX7hP\n" +
            "RiXjaRydZI39AjjO4/ZgGPa1jf6/X59mxFoQOcy1AqsTjnMWhSmVi4tMMn0MG2Xl\n" +
            "ugPxrdCFdR+aHw9wJ4SDLVwXlMau2ltzzrls/6qvlYuNm4HXWB+9qY7dPEg6eoIG\n" +
            "q0RG6YGHvhpTzxHl9u9kerMzaQ/Xgcq+m3+gtDtMcyE9kjXmm3B8a7mG9jHnK5pt\n" +
            "KYqqNP8cJCsPZreNA5BnLyspnbHxQb1Y/0D984+fA/9HqbAI2QnSjSrPpfZVfzSE\n" +
            "mwM+gcancd6+n7e84RII/73yPXwMs/4rNmEk8q81jKQDT1irA48P5uPKcZ8ewFqx\n" +
            "u7xpAoIBAQCHsrZQU2TD1TXf0pjgutdnBlWqAdUdbPG1Z8bKyn6+cFImi6GbK5wJ\n" +
            "zf67Ev8QvR3UHrpDizpoiOzM6BurPJwcA+UBkPc7PP+gUV+GtnH84/x4AkjcOm3o\n" +
            "UFQT8rHSK9hwzsIYTfCZGYYeOCxWHWGnJP7MIKkeB/417FxpN+5ihngA6wMSOr5N\n" +
            "XznZZY1nCkyXsBnPlf1g7Io1YCw1k2KdacDvArxdyLfYARZEPjuZXdGvpBTZvpOi\n" +
            "uuYlWc3VkzSZo3oFqsdYG7voPUStGM16miHiCjDEF0g3Wry90ph1i8JAGqxeMu+j\n" +
            "UK79wTuS9z8xv4mwONa/d2urrijj7Ejn";

    // Ekt 测试...
    public static String _publicCaKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4kIr7NnwGvl/NzRpV4W7\n" +
            "UXSaomzLVbLoapC1JcAKSJjIoFBiZ+afrb/GyLM6afYYEJbB3Q0/Yomu96YZh5rt\n" +
            "lAd9lAwa6R8yFMgquI0DI0M+HJqiQAa7naGsh9GQKNeCtbLvZ7cXr4WKyAK2bgoD\n" +
            "8K0cqBmqyLn5rDalRHbyA7BKKbFI29qUeZST7fpvcVi2sS+WLyqkGzeif7swZQdn\n" +
            "FQ7c4LuFpxK9jaLl2WEjBSW+PbP0YOnxXfEjXpd+yTLYh3kaaJ+imOHAeHMOECYZ\n" +
            "f+5ErlwoSUIMCJ0Zto1ZcHw5by36BzuUuwE/By8C4iiDse29jB7RV9CTgFyg0YpE\n" +
            "qQIDAQAB";

    public static String _privateCaKey = null;

    // 公私钥文件...
    public static String publicCaKf = "ca/public.pem";
    public static String privateCaKf = "ca/private.pem";

    public static final String IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + "shangyun" + File.separator + "image" + File.separator;

    public static final String HEADER_AUTH_TOKEN = "header_auth_token";
    public static final String HEADER_AUTH_TT = "header_auth_tt";
    public static final String STATUSBAR_HEIGHT = "statusbar_height";
}
