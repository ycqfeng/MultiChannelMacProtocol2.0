package han_multiChannelMacProtocol;

import han_simulator.*;

/**
 * Created by ycqfeng on 2017/1/6.
 */
public class MacProtocol implements IF_simulator, IF_HprintNode{
    //静态参数
    private static int uidBase = 0;
    //私有参数
    private int uid;
    private PacketQueue queue;
    private Packet nextSendPacket;
    private double DIFS = 0.1;
    private double SIFS = 0.1;

    //状态
    MPSubChannel mpSubChannel;
    StateMacProtocol stateMacProtocol;
    MPSendPacket mpSendPacket;

    //构造函数
    public MacProtocol(){
        this.uid = uidBase++;
        this.mpSubChannel = new MPSubChannel(this);
        this.queue = new PacketQueue();
        this.mpSendPacket = new MPSendPacket();
        this.stateMacProtocol = StateMacProtocol.IDLE;
    }
    //添加Packet到队列
    public boolean enQueue(Packet packet){
        String str = getStringUid()+"# ";
        if (this.queue.pushPacket(packet)){
            str += packet.getStringUid()+"加入队列成功";
            Hprint.printlntDebugInfo(this,str);
            if (this.nextSendPacket == null){
                nextSendPacket = this.queue.popPacket();
            }
            return true;
        }
        else{
            str += packet.getStringUid()+"加入队列失败";
            Hprint.printlntDebugInfo(this,str);
            return false;
        }

    }
    //设置信道
    public void setSubChannel(SubChannel subChannel){
        this.mpSubChannel.setSubChannel(subChannel);
    }
    //设置信道占用
    public void setSubchannelOccupy(Packet packet){
        this.mpSubChannel.addOccupy(packet);
    }
    //获取信道
    public SubChannel getSubChannel(){
        return mpSubChannel.getSubChannel();
    }
    //获取信道占用状态
    public boolean getSubChannelOccupy(){
        return this.mpSubChannel.isOccupy();
    }
    //获取Uid
    public int getUid(){
        return uid;
    }
    //获取字符串
    public String getStringUid(){
        return "MacProtocol("+uid+")";
    }
    public void sendRTS(){
        mpSendPacket.sendRTS(0,1);
    }
    /**
     * 私有类
     */
    //协商
    class MPSendPacket{
        //DIFS
        DIFS difs;
        //RTS阐述
        private int lengthRTS;
        private double timeRTS;
        private int reTryRTS;
        private int reTryRTSLimit;
        public MPSendPacket(){
            this.lengthRTS = 20*8;
            this.timeRTS = 1;
            this.reTryRTS = 0;
            this.reTryRTSLimit = 3;
        }
        public void sendRTS(int sourceUid, int destinationUid){
            Packet rts = new Packet(lengthRTS,PacketType.RTS);
            rts.setSourceUid(sourceUid);
            rts.setDestinationUid(destinationUid);
            DIFS difs = new DIFS(timeRTS,
                    new IF_Event() {//发送接口
                        @Override
                        public void run() {
                            Hprint.printlnt("发送数据包");

                        }
                    },
                    new IF_Event() {//中断接口
                        @Override
                        public void run() {
                            if (reTryRTS++ < reTryRTSLimit){
                                sendRTS(sourceUid, destinationUid);
                                Hprint.printlnt("重发RTS");
                            }
                            else{
                                reTryRTS = 0;
                                Hprint.printlnt("发送RTS失败");
                            }
                        }
                    }
            );
        }
        //添加/删除DIFS
        public void addIfs(DIFS ifs){
            this.difs = ifs;
        }
        public void deleteIfs(){
            this.difs = null;
        }
    }
    //DIFS和SIFS
    class DIFS{
        IF_Event sendInterface;
        IF_Event disturbInterface;
        double timeDIFS;
        boolean isDisturb;
        public DIFS(double timeDIFS, IF_Event sendInterface, IF_Event disturbInterface){
            this.timeDIFS = timeDIFS;
            this.sendInterface = sendInterface;
            this.disturbInterface = disturbInterface;
            this.isDisturb = false;
            Simulator.addEvent(0,new DIFSBegin());
        }
        class DIFSBegin implements IF_Event{
            @Override
            public void run(){
                DIFSEnd difsEnd = new DIFSEnd();
                Simulator.addEvent(timeDIFS, difsEnd);
            }
        }
        class DIFSEnd implements IF_Event{
            @Override
            public void run(){
                if (isDisturb){
                    Simulator.addEvent(0, disturbInterface);
                }
                else {
                    mpSendPacket.deleteIfs();
                    Simulator.addEvent(0, sendInterface);
                }
            }
        }
    }
    //信道
    class MPSubChannel{
        private MacProtocol macProtocol;
        private SubChannel subChannel;//信道
        private boolean isOccupy;
        private int numOccupy;

        public MPSubChannel(MacProtocol macProtocol){
            this.macProtocol = macProtocol;
        }
        //获取信道
        public SubChannel getSubChannel(){
            return subChannel;
        }
        //添加信道占用
        public int addOccupy(Packet packet){
            String str = "";
            str += macProtocol.getStringUid()+"# ";
            str += this.subChannel.getStringUid()+"开始被占用";
            Hprint.printlntDebugInfo(macProtocol, str);
            this.isOccupy = true;
            numOccupy += 1;
            //添加结束
            Simulator.addEvent(this.subChannel.getTimeTrans(packet),
                    new IF_Event() {
                        @Override
                        public void run() {
                            numOccupy--;
                            if (numOccupy == 0){
                                String str = "";
                                str += macProtocol.getStringUid()+"# ";
                                str += subChannel.getStringUid()+"结束被占用";
                                Hprint.printlntDebugInfo(macProtocol, str);
                                isOccupy = false;
                            }
                        }
                    });
            return this.numOccupy;
        }
        public void setSubChannel(SubChannel subChannel){
            this.subChannel = subChannel;
            this.isOccupy = false;
            this.numOccupy = 0;
        }
        public boolean isOccupy(){
            return isOccupy;
        }
    }
}
