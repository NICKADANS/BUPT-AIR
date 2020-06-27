package com.bupt.air.sys.demo.controller;

import com.bupt.air.sys.demo.entity.Check;
import com.bupt.air.sys.demo.entity.DetailedCheck;
import com.bupt.air.sys.demo.entity.Room;
import com.bupt.air.sys.demo.service.CentralAC;
import com.bupt.air.sys.demo.service.FrontService;
import com.bupt.air.sys.demo.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Api(value = "Front Controller")
@RequestMapping("/api/front")
@RestController
public class FrontController {

    @Autowired
    CentralAC centralAC;

    @Autowired
    private FrontService service;

    @ApiOperation(value = "办理入住", notes = "检验房间是否已经办理退房，退房了初始化房间并返回房间信息，否则返回BAD_REQUEST")
    @GetMapping(path = "/checkin/{roomid}", produces = "application/json")
    public Result<?> PrintAllRoomInfo(@PathVariable int roomid){
        Timestamp present = new Timestamp(System.currentTimeMillis());
        List<Room> rooms = centralAC.getRooms();
        int i = centralAC.findRoom(roomid);
        if(i == -1){
            return Result.error("房间id不存在!");
        }
        Room room = rooms.get(i);
        if(!room.getOccupied()){
            room.CheckIn(present);
            if(centralAC.setRoom(room)){
                return Result.ok(room,"操作成功");
            }
        }
        return Result.error("房间被占用!");
    }

    @ApiOperation(value = "办理退房", notes = "如果已经退房返回BAD_REQUEST，未退房返回一个Check")
    @PostMapping(path = "/checkout/{roomid}",consumes = "application/json", produces = "application/json")
    public Result<?> CheckOut(@PathVariable int roomid){
        Timestamp present = new Timestamp(System.currentTimeMillis());
        List<Room> rooms = centralAC.getRooms();
        int i = centralAC.findRoom(roomid);
        if(i == -1){
            return Result.error("房间id不存在!");
        }
        Room room = rooms.get(i);
        if(room.getOccupied()){
            Check ch = service.Checkout(roomid, present);
            room.CheckOut();
            if(centralAC.setRoom(room)){
                return Result.ok(ch,"操作成功");
            }
        }
        return Result.error("房间当前是空闲的!");
    }

    @ApiOperation(value = "办理退房", notes = "如果已经退房返回BAD_REQUEST，未退房返回一个Check")
    @PostMapping(path = "/detailed/{roomid}/{startTime}/{endTime}",consumes = "application/json", produces = "application/json")
    public Result<?> CheckOut(@PathVariable int roomid, @PathVariable Timestamp startTime,@PathVariable Timestamp endTime){
        DetailedCheck dch = service.detailedRequest(roomid,startTime,endTime);
        return  Result.ok(dch);

    }



}
