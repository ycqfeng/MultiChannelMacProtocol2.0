package han_simulator;

/**
 * Created by ycqfeng on 2017/1/4.
 */
public class HprintNode {
    private IF_HprintNode instance;

    private boolean isPrintErrorInformation;
    private boolean isPrintDebugInformation;
    private boolean isPrintLogicInformation;

    public HprintNode(IF_HprintNode instance){
        this.instance = instance;
        isPrintErrorInformation = true;
        isPrintDebugInformation = true;
        isPrintLogicInformation = false;
    }

    public IF_HprintNode getInstance(){
        return this.instance;
    }

    public void setPrintALL(boolean state){
        this.isPrintErrorInformation = state;
        this.isPrintDebugInformation = state;
        this.isPrintLogicInformation = state;
    }

    //设置
    public void setPrintErrorInformation(boolean isPrintErrorInformation){
        this.isPrintErrorInformation = isPrintErrorInformation;
    }
    public void setPrintDebugInformation(boolean isPrintDebugInformation){
        this.isPrintDebugInformation = isPrintDebugInformation;
    }
    public void setPrintLogicInformation(boolean isPrintDebugInformation){
        this.isPrintLogicInformation = isPrintDebugInformation;
    }
    //获取
    public boolean isPrintErrorInformation(){
        return this.isPrintErrorInformation;
    }
    public boolean isPrintDebugInformation(){
        return this.isPrintDebugInformation;
    }
    public boolean isPrintLogicInformation() {
        return this.isPrintLogicInformation;
    }
}
