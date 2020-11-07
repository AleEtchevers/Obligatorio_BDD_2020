/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author bruno
 */
public class queries {
    
    private String FAILURE = "Connection failure."

    /**
     * Dada el nombre de una aplicacion, devuelve el ID unico.
     * Sino, devuelve -1
     * @param nombreApp
     * @return 
     */
    public static int getIdApp(String nombreApp){
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            String query_2 = "SELECT idapp FROM aplicacion WHERE nombreapp=?";
            PreparedStatement statement = connection.prepareStatement(query_2);
            statement.setString(1,nombreApp);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getInt("idapp");
            }            
        }catch (SQLException e){
            System.out.println(FAILURE);           
        } 
        return -1;
    }
    
    /**
     * Dado el nombre de un Menu, devuelve su ID unico
     * @param nombreMenu
     * @return 
     */
    public static int getIdMenu(String nombreMenu){
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            System.out.println("Se conecto para buscar la ID del Menu");
            String query_2 = "SELECT idmenu FROM menu WHERE descripcionmenu=?";
            PreparedStatement statement = connection.prepareStatement(query_2);
            statement.setString(1,nombreMenu);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getInt("idmenu");
            }            
        }catch (SQLException e){
            System.out.println(FAILURE);           
        } 
        return -1;
    }
    
    /**
     * Dado el ID unico de un Menu, devuelve su Descripcion en lenguaje natural.
     * @param idMenu
     * @return 
     */
    public static String getDescMenu(int idMenu){
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            System.out.println("Se conecto para buscar la Descripcion del Menu");
            String query_desc = "SELECT descripcionmenu FROM menu WHERE idmenu=?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setInt(1,idMenu);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getString("descripcionmenu");
            }            
        }catch (SQLException e){
            System.out.println(FAILURE);           
        } 
        return "N/A";  
   }
   
    /**
     * Dado el ID unico de un Rol, devuelve el nombre del mismo.
     * Ej: "Admintrador" o "Editor"
     * @param idRol
     * @return 
     */
    public static String getNombreRol(int idRol){
        // Connection connection = DriverManager.getConnection();
        if !(connection.isClosed()) {
            System.out.println("Se conecto para buscar la Descripcion del Menu");
            String query_desc = "SELECT nombrerol FROM rol WHERE idrol=?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setInt(1, idrol);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getString("nombrerol");
            }
        } else {
            failure();
        }            
        return "N/A";
   }
    /**
     * Obtiene ID del Rol segun su nombre
     * @param nombreRol
     * @return 
     */
    public static int getIdRol(String nombreRol){
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            System.out.println("Se conecto para buscar la Descripcion del Menu");
            String query_desc = "SELECT idrol FROM rol WHERE nombrerol=?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setString(1, nombreRol);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getInt("idrol");
            }            
        }catch (SQLException e){
            failure();          
        }
        return -1;        
    }
   
    /**
     * Devuelve los permisos que tiene un Usuario en un Menu con un Rol especifico.
     * Ej: "Editar","Ver","Buscar" y "Crear"
     * @param alias
     * @param idmenu
     * @param idrol
     * @return 
     */
    public static String[] getPermisosPersona(String alias,int idMenu, int idRol){
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            String[] permisos = new String[5];
            for (int i = 0; i < 5; i++) {
                permisos[i] = null;               
            }
            
            String query_desc = "SELECT rol.* FROM rol rol WHERE (rol.idrol in (SELECT x.idrol FROM menurol x WHERE (x.user = ?) AND (x.idmenu = ?) AND (x.idrol = ?)))";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setString(1,alias);
            statement.setInt(2,idMenu);
            statement.setInt(3,idRol);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                if(rs.getBoolean("ver")){
                    permisos[0] = "ver";
                }
                if(rs.getBoolean("crear")){
                    permisos[1] = "crear";
                }
                if(rs.getBoolean("eliminar")){
                    permisos[2] = "eliminar";
                }
                if(rs.getBoolean("modificar")){
                    permisos[3] = "modificar";
                }
                if(rs.getBoolean("buscar")){
                    permisos[4] = "buscar";
                }
            }
            return permisos;
            
        }catch (SQLException e){
            failure();           
        }
        return null;       
    }
    
    /**
     * 
     */
    public static HashSet<String> getAliases(int idMenu){
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            String query_desc = "SELECT x.user FROM menurol x WHERE x.idrol = ?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setInt(1, idMenu);
            ResultSet rs = statement.executeQuery();  
            HashSet<String> c = new HashSet();
            while(rs.next()){
                String username = rs.getString("user");
                System.out.println(username);
                c.add(username);
            }  
            return c;
        }catch (SQLException e){
            failure()        
        }
        return new HashSet<String>();         
    }

    public static void failure() {
        System.out.println(FAILURE); 
    }
    
}
