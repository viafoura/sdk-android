package com.viafourasample.src.activities.login

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.signup.SignUpActivity
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.utils.AndroidUtils
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.utils.VFInsetsUtils

class LoginActivity : AppCompatActivity() {
    private val viewModel = LoginViewModel()

    private lateinit var emailText: TextInputEditText
    private lateinit var passwordText: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var resetText: TextView
    private lateinit var signupText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        InsetsUtils.applyActionBarInsets(this)
        VFInsetsUtils.applyBottomInsetsWithIme(findViewById<View>(R.id.login_holder))

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Log-in"

        if (ColorManager.isDarkMode(applicationContext)) {
            findViewById<View>(R.id.login_holder).setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
            )
        }

        findViewById<ProgressBar>(R.id.login_loading).indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary),
            PorterDuff.Mode.SRC_IN
        )

        setupTextInputs()

        setupReset()
        setupSignup()

        findViewById<View>(R.id.login_submit).setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            findViewById<View>(R.id.login_submit).visibility = View.GONE
            findViewById<View>(R.id.login_loading).visibility = View.VISIBLE

            viewModel.login(email, password, object : LoginViewModel.LoginCallback {
                override fun onSuccess() {
                    findViewById<View>(R.id.login_submit).visibility = View.VISIBLE
                    findViewById<View>(R.id.login_loading).visibility = View.GONE

                    onBackPressed()
                }

                override fun onError(errorMessage: String?) {
                    findViewById<View>(R.id.login_submit).visibility = View.VISIBLE
                    findViewById<View>(R.id.login_loading).visibility = View.GONE

                    showAlert("Invalid credentials", errorMessage)
                }
            })
        }
    }

    private fun showAlert(title: String, message: String?) {
        AlertDialog.Builder(this@LoginActivity)
            .setTitle(title)
            .setIcon(null)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, null)
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGNUP_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                onBackPressed()
            }
        }
    }

    private fun setupSignup() {
        signupText = findViewById(R.id.login_signup)
        signupText.setTextColor(if (ColorManager.isDarkMode(this)) Color.WHITE else Color.BLACK)
        signupText.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivityForResult(intent, SIGNUP_RESULT_CODE)
        }
    }

    private fun setupReset() {
        resetText = findViewById(R.id.login_reset)
        resetText.setTextColor(if (ColorManager.isDarkMode(this)) Color.WHITE else Color.BLACK)
        resetText.setOnClickListener {
            val emailEditText = EditText(this@LoginActivity)
            emailEditText.setSingleLine()

            val container = FrameLayout(this@LoginActivity)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = AndroidUtils.convertDpToPixel(20f, this@LoginActivity).toInt()
            params.rightMargin = AndroidUtils.convertDpToPixel(20f, this@LoginActivity).toInt()
            emailEditText.layoutParams = params
            container.addView(emailEditText)

            val dialog = AlertDialog.Builder(this@LoginActivity)
                .setTitle("Reset password")
                .setMessage("Enter your e-mail")
                .setView(container)
                .setPositiveButton("Submit") { dialog, _ ->
                    val email = emailEditText.text.toString()
                    viewModel.resetPassword(email, object : LoginViewModel.PasswordResetCallback {
                        override fun onSuccess() {
                        }

                        override fun onError() {
                        }
                    })
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }
    }

    private fun setupTextInputs() {
        emailText = findViewById(R.id.login_email_text)
        passwordText = findViewById(R.id.login_password_text)

        emailText.setTextColor(if (ColorManager.isDarkMode(this)) Color.WHITE else Color.BLACK)
        passwordText.setTextColor(if (ColorManager.isDarkMode(this)) Color.WHITE else Color.BLACK)

        passwordLayout = findViewById(R.id.login_password)
        emailLayout = findViewById(R.id.login_email)

        setInputTextLayoutColor(
            if (ColorManager.isDarkMode(this)) Color.WHITE else Color.BLACK,
            emailLayout
        )
        setInputTextLayoutColor(
            if (ColorManager.isDarkMode(this)) Color.WHITE else Color.BLACK,
            passwordLayout
        )
    }

    private fun setInputTextLayoutColor(color: Int, textInputLayout: TextInputLayout) {
        try {
            val field = textInputLayout.javaClass.getDeclaredField("focusedTextColor")
            field.isAccessible = true
            val states = arrayOf(intArrayOf())
            val colors = intArrayOf(color)
            val myList = ColorStateList(states, colors)
            field.set(textInputLayout, myList)

            val fDefaultTextColor =
                TextInputLayout::class.java.getDeclaredField("defaultHintTextColor")
            fDefaultTextColor.isAccessible = true
            fDefaultTextColor.set(textInputLayout, myList)

            val method = textInputLayout.javaClass.getDeclaredMethod(
                "updateLabelState",
                Boolean::class.javaPrimitiveType
            )
            method.isAccessible = true
            method.invoke(textInputLayout, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val SIGNUP_RESULT_CODE = 1
    }
}
