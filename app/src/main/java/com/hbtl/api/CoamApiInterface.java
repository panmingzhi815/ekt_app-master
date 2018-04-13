package com.hbtl.api;

import com.hbtl.api.model.CoamBaseResponse;
import com.hbtl.api.model.CoamResponseModel;
import com.hbtl.ekt.BuildConfig;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

interface CoamApiInterface {

    /*********
     * Common - Host Server
     *****************************************************************************************************************************************************************************************************************************************/
    // 根域
    //public final static String MAIN_DOMAIN = "coam.co";

    // 请求服务器[host]
    //public final static String WWW_HOST = "www." + MAIN_DOMAIN;
    public final static String MPI_HOST = BuildConfig.EKT_API_HOST;

    // 请求服务器[server]
    //public final static String MPI_SERVER = "https://" + MPI_HOST;
    //public static String MPI_SERVER = "http://facechecktest.hbekt.com:3000";
    //public static String MPI_SERVER = "http://120.77.11.196:3000";
    public static String MPI_SERVER = BuildConfig.EKT_API_SERVER;
    //public static String MPI_SERVER = "http://120.77.11.196:3001";
    //public final static String WWW_SERVER = "https://" + WWW_HOST;

    // 七牛云存储服务器
    //public static final String Cloud_DS = "https://qs-co.coam.co";
    //public static final String Mirror_DS = "https://qs-mo.coam.co";

    /*********
     * Common - Code
     *****************************************************************************************************************************************************************************************************************************************/

    // Code = {};
    public static final int Code_Failed = -401;
    public static final int Code_MissParams = -100;
    public static final int Code_TokenExpired = -198;
    public static final int Code_ErrorToken = -199;
    public static final int Code_DeviceNoExist = -200;
    public static final int Code_DeviceAlreadyRegister = -101;
    public static final int Code_FaceMismatch = -201;
    public static final int Code_OutOfEnterCountOneTourToday = -202;
    public static final int Code_OutOfEnterCountOneTourYear = -212;
    public static final int Code_MemberInfoNotFound = -203;
    public static final int Code_FaceDataError = -204;
    public static final int Code_CardStatusError = -205;
    public static final int Code_QrCodeError = -206;
    public static final int Code_CardInfoNotFound = -207;
    public static final int Code_IdCardMismatch = -208;
    public static final int Code_OfflineDevice = -209;
    public static final int Code_InvalidQrCodeError = -210;
    public static final int Code_CardExpired = -211;
    public static final int IdCardHasCode = 227;
    public static final int IdCardNoCode = -227;
    public static final int IdCardErrorCode = -228;

    // Code_Reservation = {};
    public static final int Code_Reservation_InvalidTime = -222;
    public static final int Code_Reservation_NotFound = -223;

    public static final int Code_IdCardEnterTypeForbidden = -224;


    public static final int Code_Success = 0;
    public static final int Code_DeviceRegisterSuccess = Code_Success;
    public static final int Code_BeatHeartSuccess = Code_Success;

    // Code_OK = {};
    public static final int Code_OK_EnterByFace = 200;
    public static final int Code_OK_EnterByIdCard = 201;
    public static final int Code_OK_DeviceControl = 202;
    public static final int Code_OK_NewVersion = 203;
    public static final int Code_OK_NoNewVersion = 204;
    public static final int Code_OK_EnterByQrCode = 205;
    public static final int Code_OK_QueryEnterCount = 206;
    public static final int Code_OK_EnterByOfflineQrCode = 210;

    // Code_OK_WxApp = {};
    public static final int Code_OK_WxApp_EnterByQrCode = Code_OK_EnterByQrCode;
    public static final int Code_OK_WxApp_EnterByFace = 208;
    public static final int Code_OK_WxApp_EnterByIdCard = 209;

    // Local Code...
    public static final int _Code_OK_EnterByFace = 2000;
    public static final int _Code_OutOfEnterCountOneTourToday = -2020;
    public static final int _Code_QrCodeError = -2060;
    public static final int _Code_OK_WxApp_EnterByIdCard = -2090;
    public static final int _Code_NoNetWork_EnterByIdCard = -2080;

    /*********
     * Common - Path
     *****************************************************************************************************************************************************************************************************************************************/

    // 腾旅一卡通《软件许可及服务协议》
    public static String LOAD_SOFTWARE_LICENSE_AND_SERVICES_AGREEMENT_BUS = MPI_SERVER + "/service/loadSoftwareLicenseAndServicesAgreement";
    // 零担订单跟踪(显示网页)
    public static String SEARCH_PIPELINE_ORDER_ROUTE_BUS = MPI_SERVER + "/service/searchPipelineOrderRoute/";

    // 用户二维码
    //public static String LOAD_ACCOUNT_QR_CODE = WWW_SERVER + "/service/loadAccountQrCode/";

    /*********
     * WWW - CommonService
     *****************************************************************************************************************************************************************************************************************************************/
    //获取 Token
    @FormUrlEncoded
    @POST("/service/requestToken")
    Call<CoamBaseResponse<CoamResponseModel.WwwCsRequestTokenModel>> requestToken(@FieldMap Map<String, String> parameter);

    // 获取图形验证码
    @GET("/service/loadAuthCodeVerifyImg")
    // Call<ResponseBody> loadAuthCodeVerifyImg(@Url String fileUrl);
    Call<ResponseBody> loadAuthCodeVerifyImg(@QueryMap Map<String, String> parameter);

    // 校验图形验证码
    @FormUrlEncoded
//    @POST("/service/sendAuthCodeVerifyCode")
//    Observable<CoamResponseModel.MainCsSendAuthCodeVerifyCodeModel> sendAuthCodeVerifyCode(@FieldMap Map<String, String> parameter);
    @POST("/service/verifyAuthCode")
    Observable<CoamBaseResponse<CoamResponseModel.WwwCsVerifyAuthCodeModel>> verifyAuthCode(@FieldMap Map<String, String> parameter);

    //获取短信验证码
    @FormUrlEncoded
    @POST("/service/getSecurityCode")
    Observable<CoamBaseResponse<CoamResponseModel.WwwCsGetSecurityCodeModel>> getSecurityCode(@FieldMap Map<String, String> parameter);

    //获取全国省市区 区域列表
    @FormUrlEncoded
    @POST("/ajaxLoad/runAreaInfo")
    Observable<CoamBaseResponse<CoamResponseModel.WwwCsRunAreaInfoModel>> runAreaInfo(@FieldMap Map<String, String> parameter);

    /*********
     * APP - CommonAccountManager
     *****************************************************************************************************************************************************************************************************************************************/
    // 使用普通手机账号密码登陆账号
    @FormUrlEncoded
    @POST("/accountInfo/sendLoginAccount/{loginWay}/")
    Observable<CoamBaseResponse<CoamResponseModel.CaiSendLoginAccountModel>> sendLoginAccount(@Path("loginWay") String loginWay, @FieldMap Map<String, String> parameter);

    //修改密码
    @FormUrlEncoded
    @POST("/accountInfo/sendRetrievePassword")
    Observable<CoamBaseResponse<CoamResponseModel.CaiSendRetrievePasswordModel>> sendRetrievePassword(@FieldMap Map<String, String> parameter);

    //第三方登陆
    @FormUrlEncoded
    @POST("/accountInfo/sendLoginSns")
    Observable<CoamBaseResponse<CoamResponseModel.CaiSendLoginSnsModel>> sendLoginSns(@FieldMap Map<String, String> parameter);

    //绑定第三方账号
    @FormUrlEncoded
    @POST("/accountInfo/sendSnsBindAccount")
    Observable<CoamBaseResponse<CoamResponseModel.CaiSendSnsBindAccountModel>> sendSNSBindAccount(@FieldMap Map<String, String> parameter);

    //解除第三方账号绑定
    @FormUrlEncoded
    @POST("/accountInfo/removeSnsAccountBind")
    Observable<CoamBaseResponse<CoamResponseModel.CaiRemoveSnsAccountBindModel>> removeSNSAccountBind(@FieldMap Map<String, String> parameter);

    //注册
    @FormUrlEncoded
    @POST("/accountInfo/sendCommonAccountInfoRegister")
    Observable<CoamBaseResponse<CoamResponseModel.CaiSendCommonAccountInfoRegisterModel>> sendCommonAccountInfoRegister(@FieldMap Map<String, String> parameter);

    //获取账户信息
    @FormUrlEncoded
    @POST("/accountInfo/searchAccountInfo")
    Observable<CoamBaseResponse<CoamResponseModel.CaiSearchAccountInfoModel>> searchAccountInfo(@FieldMap Map<String, String> parameter);

    //修改账户信息(帐户名,手机号,邮箱)
    @FormUrlEncoded
    @POST("/accountInfo/updateAdminAccountInfo")
    Observable<CoamBaseResponse<CoamResponseModel.CaiUpdateCommonAccountInfoModel>> updateAdminAccountInfo(@FieldMap Map<String, String> parameter);

    //发送验证邮箱
    @FormUrlEncoded
    @POST("/accountInfo/sendAccountAuthEmail")
    Observable<CoamBaseResponse<CoamResponseModel.CaiSendAccountAuthEmailModel>> sendAccountAuthEmail(@FieldMap Map<String, String> parameter);

    //修改账户信息(昵称,个性签名,是否开启消息推送,位置权限)
    @FormUrlEncoded
    @POST("/accountInfo/updateAppRunInfo")
    Observable<CoamBaseResponse<CoamResponseModel.CaiUpdateAccountInfoModel>> updateAppRunInfo(@FieldMap Map<String, String> parameter);

    /*********
     * APP - cloud
     *****************************************************************************************************************************************************************************************************************************************/
    @FormUrlEncoded
    @POST("/cloud/createCloudFileUpToken")
    Observable<CoamBaseResponse<CoamResponseModel.CCreateCloudFileUpTokenModel>> createCloudFileUpToken(@FieldMap Map<String, String> parameter);

    /*********
     * APP - CommonService
     *****************************************************************************************************************************************************************************************************************************************/
    //搜索历史
    @FormUrlEncoded
    @POST("/service/searchMyHsAreaList")
    Observable<CoamBaseResponse<CoamResponseModel.CsSearchMyHsAreaListModel>> searchMyHsAreaList(@FieldMap Map<String, String> parameter);

    //定时发送经纬度
    @FormUrlEncoded
    @POST("/service/sendPo")
    Observable<CoamBaseResponse<CoamResponseModel.CsSendPoModel>> sendPo(@FieldMap Map<String, String> parameter);

    //检查软件更新
    @FormUrlEncoded
    @POST("/api/v1/device/checkupdate")
    Observable<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>> checkAppUpdate(@FieldMap Map<String, String> parameter);

    // 设备注册
    @FormUrlEncoded
    @POST("/api/v1/device/registe")
    Observable<CoamBaseResponse<CoamResponseModel.CsAppDeviceRegisterModel>> appDeviceRegister(@FieldMap Map<String, String> parameter);

    // 心跳包
    @FormUrlEncoded
    @POST("/api/v1/device/beatheart")
    Observable<CoamBaseResponse<CoamResponseModel.CsAppBeatHeartModel>> appBeatHeart(@FieldMap Map<String, String> parameter);

    // 数据上报
    @FormUrlEncoded
    @POST("/api/v1/device/gate/datareport")
    Observable<CoamBaseResponse<CoamResponseModel.CsAppGateDataReportModel>> appGateDataReport(@FieldMap Map<String, String> parameter);

    // 数据上报
    @FormUrlEncoded
    @POST("/api/v1/device/ping")
    Observable<CoamBaseResponse<CoamResponseModel.CsAppGatePingModel>> appGatePing(@FieldMap Map<String, String> parameter);

    // 数据上报
    @FormUrlEncoded
    @POST("/api/v1/device/getencryptkey")
    Observable<CoamBaseResponse<CoamResponseModel.CsAppLoadEncryptKeyModel>> appLoadEncryptKey(@FieldMap Map<String, String> parameter);

    // 数据上报
    @FormUrlEncoded
    @POST("/api/v1/device/queryentercount")
    Observable<CoamBaseResponse<CoamResponseModel.CsAppQueryEnterCountModel>> appQueryEnterCount(@FieldMap Map<String, String> parameter);

    // 数据上报
    @FormUrlEncoded
    @POST("/api/v1/facecompare")
    Observable<CoamBaseResponse<CoamResponseModel.CsAppFaceCompareModel>> appFaceCompare(@FieldMap Map<String, String> parameter);

    // 投诉
    @FormUrlEncoded
    @POST("/service/sendComplaintsSuggestions")
    Observable<CoamBaseResponse<CoamResponseModel.CsSendComplaintsSuggestionsModel>> sendComplaintsSuggestions(@FieldMap Map<String, String> parameter);

    // 检索身份证卡码缓存...
    @FormUrlEncoded
    @POST("/members/checkIdCode")
    Observable<CoamBaseResponse<CoamResponseModel.CsCheckIdCodeModel>> checkIdCode(@FieldMap Map<String, String> parameter);

    /** The end... *****************************************************************************************************************************************************************************************************************************/
}
