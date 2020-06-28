package com.bupt.air.sys.demo;

import com.bupt.air.sys.demo.entity.Record;
import com.bupt.air.sys.demo.entity.RoomRequest;
import com.bupt.air.sys.demo.repository.RecordRepository;
import com.bupt.air.sys.demo.service.CentralAC;
import com.bupt.air.sys.demo.entity.Room;
import com.bupt.air.sys.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class AutoTimer {
    @Autowired
    CentralAC centralAC;//自动装配Service Bean HotelServer
    @Autowired
    RoomService roomService;
    @Autowired
    RecordRepository recordRepository;

    //每隔一秒，自动更新所有房间的waitingTime与servingTime
    @Scheduled(fixedRate = 1000)
    public void UpdateWaitingTime(){
        List<Room> rooms = centralAC.getRooms();
        float min_winrate = centralAC.getRequest_min_winrate();
        int timeslice = centralAC.getRequest_waittime();
        int queue_max_size = centralAC.getRequest_queue_size();
        //System.out.println(centralAC.getTemp_high() + ", " + centralAC.getTemp_low());
        //System.out.println(min_winrate + ", " + timeslice + ", " + queue_max_size);
        //调试房间信息
        if(rooms.size() == 0){
            System.out.println("Now rooms is NULL!!");
            return;
        }
        //为每个请求队列中的请求增加1s服务时间
        centralAC.updateRequestQueueServingTime();
        //修改每个房间的状态
//        System.out.println("now request queue's size :" + centralAC.getRequest_queue().size() + ", maxsize is " + queue_max_size);
        for(int i = 0; i<rooms.size();i++){
            Room room = rooms.get(i);
            int waitTime = room.getWaitingTime();
            int servTime = room.getServingTime();
            int autoupdateTime = room.getAutoUpdateTime();
//            System.out.println(room.getRoomid() + " autoupdatein:" + autoupdateTime + "s,\t wait:" + room.getWaitingTime()
//                    + "s,\t serving:"+ room.getServingTime() + "s,\t idle:" + room.getIdleUpdateTime()
//                    + "s,\t state:"+room.getState() + ",\t\t winmode:"+ room.getWinmode() + ",\t cost:"+room.getFee()
//                    + "$,\t now:" + room.getLocalTemp() + "C,\t tar:" + room.getTargetTemp() + "C");
            //当前房间空调当前处于等待服务状态
            if(room.getState().equals("IDLE")){
                //当待机状态累计1分钟，自动清零并更新当前温度
                if(room.getIdleUpdateTime() == 59){
                    roomService.autoUpdateIdleState(room);
                }
                else{
                    room.setIdleUpdateTime(room.getIdleUpdateTime() + 1);
                }

                //当前房间接近目标温度，并不需要服务
                if(room.getTargetTemp() + 1 > room.getLocalTemp() && room.getTargetTemp() - 1 < room.getLocalTemp()){
                    continue;
                }
                //当前房间需要服务，此时请求队列未满，直接将当前房间放入请求队列接受服务
                else if(centralAC.getRequest_queue().size() < queue_max_size){
                    System.out.println("space is free, push "+room.getRoomid() + " to queue");
                    //更新房间状态
                    roomService.transferRunningState(room);
                    //重置等待时间
                    room.setWaitingTime(0);
                    //放入请求队列
                    centralAC.offerRequest_queue(new RoomRequest(room.getRoomid(),room.getWinmode(), room.getWinrate(), servTime, waitTime));
                    //第一个入队的请求风速就是队列的最小风速
                    if(centralAC.getRequest_queue().size() == 0){
                        min_winrate = room.getWinrate();
                    }
                    //之后每次入队时，都比较当前风速是否小于最小风速，如果是，更新最小风速
                    else{
                        min_winrate = room.getWinrate() < min_winrate ? room.getWinrate() : min_winrate;
                    }
                    Record r = new Record(room, "SYS-SEND");
                    recordRepository.save(r);
                }
                //当前房间需要服务，但是请求队列已满
                else {
                    System.out.println("room " + room.getRoomid() + " needs to be serve but space isn't free");
                    //当前房间的风速大于请求队列中的某一房间的风速，将对应房间从请求队列移除，把当前房间放入请求队列
                    if(room.getWinrate() > min_winrate) {
                        //获取请求队列
                        Queue<RoomRequest> rq = centralAC.getRequest_queue();
                        //建立一个新队列
                        Queue<RoomRequest> newrq = new PriorityBlockingQueue<RoomRequest>(queue_max_size,
                                new Comparator<RoomRequest>(){
                            public int compare(RoomRequest a, RoomRequest b){
                                return a.getServingTime() - b.getServingTime() > 0 ? -1 : 1;
                            }
                        });
                        //把请求队列中的元素一个一个出队，直到找出第一个风速小于当前房间风速的房间
                        while(!rq.isEmpty()){
                            //出队
                            RoomRequest rr = rq.poll();
                            //找出需移出请求队列的房间
                            if(rr.getWinrate() < room.getWinrate()){
                                int index = centralAC.findRoom(rr.getRoomid());
                                Record record = new Record(rooms.get(index), "SYS-STOP");
                                recordRepository.save(record);
                                //更新移出房间状态
                                rooms.get(index).setState("IDLE");
                                System.out.println("offer:#" + room.getRoomid() + ", waiting:" + room.getWaitingTime()
                                        + "s, winmode:" + room.getWinmode());
                                System.out.println("remove:#" + rr.getRoomid() + ", serving:" + rr.getServingTime()
                                        + "s, winmode:" + rr.getWinmode());
                                break;
                            }
                            //将出队元素重新进入新队列
                            newrq.offer(rr);
                        }
                        //更新当前房间状态
                        roomService.transferRunningState(room);
                        //重置等待时间
                        room.setWaitingTime(0);
                        //把当前房间放入新队列
                        newrq.offer(new RoomRequest(room.getRoomid(),room.getWinmode(), room.getWinrate(), servTime,waitTime));
                        min_winrate = room.getWinrate();
                        //把剩余元素放入新队列
                        while(!rq.isEmpty()){
                            RoomRequest rr = rq.poll();
                            newrq.offer(rr);
                            min_winrate = room.getWinrate() < min_winrate ? room.getWinrate() : min_winrate;
                        }
                        //把新队列赋给老队列
                        rq = newrq;
                        centralAC.setRequest_queue(rq);
                        Record r = new Record(room, "SYS-SEND");
                        recordRepository.save(r);
                    }
                    //当前房间风速与队列中的最小风速一致，但其等待时间已经超出了时间片
                    //此时，需要把请求队列中服务时间最长，且风速与当前房间一致的房间移除，并把当前房间放入请求队列
                    else if (room.getWinrate() - min_winrate < 0.02 && room.getWinrate() - min_winrate > -0.02
                            && waitTime >= timeslice) {
                        System.out.println("room "+room.getRoomid() + " wait enough time");
                        //获取请求队列
                        Queue<RoomRequest> rq = centralAC.getRequest_queue();
                        //建立一个新队列
                        Queue<RoomRequest> newrq = new PriorityBlockingQueue<RoomRequest>(queue_max_size,
                                new Comparator<RoomRequest>(){
                                    public int compare(RoomRequest a, RoomRequest b){
                                        return a.getServingTime() - b.getServingTime() > 0 ? -1 : 1;
                                    }
                                });
                        //把请求队列中的元素一个一个出队，直到找出第一个风速等于当前房间风速的房间
                        while(!rq.isEmpty()){
                            //出队
                            RoomRequest rr = rq.poll();
                            //找出第一个需移出请求队列的房间
                            if(rr.getWinrate() < room.getWinrate() + 0.02 && rr.getWinrate() > room.getWinrate() - 0.02){
                                //更新移出房间状态
                                int index = centralAC.findRoom(rr.getRoomid());
                                Record record = new Record(rooms.get(index), "SYS-STOP");
                                recordRepository.save(record);
                                rooms.get(index).setState("IDLE");
                                System.out.println("offer:#" + room.getRoomid() + ", waiting:" + room.getWaitingTime()
                                        + "s, winmode:" + room.getWinmode());
                                System.out.println("poll:#" + rr.getRoomid() + ", serving:" + rr.getServingTime()
                                        + "s, winmode:" + rr.getWinmode());
                                break;
                            }
                            //将出队元素重新进入新队列
                            newrq.offer(rr);
                        }
                        //更新当前房间状态
                        roomService.transferRunningState(room);
                        //重置等待时间
                        room.setWaitingTime(0);
                        //把当前房间放入新队列
                        newrq.offer(new RoomRequest(room.getRoomid(),room.getWinmode(), room.getWinrate(), servTime,waitTime));
                        min_winrate = room.getWinrate();
                        //把剩余元素放入新队列
                        while(!rq.isEmpty()){
                            RoomRequest rr = rq.poll();
                            newrq.offer(rr);
                            min_winrate = room.getWinrate() < min_winrate ? room.getWinrate() : min_winrate;
                        }
                        //把新队列赋给老队列
                        rq = newrq;
                        centralAC.setRequest_queue(rq);
                        Record r = new Record(room, "SYS-SEND");
                        recordRepository.save(r);
                    }
                    //如果当前房间需要服务，却无法进入队列(时间片未到，或所有房间风速都大于该房间)
                    //则该房间继续等待，并将该房间等待时间 + 1
                    else {
                        room.setWaitingTime(waitTime + 1);
                    }
                }
            }
            //当前房间空调处于关闭状态
            else if(room.getState().equals("OFF")) {
                //如果房间到达自动更新的时间
                if (autoupdateTime == 59) {
                    //自动更新房间信息
                    roomService.autoUpdateState(room);
                }
                else {
                    //更新房间计时器
                    room.setAutoUpdateTime(autoupdateTime + 1);
                }
            }
            //当前房间空调处于工作状态(FREEZE\HEAT)
            else{
                //服务时间增加1秒
                rooms.get(i).setServingTime(servTime + 1);
                //如果房间到达自动更新的时间
                if(autoupdateTime >= 59){
                    //自动更新房间信息
                    roomService.autoUpdateState(room);
                    //接近目标温度，停止送风请求
                    if(room.getState().equals("IDLE")){
                        centralAC.removeRoominRequestQueue(room.getRoomid());
                    }
                }
                else{
                    //更新房间计时器
                    room.setAutoUpdateTime(autoupdateTime + 1);
                }
            }
        }
        centralAC.setRooms(rooms);
        centralAC.setRequest_min_winrate(min_winrate);
    }
}
