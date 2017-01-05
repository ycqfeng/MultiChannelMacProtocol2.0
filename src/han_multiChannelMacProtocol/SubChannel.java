package han_multiChannelMacProtocol;

import han_simulator.*;

import java.util.Random;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class SubChannel implements IF_simulator, IF_HprintNode{
    /**
     * 信道需要修改，信道没有存储设备的信息，所以设备的发送只能传送到目标设备，其他设备检测不到。
     */

    double bps;
    double delay;
    Random random;

    public SubChannel(){
        Simulator.register(this);
        Hprint.register(this);
        this.bps = 1000;
        this.delay = 1;
        random = new Random();
    }

    public void setBps(double bps){
        this.bps = bps;
    }

    public double getTimeTrans(Packet packet){
        return packet.getLength()/this.bps;
    }

    public double send(NetDevice from, NetDevice to, Packet packet){
        double trans = getTimeTrans(packet);
        SendToNetDevice toNetDevice = new SendToNetDevice(from, to, this, packet);
        Simulator.addEvent(delay*random.nextDouble(), toNetDevice);
        return trans;
    }
    class SendToNetDevice implements IF_Event{
        NetDevice from;
        NetDevice to;
        Packet packet;
        SubChannel subChannel;

        public SendToNetDevice(NetDevice from, NetDevice to, SubChannel subChannel, Packet packet){
            this.from = from;
            this.to = to;
            this.subChannel = subChannel;
            this.packet = packet;
        }
        @Override
        public void run(){
            this.to.receive(from, to, subChannel, packet);
        }
    }
}
