package com.bupt.air.sys.demo.entity;

import java.sql.Timestamp;

public class DetailCheckRecord {
    private Timestamp startSend;    //开始送风时间
    private Timestamp endSend;      //结束送风时间
    private int timeSend;           //送风时长
    private String winmode;         //风速
    private float fee;              //费用

    public DetailCheckRecord(Timestamp start, Timestamp end, int timeSend, String winmode, float fee){
        this.startSend = start;
        this.endSend = end;
        this.timeSend = timeSend;
        this.winmode = winmode;
        this.fee = fee;
    }

    public DetailCheckRecord(){}

    public float getFee() {
        return fee;
    }

    public String getWinmode() {
        return winmode;
    }

    public int getTimeSend() {
        return timeSend;
    }

    public Timestamp getEndSend() {
        return endSend;
    }

    public Timestamp getStartSend() {
        return startSend;
    }

    public void setWinmode(String winmode) {
        this.winmode = winmode;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public void setEndSend(Timestamp endSend) {
        this.endSend = endSend;
    }

    public void setStartSend(Timestamp startSend) {
        this.startSend = startSend;
    }

    public void setTimeSend(int timeSend) {
        this.timeSend = timeSend;
    }
}
