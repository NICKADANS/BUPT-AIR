package com.bupt.air.sys.demo.repository;

import com.bupt.air.sys.demo.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByRoomid(int roomid);
    List<Record> findByOpttimeBetween(Timestamp startTime,Timestamp endTime);
    List<Record> findByOpterAndOpttimeBetween(String opter, Timestamp starttime, Timestamp endtime);

}
