package com.example.stavroula.uber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stavroula.uber.adapter.ChatAdapter;
import com.example.stavroula.uber.entity.ChatMessage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    FirebaseUser fbuser;
    DatabaseReference dbreference;

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageButton back_btn,send_btn;
    ImageView sender_pic;
    TextView name;
    TextInputLayout til_message;
    TextInputEditText edt_message;
    String a = "rider",b = "driver";

    ChatAdapter chatAdapter;
    List<ChatMessage> chatarray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        back_btn = findViewById(R.id.return_button);
        send_btn = findViewById(R.id.send_btn);
        til_message = findViewById(R.id.til_message);
        edt_message = findViewById(R.id.edt_message);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setStackFromEnd(true);

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayout);

        Intent i = getIntent();
        Bundle extras = i.getExtras();

       //if(extras.containsKey("tripRequestId")) {
            final Long tripRequestId = i.getExtras().getLong("tripRequestId");
            Log.wtf("123", "TRIPREQUESTID"+tripRequestId);
       // }



        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edt_message.getText().toString();
                if (!message.equals("")){
                    Log.wtf("123", "TRIPID"+message);

                    sendMessage(a,b,message,tripRequestId);
                }
                edt_message.setText("");
            }
        });

        String trip_request_id = tripRequestId.toString();

        readmessage(trip_request_id);



        dbreference = FirebaseDatabase.getInstance().getReference();

        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        fbuser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void sendMessage(String sender, String receiver, String message, Long tripRequestId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.wtf("123", "TRIPID"+reference);

        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeString = simpleDateFormat.format(currentTime);
       //String time = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashmap = new HashMap<> ();
        hashmap.put("sender",sender);
        hashmap.put("receiver",receiver);
        hashmap.put("message", message);
        hashmap.put("time",timeString);


       /* // read the index key
        String chatId = reference.push().getKey();
        Log.wtf("123", "KEY"+chatId);
        // create a child with index value
        reference.child(chatId).setValue(hashmap);
*/
        //TODO If chatiD!=Null,
        String trip_request_id = tripRequestId.toString();
        reference.child("Chats").child(trip_request_id).push().setValue(hashmap);


       /* String refUi = ref.getKey();
       Log.wtf("123", "KEY"+refUi );
       Map<String, Object> childUpdates = new HashMap<>();
       childUpdates.put(trip_request_id + refUi, hashmap);
       reference.updateChildren(childUpdates);*/
    }

    private void readmessage(String tripRequestId){
        chatarray = new ArrayList<>();

        dbreference = FirebaseDatabase.getInstance().getReference("Chats").child(tripRequestId);
        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatarray.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                   //TODO if (chatMessage)
                    chatarray.add(chatMessage);


                    chatAdapter = new ChatAdapter(ChatActivity.this, chatarray);
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
