package com.bupt.air.sys.demo.entity;

import io.swagger.annotations.ApiModel;

import java.sql.Timestamp;
import java.util.List;

@ApiModel(value = "Report", description = "格式化报表")
public class Report {
    private Timestamp startTime;            //时间起点
    private Timestamp endTime;              //时间终点
    private float totalFee;                 //总共计费
    private List<RoomReport> roomReports;   //各房间报表

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public List<RoomReport> getRoomReports() {
        return roomReports;
    }

    public void setTotalFee(float totalFee) {
        this.totalFee = totalFee;
    }

    public float getTotalFee() {
        return totalFee;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setRoomReports(List<RoomReport> roomReports) {
        this.roomReports = roomReports;
    }
}
