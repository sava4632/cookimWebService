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
import java.util.stream.Collectors;

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
                    + "FROM recipe JOIN user ON recipe.id_user = user.id "
                    + "ORDER BY recipe.id DESC;"; // Modifying the query to retrieve recipes in descending order
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

    /**
     *
     * Retrieves a list of all recipes associated with a specific user
     * identified by their ID.
     *
     * @param id The ID of the user to retrieve the recipes for.
     * @return A list of all the recipes associated with the specified user.
     */
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

    /**
     *
     * Retrieves a recipe associated with a specific user token and recipe
     * details.
     *
     * @param token The token of the user.
     * @param recipe The recipe object containing the name and description of
     * the recipe to search for.
     * @return The recipe associated with the user token and recipe details, or
     * null if not found.
     */
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

    /**
     *
     * Sets the path image for a recipe identified by its ID.
     *
     * @param id The ID of the recipe to set the path image for.
     * @param pathImage The new path image to set.
     * @return True if the path image was successfully set, false otherwise.
     */
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

    /**
     *
     * Retrieves the number of likes for a recipe identified by its ID.
     *
     * @param id_recipe The ID of the recipe to get the number of likes for.
     * @return The number of likes for the specified recipe.
     */
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

    /**
     *
     * Updates the number of likes for a recipe identified by its ID.
     *
     * @param id_recipe The ID of the recipe to update the number of likes for.
     * @param numLikes The new number of likes to set.
     * @return True if the number of likes was successfully updated, false
     * otherwise.
     */
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

    /**
     *
     * Adds a step to a recipe.
     *
     * @param step The step object containing the details of the step to add.
     * @return True if the step was successfully added to the recipe, false
     * otherwise.
     */
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

    /**
     *
     * Finds a step by its details (recipe ID, step number, and description).
     *
     * @param step The step object containing the details of the step to find.
     * @return The found step, or null if not found.
     */
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

    /**
     *
     * Sets the path image for a step identified by its ID.
     *
     * @param id_step The ID of the step to set the path image for.
     * @param path_image The new path image to set.
     * @return True if the path image was successfully set, false otherwise.
     */
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

    /**
     *
     * Links an ingredient to a recipe.
     *
     * @param ingredient The ingredient object to link to the recipe.
     * @param idRecipe The ID of the recipe to link the ingredient to.
     * @return True if the ingredient was successfully linked to the recipe,
     * false otherwise.
     */
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

    /**
     *
     * Searches for recipes that contain a specified text in their name.
     *
     * @param text The text to search for in recipe names.
     * @return A list of recipes that match the search criteria.
     */
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

    /**
     *
     * Adds a new comment to the system.
     *
     * @param comment The comment object containing the details of the comment
     * to add.
     * @return True if the comment was successfully added, false otherwise.
     */
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

    /**
     *
     * Retrieves a list of all the parent comments for a specific recipe
     * identified by its ID.
     *
     * @param id_recipe The ID of the recipe to retrieve the parent comments
     * for.
     * @return A list of parent comments for the specified recipe.
     */
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
    public List<Comment> findAllChildComments(String id_recipe, String id_parent_comment) {
        List<Comment> childComments = new ArrayList<>();

        try (Connection conn = MariaDBConnection.getConnection()) {
            String query = "SELECT c.id, c.id_user, c.id_recipe, c.text, c.data_send, c.id_parent_comment, u.username, u.path_img "
                    + "FROM comment c "
                    + "JOIN user u ON c.id_user = u.id "
                    + "WHERE c.id_recipe = ? AND c.id_parent_comment = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id_recipe);
            ps.setString(2, id_parent_comment);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long commentId = rs.getLong("id");
                long userId = rs.getLong("id_user");
                long idRecipe = rs.getLong("id_recipe");
                String username = rs.getString("username");
                String pathImg = rs.getString("path_img");
                String text = rs.getString("text");
                Timestamp timestamp = rs.getTimestamp("data_send");
                long parentCommentId = rs.getLong("id_parent_comment");

                Comment comment = new Comment(commentId, userId, idRecipe, text, timestamp, parentCommentId);
                comment.setUsername(username);
                comment.setPath_user_profile(pathImg);

                childComments.add(comment);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Failed to find child comments: " + e.toString());
        }

        return childComments;
    }

    /**
     *
     * Deletes all steps associated with a recipe.
     *
     * @param id_recipe The ID of the recipe to delete the steps for.
     * @return True if the steps were successfully deleted, false otherwise.
     */
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

    /**
     *
     * Deletes all likes from a recipe.
     *
     * @param id_recipe The ID of the recipe to delete the likes from.
     * @return True if the likes were successfully deleted, false otherwise.
     */
    @Override
    public boolean deleteLikesFromRecipe(long id_recipe) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM user_recipe_likes WHERE id_recipe = ?");
            ps.setLong(1, id_recipe);
            int rowsAffected = ps.executeUpdate();
            ps.close();

            return rowsAffected >= 0; // Devolver true cuando no se afecte ninguna fila
        } catch (Exception e) {
            System.out.println("Failed to delete likes from recipe: " + e.toString());
            return false;
        }
    }

    /**
     *
     * Deletes all saved recipes associated with a recipe.
     *
     * @param id_recipe The ID of the recipe to delete the saved recipes for.
     * @return True if the saved recipes were successfully deleted, false
     * otherwise.
     */
    @Override
    public boolean deleteRecipesSaved(long id_recipe) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM favorite_recipes WHERE recipe_id = ?");
            ps.setLong(1, id_recipe);
            int rowsAffected = ps.executeUpdate();
            ps.close();

            return rowsAffected >= 0; // Devolver true cuando no se afecte ninguna fila
        } catch (Exception e) {
            System.out.println("Failed to delete saved recipes: " + e.toString());
            return false;
        }
    }

    /**
     *
     * Checks if a user has liked a specific recipe.
     *
     * @param id_user The ID of the user.
     * @param id_recipe The ID of the recipe.
     * @return True if the user has liked the recipe, false otherwise.
     */
    @Override
    public boolean existsUserRecipeLiked(long id_user, long id_recipe) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM user_recipe_likes WHERE id_user = ? AND id_recipe = ?");
            ps.setLong(1, id_user);
            ps.setLong(2, id_recipe);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Failed to check if user recipe liked: " + e.toString());
        }

        return false;
    }

    /**
     *
     * Checks if a user has saved a specific recipe.
     *
     * @param user_id The ID of the user.
     * @param recipe_id The ID of the recipe.
     * @return True if the user has saved the recipe, false otherwise.
     */
    @Override
    public boolean existsUserRecipeSaved(long user_id, long recipe_id) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM favorite_recipes WHERE user_id = ? AND recipe_id = ?");
            ps.setLong(1, user_id);
            ps.setLong(2, recipe_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Failed to check if user recipe saved: " + e.toString());
        }

        return false;
    }

    @Override
    public List<Recipe> getUserFollowedsRecipes(long id_user) {
        List<Recipe> recipes = new ArrayList<>();

        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT followed_id FROM user_followeds WHERE follower_id = ?");
            ps.setLong(1, id_user);
            ResultSet rs = ps.executeQuery();

            List<Long> followedIds = new ArrayList<>();
            while (rs.next()) {
                followedIds.add(rs.getLong("followed_id"));
            }

            rs.close();
            ps.close();

            if (!followedIds.isEmpty()) {
                String followedIdsString = followedIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

                String query = "SELECT * FROM recipe WHERE id_user IN (" + followedIdsString + ")";
                ps = conn.prepareStatement(query);
                rs = ps.executeQuery();

                while (rs.next()) {
                    long recipeId = rs.getLong("id");
                    long userId = rs.getLong("id_user");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String pathImg = rs.getString("path_img");
                    int rating = rs.getInt("rating");
                    int likes = rs.getInt("likes");

                    Recipe recipe = new Recipe(recipeId, userId, name, description, pathImg, rating, likes);
                    recipes.add(recipe);
                }

                rs.close();
                ps.close();
            }
        } catch (Exception e) {
            System.out.println("Failed to get user followeds recipes: " + e.toString());
        }

        return recipes;
    }

    @Override
    public Category findCategoryByName(String category_name) {
        Category category = null;

        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM category WHERE name = ?");
            ps.setString(1, category_name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long categoryId = rs.getLong("id");
                String categoryName = rs.getString("name");
                category = new Category(categoryId, categoryName);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Failed to find category by name: " + e.toString());
        }

        return category;
    }

    @Override
    public boolean linkCategoryToRecipe(long id_category, long id_recipe) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO recipe_category (id_recipe, id_category) VALUES (?, ?)");
            ps.setLong(1, id_recipe);
            ps.setLong(2, id_category);
            int rowsAffected = ps.executeUpdate();
            ps.close();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Failed to link category to recipe: " + e.toString());
        }

        return false;
    }

    @Override
    public List<Recipe> searchRecipesFromCategory(String category_name) {
        List<Recipe> recipes = new ArrayList<>();

        try (Connection conn = MariaDBConnection.getConnection()) {
            // Obtener el ID de la categoría sin distinguir entre mayúsculas y minúsculas
            PreparedStatement psCategory = conn.prepareStatement("SELECT id FROM category WHERE LOWER(name) = LOWER(?)");
            psCategory.setString(1, category_name);
            ResultSet rsCategory = psCategory.executeQuery();

            if (rsCategory.next()) {
                long categoryID = rsCategory.getLong("id");

                // Obtener los IDs de las recetas que pertenecen a la categoría
                PreparedStatement psRecipeCategory = conn.prepareStatement("SELECT id_recipe FROM recipe_category WHERE id_category = ?");
                psRecipeCategory.setLong(1, categoryID);
                ResultSet rsRecipeCategory = psRecipeCategory.executeQuery();

                List<Long> recipeIDs = new ArrayList<>();
                while (rsRecipeCategory.next()) {
                    recipeIDs.add(rsRecipeCategory.getLong("id_recipe"));
                }

                rsRecipeCategory.close();
                psRecipeCategory.close();

                if (!recipeIDs.isEmpty()) {
                    String recipeIDsString = recipeIDs.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));

                    // Obtener las recetas de la tabla recipe
                    String query = "SELECT * FROM recipe WHERE id IN (" + recipeIDsString + ")";
                    PreparedStatement psRecipe = conn.prepareStatement(query);
                    ResultSet rsRecipe = psRecipe.executeQuery();

                    while (rsRecipe.next()) {
                        long recipeId = rsRecipe.getLong("id");
                        long userId = rsRecipe.getLong("id_user");
                        String name = rsRecipe.getString("name");
                        String description = rsRecipe.getString("description");
                        String pathImg = rsRecipe.getString("path_img");
                        int rating = rsRecipe.getInt("rating");
                        int likes = rsRecipe.getInt("likes");

                        Recipe recipe = new Recipe(recipeId, userId, name, description, pathImg, rating, likes);
                        recipes.add(recipe);
                    }

                    rsRecipe.close();
                    psRecipe.close();
                }
            }

            rsCategory.close();
            psCategory.close();
        } catch (Exception e) {
            System.out.println("Failed to search recipes from category: " + e.toString());
        }

        return recipes;
    }


    @Override
    public boolean deleteCategoriesToRecipe(long id_recipe) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            // Eliminar los registros de la tabla recipe_category
            PreparedStatement psDelete = conn.prepareStatement("DELETE FROM recipe_category WHERE id_recipe = ?");
            psDelete.setLong(1, id_recipe);
            int rowsAffected = psDelete.executeUpdate();
            psDelete.close();

            // Verificar si se eliminaron registros
            return rowsAffected >= 0; // Devolver true cuando no se afecte ninguna fila
        } catch (Exception e) {
            System.out.println("Failed to delete categories to recipe: " + e.toString());
            return false;
        }
    }


}
