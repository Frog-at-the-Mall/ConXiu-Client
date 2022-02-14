package com.example.nfcproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    //***   Shared Page Utils   ***//

    private TextView header_title;

    //Text fields
    private EditText editT_username, editT_password;

    //Variables
    private String username_str, password_str, password_rep_srt, email_reg_str, role_reg_str;

    //***   Login Page   ***//

    //Buttons
    private Button sig_reg_button, sig_login_button;

    //***   Register Page   ***//

    private EditText editT_password_rep, editT_email;
    private Button reg_reg_Button, reg_login_Button;

    //***   Database Utils  ***//

    // DB Login URL
    public static final String URL = "http://10.0.2.2:3000/login";


    //***   Volley Utils    ***//

    // Volley Request queue
    RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Volley request queue
        mQueue = Volley.newRequestQueue(this);
        // Header Title
        header_title = findViewById(R.id.header_title);
        // EditText Views
        editT_username = findViewById(R.id.editT_username);
        editT_password = findViewById(R.id.editT_password);

        //** Login Page **//

        // Listener for opening register page.
        sig_reg_button = findViewById(R.id.sig_registerpage_btn);
        sig_reg_button.setOnClickListener(v -> openRegPage());

        sig_login_button = (Button) findViewById(R.id.sig_login_btn);
        sig_login_button.setOnClickListener(v -> login());

        //** Register Page **//

        editT_password_rep = findViewById(R.id.reg_password_rep);
        editT_email = findViewById(R.id.reg_email);

        reg_reg_Button = findViewById(R.id.reg_register_btn);
        reg_reg_Button.setOnClickListener(v -> register());

        reg_login_Button = findViewById(R.id.reg_loginpage_btn);
        reg_login_Button.setOnClickListener(v -> openLogPage());


    }

    // Changes the page for registration.
    public void openRegPage() {
        header_title.setText("Register");

        editT_password.setText("");

        sig_reg_button.setVisibility(View.INVISIBLE);
        sig_login_button.setVisibility(View.INVISIBLE);

        editT_password_rep.setVisibility(View.VISIBLE);
        editT_email.setVisibility(View.VISIBLE);

        reg_reg_Button.setVisibility(View.VISIBLE);
        reg_login_Button.setVisibility(View.VISIBLE);
    }

    // Changes the page for Login.
    public void openLogPage() {
        header_title.setText("Login");

        sig_reg_button.setVisibility(View.VISIBLE);
        sig_login_button.setVisibility(View.VISIBLE);


        editT_password_rep.setVisibility(View.INVISIBLE);
        editT_email.setVisibility(View.INVISIBLE);

        reg_reg_Button.setVisibility(View.INVISIBLE);
        reg_login_Button.setVisibility(View.INVISIBLE);

        editT_password.setText("");
        editT_password_rep.setText("");
        editT_email.setText("");
    }

    public void openMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Login if user entered correct username and password.
    public void login() {
        username_str = editT_username.getText().toString().trim();
        password_str = editT_password.getText().toString().trim();
        if(!username_str.isEmpty() && !password_str.isEmpty()) {
            JSONObject json = new JSONObject();
            try {
                json.put("username", username_str);
                json.put("password", password_rep_srt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,URL+"/login",json,response -> {
                Boolean successCheck = false;
                String returnMsg = "Error";
                try {
                    successCheck = (Boolean) response.get("success");
                    returnMsg = (String) response.get("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (successCheck == true) {
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    openMainPage();
                } else {
                    Toast.makeText(LoginActivity.this, returnMsg, Toast.LENGTH_SHORT).show();
                }
            }, error -> Toast.makeText(LoginActivity.this, (CharSequence) error, Toast.LENGTH_SHORT).show());

            mQueue.add(jsonRequest);
        }
    }

    public void register() {
        username_str = editT_username.getText().toString().trim();
        password_str = editT_password.getText().toString().trim();
        password_rep_srt = editT_password_rep.getText().toString().trim();
        email_reg_str = editT_email.getText().toString().trim();


        if(!username_str.isEmpty() && !password_str.isEmpty() && !password_rep_srt.isEmpty() && !email_reg_str.isEmpty()) {
            if(password_str.equals(password_rep_srt)) {
                JSONObject json = new JSONObject();
                try {
                    json.put("username", username_str);
                    json.put("email", email_reg_str);
                    json.put("password", password_rep_srt);
                    json.put("role","Seeker");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,URL+"/register",json,response -> {
                            Boolean successCheck = false;
                            String returnMsg = "Error";
                            try {
                                successCheck = (Boolean) response.get("success");
                                returnMsg = (String) response.get("msg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (successCheck == true) {
                                Toast.makeText(LoginActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                                openLogPage();
                            } else {
                                Toast.makeText(LoginActivity.this, returnMsg, Toast.LENGTH_SHORT).show();
                            }
                        }, error -> Toast.makeText(LoginActivity.this, (CharSequence) error, Toast.LENGTH_SHORT).show());
                mQueue.add(jsonRequest);
            } else {
                Toast.makeText(LoginActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

