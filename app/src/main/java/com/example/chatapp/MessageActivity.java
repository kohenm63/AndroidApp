package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;
    ImageButton sendBtn;
    EditText messageSend;



    FirebaseUser fUser;
    DatabaseReference databaseReference;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_message );


        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( "" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

//init views>>>>

        recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getApplicationContext() );
        linearLayoutManager.setStackFromEnd( true );
        recyclerView.setLayoutManager( linearLayoutManager );

        profile_image = findViewById( R.id.profile_image );
        username = findViewById( R.id.username );
        sendBtn = findViewById( R.id.send_btn );
        messageSend = findViewById( R.id.messageEt );

        intent = getIntent();
        final String userid = intent.getStringExtra( "userid" );
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        sendBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageSend.getText().toString();
                if (!msg.equals( "" )){
                    sendMessage( fUser.getUid(),userid,msg );
                    Toast.makeText( MessageActivity.this, "MessageAdapter sent!",Toast.LENGTH_LONG ).show();
                }else {
                     Toast.makeText( MessageActivity.this, "Can't send an empty message!",Toast.LENGTH_LONG ).show();
                }
                messageSend.setText( "" );
            }
        } );



        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child( userid );

        databaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                username.setText( user.getUsername() );
                if(user.getImageURL().equals( "default" )){
                    profile_image.setImageResource( R.mipmap.ic_launcher );
                }else {
                    Glide.with( MessageActivity.this ).load( user.getImageURL() ).into( profile_image );

                }
                readMessages( fUser.getUid(), userid, user.getImageURL() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void sendMessage(String sender, final String reciever, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>(  );
        hashMap.put( "sender", sender );
        hashMap.put( "reciever", reciever );
        hashMap.put( "message", message );

        reference.child( "Chats" ).push().setValue( hashMap );

        final String userid = intent.getStringExtra( "userid" );
        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("Chatlist").child( fUser.getUid())
                .child( userid );
        chatRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatRef.child( "id" ).setValue( userid );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }
    private void readMessages(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>(  );
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);


                    if (chat.getReciever().equals( myid ) && chat.getSender().equals( userid )) {
                        mchat.add( chat );

                    } else if (chat.getReciever().equals( userid ) && chat.getSender().equals( myid )) {
                        mchat.add( chat );

                    }
                    messageAdapter = new MessageAdapter( MessageActivity.this, mchat ,imageurl );
                    recyclerView.setAdapter( messageAdapter );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
