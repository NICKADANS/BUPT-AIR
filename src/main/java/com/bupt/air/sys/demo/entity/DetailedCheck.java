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
    private List<Record> records;
}
