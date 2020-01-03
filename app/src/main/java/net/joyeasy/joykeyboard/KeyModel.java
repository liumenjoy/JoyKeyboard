package net.joyeasy.joykeyboard;

public class KeyModel {

    private int code;
    private String lable;

    public KeyModel(int code, String lable) {
        this.code = code;
        this.lable = lable;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

}
