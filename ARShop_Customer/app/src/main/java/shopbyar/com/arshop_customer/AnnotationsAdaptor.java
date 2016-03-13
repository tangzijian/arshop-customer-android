package shopbyar.com.arshop_customer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import shopbyar.com.arshop_customer.model.Annotation;

/**
 * Created by zijiantang on 9/3/16.
 */
public class AnnotationsAdaptor extends RecyclerView.Adapter<AnnotationsAdaptor.AnnotationsViewHolder> {
    private List<Annotation> annotationList;

    public AnnotationsAdaptor(List<Annotation> annotationList) {
        this.annotationList = annotationList;
    }

    @Override
    public int getItemCount() {
        return annotationList.size();
    }

    @Override
    public void onBindViewHolder(AnnotationsViewHolder holder, int position) {
        Annotation annotation = annotationList.get(position);
        holder.annotationId.setText(annotation.text);
    }

    @Override
    public AnnotationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annotation_card_layout, parent, false);
        return new AnnotationsViewHolder(view);
    }

    public static class AnnotationsViewHolder extends RecyclerView.ViewHolder {
        protected TextView annotationId;
        public AnnotationsViewHolder(View v) {
            super(v);
            annotationId = (TextView) v.findViewById(R.id.annotation_id);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnnotationDetailFragment fragment = new AnnotationDetailFragment();
                    Bundle args = new Bundle();
                    args.putString("annotation_text", annotationId.getText().toString());
                    fragment.setArguments(args);
                    ((AppCompatActivity) v.getContext()).getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
                }
            });
        }
    }
}
