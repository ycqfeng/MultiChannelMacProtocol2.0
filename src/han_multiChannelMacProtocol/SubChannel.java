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

    IF_Channel[] devices;

    public SubChannel(){
        Simulator.register(this);
        Hprint.register(this);
        this.bps = 1000;
        this.delay = 0.1;
        random = new Random();
    }

    public void register(IF_Channel device){
        if (this.devices == null){
            this.devices = new IF_Channel[1];
            this.devices[0] = device;
        }
        else {
            IF_Channel[] temp = new IF_Channel[this.devices.length+1];
            System.arraycopy(this.devices, 0, temp, 0, this.devices.length);
            temp[this.devices.length] = device;
            this.devices = temp;
        }
    }

    public void setBps(double bps){
        this.bps = bps;
    }

    public double getTimeTrans(Packet packet){
        return packet.getLength()/this.bps;
    }

    public double send(NetDevice from, NetDevice to, Packet packet){
        double trans = getTimeTrans(packet);
        for (int i = 0 ; i < this.devices.length ; i++){
            if (this.devices[i] != from){
                SendToNetDevice toNetDevice = new SendToNetDevice(from, this.devices[i], this, packet);
                Simulator.addEvent(delay+0.1*random.nextDouble(), toNetDevice);
            }
        }
        return trans;
    }
    class SendToNetDevice implements IF_Event{
        NetDevice from;
        NetDevice to;
        Packet packet;
        SubChannel subChannel;

        public SendToNetDevice(IF_Channel from, IF_Channel to, SubChannel subChannel, Packet packet){
            this.from = (NetDevice)from;
            this.to = (NetDevice)to;
            this.subChannel = subChannel;
            this.packet = packet;
        }
        @Override
        public void run(){
            this.to.receive(from, to, subChannel, packet);
        }
    }
}
