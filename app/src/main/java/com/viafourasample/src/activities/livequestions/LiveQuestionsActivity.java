package com.viafourasample.src.activities.livequestions;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.profile.ProfileActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasdk.src.fragments.livequestions.VFLiveQuestionsFragment;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFSettings;

public class LiveQuestionsActivity extends AppCompatActivity implements VFActionsInterface {

    private String containerId;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_questions);

        title = getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE);
        containerId = getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_ID);
        if (title != null) setTitle(title);

        loadFragment(containerId);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadFragment(String id) {
        VFColors colors = new VFColors(
            ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark),
            ContextCompat.getColor(getApplicationContext(), R.color.colorVf)
        );
        VFSettings vfSettings = new VFSettings(colors);
        String siteDomain = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(SettingKeys.siteDomain, SettingKeys.DEFAULT_SITE_DOMAIN);
        String siteUrl = "https://" + siteDomain;
        VFArticleMetadata metadata = new VFArticleMetadata(
            siteUrl,
            title != null ? title : "Live Questions",
            "",
            siteUrl
        );

        VFLiveQuestionsFragment fragment = VFLiveQuestionsFragment.newInstance(
            id != null ? id : "test-livequestions",
            metadata,
            vfSettings,
            20,
            2,
            null
        );
        fragment.setActionsInterface(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.live_questions_container, fragment);
        ft.commit();
    }

    private void showChangeIdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Live Q&A container ID");
        builder.setMessage("Enter a container ID for Live Q&A");

        final EditText input = new EditText(this);
        input.setText(containerId);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, input.getPaddingTop(), padding, input.getPaddingBottom());
        builder.setView(input);

        builder.setPositiveButton("Accept", (dialog, which) -> {
            String newId = input.getText().toString().trim();
            if (!newId.isEmpty()) {
                containerId = newId;
                loadFragment(containerId);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_live_questions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_change_id) {
            showChangeIdDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewAction(VFActionType actionType, VFActionData action) {
        if (actionType == VFActionType.openProfilePressed) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra(IntentKeys.INTENT_USER_UUID, action.getOpenProfileAction().userUUID.toString());
            startActivity(intent);
        } else if (actionType == VFActionType.authPressed) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}
