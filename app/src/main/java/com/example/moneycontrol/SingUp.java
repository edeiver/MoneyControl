package com.example.moneycontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingUp extends AppCompatActivity {
    private DatabaseReference RootReference, myDta;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private Button BtnSingup;
    private EditText Name, Lastname,Email, Password, Password2;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    FirebaseAuth firebaseAuth;
    private ArrayList<User> userArrayList = new ArrayList<>();
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_up);
        button=findViewById(R.id.BtnLoginB);
        firebaseAuth =FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingUp.this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        BtnSingup = findViewById(R.id.btn_signUp);
        RootReference = FirebaseDatabase.getInstance().getReference();
        Name=findViewById(R.id.TxtName);
        Lastname=findViewById(R.id.TxtLastname);
        Email=findViewById(R.id.TxtEmail);
        Password=findViewById(R.id.TxtPassword);
        Password2=findViewById(R.id.TxtPassword2);
        BtnSingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Name.getText().length() == 0 || Lastname.getText().length() == 0 || Email.getText().length() == 0 || Password.getText().length() == 0) {
                    Toast.makeText(SingUp.this, R.string.empty, Toast.LENGTH_LONG).show();
                } else if(Password.getText().toString().equals(Password2.getText().toString())) {

                    String NameOn, LastNameOn, EmailOn, PasswordOn;
                    NameOn = Name.getText().toString();
                    LastNameOn = Lastname.getText().toString();
                    EmailOn = Email.getText().toString();
                    PasswordOn = Password.getText().toString();
                    LoadFirebaseData( NameOn, LastNameOn, EmailOn, PasswordOn);
                    SingUp(EmailOn, PasswordOn);
                    Toast.makeText(SingUp.this, R.string.done, Toast.LENGTH_LONG).show();
                }else {
                    Password.setError("Not Matching");
                    Password2.setError("Not Matching");

                }
            }
        });
    }
    private void LoadFirebaseData(String nameOn, String lastNameOn, String emailOn, String passwordOn) {
        Map<String, Object> UserData = new HashMap<>();
        UserData.put("Name", nameOn);
        UserData.put("LastName", lastNameOn);
        UserData.put("Email", emailOn);
        UserData.put("Password", passwordOn);
        RootReference.child("User").push().setValue(UserData);
    }

    private void getData(){
        RootReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    userArrayList.clear();
                    for(final DataSnapshot ds: dataSnapshot.getChildren()){
                        User user= ds.getValue(User.class);
                        String name = user.getName();
                        String lastname = user.getLastname();
                        String email = user.getEmail();
                        String password = user.getPassword();
                        userArrayList.add(user);
                    }
                    /*UserAdapter = new UserAdapter (userArrayList,R.layout.contact_detail);
                    recyclerView.setAdapter(contactAdapter);*/

                }else{

                    Toast.makeText(getApplicationContext(), R.string.empty, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void SingUp(String EmailOn,String PasswordOn) {
        firebaseAuth.createUserWithEmailAndPassword(EmailOn, PasswordOn)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast toast = Toast.makeText(SingUp.this, R.string.welcome, Toast.LENGTH_LONG);
                            toast.show();
                           /* Intent intent = new Intent(SingUp.this, UserDetails.class);
                            startActivity(intent);*/
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                        } else {
                            Toast toast = Toast.makeText(SingUp.this, R.string.error, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
    }
}
