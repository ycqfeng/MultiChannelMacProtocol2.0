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

    //状态
    MPSubChannel mpSubChannel;
    StateMacProtocol stateMacProtocol;

    //构造函数
    public MacProtocol(){
        this.uid = uidBase++;
        this.mpSubChannel = new MPSubChannel(this);
        this.queue = new PacketQueue();
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
    //获取Uid
    public int getUid(){
        return uid;
    }
    //获取字符串
    public String getStringUid(){
        return "MacProtocol("+uid+")";
    }
    /**
     * 私有类
     */
    //信道
    class MPSubChannel{
        private MacProtocol macProtocol;
        private SubChannel subChannel;//信道
        private boolean isOccupy;
        private int numOccupy;

        public MPSubChannel(MacProtocol macProtocol){
            this.macProtocol = macProtocol;
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
