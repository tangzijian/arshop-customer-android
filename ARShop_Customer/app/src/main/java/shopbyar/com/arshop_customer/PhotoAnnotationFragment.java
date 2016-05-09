package shopbyar.com.arshop_customer;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
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
    private ImageView mImageView;
    private RelativeLayout mAnnotationLabelOverlay;
    private ProgressDialog mProgressDialog;

    private float mPhotoWidth;
    private float mPhotoHeight;
    private List<LabelView> mLabels;

    private BottomAnnotationAdaptor mAnnotationAdaptor;
    private LinearLayoutManager mLinearLayoutManager;

    private List<Annotation> mAnnotationList = new ArrayList<>();

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
        mAnnotationLabelOverlay = (RelativeLayout) view.findViewById(R.id.annotation_label_overlay);
        mImageFileName = getArguments().getString("image_file_name");
        mImageMetaFileName = getArguments().getString("image_meta_file_name");
        File imageFile = new File(mImageFileName);
        File scaledImageDir = new File(imageFile.getParentFile(), "downsized");
        if (!scaledImageDir.exists()) {
            scaledImageDir.mkdir();
        }
        Bitmap scaledImage = null;
        File scaledImageFile = new File(scaledImageDir, imageFile.getName());
        if (scaledImageFile.exists()) {
            scaledImage = BitmapFactory.decodeFile(scaledImageFile.getAbsolutePath());
        } else {
            if (imageFile.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                scaledImage = scaleBitmap(bmp);
                bmp.recycle();
                System.gc();
                saveDownsizedImage(scaledImage, scaledImageFile);
            }
        }
        if (scaledImage != null) {
            Bitmap drawableBmp = scaledImage.copy(Bitmap.Config.ARGB_8888, true);
            mPhotoWidth = drawableBmp.getWidth();
            mPhotoHeight = drawableBmp.getHeight();
            scaledImage.recycle();
            System.gc();
            mCanvas = new Canvas(drawableBmp);
            mImageView = (ImageView) view.findViewById(R.id.image_view);
            mImageView.setImageBitmap(drawableBmp);
            mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    drawAnnotations();
                }
            });
        }

        RecyclerView recList = (RecyclerView) view.findViewById(R.id.annotation_list);
        recList.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recList.setLayoutManager(mLinearLayoutManager);
        mAnnotationAdaptor = new BottomAnnotationAdaptor(mAnnotationList);
        mAnnotationAdaptor.annotationClickListener = new BottomAnnotationAdaptor.OnAnnotationClickListener() {
            @Override
            public void onAnnotationClicked(int position) {
                showClickedAnnotationLabel(position);
            }
        };
        recList.setAdapter(mAnnotationAdaptor);

        Button showAll = (Button) view.findViewById(R.id.show_all);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllLabels();
            }
        });
        return view;
    }

    public void showClickedAnnotationLabel(int position) {
        for (int index=0; index<mAnnotationList.size();index++) {
            LabelView label = mLabels.get(index);
            if (index == position) {
                label.setVisibility(View.VISIBLE);
            } else {
                label.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void showAllLabels() {
        for (LabelView label : mLabels) {
            label.setVisibility(View.VISIBLE);
        }
    }


    private  void saveDownsizedImage(Bitmap bmp, File file) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float maxWidth = (float)1000.0;
        float maxHeight = (float)1000.0;
        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = (int)maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = (int)maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = (int)maxHeight;
            width = (int)maxWidth;
        }

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null && mQueryResult == null) {
            RequestBody shopId = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(getShopId()));
            File imageFile = new File(mImageFileName);
            File scaledImageDir = new File(imageFile.getParentFile(), "downsized");
            File f = new File(scaledImageDir, imageFile.getName());
//            File f = new File(mImageFileName);
            RequestBody file = RequestBody.create(MediaType.parse("multipart/form-data"), f);
            String str = FileUtils.readTextFile(new File(mImageMetaFileName));
            str = "{\"image\": {\"meta\": " + str + "}}";
            RequestBody json = RequestBody.create(MediaType.parse("multipart/form-data"), str);
            Call<ImageQueryResult> call = RestClient.getSharedInstance().getApiService().getAnnotationsOnImage(file, shopId, json);
            mProgressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Searching...");
            mProgressDialog.show();
            call.enqueue(new Callback<ImageQueryResult>() {
                @Override
                public void onResponse(Call<ImageQueryResult> call, Response<ImageQueryResult> response) {
                    mProgressDialog.hide();
                    mQueryResult = response.body();
                    mAnnotationList.clear();
                    mAnnotationList.addAll(mQueryResult.annotations);
                    mAnnotationAdaptor.notifyDataSetChanged();
                    mLinearLayoutManager.scrollToPosition(3);
                    drawAnnotations();
                }

                @Override
                public void onFailure(Call<ImageQueryResult> call, Throwable t) {
                    mProgressDialog.hide();
                    Toast.makeText(getActivity(), "Failed to fetch annotations.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public int getShopId() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return settings.getInt("shopId", 27); // default is vivo shop id
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
        mAnnotationLabelOverlay.removeAllViews();
        if (mQueryResult == null) {
            return;
        }
        int[] actImageRect = getBitmapPositionInsideImageView(mImageView);
        List<Annotation> annotations = mQueryResult.annotations;
        int index = -1;
        for (Annotation anno: annotations) {
            index++;
            LabelView label = new LabelView(getActivity());
            label.mLabelText.setVisibility(View.VISIBLE);
            label.mLabelText.setText(anno.text);
            label.mPosition = index;
            float padding = 100;
            float cx = anno.getCenterX() * actImageRect[2];
            float cy = anno.getCenterY() * actImageRect[3];
            if (cx > actImageRect[2] - padding) {
                cx = actImageRect[2] - padding + randomInt(-30, 30);
            } else if (cx < padding) {
                cx = padding + randomInt(-30, 30);
            }
            if (cy > actImageRect[3] - padding) {
                cy = actImageRect[3] - padding + randomInt(-30, 30);
            } else if (cy < padding) {
                cy = padding + randomInt(-30, 30);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int)cx + actImageRect[0];
            params.topMargin = (int)cy + actImageRect[1];
            mAnnotationLabelOverlay.addView(label, params);
            label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LabelView view = (LabelView)v;
                    Log.d("label position: ", ""+view.mPosition);
                    int position = view.mPosition;
                    if (position >= 0 && position < mAnnotationList.size()) {
                        mLinearLayoutManager.scrollToPosition(position);
                    }
//                    String text = view.mLabelText.getText().toString();
//                    AnnotationDetailFragment fragment = new AnnotationDetailFragment();
//                    Bundle args = new Bundle();
//                    args.putString("annotation_text", text);
//                    fragment.setArguments(args);
//                    getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
                }
            });
            if (mLabels == null) {
                mLabels = new ArrayList<>();
            }
            mLabels.add(label);
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

    public int randomInt(int low, int high) {
        Random random = new Random();
        return random.nextInt(high-low) + low;
    }
}
