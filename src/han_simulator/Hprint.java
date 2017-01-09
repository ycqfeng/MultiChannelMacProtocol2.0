package han_simulator;


import han_multiChannelMacProtocol.TimeUnit;
import han_multiChannelMacProtocol.TimeUnitValue;

import java.text.DecimalFormat;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class Hprint {
    public static Hprint hprint;

    private HprintNode[] nodes;
    private static DecimalFormat timeFormatilize = new DecimalFormat("0.000000000");

    //设置时间显示精度
    public static void setTimeResolution(TimeUnit timeResolution){
        switch (timeResolution){
            case s:
                timeFormatilize.applyPattern("0");
                break;
            case ms:
                timeFormatilize.applyPattern("0.000");
                break;
            case us:
                timeFormatilize.applyPattern("0.000000");
                break;
            case ns:
                timeFormatilize.applyPattern("0.000000000");
                break;
            default:
                break;
        }
    }
    //设置
    public static void setALLClose(){
        for (int i = 0 ; i < hprint.nodes.length ; i++){
            hprint.nodes[i].setPrintALL(false);
        }
    }
    public static void setPrintAllInformation(IF_HprintNode node, boolean state){
        for (int i = 0 ; i < hprint.nodes.length ; i++){
            if (hprint.nodes[i].getInstance() == node){
                hprint.nodes[i].setPrintALL(state);
                return;
            }
        }
    }
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
    public static void setPrintLogicInformation(IF_HprintNode node, boolean state){
        for (int i = 0 ; i < hprint.nodes.length ; i++){
            if (hprint.nodes[i].getInstance() == node){
                hprint.nodes[i].setPrintLogicInformation(state);
                return;
            }
        }
    }

    //打印带时间
    public static boolean printlntLogicInfo(IF_HprintNode instance, String str){
        HprintNode hprintNode = getHprintNode(instance);
        if (hprintNode == null){
            error_unRegister(instance);
            return false;
        }
        if (hprintNode.isPrintLogicInformation()){
            str = getCurrTime()+str+"--(Logic Info)";
            System.out.println(str);
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean printlntDebugInfo(IF_HprintNode instance, String str){
        HprintNode hprintNode = getHprintNode(instance);
        if (hprintNode == null){
            error_unRegister(instance);
            return false;
        }
        if (hprintNode.isPrintDebugInformation()){
            str = getCurrTime()+str+"--(Debug Info)";
            System.out.println(str);
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
            System.out.println(str);
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
    public static boolean printlnLogicInfo(IF_HprintNode instance, String str){
        HprintNode hprintNode = getHprintNode(instance);
        if (hprintNode == null){
            error_unRegister(instance);
            return false;
        }
        if (hprintNode.isPrintLogicInformation()){
            str += "--(Logic Info)";
            System.out.println(str);
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean printlnDebugInfo(IF_HprintNode instance, String str){
        HprintNode hprintNode = getHprintNode(instance);
        if (hprintNode == null){
            error_unRegister(instance);
            return false;
        }
        if (hprintNode.isPrintDebugInformation()){
            str +="--(Debug Info)";
            System.out.println(str);
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
            System.out.println(str);
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
        str += hprint.timeFormatilize.format(Simulator.getCurTime())+"s, ";
        return str;
    }
}
