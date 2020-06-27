package com.bupt.air.sys.demo.entity;

import com.bupt.air.sys.demo.service.CentralAC;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;


@Data
@ApiModel(value = "Room", description = "房间类")
public class Room {
    private int roomid;                 //房间号
    private boolean isOccupied;         //房间是否空闲
    private Timestamp t_checkin;        //入住时间
    private float initTemp;             //初始温度
    private float localTemp;            //当前温度
    private float targetTemp;           //目标温度
    private String winmode;             //风速模式，取值 {"LOW","MID","HIGH"}
    private float winrate;              //风速大小
    private String state;               //运行状态，取值 {"OFF","FREEZE","HEAT","IDLE"}
    private float fee;                  //空调费用
    private int servingTime;            //服务时间
    private int autoUpdateTime;         //空调服务中的自动更新计时器
    private int waitingTime;            //等待服务时间
    private int idleUpdateTime;         //等待服务中的自动更新计时器

    public Room(){

    }

    //创建房间对象，提供房间id和房间温度
    public Room(int roomid, float initTemp){
        this.roomid = roomid;
        this.isOccupied = false;
        this.initTemp = initTemp;
        this.localTemp = initTemp;  //当前温度默认为初始温度
        this.targetTemp = initTemp; //目标温度默认为初始温度
        this.winmode = "MID";
        this.winrate = (float)0.5;
        this.state = "OFF";
        this.fee = (float) 0.00;
        this.servingTime = 0;
        this.waitingTime = 0;
        this.autoUpdateTime = 0;
        this.idleUpdateTime = 0;
    }

    public int getRoomid() {
        return roomid;
    }

    public float getLocalTemp() {
        return localTemp;
    }

    public float getFee() {
        return fee;
    }

    public float getTargetTemp() {
        return targetTemp;
    }

    public float getWinrate() {
        return winrate;
    }

    public int getServingTime() {
        return servingTime;
    }

    public int getAutoUpdateTime() {
        return autoUpdateTime;
    }

    public String getState() {
        return state;
    }

    public String getWinmode() {
        return winmode;
    }

    public boolean getOccupied(){
        return isOccupied;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public Timestamp getT_checkin() {
        return t_checkin;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setT_checkin(Timestamp t_checkin) {
        this.t_checkin = t_checkin;
    }

    public void setTargetTemp(float targetTemp) {
        this.targetTemp = targetTemp;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public void setLocalTemp(float localTemp) {
        this.localTemp = localTemp;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setServingTime(int servingTime) {
        this.servingTime = servingTime;
    }

    public void setWinmode(String winmode) {
        this.winmode = winmode;
        if(winmode.equals("MID")){
            this.winrate = (float)0.5;
        }
        else if(winmode.equals("HIGH")){
            this.winrate = (float)0.6;
        }
        else{
            this.winrate = (float)0.4;
        }
    }

    public void setWinrate(float winrate) {
        this.winrate = winrate;
    }

    public void setAutoUpdateTime(int autoUpdateTime) {
        this.autoUpdateTime = autoUpdateTime;
    }

    public int getIdleUpdateTime() {
        return idleUpdateTime;
    }

    public void setIdleUpdateTime(int idleUpdateTime) {
        this.idleUpdateTime = idleUpdateTime;
    }

    public float getInitTemp() {
        return initTemp;
    }

    public void setInitTemp(float initTemp) {
        this.initTemp = initTemp;
    }

    public void CheckIn(Timestamp present){
        isOccupied = true;
        t_checkin = present;
        state = "OFF";
        servingTime = 0;
        fee = 0;
    }

    public void CheckOut(){
        isOccupied = false;
        t_checkin = null;
        state = "OFF";
        servingTime = 0;
        fee = 0;
    }
}
