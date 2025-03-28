package com.viafourasample.src.fragments.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.article.ArticleActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasample.src.model.Story;

public class HomeFragment extends Fragment {

    private HomeFragmentViewModel viewModel = new HomeFragmentViewModel();
    private View rootView;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView = view;

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        RecyclerView recyclerView = view.findViewById(R.id.fragment_home_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ArticleAdapter articleAdapter = new ArticleAdapter();
        recyclerView.setAdapter(articleAdapter);

        rootView.findViewById(R.id.fragment_home_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentCustomContainerDialog();
            }
        });
    }

    private void presentCustomContainerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Custom container");
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Container ID");
        builder.setView(input);

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String containerID = input.getText().toString().trim();
                Intent intent = new Intent(requireContext(), ArticleActivity.class);
                intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, containerID);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(rootView != null){
            rootView.findViewById(R.id.fragment_home_add).setVisibility(preferences.getBoolean(SettingKeys.customContainerIDs, false) ? View.VISIBLE : View.GONE);

            if(getActivity() != null && ColorManager.isDarkMode(getActivity())){
                rootView.findViewById(R.id.fragment_home_holder).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBackgroundArticle));
            } else {
                rootView.findViewById(R.id.fragment_home_holder).setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
            }
        }
    }

    public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView, descTextView, authorTextView, categoryTextView;
            public ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);

                titleTextView = (TextView) itemView.findViewById(R.id.row_article_title);
                descTextView = (TextView) itemView.findViewById(R.id.row_article_desc);
                authorTextView = (TextView) itemView.findViewById(R.id.row_article_author);
                categoryTextView = (TextView) itemView.findViewById(R.id.row_article_category);
                imageView = (ImageView) itemView.findViewById(R.id.row_article_image);
            }
        }

        @Override
        public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.row_article, parent, false);
            return new ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(ArticleAdapter.ViewHolder holder, int position) {
            Story story = viewModel.storyList.get(position);

            if(getActivity() != null){
                Glide.with(getActivity())
                        .load(story.getPictureUrl())
                        .centerCrop()
                        .into(holder.imageView);
            }

            holder.titleTextView.setText(story.getTitle());
            holder.descTextView.setText(story.getDescription());
            holder.authorTextView.setText(story.getAuthor());
            holder.categoryTextView.setText(story.getCategory());
            holder.itemView.findViewById(R.id.row_article_holder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(requireContext(), ArticleActivity.class);
                    intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, story.getContainerId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return viewModel.storyList.size();
        }
    }
}
