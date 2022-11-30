package com.viafourasample.src.fragments.livechat;


import android.content.Context;
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
import com.viafourasample.src.fragments.home.HomeFragmentViewModel;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.LiveChat;
import com.viafourasample.src.model.Story;

public class LiveChatFragment extends Fragment {

    private LiveChatFragmentViewModel viewModel = new LiveChatFragmentViewModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.fragment_home_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        LiveChatAdapter articleAdapter = new LiveChatAdapter();
        recyclerView.setAdapter(articleAdapter);
    }

    public class LiveChatAdapter extends RecyclerView.Adapter<LiveChatAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView titleTextView;
            public ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);

                titleTextView = (TextView) itemView.findViewById(R.id.row_livechat_text);
                imageView = (ImageView) itemView.findViewById(R.id.row_livechat_image);
            }
        }

        @Override
        public LiveChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.row_livechat, parent, false);
            return new LiveChatAdapter.ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(LiveChatAdapter.ViewHolder holder, int position) {
            LiveChat liveChat = viewModel.chatList.get(position);

            holder.imageView.setImageResource(liveChat.getImage());
            holder.titleTextView.setText(liveChat.getTitle());
            holder.itemView.findViewById(R.id.row_livechat_holder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(requireContext(), LiveChatActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return viewModel.chatList.size();
        }
    }
}
