package com.cookim.cookimws.model;

import com.cookim.cookimws.connection.MariaDBConnection;
import com.cookim.cookimws.utils.DataResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
    public boolean addRecipe(String token, Recipe recipe) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
            // Obtener el id_user correspondiente al token pasado como parámetro
            PreparedStatement psUserId = conn.prepareStatement("SELECT id FROM user WHERE token = ?");
            psUserId.setString(1, token);
            ResultSet rs = psUserId.executeQuery();
            if (rs.next()) {
                long idUser = rs.getLong("id");
                PreparedStatement ps;
                String query = "INSERT INTO recipe(id_user, name, description, likes) VALUES (?, ?, ?, ?);";
                ps = conn.prepareStatement(query);
                ps.setLong(1, idUser);
                ps.setString(2, recipe.getName());
                ps.setString(3, recipe.getDescription());
                ps.setInt(4, recipe.getLikes());
                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    result = true;
                }
                ps.close();
            }
            rs.close();
            psUserId.close();
        } catch (SQLException ex) {
            System.out.println("Failed to add recipe: " + ex.getMessage());
        }
        return result;
    }

    /**
     * Deletes a recipe from the database.
     *
     * @param id_user the ID of the user who owns the recipe
     * @param id_recipe the ID of the recipe to delete
     * @return true if the recipe was successfully deleted, false otherwise
     */
    @Override
    public boolean deleteRecipe(long id_user, String id_recipe) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            String query = "DELETE FROM recipe WHERE id = ? AND id_user = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, id_recipe);
            ps.setLong(2, id_user);

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

    @Override
    public List<Recipe> findAllRecipesByUserId(String id) {
        List<Recipe> recipes = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT r.* FROM recipe r INNER JOIN user u ON r.id_user = u.id WHERE u.id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
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

    @Override
    public Recipe findRecipeByUserTokenAndRecipe(String token, Recipe recipe) {
        Recipe result = null;
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT r.* FROM recipe r INNER JOIN user u ON r.id_user = u.id WHERE u.token = ? AND r.name = ? AND r.description = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, token);
            ps.setString(2, recipe.getName());
            ps.setString(3, recipe.getDescription());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = new Recipe();
                result.setId(rs.getLong("id"));
                result.setId_user(rs.getLong("id_user"));
                result.setName(rs.getString("name"));
                result.setDescription(rs.getString("description"));
                result.setPath_img(rs.getString("path_img"));
                result.setRating(rs.getInt("rating"));
                result.setLikes(rs.getInt("likes"));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find recipe by user token and recipe: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public boolean setRecipePathImage(long id, String pathImage) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "UPDATE recipe SET path_img = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, pathImage);
            ps.setLong(2, id);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                result = true;
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to set recipe path image: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public int getNumLikes(String id_recipe) {
        int numLikes = 0;
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS num_likes FROM user_recipe_likes WHERE id_recipe = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id_recipe);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                numLikes = rs.getInt("num_likes");
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to get number of likes: " + ex.getMessage());
        }
        return numLikes;
    }

    @Override
    public boolean updateLikes(String id_recipe, int numLikes) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "UPDATE recipe SET likes = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, numLikes);
            ps.setString(2, id_recipe);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                result = true;
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to update number of likes: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public boolean addStepsRecipe(Step step) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "INSERT INTO recipe_step (id_recipe, step_number, description) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, step.getRecipe_id());
            ps.setLong(2, step.getStep_number());
            ps.setString(3, step.getDescription());
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                result = true;
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to add step to recipe: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public Step findStepbyStep(Step step) {
        Step foundStep = null;
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT * FROM recipe_step WHERE id_recipe = ? AND step_number = ? AND description = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, step.getRecipe_id());
            ps.setLong(2, step.getStep_number());
            ps.setString(3, step.getDescription());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                long recipeId = rs.getLong("id_recipe");
                long stepNumber = rs.getLong("step_number");
                String description = rs.getString("description");
                foundStep = new Step(id, recipeId, stepNumber, description);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find step by step: " + ex.getMessage());
        }
        return foundStep;
    }

    @Override
    public boolean setStepPathImage(long id_step, String path_image) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "UPDATE recipe_step SET path_img = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, path_image);
            ps.setLong(2, id_step);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                result = true;
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to set step path image: " + ex.getMessage());
        }
        return result;
    }

    @Override
    public boolean linkIngredientToRecipe(Ingredient ingredient, long idRecipe) {
        boolean success = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO recipe_ingredients (id_recipe, id_ingredient) VALUES (?, ?)");
            ps.setLong(1, idRecipe);
            ps.setLong(2, ingredient.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
            ps.close();
        } catch (Exception e) {
            System.out.println("Failed to link ingredient to recipe: " + e.toString());
        }
        return success;
    }

    @Override
    public List<Recipe> searchRecipesLikeText(String text) {
        List<Recipe> recipes = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT * FROM recipe WHERE name LIKE ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + text + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                long id_user = rs.getLong("id_user");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String path_img = rs.getString("path_img");
                float rating = rs.getFloat("rating");
                int likes = rs.getInt("likes");

                Recipe recipe = new Recipe(id, id_user, name, description, path_img, rating, likes);
                recipes.add(recipe);
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to search recipes: " + ex.getMessage());
        }
        return recipes;
    }

    @Override
    public boolean addNewComment(Comment comment) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "INSERT INTO comment (id_user, id_recipe, text, data_send, id_parent_comment) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, comment.getId_user());
            ps.setLong(2, comment.getId_recipe());
            ps.setString(3, comment.getText());
            ps.setTimestamp(4, new Timestamp(comment.getData_send().getTime()));
            if (comment.getId_parent_comment() != null) {
                ps.setLong(5, comment.getId_parent_comment());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            int rowsAffected = ps.executeUpdate();
            ps.close();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Failed to add comment: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public List<Comment> findAllParentCommentByRecipeId(String id_recipe) {
        List<Comment> comments = new ArrayList<>();
        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT c.id, c.id_user, c.text, c.data_send, c.id_parent_comment, u.username, u.path_img "
                    + "FROM comment c INNER JOIN user u ON c.id_user = u.id "
                    + "WHERE c.id_recipe = ? AND c.id_parent_comment IS NULL";
            PreparedStatement ps = conn.prepareStatement(query);
            long recipeId = Long.parseLong(id_recipe);
            ps.setLong(1, recipeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                long id_user = rs.getLong("id_user");
                String commentText = rs.getString("text");
                Timestamp timestamp = rs.getTimestamp("data_send");
                Long id_parent_comment = rs.getLong("id_parent_comment");
                String username = rs.getString("username");
                String path_img = rs.getString("path_img");

                Comment comment = new Comment(id, id_user, recipeId, commentText, timestamp, id_parent_comment, username, path_img);

                comments.add(comment);
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Failed to find parent comments by recipe ID: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid recipe ID format: " + ex.getMessage());
        }
        return comments;
    }

    @Override
    public boolean deleteStepsByRecipe(long id_recipe) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM recipe_step WHERE id_recipe = ?");
            ps.setLong(1, id_recipe);
            int rowsAffected = ps.executeUpdate();
            ps.close();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Failed to delete steps for recipe: " + e.toString());
            return false;
        }
    }

    @Override
    public boolean deleteLikesFromRecipe(long id_recipe) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM user_recipe_likes WHERE id_recipe = ?");
            ps.setLong(1, id_recipe);
            int rowsAffected = ps.executeUpdate();
            ps.close();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Failed to delete likes from recipe: " + e.toString());
            return false;
        }
    }

    @Override
    public boolean deleteRecipesSaved(long id_recipe) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM favorite_recipes WHERE recipe_id = ?");
            ps.setLong(1, id_recipe);
            int rowsAffected = ps.executeUpdate();
            ps.close();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Failed to delete saved recipes: " + e.toString());
            return false;
        }
}



}
