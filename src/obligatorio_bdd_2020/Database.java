/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obligatorio_bdd_2020;

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
    private queries querie = new queries();
    private login_utils login = new login_utils();
    private register_utils register = new register_utils();
    private CurrentUser currentUser;
    
    public Database(){
        if(this.connection_db == null){
            database_login_page db = new database_login_page(connection_db,this);  
            db.setVisible(true);
        }
    }
    
    public Connection getConnection(){
        return this.connection_db;
    }
    
    public boolean conectarse(String url,String user,String password) throws SQLException{
        if(this.connection_db == null){
            if(conectarseDB(url,user,password)){
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
        }catch(SQLException e){
            return false;
        }
    }
    
    public boolean login(String username,String password) throws SQLException{
        boolean intento = this.login.login_attempt(username, password, this);
        return intento;
    }
    
     // METODOS 

    // actualizar auditoria
     public void actualizarAuditoria(ResultSet resultado, String nombreTabla, int evento){
         try{
             if(resultado.next()){
                String auditoria_query = "INSERT INTO auditoria (idApp,idEvento,Administrador,Alias,idUsuario,Evento,Rol,Fecha) VALUES(?,?,?,?,?,?,?,?);";
                Date date = new Date(Calendar.getInstance().getTime().getTime());
                PreparedStatement adutoria_statement = connection.prepareStatement(adutoria_query);

                adutoria_statement.setInt(1, Integer.parseInt(data[0]));
                adutoria_statement.setInt(2, Integer.parseInt(data[1]));
                adutoria_statement.setString(3, data[2]);
                adutoria_statement.setString(4, data[3]);
                adutoria_statement.setInt(5, Integer.parseInt(CurrentUser.getCurrentUser().get_id()); // id usuario
                adutoria_statement.setInt(6, Integer.parseInt(data[5])); // id evento
                adutoria_statement.setString(7, data[6]); // rol
                adutoria_statement.setString(date);
                
                
                int resultado = adutoria_statement.executeUpdate();
             }
         }
     }

     // eliminar una persona
     public ResultSet eliminarPersona(String nombreTabla, String ciPersona){
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            statement = connection.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_FORWARD_ONLY);
            String query_update = "UPDATE "+ nombreTabla + " SET estado = false WHERE ci = " + ciPersona;
            statement.executeUpdate(query_update);
            ResultSet resultado = statement.getGeneratedKeys();
            // aca llamar a metodo auditoria para registrar el evento de eliminar una persona

        }
        catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
}
