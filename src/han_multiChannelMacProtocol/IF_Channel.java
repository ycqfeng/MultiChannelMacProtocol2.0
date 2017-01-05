package han_multiChannelMacProtocol;


/**
 * Created by ycqfeng on 2017/1/4.
 */
public interface IF_Channel {
    boolean receive(NetDevice frome, NetDevice to, SubChannel subChannel, Packet packet);
}
