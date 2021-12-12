package com.netwokz.mystaticip;

import com.orm.SugarRecord;

public class StaticIpRecord extends SugarRecord {

    String mIpAddress;
    String mMacAddress;
    int mType;
    String mName;

    public StaticIpRecord() {

    }

    public StaticIpRecord(StaticIpRecord record) {

        this.mIpAddress = record.mIpAddress;
        this.mMacAddress = record.mMacAddress;
        this.mType = record.mType;
        this.mName = record.mName;

    }

    public StaticIpRecord(String ip, String mac, int type, String name) {

        this.mIpAddress = ip;
        this.mMacAddress = mac;
        this.mType = type;
        this.mName = name;
    }

    public String getIpAddress() {
        return mIpAddress;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public int getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }
}
