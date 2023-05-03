package com.cookim.cookimws.model;

import java.util.List;

/**
 *
 * @author Samuel
 */
public interface IngredientDaoInterface {
    public List<Ingredient> getAllIngredientsWithIdMax(String idmax);
}
