package com.cookim.cookimws.model;

import com.cookim.cookimws.connection.MariaDBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cookimadmin
 */
public class UserDao implements UserDaoInterface {

    //Connection conn = null;
    List<User> result;

    public UserDao() {
        result = new ArrayList<>();
    }

    @Override
    public boolean add(User user) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            String query = "INSERT INTO user (username, password, full_name, email, phone, path_img, id_rol)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(query);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFull_name());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getPath_img());
            ps.setLong(7, user.getId_rol());
            int rowsInserted = ps.executeUpdate(); //obtiene el numero de filas modificadas en la base de datos.
            ps.close();
            return rowsInserted > 0;
        } catch (Exception e) {
            System.out.println("failed to add user : " + e.toString());
            return false;
        }
    }

    @Override
    public boolean deleteUser(String token) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;

            String query = "DELETE FROM user WHERE token = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, token);
            int rowsDeleted = ps.executeUpdate();
            ps.close();
            return rowsDeleted > 0;
        } catch (Exception e) {
            System.out.println("failed to delete user : " + e.toString());
            return false;
        }
    }
    
    @Override
    public boolean modifyUser(User user) {
        try (Connection conn = MariaDBConnection.getConnection()){
            PreparedStatement ps;
            String query = "UPDATE user SET username=?, password=?, full_name=?, email=?, phone=?, path_img=?, description=?, id_rol=?, token=? WHERE token=?";
            ps = conn.prepareStatement(query);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFull_name());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getPath_img());
            ps.setString(7, user.getDescription());
            ps.setLong(8, user.getId_rol());
            ps.setString(9, user.getToken());
            ps.setString(10, user.getToken());
            
            int rowsUpdated = ps.executeUpdate();
            ps.close();
            return rowsUpdated > 0;
        } catch (Exception e) {
            System.out.println("failed to modify user : " + e.toString());
            return false;
        }
    }

    @Override
    public boolean validate(User user) {
        try (Connection conn = MariaDBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM user WHERE username = ? AND password = ?")) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            System.out.println("matches found with these credentials: " + count);
            return count > 0;
        } catch (Exception e) {
            System.out.println("failed to validate user : " + e.toString());
            return false;
        }
    }
    
    @Override
    public boolean autoLogin(String token) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            String query = "SELECT * FROM user WHERE token=?";
            ps = conn.prepareStatement(query);
            ps.setString(1, token);

            ResultSet rs = ps.executeQuery();
            boolean result = rs.next(); // if rs.next() is true it means a user with that token was found
            rs.close();
            ps.close();

            return result;
        } catch (Exception e) {
            System.out.println("Failed to auto-login user: " + e.toString());
            return false;
        }
    }

    @Override
    public List<User> findAllUsers() {
        //conn = MariaDBConnection.getConnection();
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT * FROM user;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFull_name(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setPath_img(rs.getString("path_img"));
                u.setDescription(rs.getString("description"));
                u.setId_rol(rs.getLong("id_rol"));
                u.setToken(rs.getString("token"));

                result.add(u);
            }
            ps.close();
        } catch (Exception e) {
            result = null;
            System.out.println("error listing users");
        }
        return result;
    }

    @Override
    public User findUser(String username, String password) {
        User user = null;
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs != null) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setFull_name(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setPath_img(rs.getString("path_img"));
                    user.setDescription(rs.getString("description"));
                    user.setId_rol(rs.getLong("id_rol"));
                    user.setToken(rs.getString("token"));
                }
            }
            ps.close();
        } catch (Exception e) {
            System.out.println("failed to find user: " + e.toString());
        }
        return user;
    }

    @Override
    public boolean updateUserToken(User user, String token) {
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;

            String query = "UPDATE user SET token = ? WHERE id = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, token);
            ps.setLong(2, user.getId());
            int rowsUpdated = ps.executeUpdate();
            ps.close();
            return rowsUpdated > 0;
        } catch (Exception e) {
            System.out.println("failed to update user token : " + e.toString());
            return false;
        }
    }

    @Override
    public User findUserByToken(String token) {
        User user = null;
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            ResultSet rs;
            String query = "SELECT * FROM user WHERE token = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, token);
            rs = ps.executeQuery();

            // Check if the query result is empty
            if (!rs.isBeforeFirst()) {
                System.out.println("User with this token not found in database");
                return null; // or you can return a special User object indicating that the user was not found
            }

            if (rs != null && rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setFull_name(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setPath_img(rs.getString("path_img"));
                user.setDescription(rs.getString("description"));
                user.setId_rol(rs.getLong("id_rol"));
            }
            ps.close();
        } catch (Exception e) {
            System.out.println("failed to find user by token: " + e.toString());
        }
        return user;
    }

    @Override
    public boolean setUserPathPicture(long id, String path) {
        boolean result = false;
        try (Connection conn = MariaDBConnection.getConnection()) {
            PreparedStatement ps;
            String query = "UPDATE user SET path_img = ? WHERE id = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, path);
            ps.setLong(2, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                result = true;
            }
            ps.close();
        } catch (Exception e) {
            System.out.println("failed to set user path picture: " + e.toString());
        }
        return result;
    }

}
