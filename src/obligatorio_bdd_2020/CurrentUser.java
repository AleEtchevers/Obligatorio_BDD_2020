public class CurrentUser {

    private int id;

    private String rol;

    private String alias;

    private static CurrentUser usuario;

    public CurrentUser(){
    }

    public static CurrentUser getCurrentUser(){
        if (usuario == null){
            usuario = new CurrentUser();
        }
        return usuario;
    }

    public int get_id(){
        return this.id;
    }

    public void setUser_id(int id){
        this.id = id;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName_Role(){
        return new StringBuilder().append(this.alias).append(this.rol).toString();
    }