package com.viafourasample.src.activities.signup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasdk.src.model.local.VFDefaultColors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SignUpActivity extends AppCompatActivity {
    private SignUpViewModel viewModel = new SignUpViewModel();

    private TextInputEditText nameText, emailText, passwordText;
    private TextInputLayout nameLayout, emailLayout, passwordLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if(ColorManager.isDarkMode(getApplicationContext())){
            findViewById(R.id.signup_holder).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign up");

        setupTextInputs();

        ((ProgressBar) findViewById(R.id.signup_loading)).getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.SRC_IN);

        findViewById(R.id.signup_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameText.getText().toString();
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                findViewById(R.id.signup_submit).setVisibility(View.GONE);
                findViewById(R.id.signup_loading).setVisibility(View.VISIBLE);

                viewModel.signup(name, email, password, new SignUpViewModel.SignUpCallback() {
                    @Override
                    public void onSuccess() {
                        findViewById(R.id.signup_submit).setVisibility(View.VISIBLE);
                        findViewById(R.id.signup_loading).setVisibility(View.GONE);

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onError() {
                        findViewById(R.id.signup_submit).setVisibility(View.VISIBLE);
                        findViewById(R.id.signup_loading).setVisibility(View.GONE);

                        showAlert("Invalid input", "The data is invalid");
                    }
                });
            }
        });
    }

    private void showAlert(String title, String message){
        new AlertDialog.Builder(SignUpActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(null)
                .setPositiveButton(android.R.string.yes, null)
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setupTextInputs(){
        nameText = findViewById(R.id.signup_name_text);
        emailText = findViewById(R.id.signup_email_text);
        passwordText = findViewById(R.id.signup_password_text);

        nameText.setTextColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK);
        emailText.setTextColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK);
        passwordText.setTextColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK);

        passwordLayout = findViewById(R.id.signup_password);
        emailLayout = findViewById(R.id.signup_email);
        nameLayout = findViewById(R.id.signup_name);

        setInputTextLayoutColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK, emailLayout);
        setInputTextLayoutColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK, nameLayout);
        setInputTextLayoutColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK, passwordLayout);
    }

    private void setInputTextLayoutColor(int color, TextInputLayout textInputLayout) {
        try {
            Field field = textInputLayout.getClass().getDeclaredField("focusedTextColor");
            field.setAccessible(true);
            int[][] states = new int[][]{
                    new int[]{}
            };
            int[] colors = new int[]{
                    color
            };
            ColorStateList myList = new ColorStateList(states, colors);
            field.set(textInputLayout, myList);

            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("defaultHintTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(textInputLayout, myList);

            Method method = textInputLayout.getClass().getDeclaredMethod("updateLabelState", boolean.class);
            method.setAccessible(true);
            method.invoke(textInputLayout, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}