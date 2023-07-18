package com.viafourasample.src.fragments.livechat;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.model.LiveChat;

import java.util.ArrayList;
import java.util.List;

public class LiveChatFragmentViewModel {
    public List<LiveChat> chatList = new ArrayList<>();

    public LiveChatFragmentViewModel() {
        chatList.add(new LiveChat("Sports", R.drawable.apple, "sports", true));
        chatList.add(new LiveChat("Politics", R.drawable.apple, "politics", false));
        chatList.add(new LiveChat("Fashion", R.drawable.apple, "fashion", false));
        chatList.add(new LiveChat("Business", R.drawable.apple, "business", false));
    }
}
