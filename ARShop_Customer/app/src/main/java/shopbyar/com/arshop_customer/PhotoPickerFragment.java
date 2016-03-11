package shopbyar.com.arshop_customer;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoPickerFragment extends Fragment {

    private Button mSelectPhotoBtn;
    private View.OnClickListener mListener;
    public PhotoPickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_picker, container, false);
        mSelectPhotoBtn = (Button) view.findViewById(R.id.select_existing_photo);
        mSelectPhotoBtn.setOnClickListener(mListener);
        Button takePhotoBtn = (Button) view.findViewById(R.id.take_photo);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCameraFragment();
            }
        });
        return view;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mListener = listener;
        if (mSelectPhotoBtn != null) {
            mSelectPhotoBtn.setOnClickListener(listener);
        }
    }

    public void gotoCameraFragment() {
        getFragmentManager().beginTransaction().replace(R.id.frame_container, new CameraFragment()).addToBackStack(null).commit();
    }
}
