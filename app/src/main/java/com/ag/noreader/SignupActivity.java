package com.ag.noreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.noreader.data.DataBaseHandler;
import com.ag.noreader.data.User;

import butterknife.ButterKnife;
import butterknife.BindView;

public class SignupActivity extends MyBaseActivity {
    private static final String TAG = "SignupActivity";
    private DataBaseHandler dataBaseHandler;

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBaseHandler = new DataBaseHandler(this);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = showProgressDialog(SignupActivity.this, "Creating Account...");
        User user = new User();
        user.setName(_nameText.getText().toString());
        user.setEmail(_emailText.getText().toString());
        user.setPassword(_passwordText.getText().toString());

        // TODO: Implement your own signup logic here.
        final boolean signed_up = dataBaseHandler.saveData(user);

        final User tmp_user = user;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(signed_up){
                            onSignupSuccess(tmp_user);
                        }else {
                            onSignupFailed();
                        }
                        dismissProgressDialog(progressDialog);
                    }
                }, 3000);
    }


    public void onSignupSuccess(User user) {
        _signupButton.setEnabled(true);
        Intent intent = new Intent(this,  LoginActivity.class);
        intent.putExtra("user", user);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}