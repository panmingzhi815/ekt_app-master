package com.hbtl.models;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by yzhang on 2017/11/26.
 * [数据库版本迁移](https://juejin.im/entry/58e4cbecac502e4957a58a42)...
 */

public class EktRealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // DynamicRealm 暴露了一个可编辑的 schema
        RealmSchema schema = realm.getSchema();

        // 迁移到版本 1 : 添加一个新的类
        // 示例:
        // public Person extends RealmObject {
        //     private String name;
        //     private int age;
        //     // getters and setters left out for brevity
        // }
        if (oldVersion == 0) {
            // EnterAuthModel
            schema.create("EnterAuthModel")
                    .addField("qrId", String.class, FieldAttribute.PRIMARY_KEY)
                    .addField("authWay", String.class)
                    .addField("icNo", String.class)
                    .addField("qrCode", String.class)
                    .addField("qrCardSn", String.class)
                    .addField("qrCardTs", long.class)
                    .addField("qrCardSign", String.class)
                    .addField("captureImgFn", String.class)
                    .addField("authEnterDts", long.class);
            oldVersion++;
        }

        // 迁移到版本 2 :添加一个primary key + 对象引用
        // 示例:
        // public Person extends RealmObject {
        //     private String name;
        //     @PrimaryKey
        //     private int age;
        //     private Dog favoriteDog;
        //     private RealmList<Dog> dogs;
        //     // getters and setters left out for brevity
        // }

//        if (oldVersion == 1) {
//            schema.get("EnterAuthModel")
//                    //.addField("qrId", long.class, FieldAttribute.PRIMARY_KEY)
//                    .addField("String", String.class, FieldAttribute.REQUIRED);
//            //.addRealmObjectField("favoriteDog", schema.get("Dog"))
//            //.addRealmListField("dogs", schema.get("Dog"));
//            oldVersion++;
//        }
    }
}
