package com.cookim.cookimws.endpoints;

import com.cookim.cookimws.model.Category;
import com.cookim.cookimws.model.Comment;
import com.cookim.cookimws.model.Ingredient;
import com.cookim.cookimws.model.Model;
import com.cookim.cookimws.model.Recipe;
import com.cookim.cookimws.model.Step;
import com.cookim.cookimws.utils.DataResult;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
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

            app.post(props.getProperty("home_page"), this::homePage);
            app.post(props.getProperty("home_page_preferences"), this::homePagePreferences);
            app.post(props.getProperty("add_recipe"), this::addRecipe);
            app.post(props.getProperty("remove_recipe"), this::removeRecipe);
            app.post(props.getProperty("modify_recipe"), this::modifyRecipe);
            app.post(props.getProperty("steps"), this::recipeView);
            app.post(props.getProperty("search_recipe"), this::searchRecipe);
            app.post(props.getProperty("search_recipe_category"), this::searchRecipeByCategory);
            app.post(props.getProperty("recipe_comments"), this::addRecipeComment);
            app.post(props.getProperty("parent_comments"), this::getRecipeParentComments);
            app.post(props.getProperty("child_comments"), this::getRecipeChildComments);
            app.post(props.getProperty("followeds_recipes"), this::followedsRecipes);
            
            

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
    /**
     * Retrieves all recipes with user information for the home page.
     */
    public void homePage(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());
        String token = ctx.header("Authorization").replace("Bearer ", "");
        
        LOGGER.info("Data obtained:{}", token);
        
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve all recipes with user information for the home page
        DataResult result = model.getAllRecipesWithUser(token);
        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    //POST
    /**
     * Retrieves all recipes by a specific category for the home page.
     */
    public void homePagePreferences(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the category ID from the request parameters
        String idCategory = ctx.formParam("idCategory");

        // Retrieve all recipes by the specified category
        DataResult result = model.getAllRecipesByCategory(idCategory);
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Result of the request: {}", result.toString());

        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }



    /**
     * Adds a new recipe to the system.
     */
    public void addRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token from the request header
        String token = ctx.header("Authorization").replace("Bearer ", "");

        // Check if the user is authenticated
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
        LOGGER.debug("Recipe JSON: {}", recipeJson);

        

        // Use the uploaded file here
        UploadedFile file = ctx.uploadedFile("image");
        if (file != null) {
            String extension = FilenameUtils.getExtension(file.filename());
            List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png");

            if (validExtensions.contains(extension.toLowerCase())) {
                LOGGER.debug("Adding new recipe with file: {}", file.filename());
                // Do something with the file here

                LOGGER.debug("Adding new recipe: {}", recipe.toString());

                // Add the recipe to the model and retrieve the result
                DataResult result = model.addNewRecipe(recipe, token, file);
                DataResult resultProcessStep = new DataResult();
                DataResult resultProcessIngredients = new DataResult();
                DataResult resultProcessCategories = new DataResult();

                long recipeId = result.getRecipeId();

                // Process each ingredient
                List<Ingredient> ingredients = recipe.getIngredients();
                boolean allIngredientsAdded = true;
                for (Ingredient ingredient : ingredients) {
                    LOGGER.debug("Processing ingredient: {}", ingredient.getName());

                    // Call the method in the model that handles the ingredient
                    resultProcessIngredients = model.processIngredientOfRecipe(ingredient, recipeId);

                    if (!resultProcessIngredients.getResult().equals("1")) {
                        allIngredientsAdded = false;
                    }

                    LOGGER.info("Result of the request Ingredient: {}", resultProcessIngredients.toString());
                }
                
                //Process each categories
                List<Category> categories = recipe.getCategories();
                if (categories != null) {
                    LOGGER.debug("Categories: {}", categories.toString());
                } else {
                    LOGGER.debug("Categories is null");
                }
                boolean allCategoriesAdded = true;
                for (Category category : categories) {
                    LOGGER.debug("Processing catefories: {}", category.getName());

                    // Call the method in the model that handles the categoty
                    resultProcessCategories = model.processCategoriesOfRecipe(category, recipeId);

                    if (!resultProcessCategories.getResult().equals("1")) {
                        allCategoriesAdded = false;
                    }

                    LOGGER.info("Result of the request Categories: {}", resultProcessCategories.toString());
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

                        // Verify the file format
                        if (validExtensions.contains(extension.toLowerCase())) {
                            // Call the method in the model that handles the step and its corresponding image
                            resultProcessStep = model.processStepOfRecipe(step, stepFile);
                            if (!resultProcessStep.getResult().equals("1")) {
                                allStepsAdded = false;
                            }
                        } else {
                            LOGGER.debug("Invalid file format: {}", stepFile.filename());
                            // Update the result if the file format is not valid
                            resultProcessStep.setResult("0");
                            resultProcessStep.setData("Invalid file format: " + stepFile.filename());
                            allStepsAdded = false;
                        }
                    } else {
                        // Call the method in the model without an attached file
                        resultProcessStep = model.processStepOfRecipe(step, null);

                        if (!resultProcessStep.getResult().equals("1")) {
                            allStepsAdded = false;
                        }
                    }

                    LOGGER.info("Result of the request Step: {}", resultProcessStep.toString());
                }

                if (result.getResult().equals("1") && allStepsAdded && allIngredientsAdded && allCategoriesAdded) {
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


    /**
     * Removes a recipe from the system.
     */
    public void removeRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token from the request form parameter
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String id = parts[1];
        
        LOGGER.info("Data:{}", data);
        
        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);

        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
            return;
        }

        // Retrieve the recipe ID from the request form parameter

        // Delete the recipe from the model and retrieve the result
        DataResult result = model.deleteRecipe(token,id);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Modifies a recipe in the system.
     */
    public void modifyRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token from the request form parameter
        String token = ctx.formParam("token");

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);

        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
            return;
        }

        // Retrieve the recipe details from the request form parameters
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

        // Create a new Recipe object with the updated details
        Recipe recipe = new Recipe(id, id_user, name, description, path_img, rating, likes);
        LOGGER.info("Recipe to modify: {}", recipe.toString());

        // Modify the recipe in the model and retrieve the result
        DataResult result = model.modifyRecipe(recipe);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }


    /**
     * Retrieves the details of a specific recipe.
     */
    public void recipeView(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token and recipe ID from the request header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String id_recipe = parts[1];

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();               
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve the full details of the recipe
        DataResult result = model.findFullRecipe(token,id_recipe);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    
    
    /**
     * Searches for recipes that match the given text.
     */
    public void searchRecipe(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token and search text from the request header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String text = parts[1];

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Perform the recipe search based on the provided text
        DataResult result = model.getRecipesEqualToText(text);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }
    
    /**
     * Searches for recipes that match the given category.
     */
    public void searchRecipeByCategory(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token and search text from the request header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String category = parts[1];

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Perform the recipe search based on the provided text
        DataResult result = model.getRecipesFromCategory(category);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    
    /**
     * Adds a comment to a recipe.
     */
    public void addRecipeComment(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token and comment details from the request header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String id_recipeStr = parts[1];
        String text = parts[2];
        String id_parentStr = parts[3];

        LOGGER.info("DATA: {}", data);

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Convert the necessary parameters to the appropriate types
        long id_recipe = Long.parseLong(id_recipeStr);
        Long id_parent = null;
        if (id_parentStr != null && !id_parentStr.isEmpty()) {
            id_parent = Long.parseLong(id_parentStr);
            if (id_parent < 1) {
                id_parent = null;
            }
        }

        // Create the Comment object
        Comment comment = new Comment(id_recipe, text, id_parent);

        // Call the model's method to add the comment
        DataResult result = model.AddCommentToRecipe(token, comment);

        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }
    
    //get followeds recipes
    public void followedsRecipes(io.javalin.http.Context ctx){
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token and search text from the request header
        String token = ctx.header("Authorization").replace("Bearer ", "");

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Perform the recipe search based on the provided text
        DataResult result = model.getUserFollowedsRecipes(token);
        LOGGER.info("Result of the request: {}", result.toString());

        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    
    /**
     * Retrieves all parent comments for a recipe.
     */
    public void getRecipeParentComments(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token and recipe ID from the request header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String id_recipe = parts[1];

        LOGGER.info("Data: {}", token + " : " + id_recipe);

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve all parent comments for the recipe
        DataResult result = model.getAllRecipeParentComments(id_recipe);

        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

    public void getRecipeChildComments (io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token and recipe ID from the request header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String id_recipe = parts[1];
        String id_comment_parent = parts[2];

        LOGGER.info("Data: {}", data);

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve all parent comments for the recipe
        DataResult result = model.getChildComments(id_recipe,id_comment_parent);

        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }
    

    //-------------------------------------INGREDIENTS-------------------------------------------------
    //-------------------------------------INGREDIENTS-------------------------------------------------
    /**
     * Retrieves all ingredients with an ID lower or equal to a given maximum
     * ID.
     */
    public void getAllIngredients(io.javalin.http.Context ctx) {
        LOGGER.info("------------------------------------------------- New request -------------------------------------------------");
        LOGGER.info("Receiving HTTP POST request on the route: {}", ctx.path());

        // Retrieve the user's token and maximum ID from the request header
        String data = ctx.header("Authorization").replace("Bearer ", "");
        String[] parts = data.split(":");
        String token = parts[0];
        String idmax = parts[1];

        // Check if the user is authenticated
        DataResult isAuthenticated = model.getUserByToken(token);
        if (isAuthenticated.getResult().equals("0")) {
            Gson gson = new Gson();
            ctx.result(gson.toJson(isAuthenticated));
            return;
        }

        // Retrieve all ingredients with an ID lower or equal to the maximum ID
        DataResult result = model.getAllIngredientsWithIdMax(idmax);

        LOGGER.info("Result of the request: {}", result.toString());
        Gson gson = new Gson();
        ctx.result(gson.toJson(result));
        LOGGER.info("Sent HTTP response with status code: {} at {}", ctx.status(), LocalDateTime.now());
        LOGGER.info("------------------------------------------------- End of request -------------------------------------------------");
    }

}
