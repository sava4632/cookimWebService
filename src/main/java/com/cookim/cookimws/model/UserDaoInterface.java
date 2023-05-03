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

}
