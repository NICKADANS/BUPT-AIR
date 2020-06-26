package com.bupt.air.sys.demo.controller;

import com.bupt.air.sys.demo.entity.Record;
import com.bupt.air.sys.demo.entity.RoomRequest;
import com.bupt.air.sys.demo.repository.RecordRepository;
import com.bupt.air.sys.demo.service.CentralAC;
import com.bupt.air.sys.demo.entity.Room;
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
@RequestMapping("/api/user")
@RestController
public class RoomController {
    @Autowired
    RecordRepository recordRepository;
    @Autowired
    CentralAC centralAC;

    @ApiOperation(value = "用户获取指定房间信息")
    @PostMapping(path = "/roominfo", produces = "application/json")
    public Result<?> printRoomInfo(@RequestBody Map<String,String> param){
        int roomid = Integer.parseInt(param.get("roomid"));
        List<Room> rooms = centralAC.getRooms();
        int i = centralAC.findRoom(roomid);
        if(i == -1) {
            return Result.error("房间不存在!");
        }
        return Result.ok(rooms.get(i), "操作成功");
    }

    @ApiOperation(value = "用户指定房间目标温度")
    @PostMapping(path = "/settargetTemp", produces = "application/json")
    public Result<?> setTargetTemp(@RequestBody Map<String,String> param){
        int roomid = Integer.parseInt(param.get("roomid"));
        float target = Float.parseFloat(param.get("target"));
        List<Room> rooms = centralAC.getRooms();
        Queue<RoomRequest> rq = centralAC.getRequest_queue();
        int i = centralAC.findRoom(roomid);
        //如果两次请求间隔小于1s

        //如果目标温度超出范围
        if (target > centralAC.getTemp_high() || target < centralAC.getTemp_low()) {
            return Result.error("房间温度设置错误!");
        }
        //如果房间号错误
        if(i == -1){
            return Result.error("房间id不存在!");
        }
        else {
            Room room = rooms.get(i);
            //如果当前温度和目标温度绝对值差异小于0.5，啥都不做
            if (room.getLocalTemp() > target - 0.5 && room.getLocalTemp() < target + 0.5) {
                return Result.error("房间温度与目标温度接近!");
            }
            //将房间从当前服务队列中移除
            centralAC.removeRoominRequestQueue(roomid);
            //重置房间状态为等待服务
            room.setState("IDLE");
            //重置房间的目标温度、空调自动更新时间、房间等待服务时间
            room.setTargetTemp(target);
            room.setWaitingTime(0);
            //根据处理结果刷新服务器
            centralAC.setRoom(room);
            //将请求记录存入数据库
            Record record = new Record(room, "USER");
            recordRepository.save(record);
            //返回HTTP状态码
            return Result.ok(room, "操作成功!");
        }
    }

    @ApiOperation(value = "用户关闭房间空调")
    @PostMapping(path = "/turnoffair", produces = "application/json")
    public Result<?> turnOffAir(@RequestBody Map<String,String> param){
        int roomid = Integer.parseInt(param.get("roomid"));
        List<Room> rooms = centralAC.getRooms();
        int i = centralAC.findRoom(roomid);
        //如果两次请求间隔小于1s，拒绝服务
//        if(room.getAutoUpdateTime() < 2 && room.getWaitingTime() < 2){
//            return new ResponseEntity<Room>(HttpStatus.FORBIDDEN);
//        }
        //如果房间号错误
        if(i == -1){
            return Result.error("房间id不存在!");
        }
        else {
            Room room = rooms.get(i);
            //将房间从当前服务队列中移除
            centralAC.removeRoominRequestQueue(roomid);
            //重置目标状态模式和等待时间
            room.setState("OFF");
            room.setWaitingTime(0);
            room.setIdleUpdateTime(0);
            room.setAutoUpdateTime(0);
            //根据处理结果刷新服务器
            centralAC.setRoom(room);
            //将请求记录存入数据库
            Record record = new Record(room, "USER");
            recordRepository.save(record);
            return Result.ok(room, "操作成功!");
        }
    }

    @ApiOperation(value = "用户指定房间风速模式")
    @PostMapping(path = "/setwinmode", produces = "application/json")
    public Result<?> setWinmode(@RequestBody Map<String,String> param){
        int roomid = Integer.parseInt(param.get("roomid"));
        String winmode = param.get("winmode");
        List<Room> rooms = centralAC.getRooms();
        int i = centralAC.findRoom(roomid);
        //如果两次请求间隔小于1s，拒绝服务
//        if(room.getAutoUpdateTime() < 2 && room.getWaitingTime() < 2){
//            return new ResponseEntity<Room>(HttpStatus.FORBIDDEN);
//        }
        //如果风速模式错误，拒绝服务
        if (!winmode.equals("HIGH") && !winmode.equals("LOW") && !winmode.equals("MID")) {
            return Result.error("风速模式设置错误!");
        }
        //如果房间号错误
        if(i == -1){
            return Result.error("房间id不存在!");
        }
        else {
            Room room = rooms.get(i);
            //将房间从当前服务队列中移除
            centralAC.removeRoominRequestQueue(roomid);
            //重置目标风速模式和等待时间
            room.setState("IDLE");
            room.setWinmode(winmode);
            room.setAutoUpdateTime(0);
            room.setWaitingTime(0);
            centralAC.setRoom(room);
            //将请求记录存入数据库
            Record record = new Record(room, "USER");
            recordRepository.save(record);
            return Result.ok(room, "操作成功!");
        }
    }
}
