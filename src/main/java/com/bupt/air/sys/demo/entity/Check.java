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
}
