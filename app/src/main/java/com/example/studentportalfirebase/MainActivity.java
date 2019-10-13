package com.example.studentportalfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText inputEmail,inputPassword;
    TextView txtError;
    Button btn_signin,btn_signup;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtError = findViewById(R.id.txt_errorField);
        txtError.setText("");
        inputEmail = findViewById(R.id.input_emailSignin);
        inputPassword = findViewById(R.id.input_passSignin);
        btn_signup = findViewById(R.id.btn_signUp);
        btn_signin = findViewById(R.id.btn_signIn);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtError.setText("");
                getResult();
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getResult() {

        final String Email=inputEmail.getText().toString();
        final String password=inputPassword.getText().toString();
        if(Email.isEmpty() || password.isEmpty())
        {
            txtError.setText("All Fields are Required");
            Toast.makeText(getApplicationContext(),"All Fields are Required",Toast.LENGTH_SHORT).show();
        }
        else {
            db = FirebaseDatabase.getInstance().getReference().child("User");
            final Query query = db.orderByChild("email").equalTo(Email);

            query.addValueEventListener(new ValueEventListener()
            {
                @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null)
                        {
                            String Role=null;
                            String Password=null;
                            String Name=null,Skill=null,Email=null,Image=null;
                            int Type=0,Age=0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                            Role = snapshot.child("role").getValue().toString();
                            Password = snapshot.child("password").getValue().toString();
                            Type = Integer.parseInt(snapshot.child("type").getValue().toString());
                            Name = snapshot.child("name").getValue().toString();
                            Skill = snapshot.child("skill").getValue().toString();
                            Email = snapshot.child("email").getValue().toString();
                            Image = snapshot.child("image").getValue().toString();
                            Age = Integer.parseInt(snapshot.child("age").getValue().toString());
                            }
                            if(Role.equals("Admin"))
                            {
                                if(Password.equals(password))
                                {
                                    Intent intent=new Intent(getApplicationContext(),AdminActivity.class);
                                    startActivity(intent);
                                    query.removeEventListener(this);
                                    finish();
                                }
                                else {
                                    txtError.setText("Wrong Password");
                                }
                            }
                            else {
                                if(Password.equals(password))
                                {
                                    if(Type == 1) {
                                        Intent intent = new Intent(getApplicationContext(), EmployeeActivity.class);
                                        intent.putExtra("Name",Name);
                                        intent.putExtra("Email",Email);
                                        intent.putExtra("Age",Age);
                                        intent.putExtra("Skill",Skill);
                                        intent.putExtra("Image",Image);
                                        startActivity(intent);
                                        query.removeEventListener(this);
                                    }
                                    else if(Type == 2)
                                    {
                                        txtError.setText("Your account is not Activated by Admin yet! Please Wait");
                                        query.removeEventListener(this);
                                    }
                                    else {
                                        txtError.setText("Something Wrong! Try Later");
                                    }
                                }
                                else {
                                    txtError.setText("Wrong Password");
                                }

                            }
                        }
                        else {
                            txtError.setText("No Account is registered at This Email! Sign Up");
                            query.removeEventListener(this);
                        }
                    }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    query.removeEventListener(this);
                }
            });

        }
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
