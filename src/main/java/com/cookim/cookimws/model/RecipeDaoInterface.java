package com.cookim.cookimws.model;

import java.util.List;

/**
 *
 * @author cookimadmin
 */
public interface RecipeDaoInterface {
    
    public List<Recipe> findAllRecipes();
    public List<Recipe> findAllRecipesByCategory(String idCategory);
    public boolean  addRecipe(Recipe recipe);
    public boolean deleteRecipe(String id);
    public boolean modifyRecipe(Recipe recipe);
    
}
