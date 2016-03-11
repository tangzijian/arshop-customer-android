package shopbyar.com.arshop_customer;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import shopbyar.com.arshop_customer.model.Annotation;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnnotationsFragment extends Fragment {

    public PhotoAnnotationFragment mPhotoFragment;

    public AnnotationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_annotations, container, false);
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.annotation_list);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        if (mPhotoFragment != null) {
            AnnotationsAdaptor adaptor = new AnnotationsAdaptor(mPhotoFragment.getAnnotations());
            recList.setAdapter(adaptor);
        } else {
            AnnotationsAdaptor adaptor = new AnnotationsAdaptor(new ArrayList<Annotation>());
            recList.setAdapter(adaptor);
        }

        return view;
    }

}
