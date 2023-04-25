/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cookim.cookimws.model;

/**
 *
 * @author jose3265
 */
public class Ingredient {

    private int id;
    private int id_ingredient;
    private int id_recipe;
    private String name;

    //CONSTRUCTOR
    public Ingredient(int id_ingredient, int id_recipe, String name) {
        this.id_ingredient = id_ingredient;
        this.id_recipe = id_recipe;
        this.name = name;
    }

    public Ingredient() {
    }
    
    //GETTERS AND SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_ingredient() {
        return id_ingredient;
    }

    public void setId_ingredient(int id_ingredient) {
        this.id_ingredient = id_ingredient;
    }

    public int getId_recipe() {
        return id_recipe;
    }

    public void setId_recipe(int id_recipe) {
        this.id_recipe = id_recipe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
