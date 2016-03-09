package shopbyar.com.arshop_customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zijiantang on 9/3/16.
 */
public class ImageQueryResult {
    @SerializedName("shop_id")
    int shopId;
    int code;
    @SerializedName("result")
    List<Annotation> annotations;
    @SerializedName("file")
    String fileName;
    @SerializedName("clusters")
    int clusterId;
    String message;

    public ImageQueryResult() {
        annotations = new ArrayList<>();
    }
}
