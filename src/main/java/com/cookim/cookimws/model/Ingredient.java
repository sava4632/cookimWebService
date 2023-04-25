
package com.cookim.cookimws.model;

/**
 *
 * @author Samuel
 */
public class Ingredient {
    private int id;
    private int id_ingredient;
    private int id_recipe;
    private String name;

    public Ingredient(int id, int id_ingredient, int id_recipe, String name) {
        this.id = id;
        this.id_ingredient = id_ingredient;
        this.id_recipe = id_recipe;
        this.name = name;
    }

    public Ingredient() {
    }


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
