package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity
{
    private Button buttonlogin;
    TextInputEditText email,password;
    FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent=new Intent(getApplicationContext(),Attendence.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();
        buttonlogin=(Button)findViewById(R.id.button3);
        email=(TextInputEditText)findViewById(R.id.idemail);
        password=(TextInputEditText)findViewById(R.id.idpassword);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String Email,Password;
                Email=String.valueOf(email.getText());
                Password=String.valueOf(password.getText());
                if(TextUtils.isEmpty(Email))
                {
                    Toast.makeText(Login.this,"enter email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(Password))
                {
                    Toast.makeText(Login.this,"enter email",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"login successfully",Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(getApplicationContext(),Attendence.class);
                                        startActivity(intent);
                                        finish();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"login failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });
    }
}
