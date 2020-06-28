package com.bupt.air.sys.demo.entity;

public class RoomReport {
    public int roomid;             //房间号
    public int serving;            //使用时长
    public float totalfee;         //总费用
    public int scheduleCount;      //被调度次数
    public int settempCount;       //调风次数
    public int setwinCount;        //调温次数
    public int switchCount;        //开关次数

    public RoomReport(){}

    public RoomReport(int roomid){
        this.roomid = roomid;
        this.serving = 0;
        this.totalfee = 0;
        this.scheduleCount = 0;
        this.settempCount = 0;
        this.setwinCount = 0;
        this.switchCount = 0;
    }

}
