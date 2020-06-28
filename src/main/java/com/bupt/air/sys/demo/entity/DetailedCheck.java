package com.bupt.air.sys.demo.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel(value = "DetailCheck", description = "详单示例")
public class DetailedCheck {
    private int roomid;
    private Timestamp startTime;
    private Timestamp endTime;
    boolean isPrinted;
    private float Fee;
    private List<DetailCheckRecord> records;

    public float getFee() {
        return Fee;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public int getRoomid() {
        return roomid;
    }

    public List<DetailCheckRecord> getRecords() {
        return records;
    }

    public void setFee(float fee) {
        Fee = fee;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public void setPrinted(boolean printed) {
        isPrinted = printed;
    }

    public void setRecords(List<DetailCheckRecord> records) {
        this.records = records;
    }
}
