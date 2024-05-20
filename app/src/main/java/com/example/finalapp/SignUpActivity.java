package com.example.finalapp;

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
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

    TextView nuevoUsuario, bienvenidoLabel, continuarLabel;
    ImageView signUpImageView;
    TextInputLayout usuarioSignUpTextField, contrasenaTextField;
    MaterialButton inicioSesion;
    TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        signUpImageView = findViewById(R.id.signUpImageView);
        bienvenidoLabel = findViewById(R.id.bienvenidoLabel);
        continuarLabel = findViewById(R.id.continuarLabel);
        usuarioSignUpTextField = findViewById(R.id.usuarioSignUpTextField);
        contrasenaTextField = findViewById(R.id.contrasenaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmarPasswordEditText);


        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionBack();
            }
        });

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        mAuth = FirebaseAuth.getInstance();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void validate(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

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

        if(!confirmPassword.equals(password)){
            confirmPasswordEditText.setError("Deben ser iguales");
            return;
        }else{
            registrar(email,password);
        }
    }

    public void registrar(String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            String uid=task.getResult().getUser().getUid();
                            Users user = new Users(uid,emailEditText.getText().toString(),passwordEditText.getText().toString(),0);




                            firebaseDatabase.getReference().child("Users").child(uid).setValue(user);




                            Intent intent = new Intent(SignUpActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Fallo en registrase", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    //@Override
    //public void onBackPressed(){
      //  transitionBack();
    //}

    public void transitionBack(){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair <View, String>(signUpImageView, "logoImageTrans");
        pairs[1] = new Pair <View, String>(bienvenidoLabel, "textTrans");
        pairs[2] = new Pair <View, String>(continuarLabel, "iniciaSesionTextTrans");
        pairs[3] = new Pair <View, String>(usuarioSignUpTextField, "emailInputTextTrans");
        pairs[4] = new Pair <View, String>(contrasenaTextField, "passwordInputTextTrans");
        pairs[5] = new Pair <View, String>(inicioSesion, "buttonSignInTrans");
        pairs[6] = new Pair <View, String>(nuevoUsuario, "newUserTrans");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this,pairs);
            startActivity(intent, options.toBundle());
        }else{
            startActivity(intent);
            finish();
        }

    }
}