package com.cookim.cookimws.endpoints;

import com.cookim.cookimws.model.Model;
import com.cookim.cookimws.model.User;
import com.cookim.cookimws.utils.DataResult;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Samuel
 */
public class UserAccessMethods {

    private final Model model;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccessMethods.class);

    public UserAccessMethods(Model model) {
        this.model = model;
    }

    public void registerEndpoints(Javalin app) {
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/configEndpoints.properties"));
            
            
            app.get(props.getProperty("users"), this::getAllUsers);
            app.post(props.getProperty("get_user_home_data"), this::getUserHomeData);
            app.post(props.getProperty("modify_user"), this::modifyUser);
            app.post(props.getProperty("delete_user"), this::deleteUser);
            app.post(props.getProperty("create_user"), this::createUser);
            app.post(props.getProperty("auto_login"), this::autoLogin);
            app.post(props.getProperty("login"), this::login);
            app.post(props.getProperty("like_recipe"), this::likeRecipe);
            app.post(props.getProperty("logout"), this::logout);
            app.post(props.getProperty("user_profile"), this::getUserProfile);
            app.post(props.getProperty("my_profile"), this::myProfile);
            app.post(props.getProperty("user_favorite_recipes"), this::favoriteRecipes);
            app.post(props.getProperty("send_favorite_recipes"), this::getFavoriteRecipes);
            app.post(props.getProperty("follow"), this::userFollow);
            
        } catch (IOException ex) {
            LOGGER.error("Error loading properties file: {}", ex.getMessage());
        }

    }

    /**
     * Retrieves all users.
     */
    public void getAllUsers(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP GET request on the route: {}", ctx.path());

        // Retrieve the list of users from the model
        List<User> userList = model.getAllUsers();

        // Convert the user list to JSON using Gson
        Gson gson = new Gson();

        // Set the JSON response as the result of the context
        ctx.result(gson.toJson(userList));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Retrieves home data for a user.
     */
    public void getUserHomeData(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract the token from the authorization header
        String token = ctx.header("Authorization").replace("Bearer ", "");
        LOGGER.info("The user with the token: {} tries to get into his profile...", token);

        // Check if the user is authenticated by the provided token
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            // Return authentication failure response
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve user home data based on the token
        DataResult result = model.getUserByToken(token);
        LOGGER.info("The user with the token: {} goes to his profile", token);
        LOGGER.info("Result of the request: {}", result.toString());

        // Convert the result to JSON using Gson and set it as the response
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Modifies a user's information.
     */
    public void modifyUser(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        
        // Extract credentials from the authorization header
        String data = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = data.split(":");
        String token = parts[0];
        String username = parts[1];
        String full_name = parts[2];
        String email = parts[3];
        String phone = parts[4];


        // Get the uploaded image file
        UploadedFile file = ctx.uploadedFile("img");
        
         // Log the received data
        LOGGER.info("Received data: {}", data);
        
        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            // If the user is not authenticated, return the authentication result
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }


        // Create a new User object with the provided information
        User user = new User(username, full_name, email, phone,token);

        // Modify the user's information in the model
        DataResult result = new DataResult();
        if(file != null && file.size() > 0){
            result = model.modifyUser(user,file);
        }else{
            result = model.modifyUser(user,null);
        }
         
        LOGGER.info("Result of the request: {}", result.toString());

        // Convert the result to JSON using Gson and set it as the response
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Deletes a user account.
     */
    public void deleteUser(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract the token from the authorization header
        String token = ctx.header("Authorization").replace("Bearer ", "");

        // Delete the user account based on the token
        DataResult result = model.deleteUser(token);
        if (result.getResult().equals("1")) {
            // If the account was successfully deleted, log the username of the deleted user
            DataResult logoutUser = model.getUserByToken(token);
            User user = (User) logoutUser.getData();
            LOGGER.info("The user has deleted the user account: {}", user.getUsername());
        } else {
            // If there was an error deleting the account, log the error message
            LOGGER.info("Error deleting account: {}", result.getData());
        }

        // Convert the result to JSON using Gson and set it as the response
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Creates a new user.
     */
    public void createUser(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract form parameters from the context
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String full_name = ctx.formParam("full_name");
        String email = ctx.formParam("email");
        String phone = ctx.formParam("phone");
        String id_rolStr = ctx.formParam("id_rol");
        long id_rol = Long.parseLong(id_rolStr);

        // Get the uploaded image file
        UploadedFile file = ctx.uploadedFile("img");

        // Create a new User object with the provided information
        User user = new User(username, password, full_name, email, phone, id_rol);

        DataResult result;

        if (file == null) {
            // If no image file is uploaded, add the user without an image
            System.out.println("The file is empty");
            result = model.addNewUser(user, null);
        } else {
            // If an image file is uploaded, add the user with the uploaded image
            System.out.println("The file is not empty");
            result = model.addNewUser(user, file);
        }

        System.out.println(result);

        // Convert the result to JSON using Gson and set it as the response
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Performs auto-login for a user.
     */
    public void autoLogin(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract the token from the authorization header
        String token = ctx.header("Authorization").replace("Bearer ", "");

        // Perform auto-login using the provided token
        DataResult result = model.autoLogin(token);

        // Convert the result to JSON using Gson and set it as the response
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Handles user login.
     */
    public void login(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract credentials from the authorization header
        String credentials = ctx.header("Authorization").replace("Bearer ", "");

        // Split credentials into username and password
        String[] parts = credentials.split(":");
        String username = parts[0];
        String password = parts[1];

        LOGGER.info("Received credentials: username={}, password={}", username, password);

        // Validate the user credentials
        DataResult result = model.validateUser(username, password);
        Gson gson = new Gson();

        LOGGER.info("Result: {}", gson.toJson(result));

        // Convert the result to JSON using Gson and set it as the response
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Handles recipe liking.
     */
    public void likeRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract data from the authorization header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String action = parts[1];
        String recipe_id = parts[2];
        int num = Integer.parseInt(action);

        LOGGER.info("Data: {}", data);

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            // If the user is not authenticated, return the authentication result
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        if (num == 1) {
            LOGGER.info("The user is trying to like the recipe");
        } else {
            LOGGER.info("The user is trying to remove the like from the recipe");
        }

        // Like or unlike the recipe based on the action
        DataResult result = model.likeRecipe(token, num, recipe_id);
        Gson gson = new Gson();
        System.out.println(gson.toJson(result));

        // Convert the result to JSON using Gson and set it as the response
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Handles user logout.
     */
    public void logout(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract the token from the authorization header
        String token = ctx.header("Authorization").replace("Bearer ", "");

        // Finish the user session and obtain the result
        DataResult result = model.finishSession(token);

        System.out.println(result.toString());
        Gson gson = new Gson();

        // Convert the result to JSON using Gson and set it as the response
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    
    /**
     * Retrieves the profile of the authenticated user.
     */
    public void myProfile(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract data from the authorization header
        String data = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = data.split(":");
        String token = parts[0];
        String id = parts[1];
        LOGGER.info("Data: {}", token + " " + id);

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            // If the user is not authenticated, return the authentication result
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve all recipes associated with the user token
        DataResult result = model.getAllRecipesByUserToken(token);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    
    /**
     * Retrieves the profile of a specific user.
     */
    public void getUserProfile(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract data from the authorization header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        
        LOGGER.info("Data: {}", data);

        String[] parts = data.split(":");
        String token = parts[0];
        String id = parts[1];

        

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            LOGGER.info("Authenticated: {}", "User not found");
            // If the user is not authenticated, return the authentication result
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve all recipes associated with the user ID
        DataResult result = model.getAllRecipesByUserId(token, id);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    
    /**
     * Saves or removes a recipe as a favorite for a user.
     */
    public void favoriteRecipes(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract data from the authorization header
        String data = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = data.split(":");
        String token = parts[0];
        String action = parts[1];
        String id_recipe = parts[2];

        LOGGER.info("Data: {}", data);

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            LOGGER.info("Authenticated: {}", "User not found");
            // If the user is not authenticated, return the authentication result
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Save or remove the recipe as a favorite for the user
        DataResult result = model.saveFavoriteRecipe(token, id_recipe, action);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    
    /**
     * Manages the follow/unfollow action between users.
     */
    public void userFollow(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract data from the authorization header
        String data = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = data.split(":");
        String token = parts[0];
        String action = parts[1];
        String id_user = parts[2];

        LOGGER.info("Data: {}", token + "--" + id_user);

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            LOGGER.info("Authenticated: {}", "User not found");
            // If the user is not authenticated, return the authentication result
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Manage the follow/unfollow action between users
        DataResult result = model.manageUserFollow(token, id_user, action);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    
    /**
     * Retrieves all favorite recipes for the user.
     */
    public void getFavoriteRecipes(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Extract the token from the authorization header
        String token = ctx.header("Authorization").replace("Bearer ", "");

        LOGGER.info("Data: {}", token);

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            LOGGER.info("Authenticated: {}", "User not found");
            // If the user is not authenticated, return the authentication result
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve all favorite recipes for the user
        DataResult result = model.getAllFavoriteRecipesForUser(token);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

}
