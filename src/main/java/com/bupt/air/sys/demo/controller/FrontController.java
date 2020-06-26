package com.bupt.air.sys.demo.controller;

import com.bupt.air.sys.demo.entity.Check;
import com.bupt.air.sys.demo.entity.Room;
import com.bupt.air.sys.demo.service.CentralAC;
import com.bupt.air.sys.demo.service.FrontService;
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
    @GetMapping(path = "/CheckIn/{roomid}", produces = "application/json")
    public ResponseEntity<Room> PrintAllRoomInfo(@PathVariable int roomid){
        Timestamp present = new Timestamp(System.currentTimeMillis());
        List<Room> rooms = centralAC.getRooms();
        Room room = rooms.get(roomid);
        if(room.isCheckOut()){
            room.CheckIn(present);
            return  new ResponseEntity<Room>(room,HttpStatus.OK);
        }
        else{
            return  new ResponseEntity<Room>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "办理退房", notes = "如果已经退房返回BAD_REQUEST，未退房返回一个Check")
    @PostMapping(path = "/CheckOut/{roomid}",consumes = "application/json", produces = "application/json")
    public ResponseEntity<Check> CheckOut(@PathVariable int roomid){
        List<Room> rooms = centralAC.getRooms();
        Room room = rooms.get(roomid);
        if(room.isCheckOut()){
            return  new ResponseEntity<Check>(HttpStatus.BAD_REQUEST);
        }
        else{
            room.CheckOut();
            Check ch = service.Checkout();
            return  new ResponseEntity<Check>(ch,HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "办理退房", notes = "如果已经退房返回BAD_REQUEST，未退房返回一个Check")
    @PostMapping(path = "/Detailed/{roomid}/{startTime}/{endTime}",consumes = "application/json", produces = "application/json")




}
