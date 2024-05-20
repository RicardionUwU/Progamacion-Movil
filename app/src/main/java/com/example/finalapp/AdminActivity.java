package com.example.finalapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import org.checkerframework.checker.units.qual.A;

public class AdminActivity extends AppCompatActivity {

    private Button selectFile, upload;
    private EditText imagename;
    private Uri pdfUri;
    private StorageReference mStorageRef;
    private AlertDialog progressDialog;
    private ProgressBar progressBar;
    private TextView progressText;

    private DatabaseReference databaseReference;

    private final int PICK_IMAGE_REQUEST = 71;
    private ActivityResultLauncher<Intent> activityResultLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Data").child("project");


        selectFile = findViewById(R.id.selectFile);

            upload = findViewById(R.id.send);
            imagename = findViewById(R.id.edittxt);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(Intent.createChooser(intent, "Choose Source file"));

            }
        });

        upload.setOnClickListener(v -> {
            if (pdfUri != null) {
                showProgressDialog();

                String fileName = "url" + System.currentTimeMillis() + "." + getFileExtension(pdfUri);
                StorageReference storageReference = mStorageRef.child(fileName);

                storageReference.putFile(pdfUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            final Task<Uri> firebasedatabaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebasedatabaseUri.addOnSuccessListener(uri -> {
                                final String downloadUrl = uri.toString();
                                Upload upload = new Upload(imagename.getText().toString().trim(), downloadUrl);

                                String uploadId = UUID.randomUUID().toString(); // Genera un ID único

                                databaseReference.child(uploadId).setValue(upload).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AdminActivity.this, "File not successfully uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                });
                            });
                        })
                        .addOnFailureListener(exception -> {
                            Toast.makeText(AdminActivity.this, "File is not successfully uploaded", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        })
                        .addOnProgressListener(taskSnapshot -> {
                            double currentProgress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) currentProgress);
                            progressText.setText("Uploaded " + (int) currentProgress + "%...");
                        });
            } else {
                Toast.makeText(AdminActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(dialogView);
        builder.setTitle("Uploading file...");
        builder.setCancelable(false);

        progressBar = dialogView.findViewById(R.id.progressBar);
        progressText = dialogView.findViewById(R.id.progressText);

        progressDialog = builder.create();
        progressDialog.show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}