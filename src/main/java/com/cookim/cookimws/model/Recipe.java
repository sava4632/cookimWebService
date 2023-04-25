package com.cookim.cookimws.model;

import java.util.List;

/**
 *
 * @author cookimadmin
 */
public class Recipe {
    private long id;
    private long id_user;
    private String name;
    private String description;
    private String path_img;
    private double rating;
    private int likes;
    private String user_name;
    private List<Ingredient> ingredients;
    private List<Step> steps;

    public Recipe(long id, long id_user, String name, String description, String path_img, double rating, int likes,List<Ingredient> ingredients,  List<Step> steps) {
        this.id = id;
        this.id_user = id_user;
        this.name = name;
        this.description = description;
        this.path_img = path_img;
        this.rating = rating;
        this.likes = likes;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public Recipe(long id_user, String name, String description, String path_img, double rating, int likes) {
        this.id_user = id_user;
        this.name = name;
        this.description = description;
        this.path_img = path_img;
        this.rating = rating;
        this.likes = likes;
    }

    //Contructor for home page recipes 
    public Recipe(long id, long id_user, String name, String description, String path_img, double rating, int likes, String user_name) {
        this.id = id;
        this.id_user = id_user;
        this.name = name;
        this.description = description;
        this.path_img = path_img;
        this.rating = rating;
        this.likes = likes;
        this.user_name = user_name;
    }
    
    
    

    public Recipe() {
    }

    public Recipe(long id, long id_user, String name, String description, String path_img, double rating, int likes) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    

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

    public String getPath_img() {
        return path_img;
    }

    public void setPath_img(String path_img) {
        this.path_img = path_img;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
    
    

    @Override
    public String toString() {
        return "Recipe{" + "id=" + id + ", id_user=" + id_user + ", name=" + name + ", description=" + description + ", path_img=" + path_img + ", rating=" + rating + ", likes=" + likes + '}';
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
    
    
}
