package han_multiChannelMacProtocol;

import han_simulator.*;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class NetDevice implements IF_simulator, IF_HprintNode, IF_Channel{
    private static int uidBase = 0;
    private int uid;
    StateNetDevice state;//设备状态
    SubChannel tSubchannel;//临时信道

    //构造函数
    public NetDevice(){
        this.uid = uidBase++;
        Simulator.register(this);
        Hprint.register(this);
        this.state = StateNetDevice.IDLE;
    }
    //获取Uid
    public int getUid(){
        return uid;
    }
    //接收
    @Override
    public boolean receive(SubChannel subChannel, Packet packet){
        NetDeviceReceiveBegin receiveBegin = new NetDeviceReceiveBegin(this, subChannel, packet);
        Simulator.addEvent(0, receiveBegin);
        return true;
    }
    //发送RTS
    public boolean sendRTS(NetDevice to){
        NetDeviceSendRTSBegin sendRTSBegin = new NetDeviceSendRTSBegin(this,to,tSubchannel);
        Simulator.addEvent(1, sendRTSBegin);
        return true;
    }
    //临时函数
    public void settSubchannel(SubChannel subchannel){
        this.tSubchannel = subchannel;
    }

    /**
     * 内部类
     */
    //开始接收
    class NetDeviceReceiveBegin implements IF_Event{
        NetDevice device;
        Packet packet;
        SubChannel subChannel;

        public NetDeviceReceiveBegin(NetDevice netDevice, SubChannel subChannel, Packet packet){
            this.device = netDevice;
            this.packet = packet;
            this.subChannel = subChannel;
        }
        @Override
        public void run(){
            Hprint.printlntDebugInfo(device, "NetDevice("+device.getUid()+")开始接收一个Packet("+packet.getUid()+")");
            device.state = StateNetDevice.RECEVING;
            NetDeviceReceiveEnd receiveEnd = new NetDeviceReceiveEnd(device, subChannel, packet);
            double trans = subChannel.getTimeTrans(packet);
            Simulator.addEvent(trans, receiveEnd);
        }
    }
    //结束接收
    class NetDeviceReceiveEnd implements IF_Event{
        NetDevice device;
        Packet packet;
        SubChannel subChannel;

        public NetDeviceReceiveEnd(NetDevice netDevice, SubChannel subChannel, Packet packet){
            this.device = netDevice;
            this.subChannel = subChannel;
            this.packet = packet;
        }
        @Override
        public void run(){
            device.state = StateNetDevice.IDLE;
            Hprint.printlntDebugInfo(device, "NetDevice("+device.getUid()+")完成接收一个Packet("+packet.getUid()+")");
        }
    }
    //开始RTS发送
    class NetDeviceSendRTSBegin implements IF_Event{
        NetDevice from;
        NetDevice to;
        SubChannel subChannel;
        Packet packet;

        public NetDeviceSendRTSBegin(NetDevice from, NetDevice to, SubChannel subChannel){
            this.from = from;
            this.to = to;
            this.subChannel = subChannel;
            this.packet = new Packet(20*8, PacketType.RTS);
        }

        @Override
        public void run(){
            from.state = StateNetDevice.TRANSMISSION;
            Hprint.printlntDebugInfo(from,"NetDevice("+from.getUid()+")开始发送一个Packet("+packet.getUid()+")");
            double trans = subChannel.send(packet,to);
            NetDeviceSendRTSEnd sendRTSEnd = new NetDeviceSendRTSEnd(from, packet);
            Simulator.addEvent(trans, sendRTSEnd);
        }
    }
    //结束RTS发送
    class NetDeviceSendRTSEnd implements IF_Event{
        NetDevice from;
        Packet packet;

        public NetDeviceSendRTSEnd(NetDevice from, Packet packet){
            this.from = from;
            this.packet = packet;
        }
        @Override
        public void run(){
            from.state = StateNetDevice.IDLE;
            Hprint.printlntDebugInfo(from, "NetDevice("+from.getUid()+")结束发送一个Packet("+packet.getUid()+")");
        }
    }
}