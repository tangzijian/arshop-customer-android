package shopbyar.com.arshop_customer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zijiantang on 25/2/16.
 */
public class LoginUser {
    @SerializedName("user_name")
    private String username;
    private String password;
    private String type;
    public LoginUser() {

    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getType() {
        return type;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setType(String type) {
        this.type = type;
    }
}
