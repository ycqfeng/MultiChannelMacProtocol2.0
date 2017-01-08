package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/5.
 */
public class PacketQueue {
    private Node queue;
    private Node end;
    private int length;

    //获取队列情况
    public String getStringPacketQueueState(){
        String str = "Queue[";
        str += this.length+"Packet]";
        return str;
    }
    //弹出第一个Packet
    public Packet popPacket(){
        if (this.queue == null){
            return null;
        }
        else{
            Node pop = this.queue;
            if (pop == this.end){
                this.end = null;
            }
            this.queue = pop.next;
            this.length--;
            return pop.getPacket();
        }
    }
    //是否为空
    public boolean isEmpty(){
        return this.queue == null;
    }
    //推入一个Packet到队列
    public boolean pushPacket(Packet packet){
        if (length >100){
            return false;
        }
        if (this.queue == null){
            this.queue = new Node(packet);
            this.end = queue;
            this.length++;
            return true;
        }
        else {
            this.end.next = new Node(packet);
            this.end = this.end.next;
            this.length++;
            return true;
        }
    }

    class Node{
        private Packet packet;
        private Node next;
        public Node(Packet packet){
            this.packet = packet;
        }
        public void setNext(Node next){
            this.next = next;
        }
        public Packet getPacket(){
            return packet;
        }
    }
}
