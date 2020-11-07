/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obligatorio_bdd_2020;

import queries; 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import utils.login_utils;
import utils.queries;
import utils.register_utils;

/**
 *
 * @author bruno
 */
public class Database {
    
    private Connection connection_db = null;
    private queries queries = new queries();
    private login_utils login = new login_utils();
    private register_utils register = new register_utils();
    private CurrentUser currentUser; // vamos a tener que ver esto 
    
    public Database(){
        if(this.connection_db == null){
            database_login_page db = new database_login_page(connection_db, this);  
            db.setVisible(true);
        }
    }
    
    public Connection getConnection(){
        return this.connection_db;
    }
    
    public boolean conectarse(String url,String user,String password) throws SQLException{
        if(this.connection_db == null){
            if(conectarseDB(url, user, password)){
                return true;
            }else{
                return false;
            }
            
        }else{
            return false;
        }
    }
    
    private boolean conectarseDB(String url, String user, String password) throws SQLException{
        try(Connection con = DriverManager.getConnection(url, user, password)){
            this.connection_db = con;
            return true;
        } catch(SQLException e){
            return false;
        }
    }
    
    public boolean login(String username, String password) throws SQLException{
        boolean intento = this.login.login_attempt(username, password, this);
        return intento;
    }
    
     // METODOS 

    // Llevar un registro de los eventos que suceden
     public void actualizarAuditoria(ResultSet resultado, String nombreTabla, int idApp, String usuario_modificado, int evento){
         try{
             if(resultado.next()){
                String auditoria_query = "INSERT INTO auditoria (id_App,id_Evento,Usuario_Actual,Usuario_Modificado,Rol,Fecha) VALUES(?,?,?,?,?,?);";
                Date date = new Date(Calendar.getInstance().getTime());
                PreparedStatement adutoria_statement = connection.prepareStatement(adutoria_query);

                adutoria_statement.setInt(1, Integer.parseInt(idApp); // id app 
                adutoria_statement.setInt(2, Integer.parseInt(evento); // evento
                adutoria_statement.setString(3, Integer.parseInt(CurrentUser.getCurrentUser().getAlias()); // usuario actual
                adutoria_statement.setString(4, usuario_modificado);
                adutoria_statement.setString(5,CurrentUser.getRol()); // rol
                adutoria_statement.setString(6,date);
                
                int resultado = adutoria_statement.executeUpdate();
             }
         }
        } catch (SQLException ex){
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
     }


     public ResultSet deshabilitarUsuario(String alias){
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            statement = connection.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_FORWARD_ONLY);
            String query_update = "UPDATE usuario SET estado = false WHERE alias = " + alias;
            statement.executeUpdate(query_update);
            ResultSet resultado = statement.getGeneratedKeys();
           
        }
        
        catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
        
    public static boolean eliminarPersona(Int cedula) {
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")) {
        Statement statement_exists = connection.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_FORWARD_ONLY);
        Statement statement_deletion = connection.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_FORWARD_ONLY);
        
        String person_exists_query = "SELECT * FROM Persona WHERE cedula = " + cedula;
        String deletion_query = "DELETE from Persona WHERE cedula = " + cedula;

        if statement_exists.executeUpdate(person_exists_query) {
            // eliminar de la tabla Persona
            statement_deletion.executeUpdate(deletion_query);
            // eliminar de la tabla MenuRol

            return true;
            }
        return false;
        } catch (SQLException exc) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, exc);
        }
    }

    //Hacer un get de elementos en una tabla X
     public ResultSet getElementosTabla(String nombreTabla){
         try(Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){

            Statement statement = connection.createStatement();
            String query_getValores = "SELECT count(*) FROM " + nombreTabla);
            ResultSet resultado = statement.executeQuery(query_getValores);
            return resultado;

        } catch (SQLException exc) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, exc);
        }
        return null;
    }
    
}
    
    
}
    
}
