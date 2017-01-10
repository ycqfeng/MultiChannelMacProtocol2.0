package han_simulator;

import java.text.DecimalFormat;

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
    private Event curEvent;
    //注册实体接口
    private IF_simulator[] interfaces;
    //结束标志
    boolean executeFinish;
    //格式化
    private DecimalFormat decimalFormat;

    //初始化
    public static void init(){
        simulator = new Simulator();
        Hprint.register(simulator);
        Hprint.printlnt("仿真器完成初始化");
        simulator.curTime = 0;
        simulator.stopTime = 0;
        simulator.decimalFormat = new DecimalFormat("#.00");
    }

    //是否完成
    public static boolean isFinish(Event event){
        if (simulator.executeFinish){
            return true;
        }
        if (simulator.stopTime != 0){
            return simulator.stopTime < event.getTimeExecute();
        }
        else{
            return false;
        }
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

    //删除一个事件
    public static boolean deleteEvent(int uid){
        Event temp = simulator.eventQueueHead;
        while ((temp != null) && (temp.getUid() != uid)){
            temp = temp.getNext();
        }
        if (temp == null){
            return false;
        }
        if (temp.getUid() == uid){
            if (temp == simulator.eventQueueHead) {
                simulator.eventQueueHead = temp.getNext();
            }
            temp.deleteSelf();
            return true;
        }
        return false;
    }

    //增加一个事件
    public static int addEvent(Event event){
        if (event.getTimeExecute() < simulator.curTime){
            String error = "新事件执行时间小于当前时间。";
            Hprint.printlnErrorInfo(simulator, error);
        }
        Event temp = simulator.eventQueueHead;
        //若为空
        if (temp == null){
            simulator.eventQueueHead = event;
            return event.getUid();
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
            return event.getUid();
        }
    }
    public static int addEvent(double interTime, IF_Event eventInterface){
        Event event = new Event();
        event.setTimeInter(interTime);
        event.setEventInterface(eventInterface);
        return addEvent(event);
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

    public static int getCurrEventUid(){
        return simulator.curEvent.getUid();
    }

    //准备阶段
    public static void start(){
        //开始事件
        class EventStart implements IF_Event{
            @Override
            public void run(){
                simulator.executeFinish = false;
                Hprint.printlntDebugInfo(simulator, "仿真器开始运行");
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
                simulator.executeFinish = true;
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

        simulator.execute();
    }
    //执行事件
    private void execute(){
        if (simulator.eventQueueHead == null){
            Hprint.printlnErrorInfo(simulator, "队列中无事件。");
        }

        //Event curEvent;
        double progress;
        double difProgress = 1;
        long startPoint = System.currentTimeMillis();
        long endPoint;
        while (!isFinish(simulator.eventQueueHead)){
            this.curTime = this.eventQueueHead.getTimeExecute();

            if (this.stopTime > 0){
                progress = 100 * this.curTime/this.stopTime;
                if (progress - difProgress >1){
                    endPoint = System.currentTimeMillis();
                    System.out.println(decimalFormat.format(progress)+"%, 耗时："+(endPoint-startPoint)+"ms");
                    difProgress = Math.floor(progress);
                    startPoint = System.currentTimeMillis();
                }
            }

            curEvent = this.eventQueueHead;
            this.eventQueueHead = this.eventQueueHead.getNext();
            curEvent.setNextToNULL();
            curEvent.run();
        }
    }
}
