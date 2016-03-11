package shopbyar.com.arshop_customer;

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
        holder.annotationId.setText("Annotation ID: " + annotation.id);
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
        }
    }
}
