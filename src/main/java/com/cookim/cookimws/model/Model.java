package com.cookim.cookimws.model;

import com.cookim.cookimws.utils.DataResult;
import com.cookim.cookimws.utils.Utils;
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

    public List<Recipe> getAllRecipes() {
        return recipeDao.findAllRecipes();
    }

    public User getUser(String username, String password) {
        return daoUsers.findUser(username, password);
    }
    
    public User getUserByToken(String token){
        return daoUsers.findUserByToken(token);
    }

    public boolean addNewUser(User user) {
        return daoUsers.add(user);
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    /*public DataResult validateUser(String username, String password) {
        DataResult result = new DataResult();
        User u = new User(username, password);
        //boolean isValid = false;
        
        if (daoUsers.validate(u)) {
            //isValid = true;
            User user = getUser(u);
            result.setResult("user validated successfully");           
            result.setData(user);
        } else {
            //isValid = false;
            result.setResult("User not fount");
            result.setData(null);
        }

        return result;
    }*/
    
    
    /**
     * validates a user in the database using the parameters provided by the client
     * and if it is in the database it generates a new token
     * 
     * @param username the username provided by the client
     * @param password the password provided by the client
     * @return if it is true it returns the new token of the user, otherwise null
     */
    public DataResult validateUser(String username, String password) {
        DataResult result = new DataResult();
        //User u = new User(username, password);
        User u = getUser(username, password);
        String token = Utils.getSHA256(username + password + new Random().nextInt(10000));
        if (u != null) {
            boolean isUpdateToken = daoUsers.updateUserToken(u,token);
            System.out.println("A new token has been assigned to the user: ".concat(u.getUsername()));
            if (isUpdateToken) {
                result.setResult("user validated successfully");
                result.setData(token);
            }
        } else {
            result.setResult("User not found");
            result.setData(null);
        }

        return result;
    }
}
