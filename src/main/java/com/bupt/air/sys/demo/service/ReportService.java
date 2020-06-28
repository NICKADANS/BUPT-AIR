package com.bupt.air.sys.demo.service;

import com.bupt.air.sys.demo.entity.RoomReport;
import com.bupt.air.sys.demo.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportService {
    @Autowired
    CentralAC centralAC;
    @Autowired
    RecordRepository recordRepository;

    public int findRoomReport(List<RoomReport> rp, int roomid){
        for(int i = 0; i<rp.size(); i++){
            if(rp.get(i).roomid == roomid){
                return i;
            }
        }
        return -1;
    }



}
