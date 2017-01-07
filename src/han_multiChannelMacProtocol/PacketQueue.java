package han_multiChannelMacProtocol;

/**
 * Created by ycqfeng on 2017/1/5.
 */
public class PacketQueue {
    private Node queue;
    private Node end;
    private int length;

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

    public boolean isEmpty(){
        return this.queue == null;
    }

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
