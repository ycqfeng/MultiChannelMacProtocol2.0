package com.company;

import han_multiChannelMacProtocol.NetDevice;
import han_multiChannelMacProtocol.Packet;
import han_multiChannelMacProtocol.SubChannel;
import han_simulator.Simulator;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Simulator.init();
        Simulator.setStopTime(100);

        NetDevice netDevice1 = new NetDevice();
        NetDevice netDevice2 = new NetDevice();
        NetDevice netDevice3 = new NetDevice();
        SubChannel subChannel = new SubChannel();

        netDevice1.settSubchannel(subChannel);
        netDevice2.settSubchannel(subChannel);
        netDevice3.settSubchannel(subChannel);

        Packet packet = new Packet(2000);
        Packet packet1 = new Packet(2000);
        packet.setToNetDevice(netDevice2);
        packet1.setToNetDevice(netDevice1);

        netDevice1.enQueue(packet);
        netDevice2.enQueue(packet1);

        Simulator.start();
    }
}
