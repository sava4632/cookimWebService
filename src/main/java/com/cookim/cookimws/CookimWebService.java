package com.cookim.cookimws;

import com.cookim.cookimws.model.Model;
import com.cookim.cookimws.model.Recipe;
import com.cookim.cookimws.model.User;
import com.cookim.cookimws.utils.DataResult;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.community.ssl.SSLPlugin;
import io.javalin.community.ssl.TLSConfig;
import io.javalin.http.UploadedFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author cookimadmin
 */
public class CookimWebService {

    public static void main(String[] args) {
        Model model = new Model();

        //HTTP
        Javalin app = Javalin.create().start(7070);
        //HTTPS        
//        SSLPlugin plugin = new SSLPlugin(conf -> {
//            conf.pemFromPath("C:\\Program Files\\OpenSSL-Win64\\bin\\cert.pem",
//                    "C:\\Program Files\\OpenSSL-Win64\\bin\\key.pem", "cookimadmin");
//            // additional configuration options
//            conf.insecurePort = 7070;
//            conf.host = null;      // Host to bind to, by default it will bind to all interfaces.
//            conf.insecure = true;    // Toggle the default http (insecure) connector.
//            conf.secure = true;   // Toggle the default https (secure) connector.
//            conf.http2 = true;        // Toggle HTTP/2 Support
//            conf.securePort = 443;   // Port to use on the SSL (secure) connector.
//            conf.insecurePort = 7070;   // Port to use on the http (insecure) connector.
//            conf.sniHostCheck = false;   // Enable SNI hostname verification.
//            conf.tlsConfig = TLSConfig.INTERMEDIATE;      // Set the TLS configuration. (by default it uses Mozilla's intermediate configuration)
//        });
//
//        Javalin app = Javalin.create(javalinConfig -> {
//            javalinConfig.plugins.register(plugin);
//        }).start();

        //------------------------------------------RECIPES METODS------------------------------------------------
        //GET
        app.get("/Cookim/home-page", ctx -> {//http://localhost:7070/Cookim/home_page
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            DataResult result = model.getAllRecipesWithUser();
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        //POST
        app.post("/Cookim/home-page-preferences", ctx -> {//http://localhost:7070/Cookim/home_page
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            String idCategory = ctx.formParam("idCategory");
            DataResult result = model.getAllRecipesByCategory(idCategory);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        app.post("/Cookim/add-recipe", ctx -> {//http://localhost:7070/Cookim/home_page
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            String token = ctx.formParam("token");
            DataResult isAuthenticated = model.getUserByToken(token);

            if (isAuthenticated.getResult().equals("0")) {
                Gson gson = new Gson();
                ctx.result(gson.toJson(isAuthenticated));
                return;
            }

            String id_UserStr = ctx.formParam("id_user");
            long id_user = Long.parseLong(id_UserStr);
            String name = ctx.formParam("name");
            String description = ctx.formParam("description");
            String path_img = ctx.formParam("path_img");
            String ratingStr = ctx.formParam("rating");
            double rating = Double.parseDouble(ratingStr);
            String likesStr = ctx.formParam("likes");
            int likes = Integer.parseInt(likesStr);

            Recipe recipe = new Recipe(id_user, name, description, path_img, rating, likes);
            System.out.println(recipe.toString());

            DataResult result = model.addNewRecipe(recipe);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        app.post("/Cookim/remove-recipe", ctx -> {//http://localhost:7070/Cookim/home_page
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            String token = ctx.formParam("token");
            DataResult isAuthenticated = model.getUserByToken(token);

            if (isAuthenticated.getResult().equals("0")) {
                Gson gson = new Gson();
                ctx.result(gson.toJson(isAuthenticated));
                return;
            }

            String id = ctx.formParam("id");

            DataResult result = model.deleteRecipe(id);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        app.post("/Cookim/modify-recipe", ctx -> {//http://localhost:7070/Cookim/modify-recipe
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            String token = ctx.formParam("token");
            DataResult isAuthenticated = model.getUserByToken(token);

            if (isAuthenticated.getResult().equals("0")) {
                Gson gson = new Gson();
                ctx.result(gson.toJson(isAuthenticated));
                return;
            }

            String idStr = ctx.formParam("id");
            long id = Long.parseLong(idStr);
            String id_UserStr = ctx.formParam("id_user");
            long id_user = Long.parseLong(id_UserStr);
            String name = ctx.formParam("name");
            String description = ctx.formParam("description");
            String path_img = ctx.formParam("path_img");
            String ratingStr = ctx.formParam("rating");
            double rating = Double.parseDouble(ratingStr);
            String likesStr = ctx.formParam("likes");
            int likes = Integer.parseInt(likesStr);

            Recipe recipe = new Recipe(id, id_user, name, description, path_img, rating, likes);
            System.out.println(recipe.toString());

            DataResult result = model.modifyRecipe(recipe);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        //-----------------------------------------USERS METODS---------------------------------------------------
        //GET
        app.get("/users", ctx -> {//http://192.168.127.80:7070/users
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            List<User> userList = model.getAllUsers();
            Gson gson = new Gson();
            ctx.result(gson.toJson(userList));
        });

        //POST
        app.post("/Cookim/my-profile", ctx -> { //http://localhost:7070/Cookim/profile
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            //String token = ctx.formParam("token");
            String token = ctx.header("Authorization").replace("Bearer ", "");
            System.out.println("the user with the token: " + token + " tries to get into his profile...");

            DataResult result = model.getUserByToken(token);
            System.out.println("The user with the token: " + token + " goes to his profile");
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        app.post("/Cookim/my-profile/modify-account", ctx -> { //http://localhost:7070/Cookim/my-profile/modify
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
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

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        app.post("/Cookim/my-profile/delete-account", ctx -> { //http://localhost:7070/Cookim/my-profile/delete-account
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            String token = ctx.header("Authorization").replace("Bearer ", "");
            System.out.println("the user with the token: " + token + " tries to delete his account");

            DataResult result = model.deleteUser(token);
            System.out.println("The user with the token: " + token + " deleted his account");

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        //UPLOAD PROFILE PICTURE METOD
        app.post("/Cookim/upload/profile_picture", ctx -> {
            System.out.println(" --------------------Receiving HTTPS POST request on the route: " + ctx.path() + "-----------------------");
            ctx.uploadedFiles("binaryFile").forEach(file -> {
                DataResult result = model.setUserProfileImage(file);
                System.out.println("Saving file to path: C:\\Users\\Samuel\\Desktop\\CookimUpload\\binary\\" + file.filename());
                
                if(result.getResult().equals("1")){
                    System.out.println("File " + file.filename() + " saved successfully ");
                }
                else{
                    System.out.println("File " + file.filename() + " could not save" + "\n" +
                            "CAUSE: " + result.getData());
                }
                
                Gson gson = new Gson();
                ctx.result(gson.toJson(result));
            });

        });

        app.post("/Cookim/sign-in", ctx -> { //http://localhost:7070/Cookim/sign-in
            System.out.println(" --------------------Receiving HTTPS POST request on the route: " + ctx.path() + "-----------------------");
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            String full_name = ctx.formParam("full_name");
            String email = ctx.formParam("email");
            String phone = ctx.formParam("phone");
            String path_img = ctx.formParam("path_img");
            String id_rolStr = ctx.formParam("id_rol");
            long id_rol = Long.parseLong(id_rolStr);
            

            User user = new User(username, password, full_name, email, phone, path_img, id_rol);

            DataResult result = model.addNewUser(user);
            
            System.out.println(result);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));

        });

        app.post("/Cookim/autologin", ctx -> { //http://localhost:7070/Cookim/autologin
            System.out.println(" --------------------Receiving HTTPS POST request on the route: " + ctx.path() + "-----------------------");
            String token = ctx.header("Authorization").replace("Bearer ", "");
            DataResult result = model.autoLogin(token);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        app.post("/Cookim/login", ctx -> { //http://localhost:7070/Cookim/login
            System.out.println(" --------------------Receiving HTTPS POST request on the route: " + ctx.path() + "-----------------------");
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            System.out.println(username + " " + password);

            DataResult result = model.validateUser(username, password);
            Gson gson = new Gson();
            System.out.println(gson.toJson(result));

            ctx.result(gson.toJson(result));
        });
        
        app.post("/Cookim/like", ctx -> { //http://localhost:7070/Cookim/like
            System.out.println(" --------------------Receiving HTTPS POST request on the route: " + ctx.path() + "-----------------------");
            System.out.println("user tries to update Recipe");
            String act = ctx.formParam("num");
            String recipe_id = ctx.formParam("recipe_id");
            
            int num = Integer.parseInt(act);

            System.out.println(act+ " " + recipe_id);

            DataResult result = model.likeRecipe(num, recipe_id);
            Gson gson = new Gson();
            System.out.println(gson.toJson(result));

            ctx.result(gson.toJson(result));
        });
        
        app.post("/Cookim/logout", ctx -> { //http://localhost:7070/Cookim/logout
            System.out.println(" --------------------Receiving HTTP POST request on the route: " + ctx.path() + "-----------------------");
            //String token = ctx.formParam("token");
            String token = ctx.header("Authorization").replace("Bearer ", "");
            System.out.println("the user with the token: " + token + " tries to end his sesssion...");

            DataResult result = model.finishSession(token);
            System.out.println("The user with the token: " + token + " ends his session");
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });


        app.post("/Cookim/user-profile-image", ctx -> {//http://localhost:7070/Cookim/user-profile-image
            System.out.println(" --------------------Receiving HTTPS POST request on the route: " + ctx.path() + "-----------------------");
            String token = ctx.formParam("token");
            DataResult result = model.getUserProfileImage(token);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

    }
}
