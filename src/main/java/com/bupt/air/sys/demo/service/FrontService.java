package com.bupt.air.sys.demo.service;

import com.bupt.air.sys.demo.entity.Check;
import com.bupt.air.sys.demo.entity.DetailedCheck;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class FrontService {
    public Check Checkout(int roomid){
        Check ch = new Check();
        return ch;
    }

    public DetailedCheck detailedRequest(int roomid,Timestamp startTime,Timestamp endTime){
        DetailedCheck dch = new DetailedCheck();
        dch.setEndTime(endTime);
        dch.setPrinted(true);

        return dch;

    }



}
