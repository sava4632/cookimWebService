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
        } catch (IOException ex) {
            LOGGER.error("Error loading properties file: {}", ex.getMessage());
        }

    }

    public void getAllUsers(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP GET request on the route: {}", ctx.path());
        List<User> userList = model.getAllUsers();
        Gson gson = new Gson();
        ctx.result(gson.toJson(userList));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void getUserHomeData(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.header("Authorization").replace("Bearer ", "");
        LOGGER.info("The user with the token: {} tries to get into his profile...", token);
        
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        DataResult result = model.getUserByToken(token);
        LOGGER.info("The user with the token: {} goes to his profile", token);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void modifyUser(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String full_name = ctx.formParam("full_name");
        String email = ctx.formParam("email");
        String phone = ctx.formParam("phone");
        String path_img = ctx.formParam("path_img");
        String description = ctx.formParam("description");
        String id_rolStr = ctx.formParam("id_rol");
        long id_rol = Long.parseLong(id_rolStr);
        String token = ctx.formParam("token");

        User user = new User(username, password, full_name, email, phone, path_img, description, id_rol, token);
        DataResult result = model.modifyUser(user);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void deleteUser(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.header("Authorization").replace("Bearer ", "");        

        DataResult result = model.deleteUser(token);
        if (result.getResult().equals("1")) {
            DataResult logoutUser = model.getUserByToken(token);
            User user = (User) logoutUser.getData();
            LOGGER.info("The user has deleted the user account:{}", user.getUsername());
        } else {
            LOGGER.info("Error deleting account: {}", result.getData());
        }

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    //UPLOAD PROFILE PICTURE METOD
////        app.post("/Cookim/upload/profile_picture", ctx -> {
////            System.out.println("----------------------------------------------------------------------------------------------------------");
////            System.out.println(" --------------------Receiving HTTPS POST request on the route: " + ctx.path() + "-----------------------");
////            String token = ctx.header("Authorization").replace("Bearer ", "");
////            ctx.uploadedFiles("binaryFile").forEach(file -> {
////                DataResult result = model.setUserProfileImage(file,token);
////                
////                if(result.getResult().equals("1")){
////                    System.out.println("File " + file.filename() + " saved successfully ");
////                }
////                else{
////                    System.out.println("File " + file.filename() + " could not save" + "\n" +
////                            "CAUSE: " + result.getData());
////                }
////                
////                Gson gson = new Gson();
////                ctx.result(gson.toJson(result));
////            });
////            System.out.println("----------------------------------------------------------------------------------------------------------");
////        });
    public void createUser(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String full_name = ctx.formParam("full_name");
        String email = ctx.formParam("email");
        String phone = ctx.formParam("phone");
        String id_rolStr = ctx.formParam("id_rol");
        long id_rol = Long.parseLong(id_rolStr);

        // Obtener el archivo de imagen cargado
        UploadedFile file = ctx.uploadedFile("img");

        User user = new User(username, password, full_name, email, phone, id_rol);

        DataResult result;

        if (file == null) {
            System.out.println("The file is empty");
            result = model.addNewUser(user, null);
        } else {
            System.out.println("The file is not empty");
            result = model.addNewUser(user, file);
        }

        System.out.println(result);

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void autoLogin(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.header("Authorization").replace("Bearer ", "");
        DataResult result = model.autoLogin(token);

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void login(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String credentials = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = credentials.split(":");
        String username = parts[0];
        String password = parts[1];

        LOGGER.info("Received credentials: username={}, password={}", username,password);

        DataResult result = model.validateUser(username, password);
        Gson gson = new Gson();
        
        LOGGER.info("Result: {}", gson.toJson(result));

        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void likeRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String action = parts[1];
        String recipe_id = parts[2];
        int num = Integer.parseInt(action);
        
        LOGGER.info("Data: {}", data);
        
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }
        
        if(num == 1){
            LOGGER.info("The user is trying to like the recipe");
        }else{
            LOGGER.info("The user is trying to remove the like from the recipe");
        }
        DataResult result = model.likeRecipe(token,num, recipe_id);
        Gson gson = new Gson();
        System.out.println(gson.toJson(result));

        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void logout(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.header("Authorization").replace("Bearer ", "");
        
        DataResult result = model.finishSession(token);
        
        
//        if (result.getResult().equals("1")) {
//            DataResult logoutUser = model.getUserByToken(token);
//            User user = (User) logoutUser.getData();
//            LOGGER.info("The user {} is logging out", user.getUsername());
//        } else {
//            LOGGER.info("Failed to sign out user:{}",result.getData());
//        }


        System.out.println(result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

//   app.post("/Cookim/user-profile-image", ctx -> {//http://localhost:7070/Cookim/user-profile-image
//            LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
//            String token = ctx.formParam("token");
//            DataResult result = model.getUserProfileImage(token);
//
//            Gson gson = new Gson();
//            ctx.result(gson.toJson(result));
//            System.out.println("----------------------------------------------------------------------------------------------------------");
//        });
    
    public void myProfile(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String data = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = data.split(":");
        String token = parts[0];
        String id = parts[1];
        LOGGER.info("Data: {}", token + " " + id);

        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        DataResult result = model.getAllRecipesByUserToken(token);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }
    
    public void getUserProfile(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String data = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = data.split(":");
        String token = parts[0];
        String id = parts[1];
        
        LOGGER.info("Data: {}", token + " " + id);

        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            LOGGER.info("Authenticated: {}", "Usuario no encontrado");
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));          
            return;
        }

        DataResult result = model.getAllRecipesByUserId(id);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }
    
    public void favoriteRecipes(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String data = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = data.split(":");
        String token = parts[0];
        String action = parts[1];
        String id_recipe = parts[2];
        
        LOGGER.info("Data: {}", token + "--" + id_recipe);

        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            LOGGER.info("Authenticated: {}", "Usuario no encontrado");
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));          
            return;
        }

        DataResult result = model.saveFavoriteRecipe(token,id_recipe,action);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }
}
