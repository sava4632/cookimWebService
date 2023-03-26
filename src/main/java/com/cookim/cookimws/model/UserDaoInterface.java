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
      * contrary.
      */
    public boolean add(User user);
    
    /**
      * Validates if a user exists in the data source.
      * @param user The user to validate.
      * @return true if the user exists in the data source, false otherwise.
      */
    public boolean validate(User user);
    
    
    /**
      * get a list of all users in the system.
      *
      * @return a list of User objects representing all users in
      * the system.
      */
    public List<User> findAllUsers();
    
    /**
     * method that obtains the data of a user in the database.
     * @param user the user with the credentials.
     * @return the data of the identified user.
     */
    public User findUser(String username,String password);
    
    public User findUserByToken(String token);
    
    public boolean updateUserToken(User user,String token);
    
}
