package shopbyar.com.arshop_customer;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class PhotoAnnotationFragment extends Fragment implements View.OnTouchListener {

    private ImageQueryResult mQueryResult;

    private String mImageFileName;
    private String mImageMetaFileName;
    private Canvas mCanvas;
    private ImageView mImageView;
    private RelativeLayout mAnnotationLabelOverlay;
    private ProgressDialog mProgressDialog;

    private float mPhotoWidth;
    private float mPhotoHeight;

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
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.d("touch location: ", "x: " + x + ",y: " + y);
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_photo_annotation, container, false);
        mAnnotationLabelOverlay = (RelativeLayout) view.findViewById(R.id.annotation_label_overlay);
        mImageFileName = getArguments().getString("image_file_name");
        mImageMetaFileName = getArguments().getString("image_meta_file_name");
        File imageFile = new File(mImageFileName);
        if (imageFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            Bitmap orientedBmp = ExifUtil.rotateBitmap(imageFile.getAbsolutePath(), bmp);
            Bitmap drawableBmp = orientedBmp.copy(Bitmap.Config.ARGB_8888, true);
            mPhotoWidth = drawableBmp.getWidth();
            mPhotoHeight = drawableBmp.getHeight();
            bmp.recycle();
            System.gc();
            mCanvas = new Canvas(drawableBmp);
            mImageView = (ImageView) view.findViewById(R.id.image_view);
            mImageView.setImageBitmap(drawableBmp);
            mImageView.setOnTouchListener(this);
            mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    drawAnnotations();
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null && mQueryResult == null) {
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
                    drawAnnotations();
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

    public void drawAnnotations() {
        if (mQueryResult == null) {
            return;
        }
        int[] actImageRect = getBitmapPositionInsideImageView(mImageView);
        List<Annotation> annotations = mQueryResult.annotations;
        for (Annotation anno: annotations) {
            LabelView label = new LabelView(getActivity());
            label.mLabelText.setVisibility(View.VISIBLE);
            label.mLabelText.setText(anno.text);
            float padding = 100;
//            float imageViewWidth = mImageView.getWidth();
//            float imageViewHeight = mImageView.getHeight();
            float cx = anno.getCenterX() * actImageRect[2];
            float cy = anno.getCenterY() * actImageRect[3];
            if (cx > actImageRect[2] - padding) {
                cx = actImageRect[2] - padding;
            } else if (cx < padding) {
                cx = padding;
            }
            if (cy > actImageRect[3] - padding) {
                cy = actImageRect[3] - padding;
            } else if (cy < padding) {
                cy = padding;
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int)cx + actImageRect[0];
            params.topMargin = (int)cy + actImageRect[1];
            mAnnotationLabelOverlay.addView(label, params);
        }
    }

    /**
     * Returns the bitmap position inside an imageView.
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    public static int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[] {0,0,0,0};

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }
}
