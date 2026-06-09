package com.viafourasample.src.activities.livequestions;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.profile.ProfileActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasdk.src.fragments.livequestions.VFLiveQuestionsFragment;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFSettings;
import android.content.Intent;

public class LiveQuestionsActivity extends AppCompatActivity implements VFActionsInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_questions);

        String title = getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE);
        String containerId = getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_ID);
        if (title != null) setTitle(title);

        VFColors colors = new VFColors(
            ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark),
            ContextCompat.getColor(getApplicationContext(), R.color.colorVf)
        );
        VFSettings vfSettings = new VFSettings(colors);
        VFArticleMetadata metadata = new VFArticleMetadata(
            "https://viafoura-mobile-demo.vercel.app",
            title != null ? title : "Live Questions",
            "",
            "https://viafoura-mobile-demo.vercel.app"
        );

        VFLiveQuestionsFragment fragment = VFLiveQuestionsFragment.newInstance(
            containerId != null ? containerId : "test-livequestions",
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

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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
