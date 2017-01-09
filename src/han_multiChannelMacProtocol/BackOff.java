package han_multiChannelMacProtocol;

import java.util.Random;

/**
 * Created by ycqfeng on 2017/1/5.
 */
public class BackOff {
    private double baseTime;
    private static int T = 16;
    private int time;
    private Random random;

    public BackOff(double baseTime){
        this.baseTime = baseTime;
        this.time = 1;
        this.random = new Random();
    }
    public double getMaxBackOffTime(){
        return baseTime*random.nextInt(T);
    }
    public double getBackOffTime(){
        int a = random.nextInt(time++);
        if (time > T){
            time = T;
        }
        return a*baseTime;
    }
    public void init(){
        time = 1;
    }
}
