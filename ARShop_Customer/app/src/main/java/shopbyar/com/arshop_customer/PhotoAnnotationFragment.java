package shopbyar.com.arshop_customer;


import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import shopbyar.com.arshop_customer.model.Annotation;
import shopbyar.com.arshop_customer.model.ImageQueryResult;
import shopbyar.com.arshop_customer.rest.RestClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoAnnotationFragment extends Fragment {

    private ImageQueryResult mQueryResult;

    private String mImageFileName;
    private String mImageMetaFileName;

    public PhotoAnnotationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_annotation, container, false);
        mImageFileName = getArguments().getString("image_file_name");
        mImageMetaFileName = getArguments().getString("image_meta_file_name");
        String[] lst = getActivity().getExternalFilesDir(null).list();
        File imageFile = new File(getActivity().getExternalFilesDir(null), mImageFileName);
        if (imageFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            Bitmap orientedBmp = ExifUtil.rotateBitmap(imageFile.getAbsolutePath(), bmp);
            TouchImageView imageView = (TouchImageView) view.findViewById(R.id.image_view);
            imageView.setImageBitmap(orientedBmp);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RequestBody shopId = RequestBody.create(MediaType.parse("multipart/form-data"), "27");
        File f = new File(getActivity().getExternalFilesDir(null), mImageFileName);
        RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        String str = FileUtils.readTextFile(new File(getActivity().getExternalFilesDir(null), mImageMetaFileName));
        str = "{\"image\": {\"meta\": " + str + "}}";
        RequestBody json = RequestBody.create(MediaType.parse("multipart/form-data"), str);
        Call<ImageQueryResult> call = RestClient.getSharedInstance().getApiService().getAnnotationsOnImage(file, shopId, json);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Searching...");
        progressDialog.show();
        call.enqueue(new Callback<ImageQueryResult>() {
            @Override
            public void onResponse(Response<ImageQueryResult> response, Retrofit retrofit) {
                mQueryResult = response.body();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(), "Failed to fetch annotations.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Photo Annotations");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }


}
