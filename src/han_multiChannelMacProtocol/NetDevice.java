package han_multiChannelMacProtocol;

import han_simulator.IF_simulator;
import han_simulator.Simulator;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class NetDevice implements IF_simulator{
    StateNetDevice state;
    //构造函数
    public NetDevice(){
        Simulator.register(this);
        this.state = StateNetDevice.IDLE;
    }
}
