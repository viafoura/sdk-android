package com.viafourasample.src.activities.main

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.activities.profile.ProfileActivity
import com.viafourasample.src.activities.settings.SettingsActivity
import com.viafourasample.src.fragments.home.HomeFragment
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.model.SettingKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFTheme
import com.viafourasdk.src.utils.VFInsetsUtils
import com.viafourasdk.src.view.notificationBell.VFNotificationBellView

class MainActivity : AppCompatActivity() {
    private val viewModel = MainViewModel()
    private var toolbarMenu: Menu? = null
    private lateinit var homeFragment: Fragment
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InsetsUtils.applyActionBarInsets(this)
        VFInsetsUtils.applyBottomInsets(findViewById<View>(R.id.home_bottom_navigation))

        homeFragment = HomeFragment()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        setCurrentFragment()

        bottomNavigationView = findViewById(R.id.home_bottom_navigation)

        val upArrow = resources.getDrawable(R.drawable.icon_settings)
        upArrow.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupNotificationBell() {
        val colors = VFColors(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary),
            ContextCompat.getColor(applicationContext, R.color.colorPrimaryLight)
        )
        val settings = VFSettings(colors)

        val bellView: VFNotificationBellView

        if (sharedPreferences.getBoolean(SettingKeys.showNotificationBellTopBar, false)) {
            val menu = toolbarMenu ?: return

            bellView = layoutInflater.inflate(R.layout.cview_bell, null) as VFNotificationBellView
            findViewById<View>(R.id.home_bell).visibility = View.GONE
            menu.findItem(R.id.menu_main_auth).actionView = bellView
        } else {
            bellView = findViewById(R.id.home_bell)
            bellView.visibility = View.VISIBLE

            toolbarMenu?.let { it.findItem(R.id.menu_main_auth).actionView = null }
        }

        bellView.applySettings(settings)
        bellView.setTheme(
            if (ColorManager.isDarkMode(applicationContext)) VFTheme.light else VFTheme.dark
        )
        bellView.setBellClickedInterface { userUUID ->
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            intent.putExtra(IntentKeys.INTENT_USER_UUID, userUUID.toString())
            intent.putExtra(IntentKeys.INTENT_USER_PRESENTATION_TYPE, "feed")
            startActivity(intent)
        }
        bellView.setActionCallback { actionType, _ ->
            if (actionType == VFActionType.authPressed) {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        }
    }

    private fun setCurrentFragment() {
        supportActionBar!!.title = resources.getString(R.string.home)
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_holder, homeFragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()

        if (ColorManager.isDarkMode(applicationContext)) {
            bottomNavigationView.setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
            )
        } else {
            bottomNavigationView.setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.white)
            )
        }

        getAuthState()
        setupNotificationBell()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        toolbarMenu = menu
        getAuthState()
        setupNotificationBell()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_main_auth) {
            viewModel.getAuthState { userIsLoggedIn, _ ->
                if (userIsLoggedIn) {
                    viewModel.logout()
                    toolbarMenu!!.findItem(R.id.menu_main_auth).title = "Log in"
                } else {
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                }
            }
            return true
        } else if (item.itemId == android.R.id.home) {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getAuthState() {
        if (sharedPreferences.getBoolean(SettingKeys.showNotificationBellTopBar, false)) {
            return
        }
        viewModel.getAuthState { userIsLoggedIn, _ ->
            toolbarMenu?.let { menu ->
                menu.findItem(R.id.menu_main_auth).title =
                    if (userIsLoggedIn) "Log out" else "Log in"
            }
        }
    }
}
