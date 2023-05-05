package com.cookim.cookimws.endpoints;

import com.cookim.cookimws.model.Ingredient;
import com.cookim.cookimws.model.Model;
import com.cookim.cookimws.model.Recipe;
import com.cookim.cookimws.model.Step;
import com.cookim.cookimws.utils.DataResult;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Samuel
 */
public class RecipeAccessMethods {

    private final Model model;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeAccessMethods.class);

    public RecipeAccessMethods(Model model) {
        this.model = model;
    }

    public void registerEndpoints(Javalin app) {
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/configEndpoints.properties"));

            app.get(props.getProperty("home_page"), this::homePage);
            app.post(props.getProperty("home_page_preferences"), this::homePagePreferences);
            app.post(props.getProperty("add_recipe"), this::addRecipe);
            app.post(props.getProperty("remove_recipe"), this::removeRecipe);
            app.post(props.getProperty("modify_recipe"), this::modifyRecipe);
            app.post(props.getProperty("steps"), this::recipeView);
            app.post(props.getProperty("full_profile"), this::userProfile);

            //INGREDIENTS
            app.post(props.getProperty("ingredient_list"), this::getAllIngredients);

            //TEST
            app.get("/hello", ctx -> ctx.result("Hello World"));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(RecipeAccessMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //------------------------------------------RECIPES METODS------------------------------------------------
    //------------------------------------------RECIPES METODS------------------------------------------------
    //GET
    public void homePage(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP GET request on the route: {}", ctx.path());
        DataResult result = model.getAllRecipesWithUser();
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    //POST
    public void homePagePreferences(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String idCategory = ctx.formParam("idCategory");
        DataResult result = model.getAllRecipesByCategory(idCategory);
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Result of the request: {}", result.toString());

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void addRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.header("Authorization").replace("Bearer ", "");
        DataResult isAuthenticated = model.getUserByToken(token);

        LOGGER.info("Token:{}", token);

        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
            return;
        }

        Gson gson = new Gson();
        Recipe recipe = gson.fromJson(ctx.body(), Recipe.class);

        LOGGER.debug("Adding new recipe: {}", recipe.toString());
        DataResult result = model.addNewRecipe(recipe, token);
        LOGGER.info("Result of the request: {}", result.toString());

        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


//    public void addRecipe(io.javalin.http.Context ctx) {
//        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
//        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
//        String token = ctx.header("Authorization").replace("Bearer ", "");
//        DataResult isAuthenticated = model.getUserByToken(token);
//
//        LOGGER.info("Token:{}", token);
//
//        if (isAuthenticated.getResult().equals("0")) {
//            Gson gson = new Gson();
//            ctx.result(gson.toJson(isAuthenticated));
//            LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
//            return;
//        }
//
//        // Extract the recipe object from the form data
//        Gson gson = new Gson();
//        Recipe recipe = gson.fromJson(ctx.formParam("recipe"), Recipe.class);
//
//        // Get the uploaded image file
//        UploadedFile uploadedFile = ctx.uploadedFile("image");
//        if (uploadedFile != null) {
//            try {
//                Path tempFile = Files.createTempFile("recipe_image_", ".tmp");
//                Files.copy(uploadedFile.content(), tempFile, StandardCopyOption.REPLACE_EXISTING);
//                recipe.setFile(tempFile.toFile());
//            } catch (IOException e) {
//                LOGGER.error("Error creating temporary file for the recipe image", e);
//            }
//        }
//
//        LOGGER.debug("Adding new recipe: {}", recipe.toString());
//        DataResult result = model.addNewRecipe(recipe, token);
//        LOGGER.info("Result of the request: {}", result.toString());
//
//        ctx.result(gson.toJson(result));
//        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
//        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
//    }

    public void removeRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.formParam("token");
        DataResult isAuthenticated = model.getUserByToken(token);

        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
            return;
        }

        String id = ctx.formParam("id");
        DataResult result = model.deleteRecipe(id);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void modifyRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.formParam("token");
        DataResult isAuthenticated = model.getUserByToken(token);

        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
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
        LOGGER.info("Recipe to modify: {}", recipe.toString());

        DataResult result = model.modifyRecipe(recipe);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void recipeView(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String data = ctx.header("Authorization").replace("Bearer ", "");

        String[] parts = data.split(":");
        String token = parts[0];
        String id = parts[1];

        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        DataResult result = model.findFullRecipe(id);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void userProfile(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.header("Authorization").replace("Bearer ", "");

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

    //-------------------------------------INGREDIENTS-------------------------------------------------
    //-------------------------------------INGREDIENTS-------------------------------------------------
    public void getAllIngredients(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        String data = ctx.header("Authorization").replace("Bearer ", ""); //token and id(max)

        String[] parts = data.split(":");
        String token = parts[0];
        String idmax = parts[1];

        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        DataResult result = model.getAllIngredientsWithIdMax(idmax);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }
}
