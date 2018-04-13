package com.hbtl.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class AppAreaInfo implements Parcelable {
    @Getter
    @Setter
    public String areaSerial;
    @Getter
    @Setter
    public String provinceName;
    @Getter
    @Setter
    public String cityName;
    @Getter
    @Setter
    public String districtName;
    @Getter
    @Setter
    public String showAreaLevel; // 显示层级 provinceLevel cityLevel districtLevel
    @Getter
    @Setter
    public String areaOne;
    @Getter
    @Setter
    public String areaLine;

    // 省市区 ...
    @ToString
    public static class AreaProvinceInfo implements Comparable<AreaProvinceInfo> {
        @Getter
        @Setter
        private String name;
        @Getter
        @Setter
        private String code;
        @Getter
        @Setter
        private List<AreaCityInfo> cityList;

        @Override
        public int compareTo(AreaProvinceInfo arg0) {
            return this.getCode().compareTo(arg0.getCode());
        }
    }

    @ToString
    public static class AreaCityInfo {
        @Getter
        @Setter
        private String name;
        @Getter
        @Setter
        private String code;
        @Getter
        @Setter
        private List<AreaDistrictInfo> districtList;
    }

    @ToString
    public static class AreaDistrictInfo {
        @Getter
        @Setter
        private String name;
        @Getter
        @Setter
        private String code;
    }

    public AppAreaInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.areaSerial);
        dest.writeString(this.provinceName);
        dest.writeString(this.cityName);
        dest.writeString(this.districtName);
        dest.writeString(this.showAreaLevel);
        dest.writeString(this.areaOne);
        dest.writeString(this.areaLine);
    }

    protected AppAreaInfo(Parcel in) {
        this.areaSerial = in.readString();
        this.provinceName = in.readString();
        this.cityName = in.readString();
        this.districtName = in.readString();
        this.showAreaLevel = in.readString();
        this.areaOne = in.readString();
        this.areaLine = in.readString();
    }

    public static final Creator<AppAreaInfo> CREATOR = new Creator<AppAreaInfo>() {
        public AppAreaInfo createFromParcel(Parcel source) {
            return new AppAreaInfo(source);
        }

        public AppAreaInfo[] newArray(int size) {
            return new AppAreaInfo[size];
        }
    };
}
