package model;

/**
 * @author xianzhi
 * @date Jan 25, 2021
 */
public class InstanceTypeInfo {
    private String instanceType = "";
    private int cores = 0;
    private int memory = 0;

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }
}
