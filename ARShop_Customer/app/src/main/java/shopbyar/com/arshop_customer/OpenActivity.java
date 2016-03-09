package shopbyar.com.arshop_customer;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import shopbyar.com.arshop_customer.model.User;

public class OpenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
    }

    @Override
    protected void onResume() {
        Log.d("OpenActity", "onResume");
        super.onResume();
        final User currentUser = User.fetchCurrentUser(PreferenceManager.getDefaultSharedPreferences(this));
        if (currentUser == null || currentUser.authToken == null) {
            gotoLoginActivity();
        } else {
            gotoMainActiviy();
        }
    }

    public void gotoLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void gotoMainActiviy() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
