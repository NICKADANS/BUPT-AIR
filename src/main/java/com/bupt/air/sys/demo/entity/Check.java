package com.bupt.air.sys.demo.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@ApiModel(value = "Check", description = "账单示例")
public class Check {
    private int roomid;
    private Timestamp startTime;
    private Timestamp endTime;
    boolean isPrinted;
    private float Fee;

    public void setPrinted(boolean printed) {
        isPrinted = printed;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void setFee(float fee) {
        Fee = fee;
    }
}
