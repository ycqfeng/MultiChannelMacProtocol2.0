package com.company;

import han_multiChannelMacProtocol.NetDevice;
import han_multiChannelMacProtocol.SubChannel;
import han_simulator.Simulator;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Simulator.init();
        Simulator.setStopTime(100);

        NetDevice netDevice1 = new NetDevice();
        NetDevice netDevice2 = new NetDevice();
        SubChannel subChannel = new SubChannel();

        netDevice1.settSubchannel(subChannel);
        netDevice2.settSubchannel(subChannel);

        netDevice1.sendRTS(netDevice2);

        Simulator.start();
    }
}
