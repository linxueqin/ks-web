package ks.web;

public class CodeMsg {

    private int code;

    private String msg;

    public CodeMsg() {
        // TODO Auto-generated constructor stub
    }
    
    public CodeMsg(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
