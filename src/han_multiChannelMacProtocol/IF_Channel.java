package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public interface IF_Channel {
    boolean receive(SubChannel subChannel, Packet packet);
}
