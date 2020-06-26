package com.bupt.air.sys.demo.service;

import com.bupt.air.sys.demo.entity.Room;
import com.bupt.air.sys.demo.entity.RoomRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class CentralAC {
    String state;                           //中央空调状态：OFF、ON
    float temp_low;                         //房间最低温度
    float temp_high;                        //房间最高温度
    float temp_default;                     //默认室温
    Queue<RoomRequest> request_queue;       //服务队列
    int request_queue_size;                 //服务队列的大小，默认为5
    int request_waittime;                   //服务的时间片，默认为10s
    float request_min_winrate;              //当前服务中的最小风速
    private List<Room> rooms;               //当前所有房间信息


    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setRequest_queue(Queue<RoomRequest> request_queue) {
        this.request_queue = request_queue;
    }

    public void setRequest_queue_size(int request_queue_size) {
        this.request_queue_size = request_queue_size;
    }

    public void setRequest_waittime(int request_waittime) {
        this.request_waittime = request_waittime;
    }

    public void setTemp_default(float temp_default) {
        this.temp_default = temp_default;
    }

    public void setTemp_high(float temp_high) {
        this.temp_high = temp_high;
    }

    public void setTemp_low(float temp_low) {
        this.temp_low = temp_low;
    }

    public String getState() {
        return state;
    }

    public float getTemp_default() {
        return temp_default;
    }

    public float getTemp_high() {
        return temp_high;
    }

    public float getTemp_low() {
        return temp_low;
    }

    public int getRequest_queue_size() {
        return request_queue_size;
    }

    public int getRequest_waittime() {
        return request_waittime;
    }

    public Queue<RoomRequest> getRequest_queue() {
        return request_queue;
    }

    public boolean offerRequest_queue(RoomRequest r){
        return request_queue.offer(r);
    }

    public RoomRequest pollRequest_queue(){
        return request_queue.poll();
    }

    public RoomRequest elementRequest_queue(){
        return request_queue.element();
    }

    public float getRequest_min_winrate() {
        return request_min_winrate;
    }

    public void setRequest_min_winrate(float request_min_winrate) {
        this.request_min_winrate = request_min_winrate;
    }

    //新增一个房间
    public boolean addNewRoom(Room r) {
        //判断是否房间号冲突
        int i = findRoom(r.getRoomid());
        //已经存在该房间
        if(i != -1){
            return false;
        }
        rooms.add(r);
        return true;
    }

    public boolean setRoom(Room r){
        //判断是否房间号冲突
        int i = findRoom(r.getRoomid());
        if(i != -1){
            rooms.set(i, r);
            return true;
        }
        return false;
    }

    public int findRoom(int roomid){
        for(int i = 0; i<rooms.size(); i++){
            if(rooms.get(i).getRoomid() == roomid){
                return i;
            }
        }
        return -1;
    }

    //为服务队列中每个房间的服务时间加1秒
    public void updateRequestQueueServingTime(){
        for(RoomRequest rr : request_queue){
            rr.setServingTime(rr.getServingTime() + 1);
        }
    }

    //删除服务队列中指定的房间请求，并且更新当前最小风速
    public void removeRoominRequestQueue(int roomid){
        //新建一个新队列
        Queue<RoomRequest> newrq = new PriorityBlockingQueue<RoomRequest>(this.request_queue_size, new Comparator<RoomRequest>(){
            public int compare(RoomRequest a, RoomRequest b){
                return a.getServingTime() - b.getServingTime() > 0 ? -1 : 1;
            }
        });
        //设置新队列的最小风速
        float min_winrate = 1;
        //把老队列出队的元素放入新队列中，并不断更新最小风速
        while(!this.request_queue.isEmpty()){
            RoomRequest rr = this.request_queue.poll();
            if(rr.getRoomid() != roomid){
                min_winrate = min_winrate < rr.getWinrate() ? min_winrate : rr.getWinrate();
                newrq.offer(rr);
            }
        }
        this.request_queue = newrq;
        this.request_min_winrate = min_winrate;
    }

    //初始化服务器
    public CentralAC(){
        rooms = new ArrayList<Room>();
        temp_default = (float)25.0;
        temp_high = (float)30.0;
        temp_low = (float)18.0;
        request_queue_size = 5;
        request_waittime = 10;
        request_queue = new PriorityBlockingQueue<RoomRequest>(10000, new Comparator<RoomRequest>(){
            public int compare(RoomRequest a, RoomRequest b){
                return a.getServingTime() - b.getServingTime() > 0 ? -1 : 1;
            }
        });
        request_min_winrate = 1;
    }

}
