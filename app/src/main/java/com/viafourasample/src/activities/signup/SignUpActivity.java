package com.viafourasample.src.activities.signup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.viafoura.viafourasdk.R;
import com.viafourasample.src.activities.login.LoginViewModel;
import com.viafourasdk.src.model.local.VFDefaultColors;

public class SignUpActivity extends AppCompatActivity {
    private SignUpViewModel viewModel = new SignUpViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sign up");

        TextInputEditText nameText = findViewById(R.id.signup_name_text);
        TextInputEditText emailText = findViewById(R.id.signup_email_text);
        TextInputEditText passwordText = findViewById(R.id.signup_password_text);

        ((ProgressBar) findViewById(R.id.signup_loading)).getIndeterminateDrawable().setColorFilter(
                VFDefaultColors.getInstance().colorPrimaryDefault,
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