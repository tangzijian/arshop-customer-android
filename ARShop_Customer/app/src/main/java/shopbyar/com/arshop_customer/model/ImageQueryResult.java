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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("============Image Query Result: =============\n");
        sb.append("annotations: \n");
        for (Annotation anno: annotations) {
            sb.append(anno.toString()+"\n");
        }
        return sb.toString();
    }
}
