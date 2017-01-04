package han_simulator;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class Event {
    //序号
    private static int index = 0;
    private int uid;
    //时间
    private double timeExecute;

    //节点
    private Event last;
    private Event next;

    //接口
    private IF_Event eventInterface;

    //构造函数
    public Event(){
        this.uid = index++;
    }

    //获取uid
    public int getUid(){
        return this.uid;
    }

    //运行
    public void run(){
        this.eventInterface.run();
    }

    //设置接口
    public void setEventInterface(IF_Event eventInterface){
        this.eventInterface = eventInterface;
    }

    //置空头/尾
    public void setLastToNULL(){
        if (this.last != null){
            if (this.last.next != null){
                this.last.next = null;
            }
            this.last = null;
        }
    }
    public void setNextToNULL(){
        if (this.next != null){
            if (this.next.last != null){
                this.next.last = null;
            }
            this.next = null;
        }
    }
    //设置头/尾
    public void addToEnd(Event event){
        Event temp = this.getEnd();
        temp.addToNext(event);
    }
    public void addToHead(Event event){
        Event temp = this.getHead();
        temp.addToLast(event);
    }
    //获取头/尾
    public Event getEnd(){
        Event event = this;
        while(event.next != null){
            event = event.next;
        }
        return event;
    }
    public Event getHead(){
        Event event = this;
        while(event.last != null){
            event = event.last;
        }
        return event;
    }
    //获取上/下一个
    public Event getLast() {
        return last;
    }
    public Event getNext() {
        return next;
    }
    //添加一个到上/下
    public void addToLast(Event event){
        if (this.last == null){
            this.last = event;
            this.last.next = this;
            return;
        }
        else {
            Event temp = this.getLast();
            this.last = event;
            this.last.next = this;
            temp.next = event;
            temp.next.last = temp;
            return;
        }
    }
    public void addToNext(Event event){
        if (this.next == null){
            this.next = event;
            this.next.last = this;
            return;
        }
        else{
            Event temp = this.getNext();
            this.next = event;
            this.next.last = this;
            temp.last = event;
            temp.last.next = temp;
            return;
        }
    }

    //设置、获取执行时间
    public void setTimeExecute(double timeExecute){
        this.timeExecute = timeExecute;
    }
    public double getTimeExecute(){
        return timeExecute;
    }
    //设置间隔时间
    public void setTimeInter(double timeInter){
        this.timeExecute = Simulator.getCurTime()+timeInter;
    }

}
