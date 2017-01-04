package han_simulator;

import com.sun.org.apache.bcel.internal.generic.IFEQ;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class Simulator implements IF_HprintNode{
    private static Simulator simulator;

    //时间
    private double curTime;
    private double stopTime;
    //事件链表
    private Event eventQueueHead;
    //注册实体接口
     private IF_simulator[] interfaces;

    //初始化
    public static void init(){
        simulator = new Simulator();
        Hprint.register(simulator);
        simulator.curTime = 0;
        simulator.stopTime = 0;
    }

    //增加一个实体接口
    public static boolean register(IF_simulator simulatorInterface){
        if (simulator.isInterfaceExistence(simulatorInterface)){
            return false;
        }
        if (simulator.interfaces == null){
            simulator.interfaces = new IF_simulator[1];
            simulator.interfaces[0] = simulatorInterface;
            return true;
        }
        else{
            IF_simulator[] temp = new IF_simulator[simulator.interfaces.length+1];
            System.arraycopy(simulator.interfaces, 0, temp, 0, simulator.interfaces.length);
            temp[simulator.interfaces.length] = simulatorInterface;
            return true;
        }
    }

    //检查接口是否存在
    public static boolean isInterfaceExistence(IF_simulator if_simulator){
        if (simulator.interfaces == null){
            return false;
        }
        for (int i = 0 ; i < simulator.interfaces.length ; i++){
            if (simulator.interfaces[i] == if_simulator){
                return true;
            }
        }
        return false;
    }

    //增加一个事件
    public static void addEvent(Event event){
        if (event.getTimeExecute() < simulator.curTime){
            String error = "新事件执行时间小于当前时间。";
            Hprint.printlnErrorInfo(simulator, error);
        }
        Event temp = simulator.eventQueueHead;
        //若为空
        if (temp == null){
            simulator.eventQueueHead = event;
            return;
        }
        //若非空
        else {
            //若头小于事件，寻找下一个，直到尾部
            while (temp.getTimeExecute() < event.getTimeExecute()){
                if (temp.getNext() != null){
                    temp = temp.getNext();
                }
                else {
                    break;
                }
            }
            //若当前小于事件，插入当前后
            if (temp.getTimeExecute() < event.getTimeExecute()){
                temp.addToNext(event);
            }
            else {
                temp.addToLast(event);
            }
            simulator.eventQueueHead = simulator.eventQueueHead.getHead();
            return;
        }
    }
    public static void addEvent(double interTime, IF_Event eventInterface){
        Event event = new Event();
        event.setTimeInter(interTime);
        event.setEventInterface(eventInterface);
        addEvent(event);
    }

    //setter and getter
    public static void setStopTime(double stopTime){
        simulator.stopTime = stopTime;
    }

    public static double getCurTime(){
        return simulator.curTime;
    }

    public static double getStopTime() {
        return simulator.stopTime;
    }

    //准备阶段
    public static void start(){
        //开始事件
        class EventStart implements IF_Event{
            @Override
            public void run(){
                Hprint.printlntDebugInfo(simulator, "仿真器运行中。");
                if (simulator.interfaces != null){
                    for (int i = 0 ; i < simulator.interfaces.length ; i++){
                        simulator.interfaces[i].simulatorStart();
                    }
                }
            }
        }
        //结束事件
        class EventEnd implements IF_Event{
            @Override
            public void run(){
                if (simulator.interfaces != null){
                    for (int i = 0 ; i < simulator.interfaces.length ; i++){
                        simulator.interfaces[i].simulatorEnd();
                    }
                }
                Hprint.printlntDebugInfo(simulator, "仿真器结束。");
            }
        }
        EventStart eventStart = new EventStart();
        EventEnd eventEnd = new EventEnd();
        addEvent(0, eventStart);
        addEvent(simulator.stopTime,eventEnd);
    }

}
