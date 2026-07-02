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

    class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_ARTICLE = 0;
        private static final int VIEW_TYPE_LIVE_QUESTIONS = 1;

        private final List<Story> stories;

        ArticleAdapter(List<Story> stories) {
            this.stories = stories;
        }

        class ArticleViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title, desc, category, author;
            View holder;

            ArticleViewHolder(View itemView) {
                super(itemView);
                holder = itemView.findViewById(R.id.row_article_holder);
                image = itemView.findViewById(R.id.row_article_image);
                title = itemView.findViewById(R.id.row_article_title);
                desc = itemView.findViewById(R.id.row_article_desc);
                category = itemView.findViewById(R.id.row_article_category);
                author = itemView.findViewById(R.id.row_article_author);
            }
        }

        class LiveQuestionsViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title;
            View holder;

            LiveQuestionsViewHolder(View itemView) {
                super(itemView);
                holder = itemView.findViewById(R.id.row_livechat_holder);
                image = itemView.findViewById(R.id.row_livechat_image);
                title = itemView.findViewById(R.id.row_livechat_text);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return stories.get(position).getStoryType() == Story.StoryType.liveQuestions
                    ? VIEW_TYPE_LIVE_QUESTIONS
                    : VIEW_TYPE_ARTICLE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == VIEW_TYPE_LIVE_QUESTIONS) {
                View view = inflater.inflate(R.layout.row_livechat, parent, false);
                return new LiveQuestionsViewHolder(view);
            }
            View view = inflater.inflate(R.layout.row_article, parent, false);
            return new ArticleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Story story = stories.get(position);
            if (holder instanceof LiveQuestionsViewHolder) {
                LiveQuestionsViewHolder liveQuestionsHolder = (LiveQuestionsViewHolder) holder;
                liveQuestionsHolder.title.setText(story.getTitle());
                liveQuestionsHolder.image.setImageResource(R.drawable.icon_question);
                liveQuestionsHolder.image.setVisibility(View.VISIBLE);
                liveQuestionsHolder.holder.setOnClickListener(v -> onArticleClicked(story));
                return;
            }

            ArticleViewHolder articleHolder = (ArticleViewHolder) holder;
            articleHolder.title.setText(story.getTitle());
            articleHolder.desc.setText(story.getDescription());
            articleHolder.category.setText(story.getCategory());
            articleHolder.author.setText(story.getAuthor());
            Glide.with(articleHolder.image.getContext())
                    .load(story.getPictureUrl())
                    .into(articleHolder.image);
            articleHolder.holder.setOnClickListener(v -> onArticleClicked(story));
        }

        @Override
        public int getItemCount() {
            return stories.size();
        }
    }
}
