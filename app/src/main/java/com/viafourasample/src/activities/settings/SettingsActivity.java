package com.viafourasample.src.activities.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.model.Setting;

public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel settingsViewModel = new SettingsViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.settings_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SettingsAdapter());
    }

    public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView settingText;
            public Switch settingSwitch;

            public ViewHolder(View itemView) {
                super(itemView);

                settingText = (TextView) itemView.findViewById(R.id.row_settings_text);
                settingSwitch = (Switch) itemView.findViewById(R.id.row_settings_switch);
            }
        }

        @Override
        public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.row_settings, parent, false);
            return new SettingsAdapter.ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(SettingsAdapter.ViewHolder holder, int position) {
            Setting setting = settingsViewModel.settingList.get(position);
            holder.settingText.setText(setting.title);
            holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return settingsViewModel.settingList.size();
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