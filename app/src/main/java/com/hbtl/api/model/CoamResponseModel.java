package com.hbtl.api.model;

import com.google.gson.annotations.SerializedName;
import com.hbtl.models.CommonAccountInfo;
import com.hbtl.models.CommonAppRunInfo;
import com.hbtl.beans.CommonMyHsAreaInfo;
import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by 亚飞 on 10/18/2015.
 */

@ToString
public class CoamResponseModel {

    @ToString
    public class CommonResponseModel {
        // TODO...
    }

    /*********
     * WWW - CommonService
     *****************************************************************************************************************************************************************************************************************************************/
    @ToString
    public class WwwCsRequestTokenModel {

        //            @Expose
//            public String refreshTokenKey;
//            @Expose
//            public String requireAccessTokenBits;
        @Expose
        public String access_token;
        @Expose
        public String eaToken; // 新建立的 token ...
        @Expose
        public String erToken; // 刷新获取的 token ...
        @Expose
        public String atType;
        @Expose
        public String atWay;
    }

    @ToString
    public class WwwCsVerifyAuthCodeModel {

    }

    /*********
     * APP - AjaxLoad
     *****************************************************************************************************************************************************************************************************************************************/
    @ToString
    public class WwwCsRunAreaInfoModel {

    }

    /*********
     * APP - AjaxManager
     *****************************************************************************************************************************************************************************************************************************************/
    @ToString
    public class WwwCsGetSecurityCodeModel {
    }

    /*********
     * Other-Tester - Common...
     *****************************************************************************************************************************************************************************************************************************************/
    @ToString
    public class OtherCsFoursquarePoiModel {
        @Expose
        @Getter
        public CoamResponseInner.FoursquareMete meta;
        @Expose
        @Getter
        public CoamResponseInner.FoursquarePoiResponse response;
    }

    @ToString
    public class OtherCsFoursquareCategoriesModel {
        @Expose
        @Getter
        public CoamResponseInner.FoursquareMete meta;
        @Expose
        @Getter
        public CoamResponseInner.FoursquareCategoriesResponse response;
    }

    @ToString
    public class OtherCsAmapPoiRequestInfoModel {
        @Expose
        @Getter
        public String status; //1
        @Expose
        @Getter
        public String count; //249
        @Expose
        @Getter
        public String info; //OK
        @Expose
        @Getter
        public String infocode; // 10000
        @Expose
        @Getter
        public CoamResponseInner.AMapSuggestion suggestion;
        @Expose
        @Getter
        public List<CoamResponseInner.AMapPoi> pois;
    }

    /*********
     * APP - CommonAccountManager
     *****************************************************************************************************************************************************************************************************************************************/

    @ToString
    public class CaiRemoveSnsAccountBindModel {
    }

    @ToString
    public class CaiSearchAccountInfoModel {
        @Getter
        @Expose
        public CommonAccountInfo accountInfo;
    }

    @ToString
    public class CaiSendAccountAuthEmailModel {

    }

    @ToString
    public class CaiSendCommonAccountInfoRegisterModel {
        @Getter
        @Expose
        public CommonAccountInfo registerAccountInfo;

        // Sns Login Info
        @Expose
        public String snsBindWay;
        @Expose
        public int snsBindId;
    }

    @ToString
    public static class CaiSendLoginAccountModel {
        @Getter
        @Expose
        public CommonAppRunInfo appRunInfo;
        @Expose
        @Getter
        @Setter
        public CommonAccountInfo loginAccountInfo;

        // Sns Login Info
        @Expose
        public String snsBindWay;
        @Expose
        public int snsBindId;
    }

    @ToString
    public class CaiSendRetrievePasswordModel {
    }

    @ToString
    public class CaiSendSnsBindAccountModel {
    }

    @ToString
    public class CaiSendLoginSnsModel {
        //@SerializedName("id")
        @Expose
        public CommonAppRunInfo appRunInfo;
        //@SerializedName("common_account_login_fragment")
        @Expose
        public CommonAccountInfo loginAccountInfo;

        // Sns Login Info
        @Expose
        public String snsBindWay;
        @Expose
        public int snsBindId;
    }

    @ToString
    public class CaiUpdateAccountInfoModel {

    }

    @ToString
    public class CaiUpdateAccountNotifyServiceSettingModel {

    }

    @ToString
    public class CaiUpdateCommonAccountInfoModel {
    }

    /*********
     * APP - CloudStorage
     *****************************************************************************************************************************************************************************************************************************************/
    @ToString
    public class CCreateCloudFileUpTokenModel {
        //@SerializedName("id")
        @Expose
        public String upToken;
    }

    /*********
     * APP - CommonService
     *****************************************************************************************************************************************************************************************************************************************/

    @ToString
    public class CsCheckAppUpdateModel {
        @Expose
        @SerializedName("versionno")
        public int versionNo;
        @Expose
        @SerializedName("downloadurl")
        public String downloadUrl;
        @Expose
        public int oUpDn;
    }

    @ToString
    public class CsAppDeviceRegisterModel {
        @Expose
        public String token;
        @Expose
        public long expires;
        @Expose
        @SerializedName("tourid")
        public int tourId;
        @Expose
        @SerializedName("beatheart_period")
        public int beathearPeriod;
        @Expose
        @SerializedName("facecheck_threshold")
        public int faceCheckThreshold;
    }

    @ToString
    public class CsAppBeatHeartModel {
        @Expose
        @SerializedName("cmdid")
        public int cmdId;
        @Expose
        @SerializedName("extrainfo")
        public String extraInfo;
    }

    @ToString
    public class CsAppGateDataReportModel {

    }

    @ToString
    public class CsAppLoadEncryptKeyModel {
        @Expose
        @SerializedName("aeskey")
        public String aesKey;
        @Expose
        @SerializedName("rsa_publickey")
        public String rsaPublicKey;
    }

    @ToString
    public class CsAppGatePingModel {

    }

    @ToString
    public class CsAppQueryEnterCountModel {
        @Expose
        @SerializedName("date")
        public String date;
        @Expose
        @SerializedName("entercount")
        public int enterCount;
    }

    @ToString
    public class CsAppFaceCompareModel {
        @Expose
        @SerializedName("memberinfo")
        public CoamResponseInner.MemberInfo memberInfo;
//        @Expose
//        @SerializedName("firstname")
//        public String firstName;
//        @Expose
//        @SerializedName("lastname")
//        public int lastName;
    }

    @ToString
    public class CsSearchAccountInfoByQrCodeModel {
        @Getter
        @Expose
        public CoamResponseInner.AdminAccountInfo adminAccountInfo;
    }

    @ToString
    public class CsSearchCommonAccountListModel {
        @Getter
        @Expose
        public List<CommonAccountInfo> commonAccountInfoList;
    }

    @ToString
    public class CsSearchMyHsAreaListModel {
        @Getter
        @Expose
        public List<CommonMyHsAreaInfo> myHsAreaList;
    }

    @ToString
    public class CsSendComplaintsSuggestionsModel {

    }

    @ToString
    public class CsCheckIdCodeModel {
        @Getter
        @Expose
        public String icNo;
    }

    @ToString
    public class CsSendPoModel {

    }

    @ToString
    public class CsShareSnsShareInfoModel {

    }
}