package com.cookim.cookimws.model;

import java.util.List;

/**
 *
 * @author cookimadmin
 */
public interface RecipeDaoInterface {
    
    public List<Recipe> findAllRecipes();
    public List<Recipe> findAllRecipesByCategory(String idCategory);
    public List<Recipe> findAllRecipesWithUser();
    public boolean  addRecipe(Recipe recipe);
    public boolean deleteRecipe(String id);
    public boolean likeRecipe(int num, Recipe recipe);
    public boolean modifyRecipe(Recipe recipe);
    public Recipe findRecipeById(String id);
    
}