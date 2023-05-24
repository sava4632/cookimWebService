package com.cookim.cookimws.model;

import com.google.gson.annotations.Expose;

/**
 *
 * @author Samuel
 */
public class Category {
    @Expose(serialize = false)
    private long id;
    private String name;

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String name) {
        this.name = name;
    }
    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{ name=" + name + '}';
    }
    
}
