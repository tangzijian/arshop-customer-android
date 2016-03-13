package shopbyar.com.arshop_customer;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnnotationDetailFragment extends Fragment {


    public AnnotationDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_annotation_detail, container, false);
        TextView textView = (TextView) view.findViewById(R.id.annotation_text);
        String text = getArguments().getString("annotation_text");
        textView.setText(text);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Annotation Details");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

}
