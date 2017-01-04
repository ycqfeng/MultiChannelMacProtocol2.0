package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class Packet {
    private static int uidBase = 0;
    private int uid;
    private PacketType packetType;
    private int length;

    //构造函数
    public Packet(){
        this.uid = uidBase++;
        this.packetType = PacketType.PACKET;
        this.length = 1;
    }
    public Packet(int length){
        this.uid = uidBase++;
        this.packetType = PacketType.PACKET;
        this.length = length;
    }
    public  Packet(int length, PacketType type){
        this.uid = uidBase++;
        this.packetType = type;
        this.length = length;
    }

    //setter and getter
    public void setPacketType(PacketType type){
        this.packetType = type;
    }
    public void setLength(int length){
        this.length = length;
    }
    public PacketType getPacketType(){
        return packetType;
    }
    public int getLength(){
        return length;
    }
    public int getUid(){
        return uid;
    }
}
