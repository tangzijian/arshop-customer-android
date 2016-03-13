package shopbyar.com.arshop_customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zijiantang on 13/3/16.
 */
public class LabelView extends LinearLayout {
    public TextView mLabelText;
    public ImageView mIconView;

    public LabelView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.view_label, this);
        mLabelText = (TextView) findViewById(R.id.label_text);
        mIconView = (ImageView) findViewById(R.id.label_icon);
    }
}
