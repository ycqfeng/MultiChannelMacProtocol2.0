package han_multiChannelMacProtocol;

import han_simulator.*;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class SubChannel implements IF_simulator, IF_HprintNode{
    double bps;
    double delay;

    public SubChannel(){
        Simulator.register(this);
        Hprint.register(this);
        this.bps = 1000;
        this.delay = 0.0001;
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
        Simulator.addEvent(delay, toNetDevice);
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
