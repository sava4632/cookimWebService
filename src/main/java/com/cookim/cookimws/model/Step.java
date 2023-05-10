
package com.cookim.cookimws.model;

import io.javalin.http.UploadedFile;

/**
 *
 * @author Samuel
 */
public class Step {

    private long id;
    private long recipe_id;
    private long step_number;
    private String description;
    private String path;

    //ADD RECIPE CONSTRUCTOR

    public Step(long id, long recipe_id, long step_number, String description) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.step_number = step_number;
        this.description = description;
    }
    
    
    

    public Step(long id, long recipe_id, long step_number, String description, String path) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.step_number = step_number;
        this.description = description;
        this.path = path;
    }

    public Step() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(long recipe_id) {
        this.recipe_id = recipe_id;
    }

    public long getStep_number() {
        return step_number;
    }

    public void setStep_number(long step_number) {
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

    

    @Override
    public String toString() {
        return "Step{" + "id=" + id + ", recipe_id=" + recipe_id + ", step_number=" + step_number + ", description=" + description + ", path=" + path + '}';
    }
    
    
}
