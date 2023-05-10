package com.cookim.cookimws.endpoints;

import com.cookim.cookimws.model.Ingredient;
import com.cookim.cookimws.model.Model;
import com.cookim.cookimws.model.Recipe;
import com.cookim.cookimws.model.Step;
import com.cookim.cookimws.utils.DataResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.commons.io.FilenameUtils;
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


    //AÑADE UNA RECETA SIN PASOS NI INGREDIENTES (FUNCIONAL).
//    public void addRecipe(io.javalin.http.Context ctx) {
//        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
//        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
//        String token = ctx.header("Authorization").replace("Bearer ", "");
//
//        DataResult isAuthenticated = model.getUserByToken(token);
//        LOGGER.info("Token:{}", token);
//        if (isAuthenticated.getResult().equals("0")) {
//            Gson gson = new Gson();
//            ctx.result(gson.toJson(isAuthenticated));
//            return;
//        }
//
//        Gson gson = new Gson();
//        // Retrieve the recipe JSON part from the request
//        String recipeJson = ctx.formParam("recipe");
//        Recipe recipe = gson.fromJson(recipeJson, Recipe.class);
//
//        // Use the uploaded file here
//        UploadedFile file = ctx.uploadedFile("image");
//        if (file != null) {
//            String extension = FilenameUtils.getExtension(file.filename());
//            List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png");
//
//            if (validExtensions.contains(extension.toLowerCase())) {
//                LOGGER.debug("Adding new recipe with file: {}", file.filename());
//                // Do something with the file here
//
//                LOGGER.debug("Adding new recipe: {}", recipe.toString());
//                DataResult result = model.addNewRecipe(recipe, token, file);
//                LOGGER.info("Result of the request: {}", result.toString());
//
//                ctx.result(gson.toJson(result));
//            } else {
//                LOGGER.debug("Provided file is not an image: {}", file.filename());
//                DataResult invalidFileResult = new DataResult();
//                invalidFileResult.setResult("2");
//                invalidFileResult.setData("Error: Image format not valid");
//                LOGGER.info("DATA: {}", invalidFileResult.toString());
//                ctx.result(gson.toJson(invalidFileResult));
//            }
//        } else {
//            LOGGER.debug("Null image has been sent");
//            // Do something here if no file was uploaded
//            DataResult noFileResult = new DataResult();
//            noFileResult.setResult("0");
//            noFileResult.setData("Error: Null image has been sent");
//            LOGGER.info("DATA: {}", noFileResult.toString());
//            ctx.result(gson.toJson(noFileResult));
//        }
//
//        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
//        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
//    }

    public void addRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.header("Authorization").replace("Bearer ", "");

        DataResult isAuthenticated = model.getUserByToken(token);
        LOGGER.info("Token:{}", token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        Gson gson = new Gson();
        // Retrieve the recipe JSON part from the request
        String recipeJson = ctx.formParam("recipe");
        Recipe recipe = gson.fromJson(recipeJson, Recipe.class);

        // Use the uploaded file here
        UploadedFile file = ctx.uploadedFile("image");
        if (file != null) {
            String extension = FilenameUtils.getExtension(file.filename());
            List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png");

            if (validExtensions.contains(extension.toLowerCase())) {
                LOGGER.debug("Adding new recipe with file: {}", file.filename());
                // Do something with the file here

                LOGGER.debug("Adding new recipe: {}", recipe.toString());
               
                DataResult result = model.addNewRecipe(recipe, token, file);
                DataResult resultProcessStep = new DataResult();
                DataResult resultProcessIngredients = new DataResult();
                
                long recipeId = result.getRecipeId();
                // Process each ingredient
                List<Ingredient> ingredients = recipe.getIngredients();
                boolean allIngredientsAdded = true;
                for (Ingredient ingredient : ingredients) {                    
                    LOGGER.debug("Processing ingredient: {}", ingredient.getName());

                    // Llamar al método en el modelo que maneja el ingrediente
                    resultProcessIngredients = model.processIngredientOfRecipe(ingredient,recipeId);

                    if (!resultProcessIngredients.getResult().equals("1")) {
                        allIngredientsAdded = false;
                    }

                    LOGGER.info("Result of the request Ingredient: {}", resultProcessIngredients.toString());
                }
                
                // Process each step
                List<Step> steps = recipe.getSteps();
                boolean allStepsAdded = true;
                for (Step step : steps) {
                    step.setRecipe_id(recipeId);
                    // Retrieve the uploaded file for the step
                    UploadedFile stepFile = ctx.uploadedFile("step_file_" + step.getStep_number());

                    LOGGER.debug("Processing step number: {}", step.getStep_number());
                    LOGGER.debug("Step description: {}", step.getDescription());

                    
                    if (stepFile != null) {
                        LOGGER.debug("Step file name: {}", stepFile.filename());

                        // Verificar el formato del archivo
                        if (validExtensions.contains(extension.toLowerCase())) {
                            // Llamar al método en el modelo que maneja el paso y su imagen correspondiente
                            resultProcessStep = model.processStepOfRecipe(step, stepFile);
                            if (!resultProcessStep.getResult().equals("1")) {
                                allStepsAdded = false;
                            }
                        } else {
                            LOGGER.debug("Invalid file format: {}", stepFile.filename());
                            // Actualizar el resultado si el formato del archivo no es válido
                            resultProcessStep.setResult("0");
                            resultProcessStep.setData("Invalid file format: " + stepFile.filename());
                            allStepsAdded = false;
                        }
                    } else {
                        // Llamar al método en el modelo sin un archivo adjunto
                        resultProcessStep = model.processStepOfRecipe(step, null);
                        if (!resultProcessStep.getResult().equals("1")) {
                            allStepsAdded = false;
                        }
                    }

                    
                    LOGGER.info("Result of the request Step: {}", resultProcessStep.toString());
                }
                
                 if (result.getResult().equals("1") && allStepsAdded && allIngredientsAdded) {
                    result.setData("Recipe and all steps added successfully");
                } else {
                    result.setResult("2");
                    result.setData("Error: Failed to add recipe or steps");
                }
                
                LOGGER.info("Result of the request: {}", result.toString());

                ctx.result(gson.toJson(result));
            } else {
                LOGGER.debug("Provided file is not an image: {}", file.filename());
                DataResult invalidFileResult = new DataResult();
                invalidFileResult.setResult("2");
                invalidFileResult.setData("Error: Image format not valid");
                LOGGER.info("DATA: {}", invalidFileResult.toString());
                ctx.result(gson.toJson(invalidFileResult));
            }
        } else {
            LOGGER.debug("Null image has been sent");
            // Do something here if no file was uploaded
            DataResult noFileResult = new DataResult();
            noFileResult.setResult("0");
            noFileResult.setData("Error: Null image has been sent");
            LOGGER.info("DATA: {}", noFileResult.toString());
            ctx.result(gson.toJson(noFileResult));
        }

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


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
