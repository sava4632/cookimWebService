package com.cookim.cookimws.model;

import java.util.List;

/**
 *
 * @author Samuel
 */
public interface IngredientDaoInterface {

    public List<Ingredient> getAllIngredientsWithIdMax(String idmax);

    // This method retrieves a list of all ingredients whose ID is less than or equal to the specified maximum ID.
    public boolean addNewIngredient(Ingredient ingredient);
    // This method adds a new ingredient to the system.

    public Ingredient findIngredientByName(String name);
    // This method finds an ingredient by its name and returns the corresponding Ingredient object.
    
    public boolean deleteIngredientsToRecipe(long id_recipe);

}
