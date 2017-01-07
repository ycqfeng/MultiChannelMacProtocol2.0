package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/7.
 */
public class PacketRTS extends Packet {
    private int lengthData;

    public PacketRTS(int length, int lengthData){
        this.length = length;
        this.packetType = PacketType.RTS;
        this.lengthData = lengthData;
    }
    public PacketRTS(int length, Packet packet){
        this.length = length;
        this.packetType = PacketType.RTS;
        this.lengthData = packet.getLength();
    }

    public void setLengthData(int lengthData){
        this.lengthData = lengthData;
    }
    public int getLengthData(){
        return lengthData;
    }
}
