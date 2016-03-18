package shopbyar.com.arshop_customer.rest;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import shopbyar.com.arshop_customer.rest.service.APIService;

/**
 * Created by zijiantang on 25/2/16.
 */
public class RestClient {
    private static RestClient sharedInstance = null;

    private static final String BASE_URL = "http://dbgpucluster-2.d2.comp.nus.edu.sg/ARShop/api/";
    private APIService apiService;

    private RestClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);
    }

    public static RestClient getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new RestClient();
        }
        return sharedInstance;
    }

    public APIService getApiService() {
        return apiService;
    }
}
