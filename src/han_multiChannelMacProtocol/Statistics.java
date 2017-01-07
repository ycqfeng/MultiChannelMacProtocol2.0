package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/7.
 */
public class Statistics {
    private static Statistics statistics = new Statistics();

    private int numDataPacketBit;
    private int numDataPacket;
    private double sumTime;

    public Statistics(){
        this.numDataPacketBit = 0;
    }
    public static void addPacket(Packet packet){
        statistics.numDataPacketBit += packet.getLength();
        statistics.numDataPacket += 1;
    }
    public static int getNumDataPacketBit(){
        return Statistics.statistics.numDataPacketBit;
    }
    public static void setSumTime(double sumTime){
        Statistics.statistics.sumTime = sumTime;
    }
    public static void print(){
        String str;
        str = "Packet Bit : " + statistics.numDataPacketBit;
        System.out.println(str);

        str = "Packet Num : " + statistics.numDataPacket;
        System.out.println(str);

        str = "bps : " + statistics.numDataPacketBit/statistics.sumTime/1000;
        str += "Kbps";
        System.out.println(str);
    }
}
