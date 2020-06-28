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
        System.out.println(roomid);
        System.out.println(inittemp);
        //初始化一个新房间
        Room room = new Room(Integer.parseInt(roomid), Float.parseFloat(inittemp));
        //服务器房间数组新增一个房间
        boolean isAddOk = centralAC.addNewRoom(room);
        if(!isAddOk) {
            //房间id已存在
            return Result.error(400, "房间已存在!");
        }
        else{
            //生成操作记录
            Record record = new Record(room, "MASTER");
            recordRepository.save(record);
        }
        return Result.ok(room, "操作成功");
    }

    @ApiOperation(value = "管理员初始化请求队列")
    @PostMapping(path = "/init/requestqueue", produces = "application/json")
    public Result<?> initQueue(@RequestBody Map<String,String> param){
        int size = Integer.parseInt(param.get("size"));              //队列的大小
        int timeslice = Integer.parseInt(param.get("slice"));        //队列的时间片
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
    public Result<?> printAllRoomInfo(){
        List<Room> rooms = centralAC.getRooms();
        class Res{
            public int idling;
            public int serving;
            public List<Room> rooms;
        }
        int idlenum = 0;
        int servingnum = 0;
        Res res = new Res();
        res.rooms = rooms;
        for(int i = 0; i<rooms.size(); i++){
            if(rooms.get(i).getState().equals("IDLE")){
                idlenum++;
            }
            else if(rooms.get(i).getState().equals("HEAT") || rooms.get(i).getState().equals("FREEZE")){
                servingnum++;
            }
        }
        res.idling = idlenum;
        res.serving = servingnum;
        return Result.ok(res, "操作成功");
    }

    @ApiOperation(value = "管理员获取指定房间信息")
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

    @ApiOperation(value = "管理员指定房间目标温度")
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
            //当前空调处于制热/制冷状态
            if(room.getState().equals("HEAT") || room.getState().equals("FREEZE")) {
                Record record = new Record(room, "SYS-STOP");
                recordRepository.save(record);
            }
            //重置房间状态为等待服务
            room.setState("IDLE");
            //重置房间的目标温度、空调自动更新时间、房间等待服务时间
            room.setTargetTemp(target);
            room.setWaitingTime(0);
            //根据处理结果刷新服务器
            centralAC.setRoom(room);
            //将请求记录存入数据库
            Record record = new Record(room, "MASTER-TEMP");
            recordRepository.save(record);
            //返回HTTP状态码
            return Result.ok(room, "操作成功!");
        }
    }

    @ApiOperation(value = "管理员开启房间空调")
    @PostMapping(path = "/turnonair", produces = "application/json")
    public Result<?> turnOnAir(@RequestBody Map<String,String> param){
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
            room.setState("IDLE");
            room.setWaitingTime(0);
            room.setIdleUpdateTime(0);
            room.setAutoUpdateTime(0);
            //根据处理结果刷新服务器
            centralAC.setRoom(room);
            //将请求记录存入数据库
            Record record = new Record(room, "MASTER-ON");
            recordRepository.save(record);
            return Result.ok(room, "操作成功!");
        }
    }

    @ApiOperation(value = "管理员关闭房间空调")
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
            //当前空调处于制热/制冷状态
            if(room.getState().equals("HEAT") || room.getState().equals("FREEZE")) {
                Record record = new Record(room, "SYS-STOP");
                recordRepository.save(record);
            }
            //重置目标状态模式和等待时间
            room.setState("OFF");
            room.setWaitingTime(0);
            room.setIdleUpdateTime(0);
            room.setAutoUpdateTime(0);
            //根据处理结果刷新服务器
            centralAC.setRoom(room);
            //将请求记录存入数据库
            Record record = new Record(room, "MASTER-OFF");
            recordRepository.save(record);
            return Result.ok(room, "操作成功!");
        }
    }

    @ApiOperation(value = "管理员指定房间风速模式")
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
            //当前空调处于制热/制冷状态
            if(room.getState().equals("HEAT") || room.getState().equals("FREEZE")) {
                Record record = new Record(room, "SYS-STOP");
                recordRepository.save(record);
            }
            //重置目标风速模式和等待时间
            room.setState("IDLE");
            room.setWinmode(winmode);
            room.setAutoUpdateTime(0);
            room.setWaitingTime(0);
            centralAC.setRoom(room);
            //将请求记录存入数据库
            Record record = new Record(room, "MASTER-WIN");
            recordRepository.save(record);
            return Result.ok(room, "操作成功!");
        }
    }

    @ApiOperation(value = "管理员指定中央空调参数")
    @PostMapping(path = "/setairparam", produces = "application/json")
    public Result<?> setAirParam(@RequestBody Map<String,String> param){
        String lower = param.get("lower");      //最低温度
        String deafulttemp = param.get("defaulttemp");
        String higher = param.get("higher");    //最高温度
        String state = param.get("state");      //中央空调开关
        centralAC.setTemp_default(Float.parseFloat(deafulttemp));
        centralAC.setTemp_low(Float.parseFloat(lower));
        centralAC.setTemp_low(Float.parseFloat(higher));
        centralAC.setState(state);
        return Result.ok("操作成功");
    }
}
