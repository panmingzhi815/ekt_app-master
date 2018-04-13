package com.hbtl.api;

import com.hbtl.api.model.CoamBaseResponse;

/**
 * Created by liukun on 16/3/10.
 */
public class ApiException extends RuntimeException {

    // 通用错误校验码
    public static final int COMMON_RESULT_ERROR_CODE = 3400;
    public static final int COMMON_EXPIRE_ACCESS_TOKEN_ERROR_CODE = 3401;
    public static final int COMMON_ACCESS_FORBIDDEN_ERROR_CODE = 3403;
    public static final int COMMON_SERVER_MAINTENANCE_ERROR_CODE = 3408;
    public static final int COMMON_ACCOUNT_CONFLICT_ERROR_CODE = 3409;
    public static final int COMMON_ACCOUNT_LOCKED_ERROR_CODE = 3406;
    // Other...
    public static final int USER_NOT_EXIST = 100;
    public static final int WRONG_PASSWORD = 101;

    public ApiException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话,用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换,在显示给用户
     *
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code) {
        String message = "";
        switch (code) {
            case COMMON_RESULT_ERROR_CODE:
                message = "通用 false 错误";
                break;
            case COMMON_EXPIRE_ACCESS_TOKEN_ERROR_CODE:
                message = "登陆超时,请重新登陆";
                break;
            case COMMON_ACCESS_FORBIDDEN_ERROR_CODE:
                message = "资源禁止访问";
                break;
            case COMMON_SERVER_MAINTENANCE_ERROR_CODE:
                message = "服务维护中,请稍后访问";//服务器繁忙
                break;
            case COMMON_ACCOUNT_CONFLICT_ERROR_CODE:
                message = "异地登陆提醒";//
                break;
            case COMMON_ACCOUNT_LOCKED_ERROR_CODE:
                message = "账户被锁定";//
                break;
            case USER_NOT_EXIST:
                message = "该用户不存在";
                break;
            case WRONG_PASSWORD:
                message = "密码错误";
                break;
            default:
                message = "未知错误";

        }
        return message;
    }

    // ...
    public <T> ApiException(CoamBaseResponse<T> baseResponseModel) {
        this(getApiExceptionMessage(baseResponseModel.re_info.re_code));
    }

    public CoamBaseResponse ApiException(CoamBaseResponse baseResponseModel) {
        //super(baseResponseModel.resultInfo);
        return baseResponseModel;
    }

    private static CoamBaseResponse getApiExceptionMessage(CoamBaseResponse baseResponseModel) {
        return baseResponseModel;
    }
}

