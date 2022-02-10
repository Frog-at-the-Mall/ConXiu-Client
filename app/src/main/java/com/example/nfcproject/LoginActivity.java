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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends Activity {

    //***   Shared Page Utils   ***//

    private TextView header_title;

    //Text fields
    private EditText editT_username, editT_password;

    //Variables
    private String username_str, password_str, password_rep_srt, email_reg_str, dataPassing_str;

    //***   Login Page   ***//

    //Buttons
    private Button sig_reg_button, sig_login_button;

    //***   Register Page   ***//

    private EditText editT_password_rep, editT_email;
    private Button reg_reg_Button, reg_login_Button;

    //***   Database Utils  ***//

    // DB Login URL
    public static final String URL = "http://10.0.2.2:3000/";           ////*** Needs changing when merge */


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
        dataPassing_str = "login?username=" + username_str + "&password=" + password_str;

        if(!username_str.isEmpty() && !password_str.isEmpty()) {
            StringRequest strRequest = new StringRequest(Request.Method.POST, URL+dataPassing_str, response -> {
                if (response.contains("success")) {
                    openMainPage();
                } else if (response.contains("failure")) {
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }, error -> Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show());
            mQueue.add(strRequest);
        }
    }

    public void register() {
        username_str = editT_username.getText().toString().trim();
        password_str = editT_password.getText().toString().trim();
        password_rep_srt = editT_password_rep.getText().toString().trim();
        email_reg_str = editT_email.getText().toString().trim();

        if(!username_str.isEmpty() && !password_str.isEmpty() && !password_rep_srt.isEmpty() && !email_reg_str.isEmpty()) {
            if(password_str.equals(password_rep_srt)) {
                dataPassing_str = "register?username=" + username_str + "&email=" + email_reg_str + "&password=" + password_str;
                StringRequest strRequest = new StringRequest(Request.Method.POST, URL + dataPassing_str, response -> {
                    if (response.contains("success")) {
                        openMainPage();
                    } else if (response.contains("username")) {
                        Toast.makeText(LoginActivity.this, "Username already in use", Toast.LENGTH_SHORT).show();
                    } else if(response.contains("email")) {
                        Toast.makeText(LoginActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show());

                mQueue.add(strRequest);
            } else {
                Toast.makeText(LoginActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

