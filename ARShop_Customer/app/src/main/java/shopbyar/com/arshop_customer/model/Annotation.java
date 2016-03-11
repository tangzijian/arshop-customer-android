package shopbyar.com.arshop_customer.model;

import android.graphics.Rect;

/**
 * Created by zijiantang on 9/3/16.
 */
public class Annotation {
    public int id;
    public float x;
    public float y;
    public float height;
    public float width;

    public Rect getRect() {
        return new Rect((int)x, (int)y, (int)(x+width), (int)(y+height));
    }
}
