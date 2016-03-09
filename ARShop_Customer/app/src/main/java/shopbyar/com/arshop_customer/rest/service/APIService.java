package shopbyar.com.arshop_customer.rest.service;

import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import shopbyar.com.arshop_customer.model.ImageQueryResult;
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

    @Multipart
    @POST("clusters/json/")
    Call<ImageQueryResult> getAnnotationsOnImage(@Part("file\"; filename=\"image.jpg\" ")RequestBody file,
                                                 @Part("shop_id") RequestBody shopId,
                                                 @Part("json") RequestBody json);
}
