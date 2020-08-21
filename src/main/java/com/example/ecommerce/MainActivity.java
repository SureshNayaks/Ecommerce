package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.prevalent.prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import static com.example.ecommerce.prevalent.prevalent.UserPasswordKey;
import static com.example.ecommerce.prevalent.prevalent.UserPhoneKey;

public class MainActivity extends AppCompatActivity
{

    private Button joinNowButton, loginButton;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        joinNowButton = (Button) findViewById(R.id.main_join_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingbar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //declare two variables for storing the UserPhoneKey and the UserPasswordKey retrive these two values from the paper library

        String UserPhoneKey = Paper.book().read(prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(prevalent.UserPasswordKey);

        //here before giving the access to the user we need to store the phone and password if that phone and password are not empty
        if (UserPhoneKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey,UserPasswordKey);
                loadingbar.setTitle("Already Logged in");
                loadingbar.setMessage("please wait");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
            }
        }

    }

    private void AllowAccess(final String userPhoneKey, final String userPasswordKey)
    {
        final DatabaseReference mDatabase;
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("Users").child(UserPhoneKey).exists())
                {
                    Users UserData = snapshot.child("Users").child(UserPhoneKey).getValue(Users.class);

                    if (UserData.getPhone().equals(UserPhoneKey))
                    {
                        if (UserData.getPassword().equals(UserPasswordKey))
                        {
                            Toast.makeText(MainActivity.this, "You are successfully Logged in", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "please enter a valid password",Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "please enter valid phone number", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

}