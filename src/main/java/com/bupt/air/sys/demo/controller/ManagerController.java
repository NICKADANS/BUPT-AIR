package com.bupt.air.sys.demo.controller;

import com.bupt.air.sys.demo.entity.Record;
import com.bupt.air.sys.demo.entity.Report;
import com.bupt.air.sys.demo.entity.Room;
import com.bupt.air.sys.demo.entity.RoomReport;
import com.bupt.air.sys.demo.repository.RecordRepository;
import com.bupt.air.sys.demo.service.ReportService;
import com.bupt.air.sys.demo.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "Manager Controller")
@RequestMapping("/api/manager")
@RestController
public class ManagerController {
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    ReportService reportService;

    @ApiOperation(value = "经理打印格式化报表")
    @PostMapping(path = "/getreport", produces = "application/json")
    public Result<?> getRoomReport(@RequestBody Map<String,String> param){
        Timestamp startTime = Timestamp.valueOf(param.get("start"));
        Timestamp endTime = Timestamp.valueOf(param.get("end"));
        List<Record> records = recordRepository.findByOpttimeBetween(startTime, endTime);
        if(records.size() == 0){
            return Result.ok("操作成功，但目前尚没有任何记录");
        }
        float totalPrice = (float) 0.0;
        List<RoomReport> rp = new ArrayList<RoomReport>();

        for(int i = 0;i<records.size();i++){
            Record r = records.get(i);
            System.out.println(records.get(i));
            int j = reportService.findRoomReport(rp, r.getRoomid());
            if(j == -1){
                rp.add(new RoomReport(r.getRoomid()));
                i--;
                continue;
            }
            else{
                //计算费用
                if(r.getOpter().equals("FRONT")) {
                    rp.get(j).totalfee += r.getFee();
                    rp.get(j).serving += r.getServingTime();
                }
                //计算调风
                else if(r.getOpter().equals("USER-WIN") || r.getOpter().equals("MASTER-WIN")){
                    rp.get(j).setwinCount++;
                }
                //计算调温
                else if(r.getOpter().equals("USER-TEMP") || r.getOpter().equals("MASTER-TEMP")){
                    rp.get(j).settempCount++;
                }
                //计算调度
                else if(r.getOpter().equals("SYS-SEND")){
                    rp.get(j).scheduleCount++;
                }
                //计算开关次数
                else if(r.getOpter().equals("MASTER-ON") || r.getOpter().equals("USER-ON")){
                    rp.get(j).switchCount++;
                }
            }
        }
        //生成报表
        Report r = new Report();
        r.setStartTime(startTime);
        r.setEndTime(endTime);
        r.setRoomReports(rp);
        r.setTotalFee(totalPrice);
        return Result.ok(r, "操作成功");
    }


}
