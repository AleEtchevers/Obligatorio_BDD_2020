public class CurrentUser {

    private int ci;

    private String rol;

    private String userName;

    private static CurrentUser user;

    public CurrentUser(){
    }

    public static CurrentUser getCurrentUser(){
        if (user == null){
            user = new CurrentUser();
        }
        return user;
    }

    public int get_ci(){
        return this.ci;
    }

    public void setci(int ci){
        this.ci = ci;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName_Role(){
        return new StringBuilder().append(this.userName).append(this.rol).toString();
    }
}