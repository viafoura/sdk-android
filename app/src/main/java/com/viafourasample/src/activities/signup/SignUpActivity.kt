package com.viafourasample.src.activities.signup

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.viafoura.sampleapp.R
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.utils.VFInsetsUtils

class SignUpActivity : AppCompatActivity() {
    private val viewModel = SignUpViewModel()

    private lateinit var nameText: TextInputEditText
    private lateinit var emailText: TextInputEditText
    private lateinit var passwordText: TextInputEditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        InsetsUtils.applyActionBarInsets(this)
        VFInsetsUtils.applyBottomInsetsWithIme(findViewById<View>(R.id.signup_holder))

        if (ColorManager.isDarkMode(applicationContext)) {
            findViewById<View>(R.id.signup_holder).setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
            )
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Sign up"

        setupTextInputs()

        findViewById<ProgressBar>(R.id.signup_loading).indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary),
            PorterDuff.Mode.SRC_IN
        )

        findViewById<View>(R.id.signup_submit).setOnClickListener {
            val name = nameText.text.toString()
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            findViewById<View>(R.id.signup_submit).visibility = View.GONE
            findViewById<View>(R.id.signup_loading).visibility = View.VISIBLE

            viewModel.signup(name, email, password, object : SignUpViewModel.SignUpCallback {
                override fun onSuccess() {
                    findViewById<View>(R.id.signup_submit).visibility = View.VISIBLE
                    findViewById<View>(R.id.signup_loading).visibility = View.GONE

                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                }

                override fun onError() {
                    findViewById<View>(R.id.signup_submit).visibility = View.VISIBLE
                    findViewById<View>(R.id.signup_loading).visibility = View.GONE

                    showAlert("Invalid input", "The data is invalid")
                }
            })
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this@SignUpActivity)
            .setTitle(title)
            .setMessage(message)
            .setIcon(null)
            .setPositiveButton(android.R.string.yes, null)
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun setupTextInputs() {
        nameText = findViewById(R.id.signup_name_text)
        emailText = findViewById(R.id.signup_email_text)
        passwordText = findViewById(R.id.signup_password_text)

        val textColor = if (ColorManager.isDarkMode(this)) Color.WHITE else Color.BLACK
        nameText.setTextColor(textColor)
        emailText.setTextColor(textColor)
        passwordText.setTextColor(textColor)

        passwordLayout = findViewById(R.id.signup_password)
        emailLayout = findViewById(R.id.signup_email)
        nameLayout = findViewById(R.id.signup_name)

        setInputTextLayoutColor(textColor, emailLayout)
        setInputTextLayoutColor(textColor, nameLayout)
        setInputTextLayoutColor(textColor, passwordLayout)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
}
