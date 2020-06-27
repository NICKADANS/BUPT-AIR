package com.bupt.air.sys.demo.service;

import com.bupt.air.sys.demo.entity.Check;
import com.bupt.air.sys.demo.entity.DetailedCheck;
import com.bupt.air.sys.demo.entity.Record;
import com.bupt.air.sys.demo.entity.Room;
import com.bupt.air.sys.demo.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
        ch.setEndTime(startTime);
        ch.setStartTime(endTime);
        ch.setPrinted(true);
        ch.setFee(room.getFee());
        Record record = new Record(room,"FRONT");
        recordRepository.save(record);
        return ch;
    }

    public DetailedCheck detailedRequest(int roomid,Timestamp startTime,Timestamp endTime){
        DetailedCheck dch = new DetailedCheck();
        dch.setRoomid(roomid);
        dch.setEndTime(endTime);
        dch.setStartTime(startTime);
        dch.setPrinted(true);
        dch.setRecords(recordRepository.findByOpttimeBetween(startTime,endTime));
        int size = dch.getRecords().size();
        dch.setFee(dch.getRecords().get(size).getFee());
        return dch;

    }



}
