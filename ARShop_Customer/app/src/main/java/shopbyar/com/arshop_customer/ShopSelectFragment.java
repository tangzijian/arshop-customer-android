package shopbyar.com.arshop_customer;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ShopSelectFragment extends Fragment {
    public View.OnClickListener mListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_select, container, false);
        Button vivoBtn = (Button) view.findViewById(R.id.btn_vivo_city);
        vivoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShopId(27); // vivo shop id
                gotoPhotoPickerFragment();
            }
        });
        Button sesameBtn = (Button) view.findViewById(R.id.btn_sesame);
        sesameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShopId(44); // sesame shop id
                gotoPhotoPickerFragment();
            }
        });
        return view;
    }

    public void saveShopId(int id) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("shopId", id);
        editor.commit();
    }

    public void gotoPhotoPickerFragment() {
        PhotoPickerFragment fragment = new PhotoPickerFragment();
        fragment.setOnClickListener(mListener);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
    }
}
