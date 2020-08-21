package com.example.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity
{
    private EditText Inputphone, Inputpassword;
    private Button LoginButton;
    private ProgressDialog loadingbar;
    private String parentDBname = "Users";
    private CheckBox CheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn) ;
        Inputphone = (EditText) findViewById(R.id.login_phone_number_input);
        Inputpassword = (EditText) findViewById(R.id.login_password_input);
        loadingbar = new ProgressDialog(this);
        CheckBox = (CheckBox) findViewById(R.id.remmember_me_ccbox);

        //initializing the check box https://github.com/pilgr/Pape Reed from here

        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginUser();
            }
        });

    }

    private void LoginUser()
    {
        String phone = Inputphone.getText().toString();
        String password = Inputpassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"please enter the phoneNumber",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"please enter the password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("please Wait , While we are checking your Credientials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password)
    {
        //check if the checkbox is clecked
        if (CheckBox.isChecked())
        {

            //store the given phone and password in to the declared variable from the prevalent package(prevalent class)
            //initialize the variables with the help of library from paper
            Paper.book().write(prevalent.UserPhoneKey,phone);
            Paper.book().write(prevalent.UserPasswordKey,password);

        }


        final DatabaseReference mDatabase;
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child(parentDBname).child(phone).exists())
                {
                    Users UserData = snapshot.child(parentDBname).child(phone).getValue(Users.class);

                    if (UserData.getPhone().equals(phone))
                    {
                        if (UserData.getPassword().equals(password))
                        {
                            Toast.makeText(LoginActivity.this, "You are successfully Logged in", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "please enter a valid password",Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "please enter valid phone number", Toast.LENGTH_SHORT).show();
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