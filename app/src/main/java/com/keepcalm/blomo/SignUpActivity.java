package com.keepcalm.blomo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private EditText sign_email_text;
    private EditText sign_pass;
    private EditText sign_rePass;
    private Button  btnSignUp;
    private TextView linkLogin;
    private ProgressBar progressBarSign;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        sign_email_text =(EditText) findViewById(R.id.input_email2);
        sign_pass = (EditText) findViewById(R.id.input_password2);
        sign_rePass = (EditText) findViewById(R.id.input_reEnterPassword);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        linkLogin = (TextView) findViewById(R.id.link_login);
        progressBarSign=(ProgressBar) findViewById(R.id.progressBar_signUp);

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = sign_email_text.getText().toString();
                String pass = sign_pass.getText().toString();
                String rePass= sign_rePass.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(rePass)){
                    if(pass.equals(rePass)){

                        progressBarSign.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                   startActivity(new Intent(SignUpActivity.this, AccountActivity.class));
                                   finish();

                                }else{
                                    String errorMess = task.getException().getMessage();
                                    Toast.makeText(SignUpActivity.this, "Error : "+errorMess, Toast.LENGTH_SHORT).show();

                                }

                                progressBarSign.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else{
                        Toast.makeText(SignUpActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser= mAuth.getCurrentUser();
        if(currentUser !=null){
            sendToMain();
        }
    }

    private void sendToMain() {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }
}
