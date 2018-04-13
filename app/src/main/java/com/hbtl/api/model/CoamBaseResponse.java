package com.hbtl.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by 亚飞 on 10/18/2015.
 */

@ToString
public class CoamBaseResponse<T> {

    @Expose
    @Getter
    @SerializedName("status")
    public int re_code;

    @Expose
    @Getter
    @SerializedName("message")
    public String re_desc;

    // 接口通用返回字段...
    public ReInfo re_info;

    // 接口通用返回数据<泛型>
    @SerializedName("data")
    public T re_data;

    // 接口通用返回字段...
    public static class ReInfo {
        @Expose
        @Getter
        public boolean result;
        @Expose
        @Getter
        public int re_code;
        @Expose
        @Getter
        public String re_desc;
    }
}