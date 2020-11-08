/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obligatorio_bdd_2020;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import utils.BCrypt;
import utils.login_utils;
import utils.queries;
import utils.register_utils;

/**
 *
 * @author bruno
 */
public class Database {
    //Connection connection = DriverManager.getConnection("jdbc:postgresql://192.168.56.102:5432/tests", "postgres", "bruno123")
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
     * Obtiene ID del Usuario segun su nombre
     * @param nombreRol
     * @return 
     */    
    public int getIdUsuario(String nombreUsuario){
        try (Connection connection = DriverManager.getConnection(url,user,password)){
            System.out.println("Se conecto para buscar la Descripcion del Menu");
            String query_desc = "SELECT ci FROM usuario WHERE alias=?";
            PreparedStatement statement = connection.prepareStatement(query_desc);
            statement.setString(1,nombreUsuario);
            ResultSet rs = statement.executeQuery();       
            while(rs.next()){
                return rs.getInt("ci");
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
    /**
     * Devuelve todos los Usuarios (con su Rol) que pertenecen a un Menu dado por parametro
     * @param idmenu
     * @return 
     */
    public ResultSet obtenerUsuariosMenu(int idmenu){
        try(Connection c = DriverManager.getConnection(url,user,password)){
            String query = "SELECT x.user,x.idrol FROM menurol x WHERE x.idmenu = ?";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1, idmenu);
            ResultSet rs = stmt.executeQuery();
            
            return rs;
            
            
        }catch(SQLException e){
            return null;
        }
        
    }
    
    /**
     * 
     * @param subAdmin  el responsable de esta accion
     * @param nuevoRol  el Rol que sera asignado a nuevoUser
     * @param idmenu    el Menu a donde se asignara
     * @param nuevoUser el Usuario que sera asignado a su nuevo Rol 
     * @return 
     */
    public boolean asignarRolUsuarioMenu(String subAdmin,String nuevoRol,int idmenu,String nuevoUser){
        try(Connection c = DriverManager.getConnection(url,user,password)){
            String query = "INSERT INTO public.menurol(idmenu, idrol, estado, admincrea, adminautoriza,user) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1, idmenu);
            stmt.setInt(2,this.getIdRol(nuevoRol));
            stmt.setString(3,"En Espera");
            stmt.setString(4,subAdmin);
            stmt.setString(5,"N/A");
            stmt.setString(6,nuevoUser);
            int resultado = stmt.executeUpdate();
            return resultado == 1;       
            
        }catch(SQLException e){
            return false;
        }      
    }
    /**
     * Funcion que le quita un Rol a un Usuario en un Menu particular.
     * @param subAdmin
     * @param nuevoRol
     * @param idmenu
     * @param nuevoUser
     * @return 
     * @throws java.sql.SQLException 
     */
    public boolean quitarRolUsuarioMenu(String subAdmin,int nuevoRol,int idmenu,String nuevoUser) throws SQLException{
        try(Connection c = DriverManager.getConnection(url,user,password)){
            String query = "DELETE FROM menurol WHERE idmenu = ? AND user = ? AND idrol = ?;";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1,idmenu);
            stmt.setString(2,nuevoUser);
            stmt.setInt(3,nuevoRol);            
            int resultado = stmt.executeUpdate();
            
            return resultado >= 1;
        }catch(SQLException e){
            return false;
            
        }        
    }
    /**
     * Asigna un nuevo Rol a un Usuario en un Menu determinado.
     * @return 
     */
    public boolean asignarRolUsuarioMenu(String subAdmin,int nuevoRol,int idmenu,String nuevoUser){
        try(Connection c = DriverManager.getConnection(url,user,password)){
            String query = "INSERT INTO menurol(idmenu, idrol, estado, admincrea, adminautoriza,user) VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1,idmenu);
            stmt.setInt(2,nuevoRol);
            stmt.setString(3,"En Espera");
            stmt.setString(4,subAdmin); 
            stmt.setString(5,"N/A");
            stmt.setString(6,nuevoUser);
            int resultado = stmt.executeUpdate();
            return resultado >= 1;
        }catch(SQLException e){
            return false;
            
        } 
       
    }
    /**
     * 
     * @param subAdmin
     * @param idmenu
     * @param usuarioTarget
     * @return 
     */
    public boolean buscarUsuarioEnMenu(String subAdmin,int idmenu,String usuarioTarget){
        try(Connection c = DriverManager.getConnection(url,user,password)){
            String query = "SELECT x.user FROM menurol x WHERE x.idmenu = ? AND x.user = ?";            
            PreparedStatement stmt = c.prepareStatement(query);
            stmt.setInt(1, idmenu);
            stmt.setString(2,usuarioTarget);           
            ResultSet rs = stmt.executeQuery();
            
            return rs.next();
            
        }catch(SQLException e){
            return false;
            
        } 
    }

    public boolean quitarUsuarioMenu(String appname, String username, String rolname, String menuname, String alias) {
        
        //AUDITORIA .... 
        
        try(Connection c = DriverManager.getConnection(url,user,password)){
            String query = "UPDATE public.menurol SET estado=?,WHERE idmenu = ? AND user = ?;";
            PreparedStatement stmt = c.prepareStatement(query);        
            stmt.setString(2,"En Espera");
            stmt.setInt(2,this.getIdMenu(menuname));
            stmt.setString(3,alias);

            int resultado = stmt.executeUpdate();
            return resultado >= 1;
        }catch(SQLException e){
            return false;
            
        }         
    }

    public String maxGenericSQL(String atributo, String nombreTabla) throws SQLException{
        String maximo="0";
        String consultaSQL;
        ResultSet resultado;
        ResultSetMetaData rsmd;
        
        int rows = 0;
        int columnsNumber;
        Statement sentencia;
         try {
            
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection(url,user,password);
            sentencia = c.createStatement();
          
            consultaSQL="SELECT "+atributo+" FROM "+nombreTabla+" WHERE "+atributo+" = ( SELECT MAX("+atributo+") FROM "+nombreTabla+");";
            resultado=sentencia.executeQuery(consultaSQL);
            rsmd = resultado.getMetaData();
            columnsNumber=rsmd.getColumnCount();


            if(resultado.next()){
                maximo=resultado.getString(1);
            }

            resultado.close();
         } catch (Exception e) {
             System.out.println(e);
            return null;
         }
         resultado.close();
         sentencia.close();
         return maximo;
    }
    
    
    
    public void crearAdutoriaEvento(String administrador,String tipoevento, String aliasUsuario, String rol,String nomnbreApp,String descripcion){
        try(Connection c = DriverManager.getConnection(url,user,password)){
            String query_evento= "INSERT INTO public.evento (idevento, tipodeevento, quienlocreo, descripcion) "+ "VALUES (?, ?, ?, ?);"; 
            PreparedStatement stmt = c.prepareStatement(query_evento); 
            String maximoEvento = maxGenericSQL("idevento","public.evento");
            int idevento = Integer.parseInt(maximoEvento)+ 1;

            stmt.setInt(1, idevento);
            stmt.setString(2,tipodeevento);
            stmt.setString(3,aliasUsuario);
            stmt.setString(4,descripcion);
            ResultSet resultadoEvento = stmt.executeQuery();

            String query_adutoria = "INSERT INTO public.auditorias (idsuceso, idapp, fechahora, usuarioinvolucrado, admincrea,adminautoriza,idevento,nombrerol) "+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";           
            PreparedStatement stmt1 = c.prepareStatement(query_adutoria); 

            String maximo = maxGenericSQL("idsuceso","public.auditorias");
            int idsuceso = Integer.parseInt(maximo)+ 1;
            stmt1.setInt(1, idsuceso);
            stmt1.setInt(2,this.getIdApp(nomnbreApp));
            stmt1.setString(3, "current_timestamp");
            stmt1.setString(4,aliasUsuario);
            stmt1.setString(5,administrador);
            stmt1.setString(6,"NULL");
            stmt1.setInt(7,idevento);
            stmt1.setString(8,rol);

            ResultSet resultado = stmt1.executeQuery();
            resultadoEvento.close();
            resultado.close();

        }catch(SQLException e){
            System.out.println("Error: "+ e);  
            
        } 
    }
}
