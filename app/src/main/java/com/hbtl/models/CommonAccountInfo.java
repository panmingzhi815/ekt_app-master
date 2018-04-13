package com.hbtl.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

//import io.realm.annotations.Ignore;
//import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by 亚飞 on 2015-10-21.
 */
public class CommonAccountInfo implements Parcelable { //  extends RealmObject
    //    @Ignore
    @Expose
    @Getter
    @Setter
    public int aiId;
    //    @PrimaryKey
    @Expose
    @Getter
    @Setter
    public String asId;
    @Expose
    @Getter
    @Setter
    public String aInfoName;
    @Expose
    @Getter
    @Setter
    public String aInfoPicture;
    @Expose
    @Getter
    @Setter
    public float aInfoLat;
    @Expose
    @Getter
    @Setter
    public float aInfoLng;
    @Expose
    @Getter
    @Setter
    public String aInfoLocation;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String aInfoPhone;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String aInfoEmail;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String icName;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String icAuthState;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String accountName;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String password;
    @Expose
    @Getter
    @Setter
    public String individualSignature;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String smsNotify;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String mailNotify;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String wcNotify;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String qqSnsName;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String wcSnsName;
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String wbSnsName;
    @Expose
    @Getter
    @Setter
    public String qqBind;
    @Expose
    @Getter
    @Setter
    public String wcBind;
    @Expose
    @Getter
    @Setter
    public String wbBind;

    // IM Login Info
    @Expose
    public String imAccount;
    @Expose
    public String imPassword;

    // Other ...
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String accountCompanyName;

    // AppRunAccountInfo List...
    /**
     * Customer, Driver, Logistics
     */
    @Expose
    @Getter
    @Setter
//    @Ignore
    public String as;

    // 是否开启自动登录
    @Expose
    @Getter
    @Setter
//    @Ignore
    public boolean autoLogin = false;

    // 是否已登陆 － 给 IM 判断是否自动连接
    @Expose
    @Getter
    @Setter
//    @Ignore
    public boolean ifLogin = false;
    // ............................................................................................................................................................................

    public CommonAccountInfo() {
    }

    //..........................

    public static class Cooperator {
        @Expose
        public String cooAsId;
        @Expose
        public String cooAsInfoPicture;
        @Expose
        public String cooTag;
        @Expose
        public String cooTagInfo;
        @Expose
        public String cooAsInfoName;
        @Expose
        public String cooWay;
        @Expose
        public String cooNum;
    }

    public static class Follower {
        @Expose
        public String foAsId;
        @Expose
        public String foAsInfoName;
        @Expose
        public String foAsInfoPicture;
        @Expose
        public String foWay;
        @Expose
        public String foNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.aiId);
        dest.writeString(this.asId);
        dest.writeString(this.aInfoName);
        dest.writeString(this.aInfoPicture);
        dest.writeFloat(this.aInfoLat);
        dest.writeFloat(this.aInfoLng);
        dest.writeString(this.aInfoLocation);
        dest.writeString(this.aInfoPhone);
        dest.writeString(this.aInfoEmail);
        dest.writeString(this.icName);
        dest.writeString(this.icAuthState);
        dest.writeString(this.accountName);
        dest.writeString(this.password);
        dest.writeString(this.individualSignature);
        dest.writeString(this.smsNotify);
        dest.writeString(this.mailNotify);
        dest.writeString(this.wcNotify);
        dest.writeString(this.qqSnsName);
        dest.writeString(this.wcSnsName);
        dest.writeString(this.wbSnsName);
        dest.writeString(this.qqBind);
        dest.writeString(this.wcBind);
        dest.writeString(this.wbBind);
        dest.writeString(this.imAccount);
        dest.writeString(this.imPassword);
        dest.writeString(this.accountCompanyName);
        dest.writeString(this.as);
        dest.writeByte(this.autoLogin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.ifLogin ? (byte) 1 : (byte) 0);
    }

    protected CommonAccountInfo(Parcel in) {
        this.aiId = in.readInt();
        this.asId = in.readString();
        this.aInfoName = in.readString();
        this.aInfoPicture = in.readString();
        this.aInfoLat = in.readFloat();
        this.aInfoLng = in.readFloat();
        this.aInfoLocation = in.readString();
        this.aInfoPhone = in.readString();
        this.aInfoEmail = in.readString();
        this.icName = in.readString();
        this.icAuthState = in.readString();
        this.accountName = in.readString();
        this.password = in.readString();
        this.individualSignature = in.readString();
        this.smsNotify = in.readString();
        this.mailNotify = in.readString();
        this.wcNotify = in.readString();
        this.qqSnsName = in.readString();
        this.wcSnsName = in.readString();
        this.wbSnsName = in.readString();
        this.qqBind = in.readString();
        this.wcBind = in.readString();
        this.wbBind = in.readString();
        this.imAccount = in.readString();
        this.imPassword = in.readString();
        this.accountCompanyName = in.readString();
        this.as = in.readString();
        this.autoLogin = in.readByte() != 0;
        this.ifLogin = in.readByte() != 0;
    }

    public static final Creator<CommonAccountInfo> CREATOR = new Creator<CommonAccountInfo>() {
        @Override
        public CommonAccountInfo createFromParcel(Parcel source) {
            return new CommonAccountInfo(source);
        }

        @Override
        public CommonAccountInfo[] newArray(int size) {
            return new CommonAccountInfo[size];
        }
    };
}
