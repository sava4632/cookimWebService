package com.cookim.cookimws.model;

import java.util.List;

/**
 *
 * @author cookimadmin
 */
public interface RecipeDaoInterface {

    /**
     * Retrieves a list of all recipes in the system.
     *
     * @return List of all recipes.
     */
    public List<Recipe> findAllRecipes();

    /**
     * Retrieves a list of all recipes in a specific category.
     *
     * @param idCategory ID of the category to retrieve recipes for.
     * @return List of recipes in the specified category.
     */
    public List<Recipe> findAllRecipesByCategory(String idCategory);

    /**
     * Retrieves a list of all recipes with their associated user.
     *
     * @return List of recipes with user.
     */
    public List<Recipe> findAllRecipesWithUser();

    /**
     * Retrieves a list of all recipes created by a specific user, identified by
     * their token.
     *
     * @param token Token of the user to retrieve recipes for.
     * @return List of recipes created by the specified user.
     */
    public List<Recipe> findAllRecipesByUserToken(String token);
    
    public List<Recipe> findAllRecipesByUserId(String id);

    /**
     * Adds a new recipe to the system.
     *
     * @param recipe Recipe object to be added.
     * @return True if the recipe was successfully added, false otherwise.
     */
    public boolean addRecipe(String token,Recipe recipe);

    /**
     * Deletes a recipe from the system.
     *
     * @param id ID of the recipe to be deleted.
     * @return True if the recipe was successfully deleted, false otherwise.
     */
    public boolean deleteRecipe(String id);

    /**
     * Adds a "like" to a given recipe.
     *
     * @param num Number of likes to add.
     * @param recipe Recipe object to add likes to.
     * @return True if the likes were successfully added, false otherwise.
     */
    public boolean likeRecipe(int num, Recipe recipe);
    

    /**
     * Modifies an existing recipe in the system.
     *
     * @param recipe Recipe object containing updated information.
     * @return True if the recipe was successfully modified, false otherwise.
     */
    public boolean modifyRecipe(Recipe recipe);

    /**
     * Retrieves a specific recipe by its ID.
     *
     * @param id ID of the recipe to retrieve.
     * @return Recipe object with the specified ID.
     */
    public Recipe findRecipeById(String id);

    /**
     * Retrieves a list of all ingredients for a given recipe.
     *
     * @param id ID of the recipe to retrieve ingredients for.
     * @return List of all ingredients for the specified recipe.
     */
    public List<Ingredient> findAllIngredientsByRecipe(String id);

    /**
     * Retrieves a list of all steps for a given recipe.
     *
     * @param id ID of the recipe to retrieve steps for.
     * @return List of all steps for the specified recipe.
     */
    public List<Step> findAllStepsByRecipe(String id);
    
    public Recipe findRecipeByUserTokenAndRecipe(String token,Recipe recipe);
    public boolean setRecipePathImage(long id, String pathImage);
    public int getNumLikes(String id_recipe);
    public boolean updateLikes(String id_recipe, int numLikes);
    public boolean addStepsRecipe(Step step);
    public Step findStepbyStep(Step step);
    public boolean setStepPathImage(long id_step,String path_image);
    public boolean linkIngredientToRecipe(Ingredient ingredient,long idRecipe);

}
