package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {
    MaterialEditText  email,password;
    Button btn_login;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );




        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( "Login" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );



        email = findViewById( R.id.email);
        password = findViewById( R.id.password);
        btn_login = findViewById( R.id.btn_login);

        auth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher( email_txt).matches()) {
                    email.setError( "Invalid Email Address" );
                    email.setFocusable( true );
                }

                else if (password.length()<6){
                    password.setError( " Password must contain 6 characters" );
                    password.setFocusable( true );
                } else {
                   auth.signInWithEmailAndPassword( email_txt,password_txt ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()) {
                               Intent intent = new Intent( LoginActivity.this,MainActivity.class );
                               intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                               startActivity( intent );
                               finish();

                           }
                       }
                   } );
                }
            }
        } );


    }
}
