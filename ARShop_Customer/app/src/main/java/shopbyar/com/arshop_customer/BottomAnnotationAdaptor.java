package shopbyar.com.arshop_customer;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import shopbyar.com.arshop_customer.model.Annotation;

/**
 * Created by zijiantang on 18/3/16.
 */
public class BottomAnnotationAdaptor extends RecyclerView.Adapter<BottomAnnotationAdaptor.BottomAnnotationAdaptorViewHolder> {

    private List<Annotation> annotationList;

    public OnAnnotationClickListener annotationClickListener;

    public interface OnAnnotationClickListener {
        public void onAnnotationClicked(int position);
    }

    public BottomAnnotationAdaptor(List<Annotation> annotationList) {
        this.annotationList = annotationList;
    }

    @Override
    public int getItemCount() {
        return annotationList.size();
    }

    @Override
    public void onBindViewHolder(BottomAnnotationAdaptorViewHolder holder, int position) {
        Annotation annotation = annotationList.get(position);
        holder.annotationText.setText(annotation.text);
        holder.mPosition = position;
        holder.listener = annotationClickListener;
    }

    @Override
    public BottomAnnotationAdaptorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_annotation_card_view, parent, false);
        return new BottomAnnotationAdaptorViewHolder(view);
    }

    public static class BottomAnnotationAdaptorViewHolder extends RecyclerView.ViewHolder {
        protected TextView annotationText;
        protected int mPosition;
        public OnAnnotationClickListener listener;
        public BottomAnnotationAdaptorViewHolder(View v) {
            super(v);
            annotationText = (TextView) v.findViewById(R.id.annotation_text);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("annotation on click: ", ""+mPosition);
                    if (listener != null) {
                        listener.onAnnotationClicked(mPosition);
                    }
                }
            });
        }
    }
}
