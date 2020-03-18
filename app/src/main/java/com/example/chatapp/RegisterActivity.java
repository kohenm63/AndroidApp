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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    MaterialEditText username, email, password;
   Button btn_register;

   FirebaseAuth auth;
   DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( "Register" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        username = findViewById( R.id.username );
        email = findViewById( R.id.email);
        password = findViewById( R.id.password);
        btn_register = findViewById( R.id.register_btn);

        auth = FirebaseAuth.getInstance();


        btn_register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_txt = username.getText().toString();
                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();

                if (TextUtils.isEmpty( username_txt )|| TextUtils.isEmpty( email_txt ) || TextUtils.isEmpty( password_txt )){
                     Toast.makeText( RegisterActivity.this, "Fill out all the fields!",Toast.LENGTH_LONG ).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher( email_txt).matches()) {
                        email.setError( "Invalid Email Address" );
                        email.setFocusable( true );
                }

                else if (password.length()<6){
                    password.setError( " Password must contain 6 characters" );
                    password.setFocusable( true );
                } else {
                    register( username_txt,email_txt,password_txt );
                }
            }
        } );


    }

    private void register (final String username, String email, String password ){
            auth.createUserWithEmailAndPassword( email,password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child( userid );

                        HashMap<String,String> hashMap = new HashMap<>(  );
                        hashMap.put( "id", userid );
                        hashMap.put( "username", username );
                        hashMap.put( "imageURL", "default");

                        reference.setValue( hashMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    Intent intent = new Intent( RegisterActivity.this,MainActivity.class );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                    startActivity( intent );
                                    Toast.makeText( RegisterActivity.this,"Registered Successfully !", Toast.LENGTH_LONG ).show();
                                    finish();
                                } else {
                                    Toast.makeText( RegisterActivity.this, "Authentication failed!",Toast.LENGTH_SHORT ).show();
                                }
                            }
                        } );
                    }
                }
            } );

    }
}
