package shopbyar.com.arshop_customer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import shopbyar.com.arshop_customer.model.User;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    protected Drawer drawer = null;
    private AccountHeader accountHeader = null;

    private LocationScanner mLocationScanner;
    private OrientationScanner mOrientationScanner;
    private WifiScanner mWifiScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PermissionCheck.init(this);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        // Handle Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        // Sample Profile
        final IProfile profile = new ProfileDrawerItem().withName("Tang Zijian").withEmail("tangzj77@gmail.com").withIcon("http://lh3.googleusercontent.com/-awMpgJZO4oA/AAAAAAAAAAI/AAAAAAAAABQ/hpbDt1I6g7M/photo.jpg");
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profile)
                .withSelectionListEnabledForSingleProfile(false)
                .withSavedInstance(savedInstanceState)
                .build();
        // Create Drawer
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Camera").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName("My Shopping Lists").withIcon(GoogleMaterial.Icon.gmd_view_list).withIdentifier(2),
                        new PrimaryDrawerItem().withName("Nearby Photos").withIcon(GoogleMaterial.Icon.gmd_book_photo).withIdentifier(3),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Logout").withIcon(GoogleMaterial.Icon.gmd_power_off).withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        selectItem(drawerItem);
                        return false;
                    }
                })
                .build();
        if (savedInstanceState == null) {
            drawer.setSelection(1, true);
        }
        ActionBar bar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        initLocationScanner();
        initOrientationScanner();
        initWifiScanner();
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return;
        }
        // disable going back to the OpenActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawer.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = accountHeader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    void selectItem(IDrawerItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch ((int)item.getIdentifier()) {
            case 1:
                fragmentClass = PhotoPickerFragment.class;
                break;
            case 2:
                fragmentClass = MyShoppingListFragment.class;
                break;
            case 3:
                fragmentClass = NearbyPhotosFragment.class;
                break;
            case 4: // Logout
                userLogout();
                return;
            default:
                fragmentClass = PhotoPickerFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            if (fragment instanceof PhotoPickerFragment) {
                ((PhotoPickerFragment)fragment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFilePickerIntent();
                    }
                });
            }
            getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void userLogout() {
        User.destoryCurrentUser(PreferenceManager.getDefaultSharedPreferences(this));
        finish();
    }
    private static int FILE_CODE = 1000;
    public void startFilePickerIntent() {
        Intent intent =new Intent(MainActivity.this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, getExternalFilesDir(null).getPath());
        startActivityForResult(intent, FILE_CODE);
    }

    public void showToast(String content) {
        Toast.makeText(MainActivity.this, content, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File imageFile = new File(uri.getPath());
            String imageFilePath = imageFile.getAbsolutePath();
            String metaFilePath = imageFilePath + ".txt";
            if (!imageFile.exists()) {
                showToast("Image file does not exist.");
                return;
            }
            if (!imageFile.isFile()) {
                showToast("Invalid file.");
                return;
            }
            if (!imageFile.getName().toLowerCase().endsWith(".jpg")) {
                showToast("Only support jpg format.");
                return;
            }
            File metaFile = new File(metaFilePath);
            if (!metaFile.exists()) {
                showToast("Image meta info does not exist.");
                return;
            }
            gotoPhotoAnnotationFragment(imageFilePath, metaFilePath);
        } else {
            showToast("File picker error.");
        }
    }

    public void gotoPhotoAnnotationFragment(String imageFileName, String metaFileName) {
        PhotoAnnotationFragment fragment = new PhotoAnnotationFragment();
        Bundle args = new Bundle();
        args.putString("image_file_name", imageFileName);
        args.putString("image_meta_file_name", metaFileName);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLocationScanner != null) {
            mLocationScanner.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationScanner.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationScanner.onResume();
        mOrientationScanner.onResume();
        mWifiScanner.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationScanner.onPause();
        mOrientationScanner.onPause();
        mWifiScanner.onPause();
    }


    private void initLocationScanner() {
        mLocationScanner = new LocationScanner(this);
        mLocationScanner.setListener(new LocationScanner.Listener() {
            @Override
            public void set(Location location) {
                switchColor(findViewById(R.id.control_gps));
                ImageMeta.setLocation(location);
            }
        });
        mLocationScanner.onCreate();
    }

    private void initOrientationScanner() {
        mOrientationScanner = new OrientationScanner(this);
        mOrientationScanner.setListener(new OrientationScanner.Listener() {
            @Override
            public void setAzimuthPitchRoll(float[] apr) {
                switchColor(findViewById(R.id.control_orien));
                ImageMeta.setAPR(apr);
            }
        });
        mOrientationScanner.onCreate();
    }

    private void initWifiScanner() {
        mWifiScanner = new WifiScanner(this);
        mWifiScanner.setListener(new WifiScanner.Listener() {
            @Override
            public void setWifiList(List<ScanResult> wifiScanList) {
                switchColor(findViewById(R.id.control_wifi));
                ImageMeta.setWifiList(wifiScanList);
            }
        });
    }

    private void switchColor(View viewById) {
        if (viewById != null) {
            try {
                int color = Color.TRANSPARENT;
                Drawable background = viewById.getBackground();
                if (background instanceof ColorDrawable) {
                    color = ((ColorDrawable) background).getColor();
                }
                if (color == Color.WHITE) {
                    viewById.setBackgroundColor(Color.GREEN);
                }
                else {
                    viewById.setBackgroundColor(Color.WHITE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
