package com.viafourasample.src.activities.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loginradius.androidsdk.helper.LoginRadiusSDK;
import com.loginradius.androidsdk.resource.SocialProviderConstant;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.signup.SignUpActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasample.src.utils.AndroidUtils;
import com.viafourasdk.src.model.local.VFDefaultColors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel = new LoginViewModel();

    private static int SIGNUP_RESULT_CODE = 1;
    private static int LOGINRADIUS_RESULT_CODE = 2;

    private TextInputEditText emailText, passwordText;
    private TextInputLayout emailLayout, passwordLayout;
    private TextView resetText, signupText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Log-in");

        if(ColorManager.isDarkMode(getApplicationContext())){
            findViewById(R.id.login_holder).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
        }

        ((ProgressBar) findViewById(R.id.login_loading)).getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.SRC_IN);

        setupTextInputs();

        findViewById(R.id.login_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRadiusSDK.NativeLogin nativeLogin = new LoginRadiusSDK.NativeLogin();
                nativeLogin.startFacebookNativeLogin(LoginActivity.this, LOGINRADIUS_RESULT_CODE);
            }
        });

        findViewById(R.id.login_linkedin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSocialLogin(SocialProviderConstant.LINKEDIN);
            }
        });

        findViewById(R.id.login_twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSocialLogin(SocialProviderConstant.TWITTER);
            }
        });

        findViewById(R.id.login_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRadiusSDK.NativeLogin nativeLogin = new LoginRadiusSDK.NativeLogin();
                nativeLogin.startGoogleNativeLogin(LoginActivity.this, LOGINRADIUS_RESULT_CODE);
            }
        });

        setupReset();
        setupSignup();

        findViewById(R.id.login_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                findViewById(R.id.login_submit).setVisibility(View.GONE);
                findViewById(R.id.login_loading).setVisibility(View.VISIBLE);

                viewModel.login(email, password, new LoginViewModel.LoginCallback() {
                    @Override
                    public void onSuccess() {
                        findViewById(R.id.login_submit).setVisibility(View.VISIBLE);
                        findViewById(R.id.login_loading).setVisibility(View.GONE);

                        onBackPressed();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        findViewById(R.id.login_submit).setVisibility(View.VISIBLE);
                        findViewById(R.id.login_loading).setVisibility(View.GONE);

                        showAlert("Invalid credentials", errorMessage);
                    }
                });
            }
        });
    }

    private void showAlert(String title, String message){
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setIcon(null)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, null)
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void launchSocialLogin(SocialProviderConstant socialProvider){
        LoginRadiusSDK.WebLogin webLogin = new LoginRadiusSDK.WebLogin();
        webLogin.setProvider(socialProvider);
        webLogin.startWebLogin(LoginActivity.this, LOGINRADIUS_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGNUP_RESULT_CODE){
            if(resultCode == Activity.RESULT_OK){
                onBackPressed();
            }
        } else if(requestCode == LOGINRADIUS_RESULT_CODE){
            if (data != null){
                String accessToken = data.getStringExtra("accesstoken");
                String provider = data.getStringExtra("provider");

                viewModel.socialLogin(accessToken, new LoginViewModel.SocialLoginCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        }
    }

    private void setupSignup(){
        signupText = findViewById(R.id.login_signup);
        signupText.setTextColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, SIGNUP_RESULT_CODE);
            }
        });
    }

    private void setupReset(){
        resetText = findViewById(R.id.login_reset);
        resetText.setTextColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK);
        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText emailEditText = new EditText(LoginActivity.this);
                emailEditText.setSingleLine();

                FrameLayout container = new FrameLayout(LoginActivity.this);
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (int) AndroidUtils.convertDpToPixel(20, LoginActivity.this);
                params.rightMargin = (int) AndroidUtils.convertDpToPixel(20, LoginActivity.this);
                emailEditText.setLayoutParams(params);
                container.addView(emailEditText);

                AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Reset password")
                        .setMessage("Enter your e-mail")
                        .setView(container)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String email = String.valueOf(emailEditText.getText());
                                viewModel.resetPassword(email, new LoginViewModel.PasswordResetCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }
        });
    }

    private void setupTextInputs(){
        emailText = findViewById(R.id.login_email_text);
        passwordText = findViewById(R.id.login_password_text);

        emailText.setTextColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK);
        passwordText.setTextColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK);

        passwordLayout = findViewById(R.id.login_password);
        emailLayout = findViewById(R.id.login_email);

        setInputTextLayoutColor(ColorManager.isDarkMode(this) ? Color.WHITE : Color.BLACK, emailLayout);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}