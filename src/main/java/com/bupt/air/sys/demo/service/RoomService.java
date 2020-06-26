package com.bupt.air.sys.demo.service;

import com.bupt.air.sys.demo.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoomService {
    @Autowired
    CentralAC centralAC;

    public void autoUpdateState(Room room){
        String state = room.getState();
        //重置自动更新时间
        room.setAutoUpdateTime(0);
        //空调关机状态下，每分钟变化0.5度，恢复至初始温度附近
        if(state.equals("OFF")){
            if(room.getLocalTemp() < room.getInitTemp()){
                room.setLocalTemp((float) (room.getLocalTemp() + 0.5));
            }
            else if(room.getLocalTemp() > room.getInitTemp()){
                room.setLocalTemp((float) (room.getLocalTemp() - 0.5));
            }
        }
        //空调工作状态下，根据风速模式调节室温
        else {
            //中风速，每分钟恒定变化0.5度，费用增加0.5元
            String winmode = room.getWinmode();
            if(winmode.equals("MID")){
                room.setWinrate((float)0.5);
                if(room.getLocalTemp() < room.getTargetTemp()){
                    room.setLocalTemp((float) (room.getLocalTemp() + 0.5));
                    transferRunningState(room);
                }
                else if(room.getLocalTemp() > room.getTargetTemp()){
                    room.setLocalTemp((float) (room.getLocalTemp() - 0.5));
                    transferRunningState(room);
                }
                room.setFee((float)(room.getFee() + 0.5));
            }
            //低风速，每分钟恒定变化0.4度，费用增加0.333元
            else if(winmode.equals("LOW")){
                room.setWinrate((float)0.4);
                if(room.getLocalTemp() < room.getTargetTemp()){
                    room.setLocalTemp((float) (room.getLocalTemp() + 0.4));
                    transferRunningState(room);
                }
                else if(room.getLocalTemp() > room.getTargetTemp()){
                    room.setLocalTemp((float) (room.getLocalTemp() - 0.4));
                    transferRunningState(room);
                }
                room.setFee((float)(room.getFee() + 0.333));
            }
            //高风速，每分钟恒定变化0.6度，费用增加1元
            else if(winmode.equals("HIGH")){
                room.setWinrate((float)0.6);
                if(room.getLocalTemp() < room.getTargetTemp()){
                    room.setLocalTemp((float) (room.getLocalTemp() + 0.6));
                    transferRunningState(room);
                }
                else if(room.getLocalTemp() > room.getTargetTemp()){
                    room.setLocalTemp((float) (room.getLocalTemp() - 0.6));
                    transferRunningState(room);
                }
                room.setFee((float)(room.getFee() + 1.0));
            }
        }
    }

    //处于等待服务状态(IDLE)下的房间，每当idleUpdateTime到达59，清零并更新房间信息
    public void autoUpdateIdleState(Room room){
        room.setIdleUpdateTime(0);
        //自动恢复至初始温度附近
        if(room.getLocalTemp() < room.getInitTemp()){
            room.setLocalTemp((float) (room.getLocalTemp() + 0.5));
        }
        else if(room.getLocalTemp() > room.getInitTemp()){
            room.setLocalTemp((float) (room.getLocalTemp() - 0.5));
        }
    }

    //自动转换房间空调运行时的状态
    public void transferRunningState(Room room){
        String state = room.getState();
        float lowerTemp = centralAC.getTemp_low();
        float defaultTemp = centralAC.getTemp_default();
        float higherTemp = centralAC.getTemp_high();
        //当空调关闭时，不需要改变状态
        if(state.equals("OFF")){
            return;
        }
        //当前温度接近目标温度时，转变状态
        else if(room.getLocalTemp() < room.getTargetTemp() + 0.35 && room.getLocalTemp() > room.getTargetTemp() - 0.35){
            room.setState("IDLE");
        }
        //目标温度介于18-25度时，模式为制冷
        else if(room.getTargetTemp() >= lowerTemp && room.getTargetTemp() <= defaultTemp){
            room.setState("FREEZE");
        }
        //目标温度介于25-30度时，模式为制热
        else if(room.getTargetTemp() > defaultTemp && room.getTargetTemp() <= higherTemp){
            room.setState("HEAT");
        }
        //当前温度不在这些范围时，不改动状态
    }

}
