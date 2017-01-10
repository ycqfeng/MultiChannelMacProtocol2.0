package han_multiChannelMacProtocol;

import han_simulator.*;

import java.util.Random;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class SubChannel implements IF_simulator, IF_HprintNode{
    private static int uidBase = 0;

    private int uid;

    double bps;
    double delay;
    Random random;

    //IF_Channel[] devices;
    IF_Channel[] macProtocols;

    public SubChannel(){
        this.uid = uidBase++;
        Simulator.register(this);
        Hprint.register(this);
        this.bps = 1000;
        this.delay = 0.1;
        random = new Random();
    }

    public int getUid(){
        return uid;
    }

    public String getStringUid(){
        return "SubChannel("+uid+")";
    }

    public void registerMac(IF_Channel macProtocol){
        if (this.macProtocols == null){
            this.macProtocols = new IF_Channel[1];
            this.macProtocols[0] = macProtocol;
        }
        else{
            IF_Channel[] temp = new IF_Channel[this.macProtocols.length+1];
            System.arraycopy(this.macProtocols, 0, temp, 0, this.macProtocols.length);
            temp[this.macProtocols.length] = macProtocol;
            this.macProtocols = temp;
        }
    }

    /*public void register(IF_Channel device){
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
    }*/

    public void setBps(double bps){
        this.bps = bps;
    }

    public double getTimeTrans(int lengthPacket){
        return lengthPacket/this.bps;
    }
    public double getTimeTrans(Packet packet){
        return packet.getLength()/this.bps;
    }

    public double send(int sourceUid, int destinationUid, Packet packet){
        double trans = getTimeTrans(packet);
        SubChannel subChannel = this;
        for (int i = 0 ; i < this.macProtocols.length ; i++){
            IF_Channel desMac = this.macProtocols[i];
            Simulator.addEvent(delay, new IF_Event() {
                @Override
                public void run() {
                    desMac.receive(sourceUid,destinationUid,subChannel, packet);
                }
            });
        }
        return trans;
    }
    /*public double send(NetDevice from, NetDevice to, Packet packet){
        double trans = getTimeTrans(packet);
        for (int i = 0 ; i < this.devices.length ; i++){
            if (this.devices[i] != from){
                SendToNetDevice toNetDevice = new SendToNetDevice(from, this.devices[i], this, packet);
                Simulator.addEvent(delay+0.04*random.nextDouble(), toNetDevice);
            }
        }
        return trans;
    }*/
    /*class SendToNetDevice implements IF_Event{
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
    }*/
}
