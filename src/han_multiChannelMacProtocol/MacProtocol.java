package han_multiChannelMacProtocol;

import han_simulator.*;

/**
 * Created by ycqfeng on 2017/1/6.
 */
public class MacProtocol implements IF_simulator, IF_HprintNode, IF_Channel{
    //静态参数
    private static int uidBase = 0;
    //私有参数
    private int uid;
    private PacketQueue queue;
    private Packet nextSendPacket;
    private double timeDIFS = 0.1;
    private double timeSIFS = 0.1;

    //状态
    MPSubChannel mpSubChannel;
    StateMacProtocol stateMacProtocol;
    MPSendPacket mpSendPacket;
    MPReceivePacket mpReceivePacket;

    //构造函数
    public MacProtocol(){
        this.uid = uidBase++;
        this.mpSubChannel = new MPSubChannel(this);
        this.queue = new PacketQueue();
        this.mpSendPacket = new MPSendPacket(this);
        this.mpReceivePacket = new MPReceivePacket(this);
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
        subChannel.registerMac(this);
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
    //临时接口，需要删除
    public void sendPacket(int destinationUid, Packet packet){
        packet.setSourceUid(this.getUid());
        packet.setDestinationUid(destinationUid);
        mpSendPacket.addNewPacketToSend(packet);
    }
    public void sendCTS(int sourceUid, int destinationUid){
        mpSendPacket.sendCTS(sourceUid, destinationUid, mpSubChannel.getSubChannel());
    }
    public void sendRTS(int sourceUid, int destinationUid){
        mpSendPacket.sendRTS(sourceUid,destinationUid);
    }

    @Override
    public boolean receive(int sourceUid, int destinationUid, SubChannel subChannel, Packet packet) {
        if (sourceUid == this.uid){
            return false;
        }
        if (destinationUid == this.uid){
            this.mpReceivePacket.reveive(subChannel, packet);
        }
        else {
            this.setSubchannelOccupy(packet);
        }
        return false;
    }

    /**
     * 私有类
     */
    //收包
    class MPReceivePacket implements IF_HprintNode{
        private MacProtocol macProtocol;
        public MPReceivePacket(MacProtocol macProtocol){
            Hprint.register(this);
            this.macProtocol = macProtocol;
        }
        String str;
        public void reveive(SubChannel subChannel, Packet packet){
            String str;
            switch (packet.getPacketType()){
                case PACKET:
                    break;
                case RTS:
                    str = getStringUid()+"# ";
                    str += "开始接收RTS，";
                    str += "from ("+packet.getSourceUid()+")";
                    Hprint.printlntDebugInfo(macProtocol, str);
                    MPReceivePacketEnd receiveRTSEnd = new MPReceivePacketEnd(subChannel, packet);
                    Simulator.addEvent(subChannel.getTimeTrans(packet), receiveRTSEnd);
                    break;
                case CTS:
                    System.out.println("收到CTS");
                    MPReceivePacketEnd receiveCTSEnd = new MPReceivePacketEnd(subChannel, packet);
                    Simulator.addEvent(subChannel.getTimeTrans(packet), receiveCTSEnd);
                    break;
                default:
                    break;
            }
        }
        class MPReceivePacketEnd implements IF_Event{
            Packet receivePacket;
            SubChannel subChannel;
            public MPReceivePacketEnd(SubChannel subChannel, Packet receivePacket){
                this.subChannel = subChannel;
                this.receivePacket = receivePacket;
            }
            @Override
            public void run(){
                String str;
                switch (receivePacket.getPacketType()){
                    case RTS:
                        str = getStringUid()+"# ";
                        str += "完成接收RTS，";
                        str += "from ("+receivePacket.getSourceUid()+")";
                        Hprint.printlntDebugInfo(macProtocol, str);
                        mpSendPacket.sendCTS(getUid(), receivePacket.getSourceUid(), mpSubChannel.getSubChannel());
                        break;
                    case CTS:
                        if (receivePacket.sourceUid == mpSendPacket.getWaitForCTSUid()){
                            System.out.println("收到指定CTS，取消RTS重发");
                            mpSendPacket.cancelReTransRTS();
                        }
                        break;
                    default:
                        break;
                }

            }
        }
    }
    //发包
    class MPSendPacket implements IF_HprintNode{
        MacProtocol macProtocol;
        BackOff backOff;
        //DIFS
        DIFS difs;
        SIFS sifs;
        //Packet
        private Packet dataPacket;
        //CTS参数
        private int lengthCTS;//CTS包Bit数目
        //RTS参数
        private int lengthRTS;//RTS包Bit数目
        private int reTryRTS;//RTS重试次数
        private int reTryRTSLimit;//RTS重试次数限制
        private double timerReTransRTS;//RTS重发计时；
        private int reTransRTStime;//RTS重发次数
        private int reTransRTStimeLimit;//RTS重发次数限制
        private int reTransRTSEventUid;//RTS重发事件
        private int waitForCTSUid;//等待的CTS的Uid
        private int backoffTime;//RTS退避次数
        private int backoffTimeLimit;//RTS退避次数限制
        public MPSendPacket(MacProtocol macProtocol){
            Hprint.register(this);
            this.backOff = new BackOff(0.1);
            this.macProtocol = macProtocol;
            this.lengthCTS = 20*8;
            this.lengthRTS = 20*8;
            this.reTryRTS = 0;
            this.reTryRTSLimit = 10;
            this.timerReTransRTS = 1;
            this.reTransRTStime = 0;
            this.reTransRTStimeLimit = 3;
            this.reTransRTSEventUid = -1;
            this.waitForCTSUid = -1;
            this.backoffTime = 0;
            this.backoffTimeLimit = 16;
        }
        //添加一个数据包进行发送
        public boolean addNewPacketToSend(Packet packet){
            if (this.dataPacket != null){
                Hprint.printlnt("上一个数据包未发完，不能添加");
                return false;
            }
            this.dataPacket = packet;
            this.sendRTS(packet.sourceUid, packet.destinationUid);
            String str = getStringUid()+"# ";
            str += "准备发送"+packet.getStringUid();
            Hprint.printlnt(str);
            return true;
        }
        //重发次数上限，丢弃数据包
        private void dropDataPacket(){
            this.dataPacket = null;
        }
        //获取waitForCTSUid
        public int getWaitForCTSUid(){
            return this.waitForCTSUid;
        }
        //取消RTS重传
        public void cancelReTransRTS(){
            if (this.reTransRTSEventUid > 0){
                Simulator.deleteEvent(reTransRTSEventUid);
                this.reTransRTSEventUid = -1;
            }
        }

        //发送CTS
        public void sendCTS(int sourceUid, int destinationUid, SubChannel subChannel){
            Packet cts = new Packet(lengthCTS, PacketType.CTS);
            cts.setSourceUid(sourceUid);
            cts.setDestinationUid(destinationUid);
            SIFS sifsInstance = new SIFS(timeSIFS,
                    new IF_Event() {//发送接口
                        @Override
                        public void run() {
                            //subChannel.send(sourceUid, destinationUid, cts);
                            String str = getStringUid()+"# ";
                            str += "SIFS成功，马上发送CTS";
                            Hprint.printlntDebugInfo(macProtocol, str);

                            MPSendPacketBegin sendPacketBegin = new MPSendPacketBegin(sourceUid,
                                    destinationUid,
                                    subChannel,
                                    cts,
                                    null);
                            Simulator.addEvent(0, sendPacketBegin);

                            deleteSifs();

                        }
                    });
            addSifs(sifsInstance);

        }
        //发送RTS
        public void sendRTS(int sourceUid, int destinationUid){
            if (mpSubChannel.isOccupy()){//信道占用则退避
                if (this.backoffTime++ < this.backoffTimeLimit){
                    String str = getStringUid()+"# ";
                    str += "发送RTS时信道被占用，执行第"+this.backoffTime+"次退避";
                    Hprint.printlntDebugInfo(this, str);
                    Simulator.addEvent(backOff.getBackOffTime(),
                            new IF_Event() {
                                @Override
                                public void run() {
                                    sendRTS(sourceUid, destinationUid);
                                }
                            });
                }
                else{
                    String str = getStringUid()+"# ";
                    str += "达到RTS退避上限，丢弃数据包";
                    dropDataPacket();//丢弃正在发送的数据包
                    Hprint.printlntDebugInfo(this, str);
                    this.backoffTime = 0;
                }
                return;
            }
            stateMacProtocol = StateMacProtocol.TRANSMISSION;//信道转换为传输
            PacketRTS rts = new PacketRTS(lengthRTS, dataPacket);
            rts.setSourceUid(sourceUid);
            rts.setDestinationUid(destinationUid);
            DIFS difsInstance = new DIFS(timeDIFS,
                    new IF_Event() {//发送接口
                        @Override
                        public void run() {
                            String str = getStringUid()+"# ";
                            str += "DIFS成功，立即发送[RTS]";
                            Hprint.printlntDebugInfo(macProtocol, str);

                            //设置重传监视
                            IF_Event reTrans;
                            if (reTransRTStime++ < reTransRTStimeLimit){
                                //到时间需要重传
                                reTrans = new IF_Event() {
                                    @Override
                                    public void run() {
                                        reTransRTSEventUid = Simulator.addEvent(timerReTransRTS,
                                                new IF_Event() {
                                                    @Override
                                                    public void run() {
                                                        String str = getStringUid();
                                                        str += "启动第"+reTransRTStime+"次RTS重传";
                                                        Hprint.printlntDebugInfo(mpSendPacket, str);
                                                        sendRTS(sourceUid, destinationUid);
                                                    }
                                                });
                                        waitForCTSUid = destinationUid;

                                    }
                                };
                            }
                            else{
                                //到时间不用重传，但是需要善后
                                reTrans = new IF_Event() {
                                    @Override
                                    public void run() {
                                        Simulator.addEvent(timerReTransRTS,
                                                new IF_Event() {
                                                    @Override
                                                    public void run() {
                                                        waitForCTSUid = -1;
                                                        reTransRTStime = 0;
                                                    }
                                                });
                                    }
                                };
                            }
                            //发送RTS包
                            MPSendPacketBegin sendPacketBegin = new MPSendPacketBegin(sourceUid,
                                    destinationUid,
                                    getSubChannel(),
                                    rts,
                                    reTrans);
                            Simulator.addEvent(0, sendPacketBegin);

                            /**
                             * 等待CTS
                             */
                            stateMacProtocol = StateMacProtocol.IDLE;
                        }
                    },
                    new IF_Event() {//中断接口
                        @Override
                        public void run() {
                            if (reTryRTS++ < reTryRTSLimit){
                                String str = getStringUid()+"# ";
                                str += "DIFS未成功,第"+reTryRTS+"次重试";
                                Hprint.printlntDebugInfo(macProtocol, str);
                                sendRTS(sourceUid, destinationUid);
                            }
                            else{
                                reTryRTS = 0;
                                String str = getStringUid()+"# ";
                                str += "DIFS连续"+reTryRTSLimit+"次失败，发送RTS失败，丢弃正在发送的数据包";
                                dropDataPacket();
                                Hprint.printlntDebugInfo(macProtocol, str);
                                deleteDifs();
                            }
                            stateMacProtocol = StateMacProtocol.IDLE;//结束传输状态
                        }
                    }
            );
            addDifs(difsInstance);
        }
        //打断DIFS
        public void distrubDIFS(){
            if (this.difs != null){
                this.difs.disturb();
            }
        }
        //添加/删除SIFS
        public void addSifs(SIFS sifs){
            this.sifs = sifs;
        }
        public void deleteSifs(){
            this.sifs = null;
        }
        //添加/删除DIFS
        public void addDifs(DIFS difs){
            this.difs = difs;
        }
        public void deleteDifs(){
            this.difs = null;
        }
        //发送开始
        class MPSendPacketBegin implements IF_Event{
            int sourceUid;
            int destinationUid;
            SubChannel subChannel;
            Packet packet;
            IF_Event finishEvent;
            public MPSendPacketBegin(int sourceUid, int destinationUid, SubChannel subChannel, Packet packet,IF_Event finishEvent){
                this.sourceUid = sourceUid;
                this.destinationUid = destinationUid;
                this.subChannel = subChannel;
                this.packet = packet;
                this.finishEvent = finishEvent;
            }
            @Override
            public void run(){
                stateMacProtocol = StateMacProtocol.TRANSMISSION;
                String str = "";
                str += macProtocol.getStringUid()+"# ";
                str += "开始传输["+packet.getPacketType()+"]";
                Hprint.printlntDebugInfo(macProtocol, str);
                double t = this.subChannel.send(this.sourceUid, this.destinationUid, this.packet);
                MPSendPacketEnd sendPacketEnd = new MPSendPacketEnd(packet, this.finishEvent);
                Simulator.addEvent(t, sendPacketEnd);
            }
        }
        //发送结束
        class MPSendPacketEnd implements IF_Event{
            Packet packet;
            IF_Event finishEvent;
            public MPSendPacketEnd(Packet packet, IF_Event finishEvent){
                this.packet = packet;
                this.finishEvent = finishEvent;
            }
            @Override
            public void run(){
                String str = "";
                str += macProtocol.getStringUid()+"# ";
                str += "完成传输["+packet.getPacketType()+"]";
                Hprint.printlntDebugInfo(macProtocol, str);
                stateMacProtocol = StateMacProtocol.IDLE;
                if (this.finishEvent != null){
                    Simulator.addEvent(0, finishEvent);
                }
            }

        }
    }
    //DIFS和SIFS
    class SIFS{
        IF_Event sendInterface;
        double timeSIFS;
        public SIFS(double timeSIFS, IF_Event sendInterface){
            this.timeSIFS = timeSIFS;
            this.sendInterface = sendInterface;
            SIFSBegin sifsBegin = new SIFSBegin();
            Simulator.addEvent(0, sifsBegin);
        }
        class SIFSBegin implements IF_Event{
            @Override
            public void run(){
                String str = getStringUid()+"# ";
                str += "SIFS开始";
                Hprint.printlntDebugInfo(mpSendPacket, str);
                SIFSEnd sifsEnd = new SIFSEnd();
                Simulator.addEvent(timeSIFS, sifsEnd);
            }
        }
        class SIFSEnd implements IF_Event{
            @Override
            public void run(){
                String str = getStringUid()+"# ";
                str += "SIFS结束";
                Hprint.printlntDebugInfo(mpSendPacket, str);
                Simulator.addEvent(0,sendInterface);
            }
        }
    }
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
        public void disturb(){
            this.isDisturb = true;
        }
        class DIFSBegin implements IF_Event{
            @Override
            public void run(){
                String str = getStringUid()+"# ";
                str += "DIFS开始";
                Hprint.printlntDebugInfo(mpSendPacket, str);
                DIFSEnd difsEnd = new DIFSEnd();
                Simulator.addEvent(timeDIFS, difsEnd);
            }
        }
        class DIFSEnd implements IF_Event{
            @Override
            public void run(){
                String str = getStringUid()+"# ";
                str += "DIFS结束";
                Hprint.printlntDebugInfo(mpSendPacket, str);
                if (isDisturb){
                    Simulator.addEvent(0, disturbInterface);
                }
                else {
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
            mpSendPacket.distrubDIFS();//打断DIFS
            numOccupy += 1;
            //添加结束
            double dTime = 0;
            switch (packet.getPacketType()){
                case RTS:
                    PacketRTS packetRTS = (PacketRTS)packet;
                    dTime = this.subChannel.getTimeTrans(packetRTS.getLengthData());
                    /**
                     * 此处时间不准确，应该由rts接收后进行判断需要退避多久。
                     */
                    break;
                default:
                    break;
            }
            Simulator.addEvent(dTime,
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
