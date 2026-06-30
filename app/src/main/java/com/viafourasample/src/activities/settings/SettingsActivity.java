package com.viafourasample.src.activities.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.Setting;
import com.viafourasample.src.model.SettingKeys;

public class SettingsActivity extends AppCompatActivity {

    private static final int VIEW_TYPE_SITE = 0;
    private static final int VIEW_TYPE_TOGGLE = 1;

    private SettingsViewModel settingsViewModel = new SettingsViewModel();
    private SharedPreferences preferences;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        recyclerView = findViewById(R.id.settings_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SettingsAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateColors();
    }

    private void updateColors() {
        if (ColorManager.isDarkMode(getApplicationContext())) {
            findViewById(R.id.settings_holder).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
        } else {
            findViewById(R.id.settings_holder).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    private String currentSiteDomain() {
        String stored = preferences.getString(SettingKeys.siteDomain, "").trim();
        return stored.isEmpty() ? SettingKeys.DEFAULT_SITE_DOMAIN : stored;
    }

    private void showSiteSwitcher() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select site");

        String[] labels = {
            "Demo (" + SettingKeys.DEFAULT_SITE_DOMAIN + ")",
            "demo.viafoura.com",
            "test.viafoura.com",
            "Custom…"
        };

        builder.setItems(labels, (dialog, which) -> {
            switch (which) {
                case 0:
                    setSiteAndRestart(SettingKeys.DEFAULT_SITE_UUID, SettingKeys.DEFAULT_SITE_DOMAIN);
                    break;
                case 1:
                    setSiteAndRestart("00000000-0000-4000-8000-d47205fca416", "demo.viafoura.com");
                    break;
                case 2:
                    setSiteAndRestart("00000000-0000-4000-8000-a3692e0c0e77", "test.viafoura.com");
                    break;
                case 3:
                    showCustomSitePrompt();
                    break;
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showCustomSitePrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Custom site");
        builder.setMessage("Changing site will restart the app.");

        View view = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, null);
        final EditText uuidInput = new EditText(this);
        uuidInput.setHint("Site UUID");
        uuidInput.setText(preferences.getString(SettingKeys.siteUUID, SettingKeys.DEFAULT_SITE_UUID));

        final EditText domainInput = new EditText(this);
        domainInput.setHint("Site domain");
        domainInput.setText(preferences.getString(SettingKeys.siteDomain, SettingKeys.DEFAULT_SITE_DOMAIN));

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, 0, padding, 0);
        layout.addView(uuidInput);
        layout.addView(domainInput);
        builder.setView(layout);

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Save & Restart", (dialog, which) -> {
            String uuid = uuidInput.getText().toString().trim();
            String domain = domainInput.getText().toString().trim();
            setSiteAndRestart(uuid, domain);
        });

        builder.show();
    }

    private void setSiteAndRestart(String siteUUID, String siteDomain) {
        if (siteUUID == null || siteUUID.isEmpty()) {
            showInvalidSiteAlert("Site UUID must not be empty.");
            return;
        }

        preferences.edit()
                .putString(SettingKeys.siteUUID, siteUUID)
                .putString(SettingKeys.siteDomain, siteDomain)
                .commit();

        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void showInvalidSiteAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Invalid site")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class SiteViewHolder extends RecyclerView.ViewHolder {
            public TextView valueText;

            public SiteViewHolder(View itemView) {
                super(itemView);
                valueText = itemView.findViewById(R.id.row_settings_site_value);
                itemView.setOnClickListener(v -> showSiteSwitcher());
            }
        }

        public class ToggleViewHolder extends RecyclerView.ViewHolder {
            public TextView settingText;
            public Switch settingSwitch;

            public ToggleViewHolder(View itemView) {
                super(itemView);
                settingText = itemView.findViewById(R.id.row_settings_text);
                settingSwitch = itemView.findViewById(R.id.row_settings_switch);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? VIEW_TYPE_SITE : VIEW_TYPE_TOGGLE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == VIEW_TYPE_SITE) {
                return new SiteViewHolder(inflater.inflate(R.layout.row_settings_site, parent, false));
            }
            return new ToggleViewHolder(inflater.inflate(R.layout.row_settings, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SiteViewHolder) {
                ((SiteViewHolder) holder).valueText.setText(currentSiteDomain());
                return;
            }

            Setting setting = settingsViewModel.settingList.get(position - 1);
            ToggleViewHolder toggleHolder = (ToggleViewHolder) holder;
            toggleHolder.settingText.setTextColor(ColorManager.isDarkMode(getApplicationContext()) ? Color.WHITE : Color.BLACK);
            toggleHolder.settingText.setText(setting.title);
            toggleHolder.settingSwitch.setOnCheckedChangeListener(null);
            toggleHolder.settingSwitch.setChecked(preferences.getBoolean(setting.key, false));
            toggleHolder.settingSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                preferences.edit().putBoolean(setting.key, b).apply();
                if (SettingKeys.darkMode.equals(setting.key)) {
                    updateColors();
                }
            });
        }

        @Override
        public int getItemCount() {
            return settingsViewModel.settingList.size() + 1;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}