package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/7.
 */
public class Statistics {
    private static Statistics statistics = new Statistics();

    private int numReceiveDataPacketBit;
    private int numReceiveDataPacket;
    private double sumTime;

    public Statistics(){
        this.numReceiveDataPacketBit = 0;
    }
    public static void addPacket(Packet packet){
        statistics.numReceiveDataPacketBit += packet.getLength();
        statistics.numReceiveDataPacket += 1;
    }
    public static int getNumDataPacketBit(){
        return Statistics.statistics.numReceiveDataPacketBit;
    }
    public static void setSumTime(double sumTime){
        Statistics.statistics.sumTime = sumTime;
    }
    public static void print(){
        String str;
        str = "Packet Bit : " + statistics.numReceiveDataPacketBit;
        System.out.println(str);

        str = "Packet Num : " + statistics.numReceiveDataPacket;
        System.out.println(str);

        str = "bps : " + statistics.numReceiveDataPacketBit/statistics.sumTime/1000;
        str += "Kbps";
        System.out.println(str);
    }
}
