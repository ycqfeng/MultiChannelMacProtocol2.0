package han_simulator;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class Hprint {
    public static Hprint hprint;

    private HprintNode[] nodes;

    //设置
    public static void setPrintErrorInformation(IF_HprintNode node, boolean state){
        for (int i = 0 ; i < hprint.nodes.length ; i++){
            if (hprint.nodes[i].getInstance() == node){
                hprint.nodes[i].setPrintErrorInformation(state);
                return;
            }
        }
    }
    public static void setPrintDebugInformation(IF_HprintNode node, boolean state){
        for (int i = 0 ; i < hprint.nodes.length ; i++){
            if (hprint.nodes[i].getInstance() == node){
                hprint.nodes[i].setPrintDebugInformation(state);
                return;
            }
        }
    }

    //打印带时间
    public static boolean printlntDebugInfo(IF_HprintNode instance, String str){
        HprintNode hprintNode = getHprintNode(instance);
        if (hprintNode == null){
            error_unRegister(instance);
            return false;
        }
        if (hprintNode.isPrintDebugInformation()){
            str = getCurrTime()+str+"--(Debug Info)";
            System.out.print(str);
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean printlntErrorInfo(IF_HprintNode instance, String str){
        HprintNode hprintNode = getHprintNode(instance);
        if (hprintNode == null){
            error_unRegister(instance);
            return false;
        }
        if (hprintNode.isPrintErrorInformation()){
            str = getCurrTime()+str+"--(Error Info)";
            System.out.print(str);
            return true;
        }
        else {
            return false;
        }
    }
    public static void printlnt(String str){
        str = getCurrTime() + str;
        System.out.println(str);
    }
    //打印不带时间
    public static boolean printlnDebugInfo(IF_HprintNode instance, String str){
        HprintNode hprintNode = getHprintNode(instance);
        if (hprintNode == null){
            error_unRegister(instance);
            return false;
        }
        if (hprintNode.isPrintDebugInformation()){
            str +="--(Debug Info)";
            System.out.print(str);
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean printlnErrorInfo(IF_HprintNode instance, String str){
        HprintNode hprintNode = getHprintNode(instance);
        if (hprintNode == null){
            error_unRegister(instance);
            return false;
        }
        if (hprintNode.isPrintErrorInformation()){
            str +="--(Error Info)";
            System.out.print(str);
            return true;
        }
        else {
            return false;
        }
    }
    public static void println(String str){
        System.out.println(str);
    }

    private static void error_unRegister(IF_HprintNode instance){
        String error = "实例"+instance.getClass().getName()+"未注册，无法打印输出。";
        System.out.println(error);
    }

    //获取node
    public static HprintNode getHprintNode(IF_HprintNode instance){
        for (int i = 0 ; i < hprint.nodes.length ; i++){
            if (hprint.nodes[i].getInstance() == instance){
                return hprint.nodes[i];
            }
        }
        return null;
    }

    //注册
    public static void register(IF_HprintNode instance){
        if (hprint == null){
            hprint = new Hprint();
            hprint.nodes = new HprintNode[1];
            hprint.nodes[0] = new HprintNode(instance);
            return;
        }
        else {
            if (getHprintNode(instance) != null){
                return;
            }
            HprintNode[] tNodes = new HprintNode[hprint.nodes.length+1];
            System.arraycopy(hprint.nodes, 0, tNodes, 0, hprint.nodes.length);
            tNodes[hprint.nodes.length] = new HprintNode(instance);
            hprint.nodes = tNodes;
            return;
        }
    }

    //获取当前时间
    public static String getCurrTime(){
        String str = "";
        str += Simulator.getCurTime() + "s, ";
        return str;
    }
}
