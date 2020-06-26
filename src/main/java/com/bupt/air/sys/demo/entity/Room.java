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

    //自动转换房间空调运行时的状态
    public void transferRunningState(){
        //当空调关闭时，不需要改变状态
        if(this.state.equals("OFF")){
            return;
        }
        //当前温度接近目标温度时，转变状态
        else if(this.localTemp < this.targetTemp + 0.35 && this.localTemp > this.targetTemp - 0.35){
            this.state = "IDLE";
        }
        //目标温度介于18-25度时，模式为制冷
        else if(this.targetTemp >= 18.0 && this.targetTemp <= 25.0){
            this.state = "FREEZE";
        }
        //目标温度介于25-30度时，模式为制热
        else if(this.targetTemp > 25.0 && this.targetTemp <= 30.0){
            this.state = "HEAT";
        }
        //当前温度不在这些范围时，不改动状态
    }

    //处于服务队列中的房间，每当autoUpdateTime到59，自动清零，并更新房间信息
    public void autoUpdateState(){
        this.autoUpdateTime = 0;//重置更新时间
        //空调关机状态下，每分钟变化0.5度，恢复至初始温度附近
        if(this.state.equals("OFF")){
            if(this.localTemp < initTemp){
                this.localTemp += 0.5;
            }
            else if(this.localTemp > initTemp){
                this.localTemp -= 0.5;
            }
        }
        //空调工作状态下，根据风速模式调节室温
        else {
            //中风速，每分钟恒定变化0.5度，费用增加0.5元
            if(this.winmode.equals("MID")){
                this.winrate = (float) 0.5;
                if(this.localTemp < this.targetTemp){
                    this.localTemp += 0.5;
                    transferRunningState();
                }
                else{
                    this.localTemp -= 0.5;
                    transferRunningState();
                }
                this.fee += 0.5;
            }
            //低风速，每分钟恒定变化0.4度，费用增加0.333元
            else if(this.winmode.equals("LOW")){
                this.winrate = (float)0.4;
                if(this.localTemp < this.targetTemp){
                    this.localTemp += 0.4;
                    transferRunningState();
                }
                else{
                    this.localTemp -= 0.4;
                    transferRunningState();
                }
                this.fee += 0.333;
            }
            //高风速，每分钟恒定变化0.6度，费用增加1元
            else if(this.winmode.equals("HIGH")){
                this.winrate = (float)0.6;
                if(this.localTemp < this.targetTemp){
                    this.localTemp += 0.6;
                    transferRunningState();
                }
                else{
                    this.localTemp -= 0.6;
                    transferRunningState();
                }
                this.fee += 1.0;
            }
        }
    }

    //处于等待服务状态(IDLE)下的房间，每当idleUpdateTime到达59，清零并更新房间信息
    public void autoUpdateIdleState(){
        this.idleUpdateTime = 0;
        //自动恢复至初始温度附近
        if(this.localTemp < initTemp){
            this.localTemp += 0.5;
        }
        else if(this.localTemp > initTemp){
            this.localTemp -= 0.5;
        }
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
        isOccupied = false;
        t_checkin = present;
        state = "OFF";
        servingTime = 0;
        fee = 0;
    }

    public void CheckOut(){
        isOccupied = true;
        t_checkin = null;
        state = "OFF";
        servingTime = 0;
        fee = 0;
    }
}
