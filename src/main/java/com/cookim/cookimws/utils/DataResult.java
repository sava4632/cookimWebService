package com.cookim.cookimws.utils;

/**
 *
 * @author cookimadmin
 */
public class DataResult {
    public String result;
    public Object data;
    
    
    public DataResult(){
        result = "";
        data = "";
    }

    public DataResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataResult{" + "result=" + result + ", data=" + data + '}';
    }
    
}
