package shopbyar.com.arshop_customer;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Canvas mCanvas;
    private TouchImageView mImageView;
    private ProgressDialog mProgressDialog;

    public PhotoAnnotationFragment() {
        // Required empty public constructor
    }

    public List<Annotation> getAnnotations() {
        if (mQueryResult.annotations != null) {
            return mQueryResult.annotations;
        }
        return new ArrayList<Annotation>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_photo_annotation, container, false);
        mImageFileName = getArguments().getString("image_file_name");
        mImageMetaFileName = getArguments().getString("image_meta_file_name");
        File imageFile = new File(mImageFileName);
        if (imageFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            Bitmap orientedBmp = ExifUtil.rotateBitmap(imageFile.getAbsolutePath(), bmp);
            Bitmap drawableBmp = orientedBmp.copy(Bitmap.Config.ARGB_8888, true);
            orientedBmp.recycle();
            System.gc();
            mCanvas = new Canvas(drawableBmp);
            mImageView = (TouchImageView) view.findViewById(R.id.image_view);
            mImageView.setImageBitmap(drawableBmp);
            drawAnnotationRects();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            RequestBody shopId = RequestBody.create(MediaType.parse("multipart/form-data"), "27");
            File f = new File(mImageFileName);
            RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), f);
            String str = FileUtils.readTextFile(new File(mImageMetaFileName));
            str = "{\"image\": {\"meta\": " + str + "}}";
            RequestBody json = RequestBody.create(MediaType.parse("multipart/form-data"), str);
            Call<ImageQueryResult> call = RestClient.getSharedInstance().getApiService().getAnnotationsOnImage(file, shopId, json);
            mProgressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Searching...");
            mProgressDialog.show();
            final PhotoAnnotationFragment self = this;
            call.enqueue(new Callback<ImageQueryResult>() {
                @Override
                public void onResponse(Response<ImageQueryResult> response, Retrofit retrofit) {
                    mProgressDialog.hide();
                    mQueryResult = response.body();
                    drawAnnotationRects();
                }

                @Override
                public void onFailure(Throwable t) {
                    mProgressDialog.hide();
                    Toast.makeText(getActivity(), "Failed to fetch annotations.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Photo Annotations");
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0,0,0,"Annotations").setIcon(R.drawable.annotation_icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                AnnotationsFragment fragment = new AnnotationsFragment();
                fragment.mPhotoFragment = this;
                getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void drawAnnotationRects() {
        if (mQueryResult == null) {
            return;
        }
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        List<Annotation> annotations = mQueryResult.annotations;
        for (Annotation anno : annotations) {
            Log.d("Draw annotation rect: ", anno.getRect().toString());
            mCanvas.drawRect(anno.getRect(), paint);
        }
        mImageView.invalidate();
    }
}
