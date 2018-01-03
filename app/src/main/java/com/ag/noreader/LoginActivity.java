package com.ag.noreader;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.noreader.data.DataBaseHandler;
import com.ag.noreader.data.User;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends MyBaseActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private boolean rightCredentials = false;
    private String valid_email = "";
    private DataBaseHandler  dataBaseHandler;
    private User user = new User();

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setting context
        MyBaseActivity.setContext(this);
        ButterKnife.inject(this);
        dataBaseHandler = new DataBaseHandler(getContext());

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                user.setEmail(_emailText.getText().toString());
                user.setPassword(_passwordText.getText().toString());
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
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


        final ProgressDialog progressDialog = showProgressDialog(getContext(), "Authenticating...");


        // TODO: Implement your own authentication logic here.
        if(user.getEmail().equals("agunga3d@gmail.com") && user.getPassword().equals("olooJade")){
            rightCredentials = true;
        }else {
            Log.d(TAG, "Not Oloo :::::olooJade ");
        }

        user = dataBaseHandler.processLogin(user.getEmail(), user.getPassword());
        if(user.getId() > 0){
            valid_email = user.getEmail();
            Log.d(TAG, "ID :: "+ user.getId() + " Email ::::: " + valid_email);
            rightCredentials = true;
        }

        final boolean finalRightCredentials = rightCredentials;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(finalRightCredentials){
                            onLoginSuccess();
                        }else {
                            onLoginFailed();
                        }
                        dismissProgressDialog(progressDialog);
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                user = (User) data.getSerializableExtra("user");
                Log.d(TAG, "USER :::::: " + user.getId() +" " + user.getName() +" " + user.getEmail() +" " + user.getPassword());
//                login();
                onLoginSuccess();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void goToMainAfterLogin(){
        Intent intent = new Intent(getContext(), CameraActivity.class);
        intent.putExtra("email", valid_email);
        startActivity(intent);
        this.finish();
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
//        finish();
        goToMainAfterLogin();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = user.getEmail();
        String password = user.getPassword();

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