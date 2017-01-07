package com.company;

import han_multiChannelMacProtocol.*;
import han_simulator.Hprint;
import han_simulator.IF_Event;
import han_simulator.Simulator;


public class Main {

    public static void main(String[] args) {
	// write your code here
        Simulator.init();
        Simulator.setStopTime(100);
        Statistics.setSumTime(100);

        SubChannel subChannel = new SubChannel();
        Simulator.register(subChannel);

        MacProtocol[] macProtocolsSource = new MacProtocol[100];
        for (int i = 0 ; i < macProtocolsSource.length ; i++){
            macProtocolsSource[i] = new MacProtocol();
            Simulator.register(macProtocolsSource[i]);
            Hprint.register(macProtocolsSource[i]);
            macProtocolsSource[i].setSubChannel(subChannel);
        }
        MacProtocol macProtocoldestination = new MacProtocol();
        Simulator.register(macProtocoldestination);
        Hprint.register(macProtocoldestination);
        macProtocoldestination.setSubChannel(subChannel);

        double sumBPS = 5000000;
        int length = 10000;
        double t = length/sumBPS;


        class AddEvent implements IF_Event{
            @Override
            public void run(){
                for (int i = 0 ; i < macProtocolsSource.length ; i++){
                    Packet packet = new Packet(length);
                    packet.setDestinationUid(macProtocoldestination.getUid());
                    macProtocolsSource[i].enQueue(packet);
                }
                Simulator.addEvent(t,new AddEvent());
                //System.out.println(Simulator.getCurTime()+" 加入1");
            }
        }
        Simulator.addEvent(t, new AddEvent());

        Hprint.setALLClose();

        Simulator.start();
        Statistics.print();
        System.out.println(length/t/1000 + "Kbps");




        /*Simulator.init();
        Simulator.setStopTime(100);

        NetDevice netDevice1 = new NetDevice();
        NetDevice netDevice2 = new NetDevice();
        NetDevice netDevice3 = new NetDevice();
        SubChannel subChannel = new SubChannel();

        subChannel.register(netDevice1);
        subChannel.register(netDevice2);
        subChannel.register(netDevice3);

        netDevice1.settSubchannel(subChannel);
        netDevice2.settSubchannel(subChannel);
        netDevice3.settSubchannel(subChannel);

        Packet packet = new Packet(2000);
        Packet packet1 = new Packet(2000);
        packet.setToNetDevice(netDevice2);
        packet1.setToNetDevice(netDevice2);

        class AB implements IF_Event{
            NetDevice netDevice;
            Packet packet;
            public AB(NetDevice netDevice, Packet packet){
                this.netDevice = netDevice;
                this.packet = packet;
            }
            public void run(){
                netDevice.enQueue(packet);
            }
        }

        AB ab1 = new AB(netDevice1,packet);
        AB ab2 = new AB(netDevice3,packet1);

        Simulator.addEvent(0,ab1);
        Simulator.addEvent(0.2, ab2);

        Simulator.start();*/
    }
}
