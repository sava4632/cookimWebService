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

        //GET
        /*app.get("/users", ctx -> {//http://192.168.127.80:7070/users
            List<User> userList = model.getAllUsers();
            Gson gson = new Gson();
            ctx.result(gson.toJson(userList));
        });*/

 /*app.get("/loginget", ctx -> { //http://localhost:7070/loginget?username=admin&password=admin
            String username = ctx.queryParam("username");
            String password = ctx.queryParam("password");

            System.out.println(username + " " + password);

            DataResult result = model.validateUser(username, password);

            if (result.getResult().equals("success")) {
                List<User> userList = model.getAllUsers();
                Gson gson = new Gson();
                ctx.result(gson.toJson(userList));
            } else {
                ctx.result(result.getData().toString());
            }
        });*/
        app.post("/perfil", ctx -> { //http://localhost:7070/perfil
            String token = ctx.formParam("token");
            User user = model.getUserByToken(token);
            System.out.println("the user with the token: " + token + " tries to get into his profile...");

            if (user != null) {
                System.out.println("The user with the token: " + token + " goes to his profile");
                DataResult result = new DataResult();
                result.setResult(user.getUsername());
                result.setResult2(user.getPath_img());
                Gson gson = new Gson();
                ctx.result(gson.toJson(result));
            } else {
                ctx.result("User not found");
            }
        });

        app.get("/principal", ctx -> {//http://localhost:7070/principal
            List<Recipe> recipesList = model.getAllRecipes();
            Gson gson = new Gson();
            ctx.result(gson.toJson(recipesList));
        });

        //POST
        app.post("/register-new-user", ctx -> { //http://localhost:7070/register-new-user
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

            boolean result = model.addNewUser(user);

            if (result) {
                Gson gson = new Gson();
                ctx.result("The user has been added successfully");
            } else {
                ctx.result("Failed to register new user");
            }
        });
        
        app.post("/login", ctx -> { //http://localhost:7070/login
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            System.out.println(username + " " + password);

            DataResult result = model.validateUser(username, password);
            //System.out.println(result.toString());

            if (result.getResult().equals("user validated successfully")) {
//               List<User> userList = model.getAllUsers();
//               for(User u : userList){
//                   System.out.println(u.toString());
//               }
                Gson gson = new Gson();
                ctx.result(gson.toJson(result.getData()));
            } else {
                Gson gson = new Gson();
                ctx.result(gson.toJson(result.getData()));
                //ctx.result(result.getData());
            }
        });

    }
}
