package shopbyar.com.arshop_customer.model;

import android.graphics.Rect;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zijiantang on 9/3/16.
 */
public class Annotation {
    public int id;
    @SerializedName("annofakeid")
    public String fakeId;
    public float x; // all proportion to width or height
    public float y;
    public float height;
    public float width;
    public String text;

    public float getCenterX() {
        return x + width/2;
    }

    public float getCenterY() {
        return y + height/2;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("annotation id: "+id);
        sb.append("\n x: "+x);
        sb.append("\n y: "+y);
        sb.append("\n width: "+width);
        sb.append("\n height: "+height);
        return sb.toString();
    }
}
