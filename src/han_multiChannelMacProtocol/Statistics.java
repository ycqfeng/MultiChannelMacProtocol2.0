package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/7.
 */
public class Statistics {
    private static Statistics statistics = new Statistics();

    //丢弃数据包
    private int numDropDataPacketBit;
    private int numDropDataPacket;
    //成功接收数据包
    private int numReceiveDataPacketBit;
    private int numReceiveDataPacket;
    private double sumTime;

    public Statistics(){
        this.numDropDataPacketBit = 0;
        this.numDropDataPacket = 0;
        this.numReceiveDataPacketBit = 0;
        this.numReceiveDataPacket = 0;
    }
    public static void addDataPacket(Packet packet){
        statistics.numReceiveDataPacket += 1;
        statistics.numReceiveDataPacketBit += packet.getLength();
    }
    public static void dropDataPacket(Packet packet){
        statistics.numDropDataPacket += 1;
        statistics.numDropDataPacketBit += packet.getLength();
    }
    public static int getNumReceiveDataPacketBit(){
        return statistics.numReceiveDataPacketBit;
    }
    public static int getNumDropDataPacketBit(){
        return statistics.numDropDataPacketBit;
    }
    public static void setSumTime(double sumTime){
        Statistics.statistics.sumTime = sumTime;
    }
    public static void print(){
        String str;
        str = "Receive Data Packet Bit : " + statistics.numReceiveDataPacketBit;
        System.out.println(str);

        str = "Receive Data Packet Num : " + statistics.numReceiveDataPacket;
        System.out.println(str);

        str = "Drop Data Packet Num Bit : " + statistics.numDropDataPacketBit;
        System.out.println(str);

        str = "Drop Data Packet Num : " + statistics.numDropDataPacket;
        System.out.println(str);

        str = "bps : " + statistics.numReceiveDataPacketBit/statistics.sumTime/1000;
        str += "Kbps";
        System.out.println(str);
    }
}
