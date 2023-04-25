
package com.cookim.cookimws.model;

/**
 *
 * @author Samuel
 */
public class Step {

    
    private int id;
    private int recipe_id;
    private int step_number;
    private String description;
    private String path;
    
    public Step(int id, int recipe_id, int step_number, String description, String path) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.step_number = step_number;
        this.description = description;
        this.path = path;
    }

    public Step() {
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public int getStep_number() {
        return step_number;
    }

    public void setStep_number(int step_number) {
        this.step_number = step_number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    
}
