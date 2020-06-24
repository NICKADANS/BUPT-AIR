package com.bupt.air.sys.demo.entity;

import java.sql.Timestamp;

//房间请求类，用于CentralAC的请求队列request_queue
public class RoomRequest {
    private int roomid;                 //房间号
    private String winmode;             //风速模式，取值 {"LOW","MID","HIGH"}
    private float winrate;              //风俗大小
    private int servingTime;            //服务时间
    private int waitingTime;            //等待服务时间

    public RoomRequest(){

    }

    public RoomRequest(int roomid, String winmode, float winrate, int servingTime, int waitingTime){
        this.roomid = roomid;
        this.winmode = winmode;
        this.winrate = winrate;
        this.servingTime = servingTime;
        this.waitingTime = waitingTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getServingTime() {
        return servingTime;
    }

    public String getWinmode() {
        return winmode;
    }

    public int getRoomid() {
        return roomid;
    }

    public float getWinrate() {
        return winrate;
    }

    public void setWinrate(float winrate) {
        this.winrate = winrate;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setServingTime(int servingTime) {
        this.servingTime = servingTime;
    }

    public void setWinmode(String winmode) {
        this.winmode = winmode;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }
}
