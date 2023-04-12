package com.cookim.cookimws.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author cookimadmin
 */
public class User {
    private long id;
    private String username;
    private String password;
    private String full_name;
    private String email;
    private String phone;
    private String path_img;
    private String description;
    private long id_rol;
    private String token;

    public User(long id, String username, String password, String full_name, String email, String phone, String path_img, String description, long id_rol, String token) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.path_img = path_img;
        this.description = description;
        this.id_rol = id_rol;
        this.token = token;
    }
    
    public User( String username, String password, String full_name, String email, String phone, String path_img, String description, long id_rol) {
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.path_img = path_img;
        this.description = description;
        this.id_rol = id_rol;
    }

    public User(String username, String password, String full_name, String email, String phone, String path_img, String description, long id_rol, String token) {
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.path_img = path_img;
        this.description = description;
        this.id_rol = id_rol;
        this.token = token;
    }

    public User(String username, String password, String full_name, String email, String phone, String path_img, long id_rol) {
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.path_img = path_img;
    }
    
    
    

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String token) {
        this.token = token;
    }
    

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPath_img() {
        return path_img;
    }

    public void setPath_img(String path_img) {
        this.path_img = path_img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId_rol() {
        return id_rol;
    }

    public void setId_rol(long id_rol) {
        this.id_rol = id_rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", password=" + password + ", full_name=" + full_name + ", email=" + email + ", phone=" + phone + ", path_img=" + path_img + ", description=" + description + ", id_rol=" + id_rol + ", token=" + token + '}';
    }

    /*public byte[] getImage() {
    try {
        FileInputStream fileInputStream = new FileInputStream(new File(this.path_img));
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);
        fileInputStream.close();
        return bytes;
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }*/
}

    
