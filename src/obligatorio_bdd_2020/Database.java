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
    private String user;
    private String password;
    private String url;
    
    public Database(){
        if(this.connection_db == null){
            database_login_page db = new database_login_page(this);  
            db.setVisible(true);
        }
    }
    
    public Connection getConnection(){
        return this.connection_db;
    }
    
    public boolean conectarse(String url,String user,String password) throws SQLException{
        if(this.connection_db == null){
            if(conectarseDB(url,user,password)){
                this.user = user;
                this.url = url;
                this.password = password;                 
                return true;
            }else{
                return false;
            }
            
        }else{
            return false;
        }
    }
    
    public Connection reConnect() throws SQLException{
        System.out.println(user);
        System.out.println(password);
        System.out.println(url);
        try(Connection connection = DriverManager.getConnection(url, user, password)){
            return connection;
        }catch(SQLException e){
            System.out.println("No se logro conectar a la base de datos.");
        }
        return null;
    }
    
    private boolean conectarseDB(String url, String user, String password) throws SQLException{
        try(Connection con = DriverManager.getConnection(url, user, password)){
            this.connection_db = con;
            return true;
        }catch(SQLException e){
            return false;
        }
    }
    /**
     * Devuelve los Menus disponibles para el Usuario dentro de una App.
     */
    public ResultSet getMenuUsuario(String username,String appname){
        try (Connection con = DriverManager.getConnection(url, user, password)){
            String query_2 = "SELECT x.* FROM menurol x WHERE (x.user = ?) AND (x.idmenu in (SELECT x.idmenu FROM menu x WHERE x.idapp in (SELECT z.idapp FROM aplicacion z where z.nombreapp = ?)))";
            PreparedStatement statement = con.prepareStatement(query_2);
            //"SELECT x.* FROM menurol x WHERE (x.user = ?) AND (x.idmenu in (SELECT x.idmenu FROM menu x WHERE x.idapp = ? and x.idmenu = ?))"
            
            statement.setString(1,username);           
            statement.setString(2,appname);       
            
            ResultSet rs = statement.executeQuery(); 
            return rs;
   
        }catch (SQLException e){
            System.out.println("Connection failure.");           
        }
        return null;
    }
    
    public boolean login(String username,String password) throws SQLException{
        boolean intento = this.login_attempt(username, password);
        return intento;
    }
    
    public boolean login_attempt(String btn1,String btn2) throws SQLException{
        try (Connection connection = DriverManager.getConnection(url,user,password)){             
            String query = "SELECT * FROM usuario";
            PreparedStatement statement = connection.prepareStatement(query);           
            ResultSet rs = statement.executeQuery();            
            while(rs.next()){
                if(rs.getString("alias").compareTo(btn1)==0 && BCrypt.checkpw(btn2,rs.getString("password"))){
                    System.out.println("Datos correctos");
                    app_select_screen after_login = new app_select_screen(btn1,this);
                    after_login.setVisible(true);
                    return true;
                }
            }
        }         
        catch (SQLException e) {
            System.out.println("Connection failure.");
            return false;
        } 
        return false;
    }
    
    public ResultSet getAppsDeUsuario(String alias){
       try (Connection connection = DriverManager.getConnection(url,user,password)){
            String query_2 = "SELECT x.* FROM aplicacion x WHERE EXISTS (SELECT y.* FROM usuarioapp y WHERE y.idapp = x.idapp AND y.alias = ?)";
            PreparedStatement statement = connection.prepareStatement(query_2);
            statement.setString(1,alias);
            ResultSet rs = statement.executeQuery();  
            return rs;
        }catch (SQLException e){
            System.out.println("Connection failure.");           
        }
       return null;
   }
   
    public ResultSet getRolesUsuarioApp(String alias,int idApp,int idMenu){
       try (Connection connection = DriverManager.getConnection(url,user,password)){
            String query_2 = "SELECT x.* FROM menurol x WHERE (x.user = ?) AND (x.idmenu in (SELECT x.idmenu FROM menu x WHERE x.idapp = ? and x.idmenu = ?))";
            PreparedStatement statement = connection.prepareStatement(query_2);
            
            statement.setString(1,alias);
            statement.setInt(2,idApp); 
            statement.setInt(3,idMenu);            
            ResultSet rs = statement.executeQuery();    
            return rs;
   
        }catch (SQLException e){
            System.out.println("Connection failure.");           
        }
       return null;
       
   }
    /**
     * Dada el nombre de una aplicacion, devuelve el ID unico.
     * Sino, devuelve -1
     * @param nombreApp
     * @return 
     */   
    public int getIdApp(String nombreApp){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            String query_2 = "SELECT idapp FROM aplicacion WHERE nombreapp=?";
            PreparedStatement statement = connection.prepareStatement(query_2);
            statement.setString(1,nombreApp);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getInt("idapp");
            }            
        }catch (SQLException e){
            System.out.println("Connection failure.");           
        } 
        return -1;
    }
    /**
     * Dado el nombre de un Menu, devuelve su ID unico
     * @param nombreMenu
     * @return 
     */
     public int getIdMenu(String nombreMenu){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            System.out.println("Se conecto para buscar la ID del Menu");
            String query_2 = "SELECT idmenu FROM menu WHERE descripcionmenu=?";
            PreparedStatement statement = connection.prepareStatement(query_2);
            statement.setString(1,nombreMenu);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getInt("idmenu");
            }            
        }catch (SQLException e){
            System.out.println("Connection failure.");           
        } 
        return -1;
    }
     
    /**
     * Dado el ID unico de un Menu, devuelve su Descripcion en lenguaje natural.
     * @param idMenu
     * @return 
     */
    public String getDescMenu(int idMenu){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            System.out.println("Se conecto para buscar la Descripcion del Menu");
            String query_desc = "SELECT descripcionmenu FROM menu WHERE idmenu=?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setInt(1,idMenu);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getString("descripcionmenu");
            }            
        }catch (SQLException e){
            System.out.println("Connection failure.");           
        } 
        return "N/A";  
   }
     /**
     * Dado el ID unico de un Rol, devuelve el nombre del mismo.
     * Ej: "Admintrador" o "Editor"
     * @param idrol
     * @return 
     */   
    public String getNombreRol(int idrol){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            System.out.println("Se conecto para buscar la Descripcion del Menu");
            String query_desc = "SELECT nombrerol FROM rol WHERE idrol=?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setInt(1,idrol);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getString("nombrerol");
            }            
        }catch (SQLException e){
            System.out.println("Connection failure.");           
        }
        return "N/A";
   }
    /**
     * Obtiene ID del Rol segun su nombre
     * @param nombreRol
     * @return 
     */    
    public int getIdRol(String nombreRol){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            System.out.println("Se conecto para buscar la Descripcion del Menu");
            String query_desc = "SELECT idrol FROM rol WHERE nombrerol=?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setString(1,nombreRol);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getInt("idrol");
            }            
        }catch (SQLException e){
            System.out.println("Connection failure.");           
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
    public String[] getPermisosPersona(String alias,int idmenu, int idrol){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            String[] permisos = new String[5];
            for (int i = 0; i < 5; i++) {
                permisos[i] = null;               
            }
            
            String query_desc = "SELECT rol.* FROM rol rol WHERE (rol.idrol in (SELECT x.idrol FROM menurol x WHERE (x.user = ?) AND (x.idmenu = ?) AND (x.idrol = ?)))";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setString(1,alias);
            statement.setInt(2,idmenu);
            statement.setInt(3,idrol);
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
            System.out.println("Connection failure.");           
        }
        return null;       
    }
    
    public HashSet<String> getAliases(int idmenu){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            String query_desc = "SELECT x.user FROM menurol x WHERE x.idrol = ?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setInt(1,idmenu);
            ResultSet rs = statement.executeQuery();  
            HashSet<String> c = new HashSet();
            while(rs.next()){
                String username = rs.getString("user");
                System.out.println(username);
                c.add(username);
            }  
            return c;
        }catch (SQLException e){
            System.out.println("Connection failure.");           
        }
        return new HashSet<String>();         
    }
    
    public boolean agregarUsuarioAMenu(String appname,String username,String rolname,String menuname,String alias){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            String query = "SELECT x.* FROM menurol x WHERE (x.user = ?) AND (x.idmenu = ?) AND (x.idrol = ?)";
            int idmenu = this.getIdMenu(menuname);
            int idrol = this.getIdRol(rolname);          
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,alias);
            statement.setInt(2,idmenu);
            statement.setInt(3,idrol);            
            ResultSet rs = statement.executeQuery();
            if(rs.next()){ // Ya hay
                return false;
            }else{ // No hay
                query = "INSERT INTO public.menurol(idmenu,idrol,estado,admincrea,adminautoriza,user) VALUES (?, ?, ?, ?, ?, ?, ?);";
                statement = connection.prepareStatement(query);
                statement.setInt(1,idmenu);
                statement.setInt(2,110);
                statement.setString(3,"En Espera");
                statement.setString(4,username);
                statement.setString(5,"N/A");
                statement.setString(6,alias);               
                int resultado = statement.executeUpdate();
                if(resultado == 1){
                    return true;
                }else{
                    return false;
                }
            }
            
        }catch(SQLException e){
            return false;
        }
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
        
    public static boolean eliminarPersona(int cedula) {
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")) {
        Statement statement_deletion = connection.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_FORWARD_ONLY);
        
        String deletion_query = "DELETE from Persona WHERE cedula = " + cedula;
        ResultSet elementosTabla;

        if personaExiste(cedula) {
            // eliminar de la tabla Persona
            //verificar si la persona existe
            statement_deletion.executeUpdate(deletion_query);
            // eliminar de la tabla MenuRol
            elementosTabla = getElementosTabla("Persona"); // sacar los datos de la tabla
            actualizarAuditoria();

            return true;
            }
        return false;
        } catch (SQLException exc) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, exc);
        }
    }

    //Hacer un get de elementos en una tabla X
    public String[] getElementosTabla(String nombreTabla){
         try(Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){

            Statement statement = connection.createStatement();
            String query_getValores = "SELECT * FROM " + nombreTabla);
            ResultSet resultado = statement.executeQuery(query_getValores);
            int cantidad_columnas = resultado.getColumnCount();
            String [] fila = new String[cantidad_columnas];
            return fila;

        } catch (SQLException exc) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, exc);
        }
        return null;
    }
    
    // buscar persona en tabla personas
    public ResultSet buscarPersona(int cedula){
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")){
            Statement statement = connection.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_FORWARD_ONLY);
            String query_persona = "SELECT * FROM Persona WHERE cedula = " + cedula);
            ResualtSet resultado = statement.executeQuery(guery_persona);
            return resultado;
    
        } catch (SQLException exc) {
        Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, exc);
        }
    return null;
    }

    //Verificar si la persona existe
    public boolean personaExiste(int cedula){
        ResultSet persona = buscarPersona(cedula);
        try {
            return persona.first();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
        
    }
}
    
    
}
    
}
    
}
    
}
    
}
    
}
    
}
