package com.example.finalapp;

import static androidx.constraintlayout.motion.widget.TransitionBuilder.validate;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextView bienvenidoLabel, continuarLabel,nuevoUsuario,olvidasteContrasena;
    ImageView loginImageView;
    TextInputLayout usuarioTextField, contrasenaTextField;
    MaterialButton inicioSesion;
    TextInputEditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginImageView = findViewById(R.id.loginImageView);
        bienvenidoLabel = findViewById(R.id.bienvenidoLabel);
        continuarLabel = findViewById(R.id.continuarLabel);
        usuarioTextField = findViewById(R.id.usuarioTextField);
        contrasenaTextField = findViewById(R.id.contrasenaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);
        olvidasteContrasena = findViewById(R.id.olvidasteContra);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        mAuth = FirebaseAuth.getInstance();


        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair <View, String>(loginImageView, "logoImageTrans");
                pairs[1] = new Pair <View, String>(bienvenidoLabel, "textTrans");
                pairs[2] = new Pair <View, String>(continuarLabel, "iniciaSesionTextTrans");
                pairs[3] = new Pair <View, String>(usuarioTextField, "emailInputTextTrans");
                pairs[4] = new Pair <View, String>(contrasenaTextField, "passwordInputTextTrans");
                pairs[5] = new Pair <View, String>(inicioSesion, "buttonSignInTrans");
                pairs[6] = new Pair <View, String>(nuevoUsuario, "newUserTrans");

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
                    startActivity(intent, options.toBundle());
                }else{
                    startActivity(intent);
                    finish();
                }

            }
        });

        olvidasteContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
                finish();

            }
        });


        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void validate(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if(email.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Correo invalido");
            return;
        }else{
            emailEditText.setError(null);
        }

        if(password.isEmpty() || password.length()<8){
            passwordEditText.setError("Se necesitan mas de 8 carcateres");
            return;
        }else if(!Pattern.compile("[0-9]").matcher(password).find()){
            passwordEditText.setError("Al menos un numero");
            return;
        }else{
            passwordEditText.setError(null);
        }



        inicioSesion(email,password);

    }

    public void inicioSesion(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            String uid = task.getResult().getUser().getUid();

                            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                            firebaseDatabase.getReference().child("Users").child(uid).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int userType=snapshot.getValue(Integer.class);
                                    if(userType == 0){
                                        Intent in = new Intent(LoginActivity.this,UserActivity.class);
                                        startActivity(in);
                                    }

                                    if(userType == 1){
                                        Intent in = new Intent(LoginActivity.this,AdminActivity.class);
                                        startActivity(in);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this,"Credenciales equivocadas, intenta de nuevo", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }


}