package com.bupt.air.sys.demo.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@ApiModel(value = "Room-Record", description = "房间记录示例")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long recordId;              //记录ID
    private int roomid;                 //房间号
    private Timestamp opttime;          //操作时刻
    private boolean isOccupied;         //房间是否空闲
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
    private String opter;               //操作者，取值 {"MASTER","USER"}

    public Record(){

    }

    //记录的构造函数
    public Record(Room r, String opter){
        this.roomid = r.getRoomid();
        this.opttime= new Timestamp(System.currentTimeMillis());
        this.isOccupied = r.getOccupied();
        this.initTemp = r.getInitTemp();
        this.localTemp = r.getLocalTemp();
        this.targetTemp = r.getTargetTemp();
        this.winmode = r.getWinmode();
        this.winrate = r.getWinrate();
        this.state = r.getState();
        this.fee = r.getFee();
        this.servingTime = r.getServingTime();
        this.autoUpdateTime = r.getAutoUpdateTime();
        this.waitingTime = r.getWaitingTime();
        this.idleUpdateTime = r.getIdleUpdateTime();
        this.opter = opter;
    }

    public float getFee() {
        return fee;
    }

    public String getOpter() {
        return opter;
    }

    public String getState() {
        return state;
    }

    public Timestamp getOpttime() {
        return opttime;
    }

    public String getWinmode() {
        return winmode;
    }

    public int getServingTime() {
        return servingTime;
    }
}
