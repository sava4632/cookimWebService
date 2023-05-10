package com.cookim.cookimws.utils;

public class DataResult {
    private String result;
    private Object data;
    private long recipeId;

    public DataResult() {
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

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public String toString() {
        return "DataResult{" +
                "result='" + result + '\'' +
                ", data=" + data +
                '}';
    }
}
