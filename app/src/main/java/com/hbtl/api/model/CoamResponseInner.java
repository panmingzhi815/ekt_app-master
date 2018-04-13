package com.hbtl.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hbtl.models.CommonAccountInfo;
import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by yafei on 2016/6/5.
 */
public class CoamResponseInner {

    // 通用支付相关
    public static class CreatePayInfo {
        @Expose
        @Getter
        public String payQrUri;// 通用[微信/支付宝]支付二维码...
        @Expose
        @Getter
        public String payUri;// 支付跳转 Uri...
        @Expose
        @Getter
        public String payNotifyUri;// 支付异步通知 Uri...
        @Expose
        @Getter
        public String payRedirectUri;// 商户支付结果跳转 Uri...
        @Expose
        @Getter
        public String payPingInfo;// Ping++支付 payChargeInfo...
        @Expose
        @Getter
        public String payAliInfo;// AliPay 支付 aliPayInfo...
        @Expose
        @Getter
        public PayWcInfo payWcInfo;// WeChat 支付 wcPayInfo...
    }

    public static class PayWcInfo {
        @Expose
        @Getter
        public String appId;// ...
        @Expose
        @Getter
        public String partnerId;// ...
        @Expose
        @Getter
        public String prepayId;// ...
        @Expose
        @Getter
        public String extData;// ...
        @Expose
        @Getter
        public String timestamp;// ...
        @Expose
        @Getter
        public String packageValue;// ["Sign=WXPay"]; // 暂填写固定值Sign=WXPay
        //        @Expose
//        @Getter
//        public String trade_type;// ...
        @Expose
        @Getter
        public String nonceStr;// ...
        @Expose
        @Getter
        public String sign;// ...
//        @Expose
//        @Getter
//        public String return_code;// ...
//        @Expose
//        @Getter
//        public String return_msg;// ...
//        @Expose
//        @Getter
//        public String result_code;// ...
    }

    public static class MemberInfo implements Parcelable {
        @Expose
        public String idcard;
        @Expose
        public int memberid;
        @Expose
        public String name;
        @Expose
        public String sex;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.idcard);
            dest.writeInt(this.memberid);
            dest.writeString(this.name);
            dest.writeString(this.sex);
        }

        public MemberInfo() {
        }

        protected MemberInfo(Parcel in) {
            this.idcard = in.readString();
            this.memberid = in.readInt();
            this.name = in.readString();
            this.sex = in.readString();
        }

        public static final Creator<MemberInfo> CREATOR = new Creator<MemberInfo>() {
            @Override
            public MemberInfo createFromParcel(Parcel source) {
                return new MemberInfo(source);
            }

            @Override
            public MemberInfo[] newArray(int size) {
                return new MemberInfo[size];
            }
        };
    }

    public static class DepositPayInfo {
        @Expose
        public String showPayNotifyInfoWay;
        @Expose
        public double selfPayDeposit;
        @Expose
        public double otherPayDeposit;
        @Expose
        public double accountBalance;
        @Expose
        public double selfWithdrawDeposit;
        @Expose
        public double otherWithdrawDeposit;
        @Expose
        public double selfExtractDeposit;
        @Expose
        public double otherExtractDeposit;
    }

    public static class AdminAccountInfo implements Parcelable {// extends CommonAccountInfo
        @Expose
        public boolean ifFollow;
        @Expose
        @Getter
        public CommonAccountInfo commonAccountInfo;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.ifFollow ? (byte) 1 : (byte) 0);
            dest.writeParcelable(this.commonAccountInfo, flags);
        }

        public AdminAccountInfo() {
        }

        protected AdminAccountInfo(Parcel in) {
            this.ifFollow = in.readByte() != 0;
            this.commonAccountInfo = in.readParcelable(CommonAccountInfo.class.getClassLoader());
        }

        public static final Creator<AdminAccountInfo> CREATOR = new Creator<AdminAccountInfo>() {
            @Override
            public AdminAccountInfo createFromParcel(Parcel source) {
                return new AdminAccountInfo(source);
            }

            @Override
            public AdminAccountInfo[] newArray(int size) {
                return new AdminAccountInfo[size];
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class FoursquareMete {
        @Expose
        public int code;
        @Expose
        public String requestId;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class FoursquareCategoriesResponse {
        @Expose
        @Getter
        public List<ResponseCategories> categories;
    }

    public static class ResponseCategories {
        @Expose
        @Getter
        public String id; // 4d4b7104d754a06370d81259
        @Expose
        @Getter
        public String name; // Arts & Entertainment
        @Expose
        @Getter
        public String pluralName; // Arts & Entertainment
        @Expose
        @Getter
        public String shortName; // Arts & Entertainment
        @Expose
        @Getter
        public ReCategorieIcon icon;
        @Expose
        @Getter
        public List<ResponseCategories> categories;
    }

    public static class ReCategorieIcon {
        @Expose
        @Getter
        public String prefix; // https://ss3.4sqi.net/img/categories_v2/arts_entertainment/default_
        @Expose
        @Getter
        public String suffix; // .png
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class FoursquarePoiResponse {
        @Expose
        @Getter
        public boolean confident;
        @Expose
        @Getter
        public List<ResponseVenues> venues;
    }

    public static class ResponseVenues {
        @Expose
        @Getter
        public String id;
        @Expose
        @Getter
        public String name;
        @Expose
        @Getter
        public VenuesLocation location;
        @Expose
        @Getter
        public boolean verified;
        @Expose
        @Getter
        public String referralId;
        @Expose
        @Getter
        public String[] venueChains;
        @Expose
        @Getter
        public List<VenuesCategories> categories;
        //        @Expose
//        public String[] contact;
        @Expose
        @Getter
        public VenuesHereNow hereNow;
        @Expose
        @Getter
        public VenuesSpecials specials;
        @Expose
        @Getter
        public VenuesStats stats;
    }

    public static class VenuesCategories {
        @Expose
        public String id;
        @Expose
        public String name;
        @Expose
        public VenuesCategoriesIcon icon;
        @Expose
        public String pluralName;
        @Expose
        public boolean primary;
        @Expose
        public String shortName;
    }

    public static class VenuesCategoriesIcon {
        @Expose
        public String prefix;
        @Expose
        public String suffix;
    }

    public static class VenuesHereNow {
        @Expose
        public int count;
        @Expose
        public List<VenuesHereNowGroup> groups;
        @Expose
        public String summary;
    }

    public static class VenuesHereNowGroup {
        @Expose
        public int count;
        @Expose
        public String[] items;
        @Expose
        public String name;
        @Expose
        public String type;
    }

    public static class VenuesLocation {
        @Expose
        public String cc;
        @Expose
        public String country;
        @Expose
        public String city;
        @Expose
        public String state;
        //@Expose
        //public int distance;
        @Expose
        public String address;
        @Expose
        public String[] formattedAddress;
        @Expose
        public double lat;
        @Expose
        public double lng;
    }

    public static class VenuesSpecials {
        @Expose
        public int count;
        @Expose
        public String[] items;
    }

    public static class VenuesStats {
        @Expose
        public int checkinsCount;
        @Expose
        public int tipCount;
        @Expose
        public int usersCount;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class AMapSuggestion {
        @Expose
        @Getter
        @Setter
        public String[] keywords;
        @Expose
        @Getter
        @Setter
        public List<AMapSuggestionCity> cities;
    }

    public static class AMapSuggestionCity {
        @Expose
        public String name;
        @Expose
        public String num;
        @Expose
        public String citycode;
        @Expose
        public String adcode;
    }

    public static class AMapPoi {
        @Expose
        public String id;//	"B000A816R6"
        @Expose
        public String name;//	"北京大学"
        @Expose
        public String type;//	"科教文化服务;学校;高等院校"
        @Expose
        public String typecode;//兴趣点类型编码	"141201"
        //@Expose
        //public String[] biz_type;//行业类型
        @Expose
        public String address;//	"颐和园路5号"
        @Expose
        public String location;//	"116.31088,39.99281"
        //@Expose
        //public String[] tel;//	"010-62752114"
        @Expose
        public String postcode;//
        @Expose
        public String website;//	"www.pku.edu.cn"
        @Expose
        public String pcode;//	"110000"
        @Expose
        public String pname;//"北京市"
        @Expose
        public String citycode;//	"010"
        @Expose
        public String cityname;//	"北京市"
        @Expose
        public String adcode;//	区域编码:"110108"
        @Expose
        public String adname;//	"海淀区"
        @Expose
        public String business_area;//所在商圈
        @Expose
        public String alias;//	"PKU|北京大学燕园校区|北大"
        @Getter
        @Expose
        public List<AMapPoiPhoto> photos;//
    }

    public static class AMapPoiPhoto {
        @Expose
        public String title;//
        @Expose
        public String url;//
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class BankRemittancesPayInfo {
        @Expose
        public String bcName;
        @Expose
        public String bankName;
        @Expose
        public String bankLogo;
        @Expose
        public String bcNo;
        @Expose
        public String depositBankName;
    }

    public static class MyAccountFundsInfo {
        @Expose
        public String balance;
        @Expose
        public String freezeFunds;
        @Expose
        public String historicalConsumption;
        @Expose
        public String icName;
        @Expose
        public String icAuthState;
    }

    public static class SignOrderInfo {
        @Expose
        public String soId;
        @Expose
        public String sId;
        @Expose
        public String oState;
        @Expose
        public String osState;
        @Expose
        public String oStateInfo;
        @Expose
        public String osStateInfo;
    }

}
