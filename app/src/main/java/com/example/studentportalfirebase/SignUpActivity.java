package com.example.studentportalfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {

    final static int Result_LoadImage=12;
    TextView txtError,txt_img;
    EditText name,age,skill,password,email;
    Button btn_register;
    ImageButton btn_img;
    long maxId=0;
    DatabaseReference db;
    private StorageTask uploadTask;
    private String Image;
    private Uri image;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        txt_img = findViewById(R.id.txt_imagetxt);
        txtError = findViewById(R.id.txt_ErrorRegistration);
        name = findViewById(R.id.input_nameRegistration);
        age = findViewById(R.id.input_ageRegistration);
        skill = findViewById(R.id.input_skillRegistration);
        password = findViewById(R.id.input_passwordRegistration);
        email = findViewById(R.id.input_emailRegistration);
        btn_img = findViewById(R.id.btn_imgSignUp);
        btn_register = findViewById(R.id.btn_registerRegistration);
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtError.setText("");
                RegisterAccount();
            }
        });
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        db= FirebaseDatabase.getInstance().getReference().child("User");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    maxId = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void RegisterAccount() {
        final String Name = name.getText().toString();
        final String Email = email.getText().toString();
        final String Skill= skill.getText().toString();
        final String Password = password.getText().toString();
        final String AgeStr = age.getText().toString();
        final String ImageName = txt_img.getText().toString();
        if(Name.isEmpty() || Email.isEmpty() || Skill.isEmpty() || Password.isEmpty() || AgeStr.isEmpty())
        {
            txtError.setText("All Fields Are Required");
            Toast.makeText(getApplicationContext(),"All Fields Are Required",Toast.LENGTH_SHORT).show();
        }
        else {
            if(Password.length() < 6)
            {
                txtError.setText("Password must be of 6 letters");
            }
            else {
                if (ImageName.isEmpty()) {
                    txtError.setText("Select Image Please");
                } else {
                    try {
                        final Query query = db.orderByChild("email").equalTo(Email);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {

                                    if (UploadImage(image)) {
                                        UserModel obj = new UserModel();
                                        obj.setName(Name);
                                        obj.setAge(Integer.parseInt(AgeStr));
                                        obj.setEmail(Email);
                                        obj.setPassword(Password);
                                        obj.setSkill(Skill);
                                        obj.setRole("Employee");
                                        obj.setType(2);
                                        obj.setImage(ImageName);
                                        db.child(String.valueOf(maxId + 1)).setValue(obj);
                                        Toast.makeText(getApplicationContext(), "Account Registered Successfully", Toast.LENGTH_SHORT).show();
                                        query.removeEventListener(this);
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        txtError.setText("Error in Uploading Image");
                                    }
                                } else {
                                    txtError.setText("An account is already registered with this Email");
                                    query.removeEventListener(this);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                query.removeEventListener(this);
                            }
                        });

                    } catch (Exception ex) {
                        Log.e("Error", ex.toString());
                        Toast.makeText(getApplicationContext(), "Error in Saving Information! Try Later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void openGallery() {
        Intent photoPicker=new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker,Result_LoadImage);
    }
    private String getFileExtension(Uri image)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(image));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                image = imageUri;
                StorageReference filereference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(image));
                txt_img.setText(filereference.getName());
            }
            catch (Exception ex)
            {
                Log.e("Error", ex.toString());
                Toast.makeText(this,"Error getting Image",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this,"No Image Selected",Toast.LENGTH_SHORT).show();
        }
    }
    public boolean   UploadImage(Uri image)
    {
        if(image != null)
        {
            try {
                StorageReference filereference = storageReference.child(txt_img.getText().toString());
                Image = filereference.getName();
                uploadTask = filereference.putFile(image);
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
        else {
            Toast.makeText(this,"No Image Selected",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
