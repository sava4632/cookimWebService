package com.cookim.cookimws.model;

import com.cookim.cookimws.connection.MariaDBConnection;
import com.cookim.cookimws.utils.DataResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cookimadmin
 */
public class RecipeDao implements RecipeDaoInterface {

    public RecipeDao() {

    }

    @Override
    public List<Recipe> findAllRecipes() {
        List<Recipe> result = new ArrayList<>();
        try ( Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT * FROM recipe;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                Recipe r = new Recipe();
                r.setId(rs.getLong("id"));
                r.setId_user(rs.getLong("id_user"));
                r.setName(rs.getString("name"));
                r.setDescription(rs.getString("description"));
                r.setPath_img(rs.getString("path_img"));
                r.setRating(rs.getDouble("rating"));
                r.setLikes(rs.getInt("likes"));

                result.add(r);
            }
            ps.close();
        } catch (SQLException ex) {
            result = null;
            System.out.println("Failed to list recipes");
        }
        return result;
    }

    @Override
    public boolean addRecipe(Recipe recipe) {
        boolean result = false;
        try ( Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            String query = "INSERT INTO recipe(id_user, name, description, path_img, rating, likes) VALUES (?, ?, ?, ?, ?, ?);";
            ps = conn.prepareStatement(query);
            ps.setLong(1, recipe.getId_user());
            ps.setString(2, recipe.getName());
            ps.setString(3, recipe.getDescription());
            ps.setString(4, recipe.getPath_img());
            ps.setDouble(5, recipe.getRating());
            ps.setInt(6, recipe.getLikes());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                result = true;
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to add recipe: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public boolean deleteRecipe(String id) {
        boolean result = false;
        try ( Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            String query = "DELETE FROM recipe WHERE id = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                result = true;
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to delete recipe: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public boolean likeRecipe(int num, Recipe recipe) {

        if (num == 1) {
            recipe.setLikes(recipe.getLikes() + 1);
        } else {
            recipe.setLikes(recipe.getLikes() - 1);
        }
        // incrementar el número de likes de la receta
        boolean result = false;

        try ( Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            String query = "UPDATE recipe SET likes=? WHERE id=?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, recipe.getLikes());
            ps.setLong(2, recipe.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                result = true;
            }
            ps.close();
        } catch (SQLException ex) {
            return result;
        }

        return result;
    }

    @Override
    public boolean modifyRecipe(Recipe recipe) {
        boolean result = false;
        try ( Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            String query = "UPDATE recipe SET id_user=?, name=?, description=?, path_img=?, rating=?, likes=? WHERE id=?";
            ps = conn.prepareStatement(query);
            ps.setLong(1, recipe.getId_user());
            ps.setString(2, recipe.getName());
            ps.setString(3, recipe.getDescription());
            ps.setString(4, recipe.getPath_img());
            ps.setDouble(5, recipe.getRating());
            ps.setInt(6, recipe.getLikes());
            ps.setLong(7, recipe.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                result = true;
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to modify recipe: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public List<Recipe> findAllRecipesByCategory(String idCategory) {
        List<Recipe> recipes = new ArrayList<>();
        try ( Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT recipe.id, recipe.id_user, recipe.name, recipe.description, recipe.path_img, recipe.rating, recipe.likes FROM recipe "
                    + "JOIN recipe_category ON recipe_category.id_recipe = recipe.id "
                    + "WHERE recipe_category.id_category = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, idCategory);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getLong("id"));
                recipe.setId_user(rs.getLong("id_user"));
                recipe.setName(rs.getString("name"));
                recipe.setDescription(rs.getString("description"));
                recipe.setPath_img(rs.getString("path_img"));
                recipe.setRating(rs.getDouble("rating"));
                recipe.setLikes(rs.getInt("likes"));
                recipes.add(recipe);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find recipes by category: " + ex.getMessage());
        }
        return recipes;
    }

    @Override
    public List<Recipe> findAllRecipesWithUser() {
        List<Recipe> result = new ArrayList<>();
        try ( Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT recipe.id, recipe.id_user, recipe.name, recipe.description, "
                    + "recipe.path_img, recipe.rating, recipe.likes, user.username "
                    + "FROM recipe JOIN user ON recipe.id_user = user.id;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getLong("id"));
                recipe.setId_user(rs.getLong("id_user"));
                recipe.setName(rs.getString("name"));
                recipe.setDescription(rs.getString("description"));
                recipe.setPath_img(rs.getString("path_img"));
                recipe.setRating(rs.getFloat("rating"));
                recipe.setLikes(rs.getInt("likes"));
                recipe.setUser_name(rs.getString("username"));
                result.add(recipe);
            }
            ps.close();
        } catch (SQLException ex) {
            result = null;
            System.out.println("Failed to list recipes");
        }
        return result;
    }

    /**
     * Find recipe by his id and returns it
     *
     * @param id
     * @return
     */
    @Override
    public Recipe findRecipeById(String id) {
        Recipe recipe = null;
        try ( Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT * FROM recipe WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                recipe = new Recipe();
                recipe.setId(rs.getLong("id"));
                recipe.setId_user(rs.getLong("id_user"));
                recipe.setName(rs.getString("name"));
                recipe.setDescription(rs.getString("description"));
                recipe.setPath_img(rs.getString("path_img"));
                recipe.setRating(rs.getDouble("rating"));
                recipe.setLikes(rs.getInt("likes"));

            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find recipes by category: " + ex.getMessage());
        }
        return recipe;
    }
    
  public Recipe findFullRecipe(String id) {
    Recipe recipe = null;
    try (Connection conn = MariaDBConnection.getConnection()) {
        String query = "SELECT r.id, r.id_user, r.name, r.description, r.path_img, r.rating, r.likes, "
                       + "ri.id as ri_id, ri.id_recipe, ri.id_ingredient, "
                       + "i.id as ingredient_id, i.name as ingredient_name, "
                       + "s.id as step_id, s.recipe_id, s.step_number, s.description as step_description, s.path as step_path "
                       + "FROM recipe r "
                       + "INNER JOIN recipe_ingredients ri ON r.id = ri.id_recipe "
                       + "INNER JOIN ingredients i ON ri.id_ingredient = i.id "
                       + "INNER JOIN steps s ON r.id = s.recipe_id "
                       + "WHERE r.id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setLong(1, Long.parseLong(id)); // Convertir id de String a long
        ResultSet rs = ps.executeQuery();
        recipe = new Recipe();
        List<Ingredient> ingredients = new ArrayList<>();
        List<Step> steps = new ArrayList<>();
        while (rs.next()) {
            recipe.setId(rs.getLong("id"));
            recipe.setId_user(rs.getLong("id_user"));
            recipe.setName(rs.getString("name"));
            recipe.setDescription(rs.getString("description"));
            recipe.setPath_img(rs.getString("path_img"));
            recipe.setRating(rs.getDouble("rating"));
            recipe.setLikes(rs.getInt("likes"));

            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getInt("ri_id"));
            ingredient.setId_ingredient(rs.getInt("id_ingredient"));
            ingredient.setId_recipe(rs.getInt("id_recipe"));
            ingredient.setName(rs.getString("ingredient_name"));
            ingredients.add(ingredient);

            Step step = new Step();
            step.setId(rs.getInt("step_id"));
            step.setRecipe_id(rs.getInt("recipe_id"));
            step.setStep_number(rs.getInt("step_number"));
            step.setDescription(rs.getString("step_description"));
            step.setPath(rs.getString("step_path"));
            steps.add(step);
        }
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);
        rs.close();
        ps.close();
    } catch (SQLException ex) {
        System.out.println("Failed to find recipe by id: " + ex.getMessage());
    }
    return recipe;
}






}
