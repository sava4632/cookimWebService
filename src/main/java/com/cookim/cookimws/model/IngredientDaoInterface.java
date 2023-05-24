package com.cookim.cookimws.model;

import java.util.List;

/**
 *
 * @author Samuel
 */
public interface IngredientDaoInterface {

    /**
     *
     * Retrieves a list of all ingredients whose ID is less than or equal to the
     * specified maximum ID.
     *
     * @param idmax The maximum ID of the ingredients.
     * @return A list of ingredients.
     */
    public List<Ingredient> getAllIngredientsWithIdMax(String idmax);

    /**
     *
     * Adds a new ingredient to the system.
     *
     * @param ingredient The new ingredient to be added.
     * @return True if the ingredient was added successfully, false otherwise.
     */
    public boolean addNewIngredient(Ingredient ingredient);

    /**
     *
     * Finds an ingredient by its name and returns the corresponding Ingredient
     * object.
     *
     * @param name The name of the ingredient.
     * @return The found ingredient.
     */
    public Ingredient findIngredientByName(String name);

    /**
     *
     * Deletes ingredients linked to a recipe.
     *
     * @param id_recipe The ID of the recipe.
     * @return True if the ingredients were deleted successfully, false
     * otherwise.
     */
    public boolean deleteIngredientsToRecipe(long id_recipe);

}
