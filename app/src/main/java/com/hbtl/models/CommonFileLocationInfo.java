package com.hbtl.models;

import com.hbtl.beans.CoamObject;

//import org.telegram.tgnet.AbstractSerializedData;

/**
 * Created by 亚飞 on 2015-10-21.
 */
public class CommonFileLocationInfo extends CoamObject {//
    public int dc_id;
    public long volume_id;
    public int local_id;
    public long secret;
    public byte[] key;
    public byte[] iv;

    public CommonFileLocationInfo() {
    }

//    public CommonFileLocationInfo(AbstractSerializedData stream, int constructor, boolean exception) {
//        //this.readParams(stream, exception);
//    }
}
