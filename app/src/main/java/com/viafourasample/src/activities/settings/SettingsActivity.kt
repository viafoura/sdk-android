package com.viafourasample.src.activities.settings

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viafoura.sampleapp.R
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.model.SettingKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.utils.VFInsetsUtils

class SettingsActivity : AppCompatActivity() {

    private val settingsViewModel = SettingsViewModel()
    private lateinit var preferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        InsetsUtils.applyActionBarInsets(this)
        VFInsetsUtils.applyBottomInsetsToScrollable(findViewById<View>(R.id.settings_list))

        supportActionBar!!.title = "Settings"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        recyclerView = findViewById(R.id.settings_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SettingsAdapter()
    }

    override fun onResume() {
        super.onResume()
        updateColors()
    }

    private fun updateColors() {
        if (ColorManager.isDarkMode(applicationContext)) {
            findViewById<View>(R.id.settings_holder).setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
            )
        } else {
            findViewById<View>(R.id.settings_holder).setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.white)
            )
        }
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun currentSiteDomain(): String {
        val stored = preferences.getString(SettingKeys.siteDomain, "")!!.trim()
        return stored.ifEmpty { SettingKeys.DEFAULT_SITE_DOMAIN }
    }

    private fun showSiteSwitcher() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select site")

        val labels = arrayOf<CharSequence>(
            "Demo (${SettingKeys.DEFAULT_SITE_DOMAIN})",
            "demo.viafoura.com",
            "test.viafoura.com",
            "Custom…"
        )

        builder.setItems(labels) { _, which ->
            when (which) {
                0 -> setSiteAndRestart(
                    SettingKeys.DEFAULT_SITE_UUID,
                    SettingKeys.DEFAULT_SITE_DOMAIN
                )

                1 -> setSiteAndRestart(
                    "00000000-0000-4000-8000-d47205fca416",
                    "demo.viafoura.com"
                )

                2 -> setSiteAndRestart(
                    "00000000-0000-4000-8000-a3692e0c0e77",
                    "test.viafoura.com"
                )

                3 -> showCustomSitePrompt()
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showCustomSitePrompt() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Custom site")
        builder.setMessage("Changing site will restart the app.")

        val uuidInput = EditText(this)
        uuidInput.hint = "Site UUID"
        uuidInput.setText(preferences.getString(SettingKeys.siteUUID, SettingKeys.DEFAULT_SITE_UUID))

        val domainInput = EditText(this)
        domainInput.hint = "Site domain"
        domainInput.setText(
            preferences.getString(SettingKeys.siteDomain, SettingKeys.DEFAULT_SITE_DOMAIN)
        )

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val padding = (16 * resources.displayMetrics.density).toInt()
        layout.setPadding(padding, 0, padding, 0)
        layout.addView(uuidInput)
        layout.addView(domainInput)
        builder.setView(layout)

        builder.setNegativeButton("Cancel", null)
        builder.setPositiveButton("Save & Restart") { _, _ ->
            val uuid = uuidInput.text.toString().trim()
            val domain = domainInput.text.toString().trim()
            setSiteAndRestart(uuid, domain)
        }

        builder.show()
    }

    private fun setSiteAndRestart(siteUUID: String, siteDomain: String) {
        if (siteUUID.isEmpty()) {
            showInvalidSiteAlert("Site UUID must not be empty.")
            return
        }

        preferences.edit()
            .putString(SettingKeys.siteUUID, siteUUID)
            .putString(SettingKeys.siteDomain, siteDomain)
            .commit()

        val intent = packageManager.getLaunchIntentForPackage(packageName)!!
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Process.killProcess(Process.myPid())
    }

    private fun showInvalidSiteAlert(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Invalid site")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    inner class SettingsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class SiteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val valueText: TextView = itemView.findViewById(R.id.row_settings_site_value)

            init {
                itemView.setOnClickListener { showSiteSwitcher() }
            }
        }

        inner class ToggleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val settingText: TextView = itemView.findViewById(R.id.row_settings_text)
            val settingSwitch: Switch = itemView.findViewById(R.id.row_settings_switch)
        }

        override fun getItemViewType(position: Int): Int =
            if (position == 0) VIEW_TYPE_SITE else VIEW_TYPE_TOGGLE

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            if (viewType == VIEW_TYPE_SITE) {
                return SiteViewHolder(inflater.inflate(R.layout.row_settings_site, parent, false))
            }
            return ToggleViewHolder(inflater.inflate(R.layout.row_settings, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SiteViewHolder) {
                holder.valueText.text = currentSiteDomain()
                return
            }

            val setting = settingsViewModel.settingList[position - 1]
            val toggleHolder = holder as ToggleViewHolder
            toggleHolder.settingText.setTextColor(
                if (ColorManager.isDarkMode(applicationContext)) Color.WHITE else Color.BLACK
            )
            toggleHolder.settingText.text = setting.title
            toggleHolder.settingSwitch.setOnCheckedChangeListener(null)
            toggleHolder.settingSwitch.isChecked = preferences.getBoolean(setting.key, false)
            toggleHolder.settingSwitch.setOnCheckedChangeListener { _, b ->
                preferences.edit().putBoolean(setting.key, b).apply()
                if (SettingKeys.darkMode == setting.key) {
                    updateColors()
                }
            }
        }

        override fun getItemCount(): Int = settingsViewModel.settingList.size + 1
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val VIEW_TYPE_SITE = 0
        private const val VIEW_TYPE_TOGGLE = 1
    }
}
