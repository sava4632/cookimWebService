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

    /**
     *
     * This method retrieves all recipes from the database.
     *
     * @return a list of all recipes in the database, or null if an error occurs
     */
    @Override
    public List<Recipe> findAllRecipes() {
        List<Recipe> result = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
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

    /**
     *
     * This method adds a new recipe to the database.
     *
     * @param recipe the recipe to add to the database
     * @return true if the recipe was successfully added, false otherwise
     */
    @Override
    public boolean addRecipe(Recipe recipe) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
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

    /**
     *
     * Deletes a recipe from the database.
     *
     * @param id the ID of the recipe to delete
     * @return true if the recipe was successfully deleted, false otherwise
     */
    @Override
    public boolean deleteRecipe(String id) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
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

    /**
     *
     * Increments or decrements the number of likes of a recipe in the database.
     *
     * @param num 1 to increment the likes, -1 to decrement the likes
     * @param recipe the recipe to update in the database
     * @return true if the recipe was successfully updated, false otherwise
     */
    @Override
    public boolean likeRecipe(int num, Recipe recipe) {

        if (num == 1) {
            recipe.setLikes(recipe.getLikes() + 1);
        } else {
            recipe.setLikes(recipe.getLikes() - 1);
        }
        // incrementar el número de likes de la receta
        boolean result = false;

        try (Connection conn = MariaDBConnection.getConnection()) {
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

    /**
     *
     * This method modifies an existing recipe in the database.
     *
     * @param recipe the Recipe object with the updated data
     * @return true if the recipe was successfully modified, false otherwise
     */
    @Override
    public boolean modifyRecipe(Recipe recipe) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
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

    /**
     *
     * This method retrieves a list of recipes that belong to a specific
     * category by querying the recipe and recipe_category tables in the
     * database.
     *
     * @param idCategory the id of the category to search for
     * @return a List of Recipe objects that belong to the specified category
     */
    @Override
    public List<Recipe> findAllRecipesByCategory(String idCategory) {
        List<Recipe> recipes = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
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

    /**
     *
     * This method retrieves a list of recipes and their corresponding user
     * names from the database by joining the recipe and user tables.
     *
     * @return a List of Recipe objects that contain the id, name, description,
     * image path, rating, likes, and user name for each recipe
     */
    @Override
    public List<Recipe> findAllRecipesWithUser() {
        List<Recipe> result = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
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
     *
     * This method retrieves a recipe from the database based on its ID. It uses
     * a SQL query to join the recipe and user tables and fetch the
     * corresponding username and user image path. The retrieved data is then
     * used to create and return a Recipe object.
     *
     * @param id a String representing the ID of the recipe to retrieve
     * @return a Recipe object that contains the ID, name, description, image
     * path, rating, likes, username, and user image path of the recipe with the
     * specified ID
     */
    @Override
    public Recipe findRecipeById(String id) {
        Recipe recipe = null;
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT r.*, u.username, u.path_img as path_img_user\n"
                    + "FROM recipe r\n"
                    + "INNER JOIN user u ON r.id_user  = u.id\n"
                    + "WHERE r.id = ?;";
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
                recipe.setUser_name(rs.getString("username"));
                recipe.setPath(rs.getString("path_img_user"));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find recipes by category: " + ex.getMessage());
        }
        return recipe;
    }

    /**
     *
     * This method retrieves a list of ingredients for a given recipe from the
     * database by joining the ingredients and recipe_ingredients tables.
     *
     * @param id the id of the recipe to retrieve the ingredients for
     * @return a List of Ingredient objects that contain the id and name for
     * each ingredient
     */
    @Override
    public List<Ingredient> findAllIngredientsByRecipe(String id) {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT ingredients.id, ingredients.name FROM ingredients \n"
                    + "                    INNER JOIN recipe_ingredients ON ingredients.id = recipe_ingredients.id_ingredient \n"
                    + "                    WHERE recipe_ingredients.id_recipe = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getLong("id"));
                ingredient.setName(rs.getString("name"));
                ingredients.add(ingredient);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find ingredients by recipe: " + ex.getMessage());
        }
        return ingredients;
    }

    /**
     *
     * This method retrieves a list of steps for a given recipe from the
     * database.
     *
     * @param id the id of the recipe for which to find steps
     * @return a List of Step objects that contain the step number, description,
     * and image path for each step
     */
    @Override
    public List<Step> findAllStepsByRecipe(String id) {
        List<Step> steps = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT * FROM recipe_step rs  WHERE id_recipe  = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Step step = new Step();
                step.setId(rs.getLong("id"));
                step.setRecipe_id(rs.getLong("id_recipe"));
                step.setStep_number(rs.getInt("step_number"));
                step.setDescription(rs.getString("description"));
                step.setPath(rs.getString("path_img"));
                steps.add(step);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find steps by recipe: " + ex.getMessage());
        }
        return steps;
    }

    /**
     *
     * This method retrieves a list of recipes from the database that belong to
     * a certain category.
     *
     * @param category a String that represents the category of the recipes to
     * be retrieved
     * @return a List of Recipe objects that contain the id, name, description,
     * image path, rating, and likes for each recipe
     */
    @Override
    public List<Recipe> findAllRecipesByUserToken(String token) {
        List<Recipe> recipes = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT r.* FROM recipe r INNER JOIN user u ON r.id_user = u.id WHERE u.token = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getLong("id"));
                recipe.setId_user(rs.getLong("id_user"));
                recipe.setName(rs.getString("name"));
                recipe.setDescription(rs.getString("description"));
                recipe.setPath_img(rs.getString("path_img"));
                recipe.setRating(rs.getInt("rating"));
                recipe.setLikes(rs.getInt("likes"));
                recipes.add(recipe);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find recipes by user token: " + ex.getMessage());
        }
        return recipes;
    }

}
