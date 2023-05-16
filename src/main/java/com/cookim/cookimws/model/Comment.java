package com.cookim.cookimws.model;

import java.util.Date;

public class Comment {
    private long id;
    private long id_user;
    private long id_recipe;
    private String text;
    private Date data_send;
    private Long id_parent_comment;
    private String username;
    private String path_user_profile;

    // Constructor

    public Comment(long id, long id_user, long id_recipe, String text, Date data_send, Long id_parent_comment, String username, String path_user_profile) {
        this.id = id;
        this.id_user = id_user;
        this.id_recipe = id_recipe;
        this.text = text;
        this.data_send = data_send;
        this.id_parent_comment = id_parent_comment;
        this.username = username;
        this.path_user_profile = path_user_profile;
    }
    
    
    public Comment(long id, long id_user, long id_recipe, String text, Date data_send, Long id_parent_comment) {
        this.id = id;
        this.id_user = id_user;
        this.id_recipe = id_recipe;
        this.text = text;
        this.data_send = data_send;
        this.id_parent_comment = id_parent_comment;
    }

    public Comment(long id_recipe, String text, Long id_parent_comment) {
        this.id_recipe = id_recipe;
        this.text = text;
        this.id_parent_comment = id_parent_comment;
    }
    

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }

    public long getId_recipe() {
        return id_recipe;
    }

    public void setId_recipe(long id_recipe) {
        this.id_recipe = id_recipe;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getData_send() {
        return data_send;
    }

    public void setData_send(Date data_send) {
        this.data_send = data_send;
    }

    public Long getId_parent_comment() {
        return id_parent_comment;
    }

    public void setId_parent_comment(Long id_parent_comment) {
        this.id_parent_comment = id_parent_comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPath_user_profile() {
        return path_user_profile;
    }

    public void setPath_user_profile(String path_user_profile) {
        this.path_user_profile = path_user_profile;
    }

    @Override
    public String toString() {
        return "Comment{" + "id=" + id + ", id_user=" + id_user + ", id_recipe=" + id_recipe + ", text=" + text + ", data_send=" + data_send + ", id_parent_comment=" + id_parent_comment + ", username=" + username + ", path_user_profile=" + path_user_profile + '}';
    }

    

    
}


