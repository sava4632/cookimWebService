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

    @Override
    public boolean addNewIngredient(Ingredient ingredient) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            // Check if the ingredient already exists
            PreparedStatement checkIfExists = conn.prepareStatement("SELECT id FROM ingredients WHERE name = ?");
            checkIfExists.setString(1, ingredient.getName());
            ResultSet resultSet = checkIfExists.executeQuery();

            if (resultSet.next()) {
                // Ingredient already exists, do not add it
                return false;
            }

            // Add the new ingredient
            PreparedStatement addIngredient = conn.prepareStatement("INSERT INTO ingredients (name) VALUES (?)");
            addIngredient.setString(1, ingredient.getName());
            addIngredient.executeUpdate();

            addIngredient.close();
            checkIfExists.close();

            return true;
        } catch (Exception e) {
            System.out.println("Failed to add ingredient: " + e.toString());
            return false;
        }
    }

    @Override
    public Ingredient findIngredientByName(String name) {
        Ingredient ingredient = null;
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM ingredients WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ingredient = new Ingredient();
                ingredient.setId(rs.getLong("id"));
                ingredient.setName(rs.getString("name"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Failed to find ingredient by name: " + e.toString());
        }
        return ingredient;
    }



    
    
}
