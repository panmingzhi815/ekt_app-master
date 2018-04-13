package com.hbtl.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import lombok.ToString;

/**
 * Created by 亚飞 on 2015-11-24.
 */
@ToString
public class CommonMyHsAreaInfo implements Parcelable {
    @Expose
    public String startAreaOne;
    @Expose
    public String startAreaSerial;
    @Expose
    public String endAreaOne;
    @Expose
    public String endAreaSerial;

    public CommonMyHsAreaInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.startAreaOne);
        dest.writeString(this.startAreaSerial);
        dest.writeString(this.endAreaOne);
        dest.writeString(this.endAreaSerial);
    }

    protected CommonMyHsAreaInfo(Parcel in) {
        this.startAreaOne = in.readString();
        this.startAreaSerial = in.readString();
        this.endAreaOne = in.readString();
        this.endAreaSerial = in.readString();
    }

    public static final Creator<CommonMyHsAreaInfo> CREATOR = new Creator<CommonMyHsAreaInfo>() {
        public CommonMyHsAreaInfo createFromParcel(Parcel source) {
            return new CommonMyHsAreaInfo(source);
        }

        public CommonMyHsAreaInfo[] newArray(int size) {
            return new CommonMyHsAreaInfo[size];
        }
    };
}
