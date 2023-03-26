package com.cookim.cookimws.utils;

/**
 *
 * @author cookimadmin
 */
public class DataResult {
    public String result;
    public String result2;
    public Object data;
    
    
    public DataResult(){
        result = "";
        data = "";
    }

    public DataResult(String result, String result2) {
        this.result = result;
        this.result2 = result2;
    }

    public String getResult2() {
        return result2;
    }

    public void setResult2(String result2) {
        this.result2 = result2;
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
    
}
