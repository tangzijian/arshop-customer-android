package shopbyar.com.arshop_customer;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoAnnotationFragment extends Fragment {


    public PhotoAnnotationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_annotation, container, false);
        String imageFileName = getArguments().getString("image_file_name");
        String imageMetaFileName = getArguments().getString("image_meta_file_name");
        String[] lst = getActivity().getExternalFilesDir(null).list();
        File imageFile = new File(getActivity().getExternalFilesDir(null), imageFileName);
        if (imageFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            Bitmap orientedBmp = ExifUtil.rotateBitmap(imageFile.getAbsolutePath(), bmp);
            TouchImageView imageView = (TouchImageView) view.findViewById(R.id.image_view);
            imageView.setImageBitmap(orientedBmp);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Photo Annotations");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}
