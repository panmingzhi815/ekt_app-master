package com.hbtl.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by 亚飞 on 2015-10-21.
 */
public class CommonLocationInfo {
    @Getter
    @Setter
    public String id;
    @Getter
    @Setter
    public double lat;
    @Getter
    @Setter
    public double lng;
    @Getter
    @Setter
    public String location;
    @Getter
    @Setter
    public String code;
    @Getter
    @Setter
    public String title;
    @Getter
    @Setter
    public String picture;
    @Getter
    @Setter
    public String country;
    @Getter
    @Setter
    public String province;
    @Getter
    @Setter
    public String city;
    @Getter
    @Setter
    public String provider;


    @Getter
    @Setter
    public boolean disableFree;
}
