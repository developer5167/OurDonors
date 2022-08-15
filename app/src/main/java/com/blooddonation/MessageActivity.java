package com.blooddonation;

import static com.blooddonation.Constants.send_notification;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import developer.semojis.Helper.EmojiconEditText;
import developer.semojis.actions.EmojIconActions;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView chat_recyclerView;
    private ImageView user_image_message;
    private EmojiconEditText editText;
    private String mesg;
    private String v1, v2;

    private FirebaseUser firebaseUser;
    private String user;
    private MyAccountDetails myAccountDetails_myAc;
    private List<Chats> messags = new ArrayList<>();
    ;
    private MessageAdapter messageAdapter;
    private int accepted;
    private String user1, user2;
    private String uniqueKey;
    private TextView user_name_message;
    private DatabaseReference databaseReferenceUnique;
    private boolean sendClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        }
        chat_recyclerView = findViewById(R.id.chatting_recyclerview);
        user_image_message = findViewById(R.id.user_image_message);
        editText = findViewById(R.id.edittext_send);
        user_name_message = findViewById(R.id.user_name_message);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        LinearLayout root_view = findViewById(R.id.root_view);
        uniqueKey = getIntent().getStringExtra("uniqueKey");
        user = getIntent().getStringExtra("user");
        ImageView emoji_btn = findViewById(R.id.emoji_btn);
        EmojIconActions emojIcon = new EmojIconActions(this, root_view, editText, emoji_btn);
        emojIcon.ShowEmojIcon();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chat_recyclerView.setLayoutManager(linearLayoutManager);
        chat_recyclerView.setHasFixedSize(true);


        messageAdapter = new MessageAdapter(messags, MessageActivity.this);
        chat_recyclerView.setAdapter(messageAdapter);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("MyAc").child(user);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MyAccountDetails myAccountDetails = snapshot.getValue(MyAccountDetails.class);
                Picasso.with(getApplicationContext()).load(myAccountDetails.getImg_url()).into(user_image_message);
                user_name_message.setText(myAccountDetails.getName());
                DatabaseReference myAc = FirebaseDatabase.getInstance().getReference().child("MyAc").child(firebaseUser.getUid());
                myAc.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        databaseReferenceUnique = FirebaseDatabase.getInstance().getReference().child("chats").child(uniqueKey);
                        readmessages(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        DatabaseReference databaseReference_myac = FirebaseDatabase.getInstance().getReference().child("MyAc").child(firebaseUser.getUid());
        databaseReference_myac.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myAccountDetails_myAc = snapshot.getValue(MyAccountDetails.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("chatpage").child(user).child(firebaseUser.getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    user2 = dataSnapshot.getValue().toString();
                } else {
                    user2 = "no";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chatpage").child(firebaseUser.getUid()).child(user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    user1 = dataSnapshot.getValue().toString();
                } else {
                    user1 = "no";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void update() {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("chatpage").child(user)
                .child(firebaseUser.getUid());
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    user2 = dataSnapshot.getValue().toString();
                } else {
                    user2 = "no";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chatpage").child(firebaseUser.getUid())
                .child(user);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                    user1 = dataSnapshot.getValue().toString();
                } else {
                    user1 = "no";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void seenMessage() {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("chatpage")
                .child(firebaseUser.getUid()).child(user);

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    v1 = snapshot.getValue().toString();
                } else {
                    v1 = "no";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chatpage")
                .child(user).child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    v2 = snapshot.getValue().toString();
                } else {
                    v2 = "no";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    public void send(View view) {
        sendClicked = true;
        sendmsg();
    }

    public void sendmsg() {
        mesg = editText.getText().toString();
        if (!mesg.equals("")) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("type", "message");
            hashMap.put("msg", mesg);
            hashMap.put("sender", firebaseUser.getUid());
            hashMap.put("receiver", user);
            hashMap.put("is_seen", "sent");
            databaseReference.child("chats").child(uniqueKey).push().setValue(hashMap);
            editText.setText("");
            if (!user1.equals(user2)) {
                send_notification(this, user, myAccountDetails_myAc.getName(), mesg, firebaseUser.getUid(), uniqueKey);
            }
        }
    }

    public void readmessages(final String userid) {
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("chats").child(uniqueKey);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messags.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Chats chats = dataSnapshot1.getValue(Chats.class);
                    messags.add(chats);
                    messageAdapter.notifyDataSetChanged();
                    chat_recyclerView.scrollToPosition(messags.size() - 1);
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats").child(uniqueKey);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        update();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chats chat = snapshot.getValue(Chats.class);
                            if (chat != null && chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("is_seen", "seen");
                                if (v1.equals(v2)) {
                                    snapshot.getRef().updateChildren(hashMap);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
//                databaseReferenceUnique.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                        Chats chats = snapshot.getValue(Chats.class);
//                        messags.add(chats);
//                        messageAdapter.notifyItemInserted(messags.size() - 1);
//                        chat_recyclerView.scrollToPosition(messags.size() - 1);
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("chatpage")
                .child(firebaseUser.getUid()).child(user);
        databaseReference2.setValue("yes");
        seenMessage();
        update();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("chatpage")
                .child(firebaseUser.getUid()).child(user);
        databaseReference2.setValue("no");
    }

    public void back(View view) {
        onBackPressed();
    }
}
