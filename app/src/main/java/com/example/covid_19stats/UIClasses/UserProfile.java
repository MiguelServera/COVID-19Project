package com.example.covid_19stats.UIClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covid_19stats.R;
import com.example.covid_19stats.Resources.DBInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.covid_19stats.UIClasses.MainLogin.editor;
import static com.example.covid_19stats.UIClasses.MainLogin.email;

public class UserProfile extends AppCompatActivity implements View.OnClickListener{
    Button editProfile, changeImage, saveButton, cancelButton, logoutButton;
    EditText editUsername, editPassword, editConfirmPassword;
    ImageView avatarImage;
    DBInterface db;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        SharedPreferences getpref = getSharedPreferences("prefs", MODE_PRIVATE);
        db = new DBInterface(this);
        db.obre();
        giveValues();
        Cursor c = db.obtainUserInfo(email);
        c.moveToFirst();
        editUsername.setText(c.getString(0));
        editPassword.setText(c.getString(2));
        byteArrayToImage(c.getBlob(3));
        c.close();
        db.tanca();
    }

    public void giveValues()
    {
        editProfile = findViewById(R.id.editProfileButton);
        editProfile.setOnClickListener(this);
        changeImage = findViewById(R.id.changeimageButton);
        changeImage.setOnClickListener(this);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);
        editUsername = findViewById(R.id.updateUsername);
        editPassword = findViewById(R.id.updatePassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        changeImage = findViewById(R.id.changeimageButton);
        changeImage.setOnClickListener(this);
        avatarImage = findViewById(R.id.avatarImage);
    }

    @Override
    public void onClick(View v) {
        if (v == editProfile) {
            changeImage.setVisibility(View.VISIBLE);
            editUsername.setEnabled(true);
            editPassword.setEnabled(true);
            editConfirmPassword.setEnabled(true);
            changeImage.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
        if (v == changeImage) {
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
        }
        if (v == saveButton) {
            db.obre();
            System.out.println(email);
            System.out.println(editPassword);
            System.out.println(editConfirmPassword);
            if (isValidPassword(editPassword.getText().toString())) {
                if (editPassword.getText().toString().equals(editConfirmPassword.getText().toString())) {
                    if (db.updateUserInfo(editUsername.getText().toString(), email, editPassword.getText().toString(), imageToByteArray()) != -1) {
                        Toast.makeText(this, "Data succesfully updated", Toast.LENGTH_SHORT).show();
                        changeImage.setVisibility(View.GONE);
                        saveButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                        editProfile.setVisibility(View.VISIBLE);
                        logoutButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        System.out.println("Nah not good my guy");
                    }
                }
                else {
                    Toast.makeText(this, "The passwords are not the same", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "The password is not valid", Toast.LENGTH_SHORT).show();
            }
            db.tanca();
        }
        if (v == cancelButton)
        {
            changeImage.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            editProfile.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        }

        if (v == logoutButton)
        {
            editor.putBoolean("loggedIn", false);
            editor.apply();
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), MainLogin.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Doesn't work for the intended purpose, to clear all activities always
            finishAffinity();
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                avatarImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void byteArrayToImage(byte[] avatarBytes)
    {
        Bitmap bmp = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
        avatarImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 150,
                150, false));
    }

    private byte[] imageToByteArray()
    {
        Bitmap bitmap = ((BitmapDrawable) avatarImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        return imageInByte;
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
