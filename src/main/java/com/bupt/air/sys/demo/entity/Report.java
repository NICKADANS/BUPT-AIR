package com.bupt.air.sys.demo.entity;

import io.swagger.annotations.ApiModel;

import java.sql.Timestamp;

@ApiModel(value = "Report", description = "格式化报表")
public class Report {
    private Timestamp startTime;//时间起点
    private Timestamp endTime;  //时间终点
    private float totalFee;     //总共计费
    private int maxServingTime; //最长服务时间
    private int minServingTime; //最短服务时间

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void setMaxServingTime(int maxServingTime) {
        this.maxServingTime = maxServingTime;
    }

    public void setMinServingTime(int minServingTime) {
        this.minServingTime = minServingTime;
    }

    public void setTotalFee(float totalFee) {
        this.totalFee = totalFee;
    }

    public int getMaxServingTime() {
        return maxServingTime;
    }

    public int getMinServingTime() {
        return minServingTime;
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

}
