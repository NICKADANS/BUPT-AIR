package com.bupt.air.sys.demo.entity;

import java.io.Serializable;
import java.sql.Timestamp;

//操作记录Record的主键
public class RecordPrimaryKey implements Serializable {
    private int roomid;                 //房间号
    private Timestamp opttime;          //操作时刻
}
