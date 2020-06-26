package com.bupt.air.sys.demo.controller;
import com.bupt.air.sys.demo.entity.Record;
import com.bupt.air.sys.demo.entity.Room;
import com.bupt.air.sys.demo.entity.RoomRequest;
import com.bupt.air.sys.demo.repository.RecordRepository;
import com.bupt.air.sys.demo.service.CentralAC;
import com.bupt.air.sys.demo.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

@Api(value = "User Controller")
@RequestMapping("/api/master")
@RestController
public class MasterController {
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    CentralAC centralAC;

    @ApiOperation(value = "管理员向服务器初始化房间信息")
    @PostMapping(path = "/add/room", produces = "application/json")
    public Result<?> addNewRoom(@RequestBody Map<String,String> param){
        String roomid = param.get("roomid");
        String inittemp = param.get("inittemp");
        //初始化一个新房间
        Room room = new Room(Integer.valueOf(roomid), Float.valueOf(inittemp));
        //生成操作记录
        Record record = new Record(room, "MASTER");
        recordRepository.save(record);
        //服务器房间数组新增一个房间
        boolean isAddOk = centralAC.addNewRoom(room);
        if(!isAddOk) {
            return Result.error(400, "房间已存在!");
        }
        return Result.ok(room, "操作成功");
    }

    @ApiOperation(value = "管理员初始化请求队列")
    @PostMapping(path = "/init/requestqueue", produces = "application/json")
    public Result<?> InitQueue(@RequestBody Map<String,String> param){
        int size = Integer.valueOf(param.get("size"));              //队列的大小
        int timeslice = Integer.valueOf(param.get("slice"));        //队列的时间片
        List<Room> rooms = centralAC.getRooms();
        //清空队列里正在服务的房间请求
        for(RoomRequest r : centralAC.getRequest_queue()){
            rooms.get(r.getRoomid()).setState("IDLE");
            rooms.get(r.getRoomid()).setWaitingTime(0);
            rooms.get(r.getRoomid()).setAutoUpdateTime(0);
        }
        centralAC.setRooms(rooms);
        //初始化请求队列
        Queue<RoomRequest> rq = new PriorityBlockingQueue<RoomRequest>(size, new Comparator<RoomRequest>(){
            public int compare(RoomRequest a, RoomRequest b){
                return a.getServingTime() - b.getServingTime() > 0 ? -1 : 1;
            }
        });
        //更新服务器的队列信息
        centralAC.setRequest_queue(rq);
        centralAC.setRequest_queue_size(size);
        centralAC.setRequest_waittime(timeslice);
        return Result.ok(rq,"操作成功");
    }

    @ApiOperation(value = "管理员获取所有房间信息")
    @GetMapping(path = "/allroominfo", produces = "application/json")
    public Result<?> PrintAllRoomInfo(){
        List<Room> rooms = centralAC.getRooms();
        return Result.ok(rooms, "操作成功");
    }

    @ApiOperation(value = "管理员获取指定房间信息")
    @GetMapping(path = "/roominfo", produces = "application/json")
    public Result<?> PrintRoomInfo(@RequestParam("roomid") int roomid){
        List<Room> rooms = centralAC.getRooms();
        if(rooms.get(roomid) == null) {
            return Result.error("房间不存在!");
        }
        return Result.ok(rooms.get(roomid), "操作成功");
    }
}
