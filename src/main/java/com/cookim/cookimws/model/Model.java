package com.cookim.cookimws.model;

import com.cookim.cookimws.utils.DataResult;
import com.cookim.cookimws.utils.Utils;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cookimadmin
 */
public class Model {

    UserDaoInterface daoUsers;
    RecipeDaoInterface recipeDao;

    public Model() {
        daoUsers = new UserDao();
        recipeDao = new RecipeDao();
        //loadUsers();
    }

    public List<User> getAllUsers() {
        return daoUsers.findAllUsers();
    }

  

    public User getUser(String username, String password) {
        return daoUsers.findUser(username, password);
    }
    
    /**
     * 
     * @param token
     * @return 
     */
    public DataResult getUserByToken(String token) {
        DataResult result = new DataResult();
        User user = daoUsers.findUserByToken(token);
        
        if (user != null) {
            result.setResult("1");
            result.setData(user);
        }
        else{
            result.setResult("0");
            result.setData("Error: token could not be validated");
        }
        return result;
    }
    
    /**
     * 
     * @param username
     * @return 
     */
    public DataResult deleteUser(String token){
        DataResult result = new DataResult();
        boolean deleted = daoUsers.deleteUser(token);
        if (deleted) {
            result.setResult("1");
            result.setData("User deleted successfully");
        }
        else{
            result.setResult("0");
            result.setData("Failed to try to delete user");
        }
        return result;
    }
    
    public DataResult modifyUser(User user){
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
     * 
     * @param user
     * @return 
     */
    public DataResult addNewUser(User user) {
        DataResult result = new DataResult();
        boolean added = daoUsers.add(user);
        if (added) {
            result.setResult("1");
            result.setData("The user has been added successfully");
        } else {
            result.setResult("0");
            result.setData("Failed to register new user");
        }

        return result;
        //return daoUsers.add(user);
    }
    
    public DataResult autoLogin(String token){
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
    
   
    //--------------------------------------------------RECIPES-------------------------------------------------------------

    public DataResult getAllRecipes() {
        DataResult result = new DataResult();
        List<Recipe> recipes = recipeDao.findAllRecipes();
        
        if (recipes != null) {
            result.setResult("1");
            System.out.println(recipes);
        } else {
            result.setResult("0");
            result.setData("Failed to get reciepes");
        }
        return result;
    }
}
