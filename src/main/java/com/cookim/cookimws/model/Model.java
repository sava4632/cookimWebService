package com.cookim.cookimws.model;

import com.cookim.cookimws.CookimWebService;
import com.cookim.cookimws.utils.DataResult;
import com.cookim.cookimws.utils.Utils;
import io.javalin.http.UploadedFile;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author cookimadmin
 */
public class Model {

    UserDaoInterface daoUsers;
    RecipeDaoInterface daoRecipe;
    IngredientDaoInterface daoIngredient;

    public Model() {
        daoUsers = new UserDao();
        daoRecipe = new RecipeDao();
        daoIngredient = new IngredientDao();
        //loadUsers();
    }

    //-----------------------------------------USERS METODS---------------------------------------------------
    //-----------------------------------------USERS METODS---------------------------------------------------
    public List<User> getAllUsers() {
        return daoUsers.findAllUsers();
    }

    public User getUser(String username, String password) {
        return daoUsers.findUser(username, password);
    }

    /**
     * Method that gets a user from the database for their token.
     *
     * @param token the token of the user to look up
     * @return 1 if the user has been found in the database, 0 otherwise.
     */
    public DataResult getUserByToken(String token) {
        DataResult result = new DataResult();
        User user = daoUsers.findUserByToken(token);
        //List<Long> userRecipesLiked = daoUsers.getRecipesIdLiked(token);

        if (user != null) {
            //user.setRecipe_likes(userRecipesLiked);
            result.setResult("1");
            result.setData(user);
        } else {
            result.setResult("0");
            result.setData("Error: token could not be validated");
        }
        return result;
    }

    /**
     * Method that gets a user from the database for their token and ends the
     * session of the user bay removings his token.
     *
     * @param token the token of the user to look up
     * @return 1 if the user has been found in the database, 0 otherwise.
     */
    public DataResult finishSession(String token) {
        DataResult result = new DataResult();
        User user = daoUsers.findUserByToken(token);

        if (user != null) {
            boolean finished = daoUsers.updateUserToken(user, null);
            if (finished) {
                result.setResult("1");
                result.setData("The user session is finished");
            } else {
                result.setResult("0");
                result.setData("The user session was'nt finished");
            }

        } else {
            result.setResult("2");
            result.setData("Error: token could not be validated");
        }
        return result;
    }

    /**
     * Deletes a user based on the provided token.
     *
     * @param token The user's token.
     * @return The result of the operation.
     */
    public DataResult deleteUser(String token) {
        DataResult result = new DataResult();

        // Call the appropriate method in the DAO to delete the user
        boolean deleted = daoUsers.deleteUser(token);

        if (deleted) {
            result.setResult("1");
            result.setData("User deleted successfully");
        } else {
            result.setResult("0");
            result.setData("Failed to delete user");
        }

        return result;
    }

    /**
     * Modifies a user with the provided user object.
     *
     * @param user The user object containing the modified user information.
     * @return The result of the operation.
     */
    public DataResult modifyUser(User user) {
        DataResult result = new DataResult();

        // Call the appropriate method in the DAO to modify the user
        boolean modified = daoUsers.modifyUser(user);

        if (modified) {
            result.setResult("1");
            result.setData("User modified successfully");
        } else {
            result.setResult("0");
            result.setData("Error when trying to modify the user");
        }

        return result;
    }

    /**
     * Adds a new user with the provided user object and uploaded file.
     *
     * @param user The user object containing the new user information.
     * @param file The uploaded file for the user's profile image.
     * @return The result of the operation.
     */
    public DataResult addNewUser(User user, UploadedFile file) {
        DataResult result = new DataResult();

        // Call the appropriate method in the DAO to add the new user
        boolean added = daoUsers.add(user);

        if (added) {
            // Generate a unique token for the user
            String token = Utils.getSHA256(user.getUsername() + user.getPassword() + new Random().nextInt(10000));

            // Get the user with the username and password
            User u = getUser(user.getUsername(), user.getPassword());

            // Update the user's token in the database
            boolean isUpdateToken = daoUsers.updateUserToken(u, token);

            if (isUpdateToken) {
                result.setResult("1");
                result.setData(token);

                // Save the uploaded file with a unique filename
                String path = "/var/www/html/resources/users/";
                String pathUsers = "/resources/users/";
                String uniqueFilename = "default.png";
                File uploadedFile;

                if (file != null && file.size() > 0 && file.filename().toLowerCase().endsWith(".jpg")) {
                    String randomString = RandomStringUtils.randomAlphanumeric(10);
                    String timestamp = Long.toString(System.currentTimeMillis());
                    uniqueFilename = randomString + "-" + timestamp + ".jpg";
                    uploadedFile = new File(path + uniqueFilename);

                    int suffix = 1;
                    while (uploadedFile.exists()) {
                        uniqueFilename = randomString + "-" + timestamp + "-" + suffix + ".jpg";
                        uploadedFile = new File(path + uniqueFilename);
                        suffix++;
                    }

                    try {
                        FileUtils.copyInputStreamToFile(file.content(), uploadedFile);
                        System.out.println("Saving image" + file.filename() + " as: " + uploadedFile);

                        // Update the user with the path to the profile image
                        String pathImage = pathUsers + uniqueFilename;
                        boolean updated = daoUsers.setUserPathPicture(u.getId(), pathImage);

                        if (updated) {
                            System.out.println("User image path updated: " + u.getUsername());
                        } else {
                            System.out.println("Failed to update user image path: " + u.getUsername());
                        }
                    } catch (IOException ex) {
                        System.out.println("Error POST FILE:" + ex.toString());
                        result.setResult("0");
                        result.setData("Failed when trying to upload the image to the server");
                        return result;
                    }

                } else {
                    // Update the user with the path to the default profile image
                    String pathImage = pathUsers + uniqueFilename;
                    boolean updated = daoUsers.setUserPathPicture(u.getId(), pathImage);

                    if (updated) {
                        System.out.println("User image path updated to default image: " + u.getUsername());
                    } else {
                        System.out.println("Failed to update user image path to default image: " + u.getUsername());
                    }
                }

            } else {
                result.setResult("2");
                result.setData("Failed to validate token");
            }
        } else {
            result.setResult("0");
            result.setData("Failed to register new user");
        }

        return result;
    }

    /**
     * Method that verifies if the token sent by the client exists in the
     * database so that the user session is not closed.
     *
     * @param token the token of the user who does the autologin
     * @return 1 if the auto login is successful, 0 otherwise
     */
    public DataResult autoLogin(String token) {
        DataResult result = new DataResult();
        boolean autologed = daoUsers.autoLogin(token);
        if (autologed) {
            result.setResult("1");
            result.setData("user has successfully autologin");
        } else {
            result.setResult("0");
            result.setData("Failed to autologin");
        }
        return result;
    }

    /**
     * validates a user in the database using the parameters provided by the
     * client and if it is in the database it generates a new token
     *
     * @param username the username provided by the client
     * @param password the password provided by the client
     * @return if it is true it returns the new token of the user, otherwise
     * null
     */
    public DataResult validateUser(String username, String password) {
        DataResult result = new DataResult();
        //User u = new User(username, password);
        User u = getUser(username, password);
        String token = Utils.getSHA256(username + password + new Random().nextInt(10000));
        if (u != null) {
            boolean isUpdateToken = daoUsers.updateUserToken(u, token);
            if (isUpdateToken) {
                result.setResult("1");
                result.setData(token);
            }
        } else {
            result.setResult("0");
            result.setData("User not found");
        }

        return result;
    }

    /**
     * Saves or removes a recipe as a favorite for a user.
     *
     * @param token The user's authentication token.
     * @param id_recipe The ID of the recipe.
     * @param action The action to perform: "1" to save as favorite, "0" to
     * remove from favorites.
     * @return The result of the operation.
     */
    public DataResult saveFavoriteRecipe(String token, String id_recipe, String action) {
        DataResult result = new DataResult();

        // Find the user by token
        User user = daoUsers.findUserByToken(token);

        // Call the appropriate method in the DAO to save or remove the recipe as a favorite
        Boolean isSaved = daoUsers.favoriteRecipe(user.getId(), id_recipe, action);

        if (isSaved && action.equals("1")) {
            result.setResult("1");
            result.setData("Recipe has been saved to favorites successfully");
        } else if (isSaved && action.equals("0")) {
            result.setResult("1");
            result.setData("Recipe has been removed from favorites successfully");
        } else if (!isSaved && action.equals("1")) {
            result.setResult("0");
            result.setData("Failed to save recipe to favorites");
        } else if (!isSaved && action.equals("0")) {
            result.setResult("0");
            result.setData("Failed to remove recipe from favorites");
        } else {
            result.setResult("2");
            result.setData("Error when trying to update favorite recipes");
        }

        return result;
    }

    /**
     * Manages the follow or unfollow action of a user.
     *
     * @param token The user's authentication token.
     * @param id_user The ID of the user to follow or unfollow.
     * @param action The action to perform: "1" to follow, "0" to unfollow.
     * @return The result of the operation.
     */
    public DataResult manageUserFollow(String token, String id_user, String action) {
        DataResult result = new DataResult();

        // Find the user by token
        User user = daoUsers.findUserByToken(token);

        // Call the appropriate method in the DAO to manage the user follow
        boolean isUpdate = daoUsers.manageUserFollow(user.getId(), id_user, action);

        if (isUpdate && action.equals("1")) {
            result.setResult("1");
            result.setData("User has been followed successfully");
        } else if (isUpdate && action.equals("0")) {
            result.setResult("1");
            result.setData("User has been unfollowed successfully");
        } else if (!isUpdate && action.equals("1")) {
            result.setResult("0");
            result.setData("Failed to follow user");
        } else if (!isUpdate && action.equals("0")) {
            result.setResult("0");
            result.setData("Failed to unfollow user");
        } else {
            result.setResult("2");
            result.setData("Error when trying to update user follow");
        }

        return result;
    }

    /**
     * Retrieves all favorite recipes for a user.
     *
     * @param token The user's authentication token.
     * @return The result of the operation containing the list of favorite
     * recipes.
     */
    public DataResult getAllFavoriteRecipesForUser(String token) {
        DataResult result = new DataResult();

        // Find the user by token
        User user = daoUsers.findUserByToken(token);

        // Call the appropriate method in the DAO to get the favorite recipes
        List<Recipe> recipes = daoUsers.getFavoriteRecipes(user.getId());

        if (!recipes.isEmpty()) {
            result.setResult("1");
            result.setData(recipes);
        } else {
            result.setResult("2");
            result.setData("User recipe list is empty");
        }

        return result;
    }

    //--------------------------------------------------RECIPES-------------------------------------------------------------
    //--------------------------------------------------RECIPES-------------------------------------------------------------
    /**
     * Retrieves all recipes.
     *
     * @return The result of the operation containing the list of recipes.
     */
    public DataResult getAllRecipes() {
        DataResult result = new DataResult();

        // Call the appropriate method in the DAO to get all recipes
        List<Recipe> recipes = daoRecipe.findAllRecipes();

        if (recipes != null) {
            result.setResult("1");
            result.setData(recipes);
            System.out.println(recipes);
        } else {
            result.setResult("0");
            result.setData("Failed to get recipes");
        }

        return result;
    }

    /**
     * Likes or unlikes a recipe.
     *
     * @param token The user token.
     * @param action The action to perform (1 for like, 0 for unlike).
     * @param id_recipe The ID of the recipe.
     * @return The result of the operation containing the updated number of
     * likes for the recipe.
     */
    public DataResult likeRecipe(String token, int action, String id_recipe) {
        DataResult result = new DataResult();

        // Call the appropriate method in the DAO to like or unlike the recipe
        boolean isLiked = daoUsers.userLikeRecipe(token, id_recipe, action);

        if (isLiked) {
            int numLikes = daoRecipe.getNumLikes(id_recipe);
            boolean updated = daoRecipe.updateLikes(id_recipe, numLikes);
            if (updated) {
                Recipe recipe = daoRecipe.findRecipeById(id_recipe); // Update the total likes to send to the client after liking
                result.setResult("1");
                result.setData(recipe.getLikes());
            } else {
                result.setResult("0");
                result.setData("Recipe not updated");
            }
        } else {
            result.setResult("0");
            result.setData("Recipe not updated");
        }

        return result;
    }

    /**
     * Retrieves all recipes belonging to a specific category.
     *
     * @param idCategory The ID of the category.
     * @return The result of the operation containing the list of recipes.
     */
    public DataResult getAllRecipesByCategory(String idCategory) {
        DataResult result = new DataResult();

        // Call the appropriate method in the DAO to retrieve recipes by category
        List<Recipe> recipes = daoRecipe.findAllRecipesByCategory(idCategory);

        if (recipes.isEmpty()) {
            result.setResult("2");
            result.setData("Empty recipe list");
        } else if (recipes != null) {
            result.setResult("1");
            result.setData(recipes);
        } else {
            result.setResult("0");
            result.setData("Failed to get recipes by category");
        }

        return result;
    }

    /**
     * Retrieves all recipes along with their corresponding users.
     *
     * @return The result of the operation containing the list of recipes with
     * users.
     */
    public DataResult getAllRecipesWithUser(String token) {
        DataResult result = new DataResult();

        User user = daoUsers.findUserByToken(token);
        // Call the appropriate method in the DAO to retrieve recipes with users
        List<Recipe> recipes = daoRecipe.findAllRecipesWithUser();

        if (recipes.isEmpty()) {
            result.setResult("2");
            result.setData("Empty recipe list");
        } else if (recipes != null) {
            
            for (Recipe recipe : recipes) {
                //Comprueba si le ha dado like a la receta
                boolean isLiked = daoRecipe.existsUserRecipeLiked(user.getId(),recipe.getId());
                recipe.setLiked(isLiked);
                
                //comprueba si tiene la receta como favorita
                boolean isSaved = daoRecipe.existsUserRecipeSaved(user.getId(), recipe.getId());
                recipe.setSaved(isSaved);
            }
            
            result.setResult("1");
            result.setData(recipes);
        } else {
            result.setResult("0");
            result.setData("Failed to get all recipes with users");
        }

        return result;
    }

    /**
     * Adds a new recipe to the system.
     *
     * @param recipe The recipe to be added.
     * @param token The user token for authentication.
     * @param file The uploaded image file for the recipe (optional).
     * @return The result of the operation indicating if the recipe was added
     * successfully.
     */
    public DataResult addNewRecipe(Recipe recipe, String token, UploadedFile file) {
        DataResult result = new DataResult();

        // Add the recipe to the database
        boolean added = daoRecipe.addRecipe(token, recipe);
        Recipe rp = daoRecipe.findRecipeByUserTokenAndRecipe(token, recipe);

        if (added) {
            // Upload recipe image to the server (NGINX)
            // Save the uploaded file with the unique filename
            String path = "/var/www/html/resources/recipes/";
            String pathRecipe = "/resources/recipes/";
            String uniqueFilename = "default.png";
            File uploadedFile;

            if (file != null && file.size() > 0) {
                String randomString = RandomStringUtils.randomAlphanumeric(10);
                String timestamp = Long.toString(System.currentTimeMillis());
                uniqueFilename = randomString + "-" + timestamp + ".jpg";
                uploadedFile = new File(path + uniqueFilename);

                int suffix = 1;
                while (uploadedFile.exists()) {
                    uniqueFilename = randomString + "-" + timestamp + "-" + suffix + ".jpg";
                    uploadedFile = new File(path + uniqueFilename);
                    suffix++;
                }

                try {
                    FileUtils.copyInputStreamToFile(file.content(), uploadedFile);
                    System.out.println("Saving image" + file.filename() + " as: " + uploadedFile);

                    // Update the recipe with the path to the recipe image
                    String pathImage = pathRecipe + uniqueFilename;
                    boolean updated = daoRecipe.setRecipePathImage(rp.getId(), pathImage);

                    if (updated) {
                        System.out.println("Recipe image path updated: " + rp.getName());
                    } else {
                        System.out.println("Failed to update recipe image path: " + rp.getName());
                    }
                } catch (IOException ex) {
                    System.out.println("Error POST FILE:" + ex.toString());
                    result.setResult("0");
                    result.setData("Failed when trying to upload the image to the server");
                    return result;
                }
            }

            result.setResult("1");
            result.setData("Recipe added successfully");
            result.setRecipeId(rp.getId());
        } else {
            result.setResult("0");
            result.setData("Failed when trying to add a new recipe");
        }

        return result;
    }

    /**
     * Method that removes a recipe from the database .
     *
     * @param id the id of the recipe to delete
     * @return 1 if the recipe was added successfully, 0 otherwise
     */
    public DataResult deleteRecipe(String token, String id_recipe) {
        DataResult result = new DataResult();
        Utils utils = new Utils();

        User user = daoUsers.findUserByToken(token);
        Recipe recipe = daoRecipe.findRecipeById(id_recipe);
        System.out.println("Recipe: " + recipe.toString());
        List<Step> steps = daoRecipe.findAllStepsByRecipe(id_recipe);

        if (recipe != null) {
            // Remove steps of the recipe
            boolean isRemovedSteps = daoRecipe.deleteStepsByRecipe(recipe.getId());
            if (isRemovedSteps) {
                System.out.println("Se han eliminado los pasos de la receta");
            } else {
                System.out.println("No se han podido eliminar los pasos de la receta");
                result.setResult("0");
                result.setData("Failed when trying to remove steps");
                return result;
            }

            // Remove image files for each step
            if (!steps.isEmpty()) {
                for (Step step : steps) {
                    String pathImg = "/var/www/html" + step.getPath();
                    System.out.println("Removing step image: " + pathImg);
                    if (pathImg != null && !pathImg.equals("/var/www/html/resources/recipes/recipeSteps/default.jpg")) {
                        // Delete file from the server using the path
                        utils.deleteFileFromServer(pathImg);
                    }
                }
            }

            // Remove the recipe from the database
            boolean isRemovedRecipe = daoRecipe.deleteRecipe(user.getId(), id_recipe);

            // If the recipe and all steps were removed successfully, delete the recipe image
            if (isRemovedRecipe && utils.areAllStepImagesRemoved(steps)) {
                String recipeImg = "/var/www/html" + recipe.getPath_img();
                System.out.println("Removing recipe image: " + recipeImg);
                if (recipeImg != null && !recipeImg.equals("/var/www/html/resources/recipes/default.jpg")) {
                    // Delete recipe image from the server using the path
                    utils.deleteFileFromServer(recipeImg);
                }
            }

            if (isRemovedRecipe) {
                result.setResult("1");
                result.setData("Recipe removed successfully");
            } else {
                result.setResult("0");
                result.setData("Failed when trying to remove recipe");
            }
        } else {
            result.setResult("3");
            result.setData("No se ha encontrado una receta con ese id");
        }

        return result;
    }



    /**
     * Method to modify an already existing line in the database.
     *
     * @param recipe the modified recipe
     * @return 1 if the recipe is modified successfully, 0 otherwise
     */
    public DataResult modifyRecipe(Recipe recipe) {
        DataResult result = new DataResult();
        boolean modified = daoRecipe.modifyRecipe(recipe);

        if (modified) {
            result.setResult("1");
            result.setData("Recipe modified successfully");
        } else {
            result.setResult("0");
            result.setData("Failed when trying to modify recipe");
        }
        return result;
    }

    /**
     * Method that returns a complete recipe with its list of ingredients and
     * another list with all its steps.
     *
     * If any recipe or step does not have an image assigned in the database,
     * this method assigns the image by default.
     *
     * @param id the id of the recipe for which you want to see information.
     * @return Dataresult class with a 1 and the complete recipe if everything
     * is done correctly and a 0 with a message otherwise.
     */
    public DataResult findFullRecipe(String id) {
        DataResult result = new DataResult();

        Recipe recipe = daoRecipe.findRecipeById(id);
        if (recipe.getPath() == null) {
            recipe.setPath("/resources/users/default.png");
        }
        List<Ingredient> ingredients = daoRecipe.findAllIngredientsByRecipe(id);
        List<Step> steps = daoRecipe.findAllStepsByRecipe(id);

        for (Step s : steps) {
            if (s.getPath() == null) {
                s.setPath("/resources/recipes/recipeSteps/default.jpg");
            }
        }

        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);

        if (recipe != null) {

            result.setResult("1");
            result.setData(recipe);

        } else {
            result.setResult("0");
            result.setData("error finding Recipe");

        }

        return result;
    }

    /**
     *
     * This method retrieves all recipes associated with a user token.
     *
     * @param token The token of the user.
     *
     * @return DataResult An object containing the result of the operation and
     * the user's recipes.
     */
    public DataResult getAllRecipesByUserToken(String token) {
        DataResult result = new DataResult();

        // Find the user using the provided token
        User user = daoUsers.findUserByToken(token);

        // Find all recipes associated with the user token
        List<Recipe> recipes = daoRecipe.findAllRecipesByUserToken(token);

        // Set the recipes for the user
        user.setRecipes(recipes);

        // Check if the user was found
        if (user != null) {
            result.setResult("1");
            result.setData(user);
        } else {
            // User not found
            result.setResult("0");
            result.setData("Failed when trying to load the entire user profile");
        }

        return result;
    }

    /**
     *
     * This method retrieves all recipes associated with a specific user ID and
     * provides information about following status.
     *
     * @param token The token of the current user.
     *
     * @param id The ID of the user whose recipes are to be retrieved.
     *
     * @return DataResult An object containing the result of the operation, the
     * user's recipes, and the following status.
     */
    public DataResult getAllRecipesByUserId(String token, String id) {
        DataResult result = new DataResult();

        // Find the current user using the provided token
        User this_user = daoUsers.findUserByToken(token);

        // Find the user whose recipes are to be retrieved using the provided ID
        User user = daoUsers.findUserById(id);

        // Find all recipes associated with the user ID
        List<Recipe> recipes = daoRecipe.findAllRecipesByUserId(id);

        // Check if the current user is following the user
        boolean isFollowing = daoUsers.checkFollow(this_user.getId(), id);

        // Set the recipes and following status for the user
        user.setRecipes(recipes);
        user.setFollow(isFollowing);

        // Check if the user was found
        if (user != null) {
            result.setResult("1");
            result.setData(user);
        } else {
            // User not found
            result.setResult("0");
            result.setData("Failed when trying to load the entire user profile");
        }

        return result;
    }

    /**
     *
     * This method processes a step of a recipe, including adding the step to
     * the recipe and handling the associated step image file.
     *
     * @param step The step to be added.
     *
     * @param stepFile The uploaded step image file.
     *
     * @return DataResult An object containing the result of the operation.
     */
    public DataResult processStepOfRecipe(Step step, UploadedFile stepFile) {
        DataResult result = new DataResult();

        // Add the step to the recipe
        boolean isAdded = daoRecipe.addStepsRecipe(step);

        // Find the step by its content
        Step st = daoRecipe.findStepbyStep(step);

        if (isAdded && st != null) {
            result.setResult("1");
            result.setData("Step added successfully");
            // Define the file path for storing the step image
            String path = "/var/www/html/resources/recipes/recipeSteps/";
            String pathStep = "/resources/recipes/recipeSteps/";
            String uniqueFilename = "default.jpg";
            File uploadedFile;

            // Check if a step image file was provided
            if (stepFile != null && stepFile.size() > 0) {
                // Generate a unique filename based on random alphanumeric characters and timestamp
                String randomString = RandomStringUtils.randomAlphanumeric(10);
                String timestamp = Long.toString(System.currentTimeMillis());
                uniqueFilename = randomString + "-" + timestamp + ".jpg";
                uploadedFile = new File(path + uniqueFilename);

                // Handle potential filename conflicts by appending a suffix
                int suffix = 1;
                while (uploadedFile.exists()) {
                    uniqueFilename = randomString + "-" + timestamp + "-" + suffix + ".jpg";
                    uploadedFile = new File(path + uniqueFilename);
                    suffix++;
                }

                try {
                    // Save the step image file to the server
                    FileUtils.copyInputStreamToFile(stepFile.content(), uploadedFile);
                    System.out.println("Saving image " + stepFile.filename() + " as: " + uploadedFile);

                    // Update the step with the path to the uploaded image
                    String pathImage = pathStep + uniqueFilename;
                    boolean updated = daoRecipe.setStepPathImage(st.getId(), pathImage);

                    if (updated) {
                        System.out.println("Step image path updated: " + st.getId());
                    } else {
                        System.out.println("Failed to update step image path: " + st.getId());
                    }
                } catch (IOException ex) {
                    System.out.println("Error POST FILE: " + ex.toString());
                    result.setResult("0");
                    result.setData("Failed when trying to upload the image to the server");
                    return result;
                }
            } else {
                // Update the step with the path to the default image when no step image file is provided
                String pathImage = pathStep + uniqueFilename;
                boolean updated = daoRecipe.setStepPathImage(st.getId(), pathImage);

                if (updated) {
                    System.out.println("Step image path updated to default image: " + st.getId());
                } else {
                    System.out.println("Failed to update step image path to default image: " + st.getId());
                }
            }

        } else {
            result.setResult("0");
            result.setData("Failed to add step");
        }

        return result;
    }

    /**
     *
     * This method processes an ingredient for a recipe, including adding the
     * ingredient to the database and linking it to a specific recipe.
     *
     * @param ingredient The ingredient to be added.
     *
     * @param recipeId The ID of the recipe to which the ingredient will be
     * linked.
     *
     * @return DataResult An object containing the result of the operation.
     */
    public DataResult processIngredientOfRecipe(Ingredient ingredient, long recipeId) {
        DataResult result = new DataResult();

        // Add the ingredient to the database
        boolean isCreated = daoIngredient.addNewIngredient(ingredient);

        // Find the ingredient by its name
        Ingredient ig = daoIngredient.findIngredientByName(ingredient.getName());

        // Link the ingredient to the recipe
        boolean isLinked = daoRecipe.linkIngredientToRecipe(ig, recipeId);

        if (isLinked) {
            result.setResult("1");
            result.setData("Ingredient linked to the recipe correctly");
        } else {
            result.setResult("0");
            result.setData("The ingredient could not be linked to the recipe");
        }

        return result;
    }

    /**
     *
     * This method retrieves recipes that are equal to the provided text.
     *
     * @param text The text to search for.
     *
     * @return DataResult An object containing the result of the operation and
     * the matching recipes.
     */
    public DataResult getRecipesEqualToText(String text) {
        DataResult result = new DataResult();

        try {
            // Search for recipes matching the provided text
            List<Recipe> recipes = daoRecipe.searchRecipesLikeText(text);
            if (recipes.isEmpty()) {
                // No matches found
                result.setResult("2");
                result.setData("No matches found for: " + text);
            } else {
                // Matches found
                result.setResult("1");
                result.setData(recipes);
            }
        } catch (Exception ex) {
            System.out.println("Failed to get recipes: " + ex.getMessage());
            result.setResult("0");
            result.setData("Failed when trying to get the recipes by: " + text);
        }

        return result;
    }

    /**
     *
     * This method adds a comment to a recipe.
     *
     * @param token The token of the user adding the comment.
     *
     * @param comment The comment to be added.
     *
     * @return DataResult An object containing the result of the operation.
     */
    public DataResult AddCommentToRecipe(String token, Comment comment) {
        DataResult result = new DataResult();
        User user = daoUsers.findUserByToken(token);

        // Set the user ID for the comment
        comment.setId_user(user.getId());

        // Set the current date and time for the comment
        comment.setData_send(new Date());

        // Add the comment to the database
        boolean isAdded = daoRecipe.addNewComment(comment);

        if (isAdded) {
            result.setResult("1");
            result.setData("Comment added successfully");
        } else {
            result.setResult("0");
            result.setData("Failed to add comment");
        }

        return result;
    }

    /**
     *
     * This method retrieves all parent comments for a specific recipe.
     *
     * @param id_recipe The ID of the recipe.
     *
     * @return DataResult An object containing the result of the operation and
     * the parent comments.
     */
    public DataResult getAllRecipeParentComments(String id_recipe) {
        DataResult result = new DataResult();

        try {
            // Find all parent comments for the recipe ID
            List<Comment> comments = daoRecipe.findAllParentCommentByRecipeId(id_recipe);
            if (!comments.isEmpty()) {
                // Parent comments found
                result.setResult("1");
                result.setData(comments);
            } else {
                // No comments found for the recipe
                result.setResult("2");
                result.setData("No comments found for this recipe.");
            }
        } catch (Exception ex) {
            result.setResult("0");
            result.setData("Failed to retrieve comments for this recipe.");
            System.out.println("Error in getAllRecipeParentComments: " + ex.getMessage());
        }

        return result;
    }

    //-------------------------------------CATEGORIES-------------------------------------------------
    //-------------------------------------CATEGORIES-------------------------------------------------
    //-------------------------------------INGREDIENTS-------------------------------------------------
    //-------------------------------------INGREDIENTS-------------------------------------------------
    /**
     *
     * This method retrieves all ingredients with an ID less than or equal to
     * the specified maximum ID.
     *
     * @param idmax The maximum ID for the ingredients.
     *
     * @return DataResult An object containing the result of the operation and
     * the list of ingredients.
     */
    public DataResult getAllIngredientsWithIdMax(String idmax) {
        DataResult result = new DataResult();

        // Retrieve all ingredients with ID less than or equal to idmax
        List<Ingredient> ingredients = daoIngredient.getAllIngredientsWithIdMax(idmax);

        if (ingredients != null) {
            // Ingredients list successfully retrieved
            result.setResult("1");
            result.setData(ingredients);
        } else {
            // Failed to retrieve ingredients list (null)
            result.setResult("0");
            result.setData("Failed to list ingredients list because it is null");
        }

        return result;
    }

}
