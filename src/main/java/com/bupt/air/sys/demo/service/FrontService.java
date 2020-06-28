package com.bupt.air.sys.demo.service;

import com.bupt.air.sys.demo.entity.*;
import com.bupt.air.sys.demo.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class FrontService {
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    CentralAC centralAC;

    public Check Checkout(int roomid,Timestamp endTime){
        List<Room> rooms = centralAC.getRooms();
        int i = centralAC.findRoom(roomid);
        Check ch = new Check();
        Room room = rooms.get(i);
        ch.setRoomid(roomid);
        Timestamp startTime = room.getT_checkin();
        ch.setEndTime(endTime);
        ch.setStartTime(startTime);
        ch.setPrinted(true);
        ch.setFee(room.getFee());
        Record record = new Record(room,"FRONT");
        recordRepository.save(record);
        return ch;
    }

    public DetailedCheck detailCheckOut(int roomid, Timestamp endTime){
        List<Room> rooms = centralAC.getRooms();
        int i = centralAC.findRoom(roomid);
        DetailedCheck dch = new DetailedCheck();
        Room room = rooms.get(i);
        dch.setRoomid(roomid);
        Timestamp startTime = room.getT_checkin();
        dch.setStartTime(startTime);
        dch.setEndTime(endTime);
        dch.setPrinted(true);
        List<Record> records = recordRepository.findByRoomidAndOpttimeBetween(roomid, startTime, endTime);
        Timestamp startt = null;
        Timestamp endt = null;
        float feestart = 0;
        float feeend = 0;
        int startServ = 0;
        int endServ = 0;
        String winmode = "MID";
        boolean haveSend = false; //上一个记录处于送风状态下
        List<DetailCheckRecord> rd = new ArrayList<DetailCheckRecord>();
        System.out.println(records.size());
        for(int j = 0; j <records.size();j++){
            Record r = records.get(j);
            System.out.println(r);
            if(›r.getOpter().equals("SYS-SEND")){
                startt = r.getOpttime();
                feestart = r.getFee();
                winmode = r.getWinmode();
                startServ = r.getServingTime();
                haveSend = true;
            }
            else if(r.getOpter().equals("SYS-STOP")){
                endt = r.getOpttime();
                feeend = r.getFee();
                endServ = r.getServingTime();
                if(haveSend == true){
                    DetailCheckRecord d = new DetailCheckRecord(startt, endt, endServ -startServ, winmode, feeend-feestart);
                    rd.add(d);
                    haveSend = false;
                }
            }
        }
        dch.setRecords(rd);
        dch.setFee(room.getFee());
        Record record = new Record(room,"FRONT");
        recordRepository.save(record);
        return dch;
    }



}
