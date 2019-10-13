package com.example.studentportalfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class EmployeeActivity extends AppCompatActivity {

    private String Name,Skill,Email,Image;
    private int Age;
    private DatabaseReference db;
    private StorageReference storageReference;
    private ImageView imageView;
    private TextView txt_name,txt_email,txt_age,txt_skill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        Name=getIntent().getStringExtra("Name");
        Email=getIntent().getStringExtra("Email");
        Image = getIntent().getStringExtra("Image");
        Skill =getIntent().getStringExtra("Skill");
        Age = getIntent().getIntExtra("Age",0);
        imageView = findViewById(R.id.imgView_EmplyeeImg);
        txt_name = findViewById(R.id.txt_EmployeeName);
        txt_email = findViewById(R.id.txt_EmployyeeEmail);
        txt_age = findViewById(R.id.txt_EmployeeAge);
        txt_skill = findViewById(R.id.txt_EmployeeSkill);
        db = FirebaseDatabase.getInstance().getReference("User");
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        GetImage(Email);
        txt_name.setText("Name : "+Name);
        txt_email.setText("Email : "+Email);
        txt_skill.setText("Skill : "+Skill);
        txt_age.setText("Age : "+Age);

    }

    private void GetImage(String email) {

        Query query=db.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StorageReference reference=null;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    UserModel obj=snapshot.getValue(UserModel.class);
                    //reference= FirebaseStorage.getInstance().getReference(obj.getName());
                    reference = storageReference.child(obj.getImage());
                }
                try{
                final File localFile = File.createTempFile("images", "jpg");

                reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        imageView.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });}
                catch (Exception e)
                {

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
