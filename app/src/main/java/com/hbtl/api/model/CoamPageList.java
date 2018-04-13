package com.hbtl.api.model;

import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by 亚飞 on 10/18/2015.
 */

@ToString
public class CoamPageList<T> {
    // load page list...
    @Getter
    @Expose
    public int first;
    @Getter
    @Expose
    public int before;
    @Getter
    @Expose
    public int current;
    @Getter
    @Expose
    public int last;
    @Getter
    @Expose
    public int next;
    @Getter
    @Expose
    public int total_pages;
    @Getter
    @Expose
    public int total_items;
    @Getter
    @Expose
    public int limit;

    @Getter
    @Expose
    public List<T> items;
}