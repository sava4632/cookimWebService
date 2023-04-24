package com.cookim.cookimws.model;

import com.cookim.cookimws.utils.DataResult;
import com.cookim.cookimws.utils.Utils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import io.javalin.http.UploadedFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author cookimadmin
 */
public class Model {

    UserDaoInterface daoUsers;
    RecipeDaoInterface daoRecipe;

    public Model() {
        daoUsers = new UserDao();
        daoRecipe = new RecipeDao();

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
     * Method that adds a new user to the database and assigns him his first
     * token.
     *
     * @param user the user to add to the database.
     * @return 1 y el token si el usuario se ha añadido correctamente, 0 en el
     * caso contrario.
     */
    public DataResult addNewUser(User user,UploadedFile file) {
        DataResult result = new DataResult();


        boolean added = daoUsers.add(user);
        User u = getUser(user.getUsername(), user.getPassword());

        if (added) {
            String token = Utils.getSHA256(u.getUsername() + u.getPassword() + new Random().nextInt(10000));

            boolean isUpdateToken = daoUsers.updateUserToken(u, token);
            if (isUpdateToken) {
                result.setResult("1");
                result.setData(token);
                
                setUserProfileImage(file, token);
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
            System.out.println("A new token has been assigned to the user: ".concat(u.getUsername()));
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

    /**
     * Method that receives an image from the client and stores it in a remote
     * location on the server.
     *
     * @param file the file to save on the server
     * @return 1 if the image was saved correctly, 2 if the file does not meet
     * the required extension or 0 if the image cannot be added.
     */
//    public DataResult setUserProfileImageRemote(UploadedFile file) {
//        DataResult result = new DataResult();
//        try {
//            //Verify that the file has a .jpg extension
//            if (!FilenameUtils.getExtension(file.filename()).equalsIgnoreCase("jpg")) {
//                result.setResult("2");
//                result.setData("Only jpg files can be uploaded");
//                return result;
//            }
//
//            //SERVER
//            // Create a unique filename for the uploaded file
//            String randomString = RandomStringUtils.randomAlphanumeric(10);
//            String timestamp = Long.toString(System.currentTimeMillis());
//            String uniqueFilename = randomString + "-" + timestamp + ".jpg";
//
//            // Connect to the remote server using SSH
//            String username = "cookimadmin";
//            String password = "admin";
//            String hostname = "91.107.198.64";
//            int port = 22;
//            JSch jsch = new JSch();
//            Session session = jsch.getSession(username, hostname, port);
//            session.setPassword(password);
//            session.setConfig("StrictHostKeyChecking", "no");
//            session.connect();
//
//            // Create an SFTP channel and upload the file
//            Channel channel = session.openChannel("sftp");
//            channel.connect();
//            ChannelSftp sftpChannel = (ChannelSftp) channel;
//            String remotePath = "/var/www/resources/users/" + uniqueFilename;
//            sftpChannel.put(file.content(), remotePath);
//            
//
//            // Close the SFTP channel and SSH session
//            sftpChannel.disconnect();
//            session.disconnect();
//
//
//            result.setResult("1");
//            result.setData("Image saved successfully");
//        } catch (Exception ex) {
//            System.out.println("Error POST FILE:" + ex.toString());
//            result.setResult("0");
//            result.setData("Failed when trying to upload the image to server");
//        }
//
//        return result;
//    }
    /**
     * Method that receives an image from the client and stores it in a local
     * location on the server.
     *
     * @param file the file to save on the server
     * @return 1 if the image was saved correctly, 2 if the file does not meet
     * the required extension or 0 if the image cannot be added.
     */
    public DataResult setUserProfileImage(UploadedFile file,String token) {
        DataResult result = new DataResult();

        try {
            //Verify that the file has a .jpg extension
            if (file != null && !FilenameUtils.getExtension(file.filename()).equalsIgnoreCase("jpg")) {
                result.setResult("2");
                result.setData("Only jpg files can be uploaded");
                return result;
            }
            
            //SERVER    
            // Create a unique filename for the uploaded file
            String randomString = RandomStringUtils.randomAlphanumeric(10);
            String timestamp = Long.toString(System.currentTimeMillis());
            String uniqueFilename = randomString + "-" + timestamp + ".jpg";
            
            // Check if the filename already exists in the server folder
            String path = "/var/www/html/resources/users/";
            File uploadedFile;
            if (file != null) {
                uploadedFile = new File(path + file.filename());
            } else {
                uploadedFile = new File(path + "default.png");
            }
            int suffix = 1;
            
            while (uploadedFile.exists()) {
                uniqueFilename = randomString + "-" + timestamp + "-" + suffix + ".jpg";
                uploadedFile = new File(path + uniqueFilename);
                suffix++;
            }

            // Save the uploaded file with the unique filename
            if (file != null) {
                FileUtils.copyInputStreamToFile(file.content(), uploadedFile);
                System.out.println("Saving image" + file.filename() + " as: " + uploadedFile);
            }

            if (uploadedFile.exists()) {
                result.setResult("1");
                result.setData("Image saved successfully");
                
                User user = daoUsers.findUserByToken(token);
                if(user != null){
                    System.out.println("Se encontro un usuario");
                    System.out.println(user.toString());
                    boolean updated = daoUsers.setUserPathPicture(user.getId(),path+uniqueFilename);
                    if (updated) {
                        System.out.println("Se ha asignado una imagen al usuario: " + user.getUsername());
                    }else{
                        System.out.println("No se pudo asignar la imagen");
                    }
                    
                }
            } else {
                result.setResult("0");
                result.setData("Can't save image to server");
            }
        } 
        catch (IOException ex) {
            System.out.println("Error POST FILE:" + ex.toString());
            result.setResult("0");
            result.setData("Failed when trying to upload the image to server");
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
    public DataResult addNewRecipe(Recipe recipe) {
        DataResult result = new DataResult();
        boolean added = daoRecipe.addRecipe(recipe);

        if (added) {
            result.setResult("1");
            result.setData("Recipe added successfully");
        } else {
            result.setResult("0");
            result.setData("Failed when trying to add a new recipe");
        }
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

    //-------------------------------------CATEGORIES-------------------------------------------------
}
