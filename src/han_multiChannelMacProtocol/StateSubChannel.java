package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/8.
 */
public enum StateSubChannel {
    IDLE,//信道空闲，可以接收、发送
    RECEIVING,//正在接收数据包，如果有其他数据包达到，不论是否目标是自己，都会发生碰撞
    SENDING,//正在发生，会错过发送给自己的数据包
    NAV//避让，收到RTS后会根据需求避让开一段时间
}
class hantongzhou{
    StateSubChannel stateTransmitter;//发射机状态
    StateSubChannel stateReceiver;//接收机状态
    double numReceiving;//正在接收的包数目
    boolean isReceiveCollision;//接收碰撞

    public hantongzhou(){
        this.stateTransmitter = StateSubChannel.IDLE;
        this.stateReceiver = StateSubChannel.RECEIVING;
        this.numReceiving = 0;
        this.isReceiveCollision = false;
    }

    public boolean turnToNAV(double duration){
        if (this.stateTransmitter == StateSubChannel.IDLE){
            this.stateTransmitter = StateSubChannel.NAV;
        }
        return true;
    }

}
