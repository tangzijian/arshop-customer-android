package shopbyar.com.arshop_customer.model;

/**
 * Created by zijiantang on 25/2/16.
 */
public class RegisterUser {
    private String username;
    private String password;
    private String email;

    public RegisterUser() {

    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
