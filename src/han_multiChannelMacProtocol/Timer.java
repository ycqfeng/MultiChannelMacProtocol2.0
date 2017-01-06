package han_multiChannelMacProtocol;

import han_simulator.Event;
import han_simulator.IF_Event;
import han_simulator.Simulator;

/**
 * Created by ycqfeng on 2017/1/6.
 */
public class Timer {
    private double runTime;
    private boolean running;
    private int eventUid;

    public Timer(){
        this.runTime = 0;
        this.running = false;
        this.eventUid = -1;
    }

    public void setTimer(double timeReminder, IF_Event if_event){
        this.runTime = timeReminder + Simulator.getCurTime();
        this.eventUid = Simulator.addEvent(timeReminder, if_event);
        this.running = true;
    }

    public boolean isRunning(){
        return running;
    }

    public double getTimeReminder(){
        if (this.running){
            return this.runTime - Simulator.getCurTime();
        }else {
            return -1;
        }
    }

    public boolean cancel(){
        this.running = false;
        return Simulator.deleteEvent(this.eventUid);
    }

}
