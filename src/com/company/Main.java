package com.company;

import han_multiChannelMacProtocol.*;
import han_simulator.Hprint;
import han_simulator.Simulator;


public class Main {

    public static void main(String[] args) {
	// write your code here
        Hprint.setTimeResolution(TimeUnit.ns);

        Simulator.init();
        Simulator.setStopTime(10);

        SubChannel subChannel = new SubChannel();
        Simulator.register(subChannel);

        MacProtocol source1 = new MacProtocol();
        MacProtocol source2 = new MacProtocol();
        MacProtocol destination = new MacProtocol();

        Simulator.register(source1);
        Simulator.register(source2);
        Simulator.register(destination);
        Hprint.register(source1);
        Hprint.register(source2);
        Hprint.register(destination);
        source1.setSubChannel(subChannel);
        source2.setSubChannel(subChannel);
        destination.setSubChannel(subChannel);

        //Hprint.setPrintAllInformation(source1, false);
        //Hprint.setPrintAllInformation(destination, false);

        Packet packet1 = new Packet(100, PacketType.PACKET);
        packet1.setDestinationUid(destination.getUid());
        source1.enQueue(packet1);

        Packet packet2 = new Packet(100, PacketType.PACKET);
        packet2.setDestinationUid(destination.getUid());
        source2.enQueue(packet2);




        Simulator.start();
    }
}
