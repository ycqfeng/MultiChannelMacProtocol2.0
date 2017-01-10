package han_multiChannelMacProtocol;


/**
 * Created by ycqfeng on 2017/1/4.
 */
public interface IF_Channel {
    //default boolean receive(NetDevice from, NetDevice to, SubChannel subChannel, Packet packet){return false;}
    default boolean receive(int sourceUid, int destinationUid, SubChannel subChannel, Packet packet){return false;}
}
