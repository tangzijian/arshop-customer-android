package shopbyar.com.arshop_customer.rest.service;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.PUT;
import shopbyar.com.arshop_customer.model.LoginUser;
import shopbyar.com.arshop_customer.model.RegisterUser;
import shopbyar.com.arshop_customer.model.User;

/**
 * Created by zijiantang on 25/2/16.
 */
public interface APIService {
    @POST("users/signup/")
    Call<User> userSignup(@Body RegisterUser user);

    @PUT("users/login/")
    Call<User> userLogin(@Body LoginUser user);

    @POST("users/login_check/")
    Call<User> userVerifyToken(@Body User user);
}
