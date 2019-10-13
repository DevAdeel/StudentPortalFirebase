package com.example.studentportalfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference db;
    ArrayList<UserModel> emails=new ArrayList<>();
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        recyclerView = findViewById(R.id.recyclerView);
        db= FirebaseDatabase.getInstance().getReference().child("User");
        final Query query = db.orderByChild("email");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserModel obj=new UserModel();
                obj.setType(Integer.parseInt(dataSnapshot.child("type").getValue().toString()));
                obj.setEmail(dataSnapshot.child("email").getValue().toString());
                emails.add(obj);
                adapter = new MyAdapter(emails,AdminActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                query.addValueEventListener(new ValueEventListener() {
                    ArrayList<UserModel> obj=new ArrayList<>();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren())
                        {
                            obj.add(snapshot.getValue(UserModel.class));
                        }
                        adapter = new MyAdapter(obj,AdminActivity.this);
                        recyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Database Error",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                query.addValueEventListener(new ValueEventListener() {
                    ArrayList<UserModel> obj=new ArrayList<>();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren())
                        {
                            obj.add(snapshot.getValue(UserModel.class));
                        }
                        adapter = new MyAdapter(obj,AdminActivity.this);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Database Error",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Database Error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent intent=new Intent(AdminActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
