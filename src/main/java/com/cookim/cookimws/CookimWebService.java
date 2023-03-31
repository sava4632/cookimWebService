package com.cookim.cookimws;

import com.cookim.cookimws.model.Model;
import com.cookim.cookimws.model.Recipe;
import com.cookim.cookimws.model.User;
import com.cookim.cookimws.utils.DataResult;
import com.google.gson.Gson;
import io.javalin.Javalin;
import java.util.List;



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
        //Javalin app = Javalin.create().enableHttps(keystoreInputStream, keystorePassword).start(443);
        

        //GET
        app.get("/users", ctx -> {//http://192.168.127.80:7070/users
            List<User> userList = model.getAllUsers();
            Gson gson = new Gson();
            ctx.result(gson.toJson(userList));
        });

        

        app.get("/Cookim/home_page", ctx -> {//http://localhost:7070/Cookim/home_page
            DataResult result = model.getAllRecipes();
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
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
            String description = ctx.formParam("description");
            String id_rolStr = ctx.formParam("id_rol");
            long id_rol = Long.parseLong(id_rolStr);

            User user = new User(username, password, full_name, email, phone, path_img, description, id_rol);

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

    }
}
