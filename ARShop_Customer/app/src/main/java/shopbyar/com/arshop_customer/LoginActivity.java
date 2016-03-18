package shopbyar.com.arshop_customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import shopbyar.com.arshop_customer.model.LoginUser;
import shopbyar.com.arshop_customer.model.User;
import shopbyar.com.arshop_customer.rest.RestClient;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_username)
    EditText _usernameText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        LoginUser user = new LoginUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setType("name_and_password");
        Call<User> call = RestClient.getSharedInstance().getApiService().userLogin(user);
        final LoginActivity self = this;
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User.currentUser = response.body();
                if (User.currentUser != null) {
                    User.saveCurrentUser(PreferenceManager.getDefaultSharedPreferences(self));
                    if (User.currentUser.code.equals("200")) {
                        onLoginSuccess();
                    } else {
                        onLoginFailed();
                    }
                    progressDialog.dismiss();
                } else {
                    Log.d(TAG, response.message());
                    onLoginFailed();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                onLoginFailed();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here

                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the OpenActivity
        moveTaskToBack(true);
    }
    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast toast = Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("must not be empty.");
            valid = false;
        } else {
            _usernameText.setError(null);
        }
        if (password.isEmpty()) {
            _passwordText.setError("must not be empty.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
