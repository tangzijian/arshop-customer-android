package shopbyar.com.arshop_customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zijiantang on 9/3/16.
 */
public class ImageQueryResult {
    @SerializedName("shop_id")
    public int shopId;
    public int code;
    @SerializedName("result")
    public List<Annotation> annotations;
    @SerializedName("file")
    public String fileName;
    @SerializedName("clusters")
    public int clusterId;
    public String message;

    public ImageQueryResult() {
        annotations = new ArrayList<>();
    }
}
