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
    public boolean deleteRecipe(long id_user,String id_recipe);

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
    
    public Recipe findRecipeByUserTokenAndRecipe(String token, Recipe recipe);
    // This method finds a recipe based on the user token and the recipe object provided.

    public boolean setRecipePathImage(long id, String pathImage);
    // This method sets the path of the image associated with a recipe identified by its ID.

    public int getNumLikes(String id_recipe);
    // This method retrieves the number of likes for a specific recipe identified by its ID.

    public boolean updateLikes(String id_recipe, int numLikes);
    // This method updates the number of likes for a specific recipe identified by its ID.

    public boolean addStepsRecipe(Step step);
    // This method adds a new step to a recipe.

    public Step findStepbyStep(Step step);
    // This method finds a step by its corresponding step object.

    public boolean setStepPathImage(long id_step, String path_image);
    // This method sets the path of the image associated with a step identified by its ID.

    public boolean linkIngredientToRecipe(Ingredient ingredient, long idRecipe);
    // This method links an ingredient to a recipe, identified by the ingredient and the recipe ID.

    public List<Recipe> searchRecipesLikeText(String text);
    // This method searches for recipes that contain the provided text in their title or description.

    public boolean addNewComment(Comment comment);
    // This method adds a new comment to a recipe.

    public List<Comment> findAllParentCommentByRecipeId(String id_recipe);
    // This method retrieves a list of all the parent comments for a specific recipe identified by its ID.
    
    public boolean deleteStepsByRecipe(long id_recipe);
    
    public boolean deleteLikesFromRecipe(long id_recipe);
    
    public boolean deleteRecipesSaved(long id_recipe);
}
