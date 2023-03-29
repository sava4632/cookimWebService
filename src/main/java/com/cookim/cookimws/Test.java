package com.cookim.cookimws;

import com.cookim.cookimws.model.Model;
import com.cookim.cookimws.model.User;
import com.cookim.cookimws.utils.DataResult;
import com.cookim.cookimws.utils.Utils;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author cookimadmin
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
//        DataResult dr  = m.validateUser("admin", "admin");
//        List<User> users = m.getAllUsers();
//        for(User u : users){
//            System.out.println(u.toString());
//        }
//        
////       DataResult dr = m.validateUser("admin", "admin");
////       System.out.println(dr.getResult() +"\n" + dr.getData());
        //listUsers();
        //addUser();
    }

//    private static void addUser() {
//        Model m = new Model();
//        System.out.println("Add new user...");
//        User user = getUserData();
//        boolean isAdded = m.addNewUser(user);
//        
//        if(isAdded){
//            System.out.println("New user added successfully!");
//        }else{
//            System.out.println("Failed to add new user.");
//        }
//    }
    
    private static void listUsers() {
        Model m = new Model();
        List<User> users = m.getAllUsers();
        for(User u : users){
            System.out.println(u.toString());
        }
    }

    private static User getUserData() {
        System.out.println("put the data for the new user");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Input username:");
        String username = scanner.nextLine();
        System.out.println("Input password:");
        String password = scanner.nextLine();
        System.out.println("Input full name:");
        String fullName = scanner.nextLine();
        System.out.println("Input email:");
        String email = scanner.nextLine();
        System.out.println("Input phone:");
        String phone = scanner.nextLine();
        System.out.println("Input path image:");
        String pathImg = scanner.nextLine();
        System.out.println("Input description:");
        String description = scanner.nextLine();
        System.out.println("Input rol id:");
        long rolId = scanner.nextLong(); //it will have to be automated
        scanner.nextLine(); //clean scanner line

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFull_name(fullName);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setPath_img(pathImg);
        newUser.setDescription(description);
        newUser.setId_rol(rolId);
        newUser.setToken(Utils.getSHA256(username + password));
        return newUser;
    }

    

}
