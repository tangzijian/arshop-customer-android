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


    public PhotoPickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_picker, container, false);
        Button selectPhotoBtn = (Button) view.findViewById(R.id.select_existing_photo);
        selectPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Button takePhotoBtn = (Button) view.findViewById(R.id.take_photo);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCameraFragment();
            }
        });
        return view;
    }

    public void gotoCameraFragment() {
        getFragmentManager().beginTransaction().replace(R.id.frame_container, new CameraFragment()).addToBackStack(null).commit();
    }
}
