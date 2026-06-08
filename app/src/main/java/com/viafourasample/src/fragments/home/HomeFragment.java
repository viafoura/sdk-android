package com.viafourasample.src.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.article.ArticleActivity;
import com.viafourasample.src.activities.livechat.LiveChatActivity;
import com.viafourasample.src.activities.livequestions.LiveQuestionsActivity;
import com.viafourasample.src.managers.StoryManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.Story;

import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fragment_home_add).setVisibility(View.GONE);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_home_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new ArticleAdapter(StoryManager.getInstance().getStoryList()));
    }

    private void onArticleClicked(Story story) {
        Intent intent;
        if (story.getStoryType() == Story.StoryType.liveQuestions) {
            intent = new Intent(requireContext(), LiveQuestionsActivity.class);
        } else if (story.getStoryType() == Story.StoryType.liveChat) {
            intent = new Intent(requireContext(), LiveChatActivity.class);
        } else {
            intent = new Intent(requireContext(), ArticleActivity.class);
        }
        intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, story.getContainerId());
        intent.putExtra(IntentKeys.INTENT_STORY_TITLE, story.getTitle());
        startActivity(intent);
    }

    class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

        private final List<Story> stories;

        ArticleAdapter(List<Story> stories) {
            this.stories = stories;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title, desc, category, author;
            View holder;

            ViewHolder(View itemView) {
                super(itemView);
                holder = itemView.findViewById(R.id.row_article_holder);
                image = itemView.findViewById(R.id.row_article_image);
                title = itemView.findViewById(R.id.row_article_title);
                desc = itemView.findViewById(R.id.row_article_desc);
                category = itemView.findViewById(R.id.row_article_category);
                author = itemView.findViewById(R.id.row_article_author);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_article, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Story story = stories.get(position);
            holder.title.setText(story.getTitle());
            holder.desc.setText(story.getDescription());
            holder.category.setText(story.getCategory());
            holder.author.setText(story.getAuthor());
            Glide.with(holder.image.getContext())
                    .load(story.getPictureUrl())
                    .into(holder.image);
            holder.holder.setOnClickListener(v -> onArticleClicked(story));
        }

        @Override
        public int getItemCount() {
            return stories.size();
        }
    }
}
