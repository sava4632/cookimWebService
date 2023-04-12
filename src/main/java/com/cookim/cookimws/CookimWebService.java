package com.cookim.cookimws;

import com.cookim.cookimws.model.Model;
import com.cookim.cookimws.model.Recipe;
import com.cookim.cookimws.model.User;
import com.cookim.cookimws.utils.DataResult;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.community.ssl.SSLConfig;
import io.javalin.community.ssl.SSLPlugin;
import io.javalin.community.ssl.TLSConfig;
import io.javalin.jetty.JettyUtil;
import java.io.File;
import java.util.List;




/**
 *
 * @author cookimadmin
 */
public class CookimWebService {

    public static void main(String[] args) {
        Model model = new Model();

        //HTTP
        //Javalin app = Javalin.create().start(7070);
        
        //HTTPS
//        File f = new File("C:\\Program Files\\OpenSSL-Win64\\bin\\cert.pem");
//        System.out.println(f.getAbsolutePath());
        
        SSLPlugin plugin = new SSLPlugin(conf -> {
            conf.pemFromPath("C:\\Program Files\\OpenSSL-Win64\\bin\\cert.pem", 
                    "C:\\Program Files\\OpenSSL-Win64\\bin\\key.pem","cookimadmin");
            // additional configuration options
            conf.insecurePort=7070;
            conf.host=null;      // Host to bind to, by default it will bind to all interfaces.
            conf.insecure=true;    // Toggle the default http (insecure) connector.
            conf.secure=true;   // Toggle the default https (secure) connector.
            conf.http2=true;        // Toggle HTTP/2 Support
            conf.securePort=443;   // Port to use on the SSL (secure) connector.
            conf.insecurePort=7070;   // Port to use on the http (insecure) connector.
            conf.sniHostCheck=false;   // Enable SNI hostname verification.
            conf.tlsConfig=TLSConfig.INTERMEDIATE;      // Set the TLS configuration. (by default it uses Mozilla's intermediate configuration)
        });

        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.plugins.register(plugin);
        }).start();
       

        
        
        //------------------------------------------RECIPES-----------------------------------------------------
        //GET
        app.get("/Cookim/home-page", ctx -> {//http://localhost:7070/Cookim/home_page
            DataResult result = model.getAllRecipes();
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });
        
        //POST
        app.post("/Cookim/home-page-preferences", ctx -> {//http://localhost:7070/Cookim/home_page
            String idCategory = ctx.formParam("idCategory");
            DataResult result = model.getAllRecipesByCategory(idCategory);
            
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });
        
        
        app.post("/Cookim/add-recipe", ctx -> {//http://localhost:7070/Cookim/home_page
            String token = ctx.formParam("token");
            DataResult isAuthenticated = model.getUserByToken(token);
            
            if(isAuthenticated.getResult().equals("0")){
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
            String token = ctx.formParam("token");
            DataResult isAuthenticated = model.getUserByToken(token);
            
            if(isAuthenticated.getResult().equals("0")){
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
            String token = ctx.formParam("token");
            DataResult isAuthenticated = model.getUserByToken(token);
            
            if(isAuthenticated.getResult().equals("0")){
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
            
            Recipe recipe = new Recipe(id,id_user, name, description, path_img, rating, likes);
            System.out.println(recipe.toString());
            
            DataResult result = model.modifyRecipe(recipe);
            
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });
        
        //-----------------------------------------USERS-------------------------------------------------------
        //GET
        app.get("/users", ctx -> {//http://192.168.127.80:7070/users
            List<User> userList = model.getAllUsers();
            Gson gson = new Gson();
            ctx.result(gson.toJson(userList));
        });
        
        //POST
        app.post("/Cookim/my-profile", ctx -> { //http://localhost:7070/Cookim/profile
            //String token = ctx.formParam("token");
            String token = ctx.header("Authorization").replace("Bearer token=", "");
            System.out.println("the user with the token: " + token + " tries to get into his profile...");

            DataResult result = model.getUserByToken(token);
            System.out.println("The user with the token: " + token + " goes to his profile");
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });
        
        app.post("/Cookim/my-profile/modify-account", ctx -> { //http://localhost:7070/Cookim/my-profile/modify
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
            String token = ctx.header("Authorization").replace("Bearer ", "");
            System.out.println("the user with the token: " + token + " tries to delete his account");

            DataResult result = model.deleteUser(token);
            System.out.println("The user with the token: " + token + " deleted his account");
            
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });
        
        
        app.post("/Cookim/sign-in", ctx -> { //http://localhost:7070/Cookim/sign-in
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

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));

        });
        
        app.post("/Cookim/autologin", ctx -> { //http://localhost:7070/Cookim/login
            String token = ctx.header("Authorization").replace("Bearer ", "");
            DataResult result = model.autoLogin(token);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

        app.post("/Cookim/login", ctx -> { //http://localhost:7070/Cookim/login
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            System.out.println(username + " " + password);

            DataResult result = model.validateUser(username, password);
            Gson gson = new Gson();
            System.out.println(gson.toJson(result));
            
            ctx.result(gson.toJson(result));

        });
        
        app.post("/Cookim/user-profile-image", ctx -> {//http://localhost:7070/Cookim/user-profile-image
            String token = ctx.formParam("token");
            DataResult result = model.getUserProfileImage(token);

            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
        });

    }
}
