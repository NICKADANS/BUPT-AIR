package com.bupt.air.sys.demo.controller;

import com.bupt.air.sys.demo.entity.Record;
import com.bupt.air.sys.demo.entity.Report;
import com.bupt.air.sys.demo.entity.Room;
import com.bupt.air.sys.demo.repository.RecordRepository;
import com.bupt.air.sys.demo.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Api(value = "Manager Controller")
@RequestMapping("/api/manager")
@RestController
public class ManagerController {
    @Autowired
    RecordRepository recordRepository;

    @ApiOperation(value = "经理打印格式化报表")
    @PostMapping(path = "/getreport", produces = "application/json")
    public Result<?> getRoomReport(@RequestBody Map<String,String> param){
        Timestamp startTime = Timestamp.valueOf(param.get("start"));
        Timestamp endTime = Timestamp.valueOf(param.get("end"));
        List<Record> records = recordRepository.findByOpterAndOpttimeBetween("FRONT", startTime, endTime);
        if(records.size() == 0){
            return Result.ok("操作成功，但目前尚没有任何记录");
        }
        float totalPrice = (float) 0.0;
        int maxServing = -1;
        int minServing = 9999999;
        for(int i = 0;i<records.size();i++){
            int tempServing = records.get(i).getServingTime();
            maxServing = tempServing > maxServing ? tempServing : maxServing;
            minServing = tempServing < minServing ? tempServing : minServing;
            totalPrice += records.get(i).getFee();
        }
        //生成报表
        Report r = new Report();
        r.setStartTime(startTime);
        r.setEndTime(endTime);
        r.setMaxServingTime(maxServing);
        r.setMinServingTime(minServing);
        r.setTotalFee(totalPrice);
        return Result.ok(r, "操作成功");
    }


}
