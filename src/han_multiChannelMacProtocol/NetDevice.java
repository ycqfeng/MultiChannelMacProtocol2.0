package han_multiChannelMacProtocol;

import han_simulator.*;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class NetDevice implements IF_simulator, IF_HprintNode, IF_Channel{
    private static int uidBase = 0;
    private int uid;
    private StateNetDevice state;//设备状态
    private BackOff backOff;
    private boolean isWaitForCTS;
    private boolean isCollision;
    private int numReceiving;
    private Packet nextSendPacket;
    private PacketQueue queue;

    SubChannel tSubchannel;//临时信道
    int tSubChannelOccupy;//临时信道占用情况

    //构造函数
    public NetDevice(){
        this.uid = uidBase++;
        this.queue = new PacketQueue();
        Simulator.register(this);
        Hprint.register(this);
        this.state = StateNetDevice.IDLE;
        this.numReceiving = 0;
        this.backOff = new BackOff(0.001);
    }
    //获取Uid
    public int getUid(){
        return uid;
    }
    //获取状态
    public StateNetDevice getState(){
        return this.state;
    }
    //添加Packet到队列
    public boolean enQueue(Packet packet){
        if (this.queue.pushPacket(packet)){
            Hprint.printlntDebugInfo(this, "一个Packet("+packet.getUid()+")成功加入队列");
            if (nextSendPacket == null){
                nextSendPacket = this.queue.popPacket();
                this.sendRTS(0, packet.getToNetDevice());
            }
            return true;
        }
        else{
            Hprint.printlntDebugInfo(this, "一个Packet("+packet.getUid()+")失败加入队列");
            return false;
        }
    }
    //包碰撞
    public void collidePacket(Packet packet){
        String str = "";
        str += "NetDevice("+this.getUid()+")碰撞[";
        str += packet.getPacketType()+"("+packet.getUid()+")]";
        Hprint.printlntDebugInfo(this,str);
    }
    //接收
    @Override
    public boolean receive(NetDevice from, NetDevice to, SubChannel subChannel, Packet packet){
        if (packet.getToNetDevice().getUid() == this.getUid()){
            NetDeviceReceiveBegin receiveBegin = new NetDeviceReceiveBegin(from, this, subChannel, packet);
            Simulator.addEvent(0, receiveBegin);
            return true;
        }
        else{
            this.tSubChannelOccupy++;
            SubChannelSelfDe selfDe = new SubChannelSelfDe(this, from);
            double t = subChannel.getTimeTrans(packet);
            Simulator.addEvent(t, selfDe);
            String str = "";
            str += "NetDevice("+this.getUid()+") 信道被占用";
            str += "by["+packet.getPacketType()+"("+packet.getUid()+")]";
            str += ", NetDevice("+from.getUid()+")->NetDevice("+packet.getToNetDevice().getUid()+")";
            Hprint.printlntDebugInfo(this, str);
        }
        return false;
    }
    //发送Packet
    public boolean sendPacket(double interTime, NetDevice to, Packet packet){
        NetDeviceSendPacketBegin sendPacketBegin = new NetDeviceSendPacketBegin(this, to, tSubchannel, packet);
        Simulator.addEvent(interTime, sendPacketBegin);
        return true;
    }
    //发送RTS
    public boolean sendRTS(double interTime, NetDevice to){
        Packet packetRTS = new Packet(20*8, PacketType.RTS);
        packetRTS.setToNetDevice(to);
        this.sendPacket(interTime, to, packetRTS);
        return true;
    }
    //发送CTS
    public boolean sendCTS(double interTime, NetDevice to){
        Packet packetCTS = new Packet(20*8, PacketType.CTS);
        packetCTS.setToNetDevice(to);
        this.sendPacket(interTime, to, packetCTS);
        return true;
    }
    //临时函数
    public void settSubchannel(SubChannel subchannel){
        this.tSubchannel = subchannel;
    }

    /**
     * 内部类
     */
    //开始发送Packet
    class NetDeviceSendPacketBegin implements IF_Event{
        NetDevice netDevice;
        NetDevice to;
        SubChannel subChannel;
        Packet packet;

        public NetDeviceSendPacketBegin(NetDevice netDevice, NetDevice to, SubChannel subChannel, Packet packet){
            this.netDevice = netDevice;
            this.to = to;
            this.subChannel = subChannel;
            this.packet = packet;
        }

        @Override
        public void run(){
            String str;
            if (netDevice.tSubChannelOccupy > 0){
                double t = netDevice.backOff.getBackOffTime();
                str = "";
                str += "NetDevice("+netDevice.getUid()+") 发送 [";
                str += packet.getPacketType()+"("+packet.getUid()+")]";
                str += "退避 "+t+"秒";
                Hprint.printlntDebugInfo(netDevice, str);
                //Hprint.printlnt("backoff");
                NetDeviceSendPacketBegin sendPacketBegin = new NetDeviceSendPacketBegin(netDevice, to, subChannel, packet);
                Simulator.addEvent(t, sendPacketBegin);
                return;
            }
            str = "";
            str += "NetDevice("+netDevice.getUid()+") 开始发送 [";
            str += packet.getPacketType()+"("+packet.getUid()+")]";
            str += "->NetDevice("+to.getUid()+")";
            Hprint.printlntDebugInfo(netDevice,str);
            netDevice.state = StateNetDevice.TRANSMISSION;
            double trans = subChannel.send(netDevice, to, packet);
            NetDeviceSendPacketEnd sendPacketEnd = new NetDeviceSendPacketEnd(netDevice, to, subChannel, packet);
            Simulator.addEvent(trans, sendPacketEnd);
        }
    }
    //结束发送Packet
    class NetDeviceSendPacketEnd implements IF_Event{
        NetDevice netDevice;
        NetDevice to;
        SubChannel subChannel;
        Packet packet;

        public NetDeviceSendPacketEnd(NetDevice netDevice, NetDevice to, SubChannel subChannel, Packet packet) {
            this.netDevice = netDevice;
            this.to = to;
            this.subChannel = subChannel;
            this.packet = packet;
        }

        @Override
        public void run() {
            netDevice.backOff.init();
            netDevice.state = StateNetDevice.IDLE;
            switch (packet.getPacketType()){
                case PACKET:
                    break;
                case RTS:
                    netDevice.isWaitForCTS = true;
                    break;
                case CTS:
                    break;
                case OTHER:
                    break;
                default:
                    break;
            }
            String str = "";
            str += "NetDevice("+netDevice.getUid()+") 完成发送 [";
            str += packet.getPacketType()+"("+packet.getUid()+")]";
            str += "->NetDevice("+to.getUid()+")";
            Hprint.printlntDebugInfo(netDevice, str);
        }
    }
    //开始接收
    class NetDeviceReceiveBegin implements IF_Event{
        NetDevice from;
        NetDevice netDevice;
        Packet packet;
        SubChannel subChannel;

        public NetDeviceReceiveBegin(NetDevice from, NetDevice netDevice, SubChannel subChannel, Packet packet){
            this.from = from;
            this.netDevice = netDevice;
            this.packet = packet;
            this.subChannel = subChannel;
        }
        @Override
        public void run(){



            switch (netDevice.state){
                case IDLE:
                    netDevice.numReceiving++;
                    netDevice.isCollision = false;
                    break;
                case RECEVING:
                    netDevice.numReceiving++;
                    netDevice.isCollision = true;
                    break;
                case TRANSMISSION:
                    SubChannelSelfDe selfDe = new SubChannelSelfDe(netDevice, from);
                    double t = subChannel.getTimeTrans(packet);
                    Simulator.addEvent(t, selfDe);
                    String str = "";
                    str += "NetDevice("+netDevice.getUid()+")["+netDevice.state+"],不能收[";
                    str += packet.getPacketType()+"("+packet.getUid()+")]";
                    Hprint.printlntDebugInfo(netDevice, str);
                    return;
                default:
                    break;
            }
            netDevice.state = StateNetDevice.RECEVING;
            netDevice.tSubChannelOccupy ++;
            NetDeviceReceiveEnd receiveEnd = new NetDeviceReceiveEnd(from, netDevice, subChannel, packet);
            double trans = subChannel.getTimeTrans(packet);
            Simulator.addEvent(trans, receiveEnd);
        }
    }
    //结束接收
    class NetDeviceReceiveEnd implements IF_Event{
        NetDevice from;
        NetDevice netDevice;
        Packet packet;
        SubChannel subChannel;

        public NetDeviceReceiveEnd(NetDevice from, NetDevice netDevice, SubChannel subChannel, Packet packet){
            this.from = from;
            this.netDevice = netDevice;
            this.subChannel = subChannel;
            this.packet = packet;
        }
        @Override
        public void run(){
            if (netDevice.isCollision){
                netDevice.collidePacket(packet);
            }
            else{
                String str = "";
                str += "NetDevice("+netDevice.getUid()+")收到一个[";
                str += packet.getPacketType()+"("+packet.getUid()+")] from ";
                str += "NetDevice("+ from.getUid()+")";
                Hprint.printlntDebugInfo(netDevice, str);
                switch (packet.getPacketType()){
                    case PACKET:
                        break;
                    case RTS:
                        sendCTS(0, from);
                        break;
                    case CTS:
                        if (netDevice.isWaitForCTS){
                            if (netDevice.nextSendPacket != null){
                                netDevice.sendPacket(0, from, netDevice.nextSendPacket);
                            }
                            netDevice.isWaitForCTS = false;
                        }
                        break;
                    case OTHER:
                        break;
                    default:
                        break;
                }
            }
            netDevice.numReceiving--;
            netDevice.tSubChannelOccupy--;
            if (netDevice.numReceiving == 0){
                netDevice.state = StateNetDevice.IDLE;
            }
        }
    }
    //信道占用自降
    class SubChannelSelfDe implements IF_Event{
        NetDevice occupyNetDevice;
        NetDevice netDevice;
        public SubChannelSelfDe(NetDevice netDevice, NetDevice occupyNetDevice){
            this.netDevice = netDevice;
            this.occupyNetDevice = occupyNetDevice;
        }
        @Override
        public void run(){
            netDevice.tSubChannelOccupy--;
            String str = "";
            str += "NetDevice("+this.occupyNetDevice.getUid()+")完成占用信道";
            str += "in NetDevice("+netDevice.getUid()+")";
            Hprint.printlntDebugInfo(occupyNetDevice, str);
        }
    }
}