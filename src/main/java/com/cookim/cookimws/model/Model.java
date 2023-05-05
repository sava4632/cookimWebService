package com.cookim.cookimws.model;

import com.cookim.cookimws.utils.DataResult;
import com.cookim.cookimws.utils.Utils;
import io.javalin.http.UploadedFile;
import java.io.File;
import java.io.IOException;
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

        if (user != null) {
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
     *
     * @param username
     * @return
     */
    public DataResult deleteUser(String token) {
        DataResult result = new DataResult();
        boolean deleted = daoUsers.deleteUser(token);
        if (deleted) {
            result.setResult("1");
            result.setData("User deleted successfully");
        } else {
            result.setResult("0");
            result.setData("Failed to try to delete user");
        }
        return result;
    }

    public DataResult modifyUser(User user) {
        DataResult result = new DataResult();
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
     *
     * Method that adds a new user to the database and assigns him his first
     * token. It also encrypts and saves the user's profile image to the server,
     * and updates the user's record in the database with the path to the stored
     * image. If no image is provided, it assigns the default profile image to
     * the user.
     *
     * @param user the user to add to the database.
     * @param file the uploaded profile image.
     * @return a DataResult object with a result code and message indicating
     * success or failure, as well as the token if the user was added
     * successfully.
     */
    public DataResult addNewUser(User user, UploadedFile file) {
        DataResult result = new DataResult();
        boolean added = daoUsers.add(user);

        if (added) {
            String token = Utils.getSHA256(user.getUsername() + user.getPassword() + new Random().nextInt(10000));
            User u = getUser(user.getUsername(), user.getPassword());
            boolean isUpdateToken = daoUsers.updateUserToken(u, token);

            if (isUpdateToken) {
                result.setResult("1");
                result.setData(token);

                // Save the uploaded file with the unique filename
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
                        result.setData("Failed when trying to upload the image to server");
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

    public DataResult getUserProfileImage(String token) {
        DataResult result = new DataResult();
        User user = daoUsers.findUserByToken(token);
        String imageUrl = "http://192.168.127.80/users";

        if (user != null) {
            if (user.getPath_img().equals("")) {
                result.setResult("2");
                result.setData(imageUrl.concat("/default"));
            } else if (user.getPath_img() != null) {
                result.setResult("1");
                result.setData(imageUrl.concat(user.getPath_img()));
            } else {
                result.setResult("0");
                result.setData("Failed when trying to load the image");
            }
        } else {
            result.setResult("0");
            result.setData("Failed when trying to load the image - validate token");
        }

        return result;
    }

    
    //--------------------------------------------------RECIPES-------------------------------------------------------------
    //--------------------------------------------------RECIPES-------------------------------------------------------------
    public DataResult getAllRecipes() {
        DataResult result = new DataResult();
        List<Recipe> recipes = daoRecipe.findAllRecipes();

        if (recipes != null) {
            result.setResult("1");
            result.setData(recipes);
            System.out.println(recipes);
        } else {
            result.setResult("0");
            result.setData("Failed to get reciepes");
        }
        return result;
    }

    public DataResult likeRecipe(int num, String id) {
        DataResult result = new DataResult();

        Recipe recipe = daoRecipe.findRecipeById(id);

        if (recipe != null) {
            System.out.println("Recipe founded");
            System.out.println("like Intent");
            Boolean response = daoRecipe.likeRecipe(num, recipe);
            if (response) {
                result.setResult("1");
                result.setData(recipe.getLikes());
            } else {
                result.setResult("0");
                result.setData("recipe not updated");
            }

        } else {
            result.setResult("2");
            result.setData("recipe not found");
        }

        return result;
    }

    public DataResult getAllRecipesByCategory(String idCategory) {
        DataResult result = new DataResult();
        List<Recipe> recipes = daoRecipe.findAllRecipesByCategory(idCategory);

        if (recipes.isEmpty()) {
            result.setResult("2");
            result.setData("Empty recipe list");
        } else if (recipes != null) {
            result.setResult("1");
            result.setData(recipes);
        } else {
            result.setResult("0");
            result.setData("Failed to get reciepes by category");
        }
        return result;
    }

    /**
     * A method that fetches all recipes with their publishers.
     *
     * @return 2 if the database list is empty, 1 if the recipes could be listed
     * correctly and 0 otherwise.
     */
    public DataResult getAllRecipesWithUser() {
        DataResult result = new DataResult();
        List<Recipe> recipes = daoRecipe.findAllRecipesWithUser();

        if (recipes.isEmpty()) {
            result.setResult("2");
            result.setData("Empty recipe list");
        } else if (recipes != null) {
            result.setResult("1");
            result.setData(recipes);
        } else {
            result.setResult("0");
            result.setData("Failed to get all users reciepes");
        }
        return result;
    }

    /**
     * method that receives the token from the user who wants to add a new
     * recipe, if that token is valid in the database it will take the
     * attributes of the recipe sent by the user and create a new recipe to be
     * added to the database.
     *
     * @param recipe the new recipe to add
     * @return 1 if the recipe was added successfully, 0 otherwise
     */
    public DataResult addNewRecipe(Recipe recipe,String token) {
        DataResult result = new DataResult();
        
//        Recipe r = new Recipe(recipe.getName(),recipe.getDescription(),recipe.getLikes());
//        boolean added = daoRecipe.addRecipe(token,r);  
//        Recipe rp = daoRecipe.findRecipeByUserTokenAndRecipe(token,recipe);
//
//        
//
//        if (added && recipe.getFile() != null) {
//            //UPLOAD RECIPE IMAGE TO SERVER NGINX
//            // Save the uploaded file with the unique filename
//            UploadedFile file = recipe.getFile();
//            String path = "/var/www/html/resources/recipes/";
//            String pathRecipe = "/resources/recipes/";
//            String uniqueFilename = "default.png";
//            File uploadedFile;
//            
//           
//            if (file != null && file.size() > 0 && file.filename().toLowerCase().endsWith(".jpg")) {
//                    String randomString = RandomStringUtils.randomAlphanumeric(10);
//                    String timestamp = Long.toString(System.currentTimeMillis());
//                    uniqueFilename = randomString + "-" + timestamp + ".jpg";
//                    uploadedFile = new File(path + uniqueFilename);
//
//                    int suffix = 1;
//                    while (uploadedFile.exists()) {
//                        uniqueFilename = randomString + "-" + timestamp + "-" + suffix + ".jpg";
//                        uploadedFile = new File(path + uniqueFilename);
//                        suffix++;
//                    }
//
//                    try {
//                        FileUtils.copyInputStreamToFile(file.content(), uploadedFile);
//                        System.out.println("Saving image" + file.filename() + " as: " + uploadedFile);
//
//                        // Update the user with the path to the profile image
//                        String pathImage = pathRecipe + uniqueFilename;
//                        boolean updated = daoRecipe.setRecipePathImage(rp.getId(), pathImage);
//
//                        if (updated) {
//                            System.out.println("Recipe image path updated: " + rp.getName());
//                        } else {
//                            System.out.println("Failed to update recipe image path: " + rp.getName());
//                        }
//                    } catch (IOException ex) {
//                        System.out.println("Error POST FILE:" + ex.toString());
//                        result.setResult("0");
//                        result.setData("Failed when trying to upload the image to server");
//                        return result;
//                    }
//
//                } else {
//                    // Update the user with the path to the default profile image
//                    String pathImage = pathRecipe + uniqueFilename;
//                    boolean updated = daoUsers.setUserPathPicture(rp.getId(), pathImage);
//
//                    if (updated) {
//                        System.out.println("Recipe image path updated to default image: " + rp.getName());
//                    } else {
//                        System.out.println("Failed to update recipe image path to default image: " + rp.getName());
//                    }
//                }
//                //ADD INGREDIENTS TO RECIPE
//                List<Ingredient> ingredients = recipe.getIngredients();
//               
//                //ADD STEPS TO RECIPE
//                List<Step> steps = recipe.getSteps();
//                
//            result.setResult("1");
//            result.setData("Recipe added successfully");
//        } else {
//            if (recipe.getFile() == null) {
//                System.out.println("El FIlE ES NULO");
//                System.out.println(recipe.getFile().content().toString());
//            }
//            result.setResult("0");
//            result.setData("Failed when trying to add a new recipe");
//        }
        return result;
    }

    /**
     * Method that removes a recipe from the database .
     *
     * @param id the id of the recipe to delete
     * @return 1 if the recipe was added successfully, 0 otherwise
     */
    public DataResult deleteRecipe(String id) {
        DataResult result = new DataResult();
        boolean removed = daoRecipe.deleteRecipe(id);

        if (removed) {
            result.setResult("1");
            result.setData("Recipe removed successfully");
        } else {
            result.setResult("0");
            result.setData("Failed when trying to remove recipe");
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
     * Method that returns a complete recipe with its list of ingredients and another list with all its steps.
     * 
     * If any recipe or step does not have an image assigned in the database, this method assigns the image by default.
     * 
     * @param id the id of the recipe for which you want to see information.
     * @return Dataresult class with a 1 and the complete recipe if everything is done correctly and a 0 with a message otherwise.
     */
    public DataResult findFullRecipe(String id) {
        DataResult result = new DataResult();

        Recipe recipe = daoRecipe.findRecipeById(id);
        if(recipe.getPath() == null){
            recipe.setPath("/resources/users/default.png");
        }
        List<Ingredient> ingredients = daoRecipe.findAllIngredientsByRecipe(id);
        List<Step> steps = daoRecipe.findAllStepsByRecipe(id);
        
        for(Step s : steps){
            if(s.getPath() == null){
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
    
    public DataResult getAllRecipesByUserToken(String token) {
        DataResult result = new DataResult();
        User user =  daoUsers.findUserByToken(token);
        List<Recipe> recipes = daoRecipe.findAllRecipesByUserToken(token);
        
        user.setRecipes(recipes);
        
        if (user != null) {
            result.setResult("1");
            result.setData(user);
            
        }else{
            result.setResult("0");
            result.setData("Failed when trying to load the entire user profile");
        }
        return result;
    }

    //-------------------------------------CATEGORIES-------------------------------------------------
    //-------------------------------------CATEGORIES-------------------------------------------------
    
    
    //-------------------------------------INGREDIENTS-------------------------------------------------
    //-------------------------------------INGREDIENTS-------------------------------------------------
    
    public DataResult getAllIngredientsWithIdMax(String idmax) {
        DataResult result = new DataResult();
        List<Ingredient> ingredients = daoIngredient.getAllIngredientsWithIdMax(idmax);
        
        if (ingredients != null) {
            result.setResult("1");
            result.setData(ingredients);
            
        }else{
            result.setResult("0");
            result.setData("Failed to list ingredients list because it is null");
        }
        return result;
    }
}
