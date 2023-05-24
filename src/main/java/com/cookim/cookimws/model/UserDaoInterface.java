package com.cookim.cookimws.model;

import java.util.List;

/**
 * Esta interfaz define los métodos para acceder a la lista/tabla de usuarios.
 *
 * @author cookimadmin
 */
public interface UserDaoInterface {

    /**
     * This method is responsible for adding a new User object to the list of
     * users.
     *
     * @param user The User object to add to the list.
     * @return true if the user was added successfully, false otherwise
     */
    public boolean add(User user);

    /**
     * Validates if a user exists in the data source.
     *
     * @param user The user to validate.
     * @return true if the user exists in the data source, false otherwise.
     */
    public boolean validate(User user);

    /**
     * Auto login method that automatically logs a user in using a token.
     *
     * @param token The token of the user to automatically log in.
     * @return true if the user was successfully logged in, false otherwise.
     */
    public boolean autoLogin(String token);

    /**
     * get a list of all users in the system.
     *
     * @return a list of User objects representing all users in the system.
     */
    public List<User> findAllUsers();

    /**
     * Method that obtains the data of a user in the database.
     *
     * @param username The username of the user to find.
     * @param password The password of the user to find.
     * @return the data of the identified user.
     */
    public User findUser(String username, String password);

    /**
     * Method that searches the database for a user by his token.
     *
     * @param token the token with which the user will be searched.
     * @return the user who has that token, if that token doesn't exist in the
     * database it returns null.
     */
    public User findUserByToken(String token);

    /**
     *
     * Finds a user by their ID.
     *
     * @param id The ID of the user.
     * @return The found user.
     */
    public User findUserById(String id);

    /**
     * Method that assigns/updates a user's token.
     *
     * @param user the user to assign the token to.
     * @param token the token to be assigned to the user.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateUserToken(User user, String token);

    /**
     * Method that deletes a user from the database using his token.
     *
     * @param token the token with which the user will be searched in the
     * database
     * @return true if it was successfully removed , false otherwise
     */
    public boolean deleteUser(String token);

    /**
     * Method that modifies an existing user in the database.
     *
     * @param user the user with the modifications
     * @return true if the user has been successfully updated, false otherwise
     */
    public boolean modifyUser(User user);

    /**
     * Method that sets the path of a user's profile picture in the database.
     *
     * @param id The id of the user to set the profile picture path for.
     * @param path The path of the user's profile picture.
     * @return true if the path was successfully set, false otherwise.
     */
    public boolean setUserPathPicture(long id, String path);

    /**
     *
     * Allows a user to like or unlike a recipe based on the provided user
     * token, recipe ID, and action.
     *
     * @param token The user token.
     * @param id_recipe The ID of the recipe.
     * @param action The action to perform (1 for like, 0 for unlike).
     * @return True if the user's like status was updated successfully, false
     * otherwise.
     */
    public boolean userLikeRecipe(String token, String id_recipe, int action);

    /**
     *
     * Retrieves a list of recipe IDs that the user has liked based on the
     * provided user token.
     *
     * @param token The user token.
     * @return A list of recipe IDs liked by the user.
     */
    public List<Long> getRecipesIdLiked(String token);

    /**
     *
     * Allows a user to favorite or unfavorite a recipe identified by the user
     * ID, recipe ID, and action.
     *
     * @param id_user The ID of the user.
     * @param id_recipe The ID of the recipe.
     * @param action The action to perform (favorite or unfavorite).
     * @return True if the user's favorite status was updated successfully,
     * false otherwise.
     */
    public boolean favoriteRecipe(long id_user, String id_recipe, String action);

    /**
     *
     * Manages the follow relationship between two users, identified by the
     * follower ID, followed ID, and action.
     *
     * @param id_follower The ID of the follower user.
     * @param id_followed The ID of the followed user.
     * @param action The action to perform (follow or unfollow).
     * @return True if the follow relationship was managed successfully, false
     * otherwise.
     */
    public boolean manageUserFollow(long id_follower, String id_followed, String action);

    /**
     *
     * Checks if a user with the specified ID follows another user identified by
     * the other user's ID.
     *
     * @param user_id The ID of the user.
     * @param other_user_id The ID of the other user.
     * @return True if the user follows the other user, false otherwise.
     */
    public boolean checkFollow(long user_id, String other_user_id);

    /**
     *
     * Retrieves a list of favorite recipes for a specific user identified by
     * their user ID.
     *
     * @param id_user The ID of the user.
     * @return A list of favorite recipes for the user.
     */
    public List<Recipe> getFavoriteRecipes(long id_user);

    /**
     *
     * Retrieves the number of followers for a specific user identified by their
     * user ID.
     *
     * @param id_user The ID of the user.
     * @return The number of followers for the user.
     */
    public int getNumberOfFollowers(String id_user);

    /**
     *
     * Modifies the password for a specific user identified by their user ID.
     *
     * @param id_user The ID of the user.
     * @param newPassword The new password.
     * @return True if the user's password was modified successfully, false
     * otherwise.
     */
    public boolean modifyUserPassword(long id_user, String newPassword);

}
