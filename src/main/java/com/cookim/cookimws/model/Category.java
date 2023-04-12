package com.cookim.cookimws.model;

/**
 *
 * @author Samuel
 */
public class Category {
    private long id;
    private String name;
    private String description;
    private String icon_path;

    public Category(long id, String name, String description, String icon_path) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon_path = icon_path;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon_path() {
        return icon_path;
    }

    public void setIcon_path(String icon_path) {
        this.icon_path = icon_path;
    }

   
}
