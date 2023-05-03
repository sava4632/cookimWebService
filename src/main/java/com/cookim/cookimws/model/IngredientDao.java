package com.cookim.cookimws.model;

import com.cookim.cookimws.connection.MariaDBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Samuel
 */
public class IngredientDao implements IngredientDaoInterface{
    List<Ingredient> result;

    public IngredientDao() {
        result = new ArrayList<>();
    }

    @Override
    public List<Ingredient> getAllIngredientsWithIdMax(String idmax) {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ingredients WHERE id > ?");
            ps.setString(1, idmax);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getLong("id"));
                ingredient.setName(rs.getString("name"));
                ingredients.add(ingredient);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("failed to get ingredients : " + e.toString());
        }
        return ingredients;
    }

    
    
}
