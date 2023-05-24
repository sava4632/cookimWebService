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
    public boolean addRecipe(String token, Recipe recipe);

    /**
     * Deletes a recipe from the system.
     *
     * @param id ID of the recipe to be deleted.
     * @return True if the recipe was successfully deleted, false otherwise.
     */
    public boolean deleteRecipe(long id_user, String id_recipe);

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

    /**
     *
     * Finds a recipe based on the user token and the recipe object provided.
     *
     * @param token The user token.
     * @param recipe The recipe object.
     * @return The found recipe.
     */
    public Recipe findRecipeByUserTokenAndRecipe(String token, Recipe recipe);

    /**
     *
     * Sets the path of the image associated with a recipe identified by its ID.
     *
     * @param id The ID of the recipe.
     * @param pathImage The path of the image.
     * @return True if the path image was set successfully, false otherwise.
     */
    public boolean setRecipePathImage(long id, String pathImage);

    /**
     *
     * Retrieves the number of likes for a specific recipe identified by its ID.
     *
     * @param id_recipe The ID of the recipe.
     * @return The number of likes for the recipe.
     */
    public int getNumLikes(String id_recipe);

    /**
     *
     * Updates the number of likes for a specific recipe identified by its ID.
     *
     * @param id_recipe The ID of the recipe.
     * @param numLikes The new number of likes for the recipe.
     * @return True if the likes were updated successfully, false otherwise.
     */
    public boolean updateLikes(String id_recipe, int numLikes);

    /**
     *
     * Adds a new step to a recipe.
     *
     * @param step The new step to be added.
     * @return True if the step was added successfully, false otherwise.
     */
    public boolean addStepsRecipe(Step step);

    /**
     *
     * Finds a step by its corresponding step object.
     *
     * @param step The step object.
     * @return The found step.
     */
    public Step findStepbyStep(Step step);

    /**
     *
     * Sets the path of the image associated with a step identified by its ID.
     *
     * @param id_step The ID of the step.
     * @param path_image The path of the image.
     * @return True if the path image was set successfully, false otherwise.
     */
    public boolean setStepPathImage(long id_step, String path_image);

    /**
     *
     * Links an ingredient to a recipe, identified by the ingredient and the
     * recipe ID.
     *
     * @param ingredient The ingredient to be linked.
     * @param idRecipe The ID of the recipe.
     * @return True if the ingredient was linked successfully, false otherwise.
     */
    public boolean linkIngredientToRecipe(Ingredient ingredient, long idRecipe);

    /**
     *
     * Searches for recipes that contain the provided text in their title or
     * description.
     *
     * @param text The text to search for.
     * @return A list of recipes matching the search criteria.
     */
    public List<Recipe> searchRecipesLikeText(String text);

    /**
     *
     * Adds a new comment to a recipe.
     *
     * @param comment The new comment to be added.
     * @return True if the comment was added successfully, false otherwise.
     */
    public boolean addNewComment(Comment comment);

    /**
     *
     * Retrieves a list of all the parent comments for a specific recipe
     * identified by its ID.
     *
     * @param id_recipe The ID of the recipe to retrieve the parent comments
     * for.
     * @return A list of parent comments associated with the specified recipe.
     */
    public List<Comment> findAllParentCommentByRecipeId(String id_recipe);

    /**
     *
     * Deletes all the steps associated with a recipe identified by its ID.
     *
     * @param id_recipe The ID of the recipe to delete the steps from.
     * @return True if the steps were successfully deleted, false otherwise.
     */
    public boolean deleteStepsByRecipe(long id_recipe);

    /**
     *
     * Deletes all the likes associated with a recipe identified by its ID.
     *
     * @param id_recipe The ID of the recipe to delete the likes from.
     * @return True if the likes were successfully deleted, false otherwise.
     */
    public boolean deleteLikesFromRecipe(long id_recipe);

    /**
     *
     * Deletes all the saved recipes associated with a recipe identified by its
     * ID.
     *
     * @param id_recipe The ID of the recipe to delete the saved recipes from.
     * @return True if the saved recipes were successfully deleted, false
     * otherwise.
     */
    public boolean deleteRecipesSaved(long id_recipe);

    /**
     *
     * Checks if a user has liked a specific recipe.
     *
     * @param id_user The ID of the user to check.
     * @param id_recipe The ID of the recipe to check if the user has liked.
     * @return True if the user has liked the recipe, false otherwise.
     */
    public boolean existsUserRecipeLiked(long id_user, long id_recipe);

    /**
     *
     * Checks if a user has saved a specific recipe.
     *
     * @param id_user The ID of the user to check.
     * @param id_recipe The ID of the recipe to check if the user has saved.
     * @return True if the user has saved the recipe, false otherwise.
     */
    public boolean existsUserRecipeSaved(long id_user, long id_recipe);

    /**
     *
     * Retrieves the recipes followed by a user.
     *
     * @param id_user The ID of the user.
     * @return A list of recipes followed by the user.
     */
    public List<Recipe> getUserFollowedsRecipes(long id_user);

    /**
     *
     * Finds all child comments of a specific recipe and parent comment.
     *
     * @param id_recipe The ID of the recipe.
     * @param id_parent_comment The ID of the parent comment.
     * @return A list of child comments.
     */
    public List<Comment> findAllChildComments(String id_recipe, String id_parent_comment);

    /**
     *
     * Finds a category by its name.
     *
     * @param category_name The name of the category.
     * @return The found category.
     */
    public Category findCategoryByName(String category_name);

    /**
     *
     * Links a category to a recipe, identified by the category ID and the
     * recipe ID.
     *
     * @param id_category The ID of the category.
     * @param id_recipe The ID of the recipe.
     * @return True if the category was linked successfully, false otherwise.
     */
    public boolean linkCategoryToRecipe(long id_category, long id_recipe);

    /**
     *
     * Searches for recipes belonging to a specific category.
     *
     * @param category_name The name of the category.
     * @return A list of recipes from the category.
     */
    public List<Recipe> searchRecipesFromCategory(String category_name);

    /**
     *
     * Deletes categories linked to a recipe.
     *
     * @param id_recipe The ID of the recipe.
     * @return True if the categories were deleted successfully, false
     * otherwise.
     */
    public boolean deleteCategoriesToRecipe(long id_recipe);
}
