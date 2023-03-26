package com.cookim.cookimws.model;

import com.cookim.cookimws.connection.MariaDBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cookimadmin
 */
public class RecipeDao implements RecipeDaoInterface{
    Connection conn = null;

    public RecipeDao() {
        conn = MariaDBConnection.getConnection();
    }
    
  

    @Override
    public List<Recipe> findAllRecipes() {
        List<Recipe> result = new ArrayList<>();
        try {
            PreparedStatement ps;
            ResultSet rs;
            String query = "select * from recipe;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            
            while (rs.next()) {                
                Recipe r = new Recipe();
                r.setId(rs.getLong("id"));
                r.setId_user(rs.getLong("id_user"));
                r.setName(rs.getString("name"));
                r.setDescription(rs.getString("description"));
                r.setPath_img(rs.getString("path_img"));
                r.setRating(rs.getDouble("rating"));
                r.setLikes(rs.getInt("likes"));
                
                result.add(r);
            }
            ps.close();
            //conn.close();
        } catch (SQLException ex) {
            result = null;
            System.out.println("Error al listar las recetas");    
        }
        return result;
    }
}
