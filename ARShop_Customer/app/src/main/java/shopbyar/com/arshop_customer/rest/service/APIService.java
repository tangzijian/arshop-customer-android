package shopbyar.com.arshop_customer.rest.service;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import shopbyar.com.arshop_customer.model.ImageQueryResult;
import shopbyar.com.arshop_customer.model.LoginUser;
import shopbyar.com.arshop_customer.model.User;

/**
 * Created by zijiantang on 25/2/16.
 */
public interface APIService {
    @FormUrlEncoded
    @POST("users/signup/")
    Call<User> userSignup(@FieldMap Map<String, String> params);

    @PUT("users/login/")
    Call<User> userLogin(@Body LoginUser user);

    @POST("users/login_check/")
    Call<User> userVerifyToken(@Body User user);

    @Multipart
    @POST("clusters/json/")
    Call<ImageQueryResult> getAnnotationsOnImage(@Part("file\"; filename=\"image.jpg\" ") RequestBody file,
                                                 @Part("shop_id") RequestBody shopId,
                                                 @Part("json") RequestBody json);
}
