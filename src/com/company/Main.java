package com.company;

import han_multiChannelMacProtocol.*;
import han_simulator.Hprint;
import han_simulator.Simulator;


public class Main {

    public static void main(String[] args) {
	// write your code here
        Hprint.setTimeResolution(TimeUnit.ns);

        Simulator.init();
        Simulator.setStopTime(100);

        SubChannel subChannel = new SubChannel();
        Simulator.register(subChannel);

        MacProtocol[] source = new MacProtocol[5];
        for (int i = 0 ; i < source.length ; i++){
            source[i] = new MacProtocol();
            Simulator.register(source[i]);
            Hprint.register(source[i]);
            source[i].setSubChannel(subChannel);
        }
        MacProtocol destination = new MacProtocol();
        Simulator.register(destination);
        Hprint.register(destination);
        destination.setSubChannel(subChannel);

        int length = 500;
        Packet packet;
        for (int i = 0 ; i < source.length ; i++){
            packet = new Packet(length, PacketType.PACKET);
            packet.setDestinationUid(destination.getUid());
            source[i].enQueue(packet);
        }

        Statistics.setSumTime(100);

        Simulator.start();

        Statistics.print();
    }
}
