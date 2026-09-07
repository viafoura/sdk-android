package com.viafourasample.src.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.article.ArticleActivity
import com.viafourasample.src.activities.livechat.LiveChatActivity
import com.viafourasample.src.activities.livequestions.LiveQuestionsActivity
import com.viafourasample.src.managers.StoryManager
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.model.Story

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.fragment_home_add).visibility = View.GONE
        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_home_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ArticleAdapter(StoryManager.getInstance().storyList)
    }

    private fun onArticleClicked(story: Story) {
        val intent = when (story.storyType) {
            Story.StoryType.liveQuestions -> Intent(requireContext(), LiveQuestionsActivity::class.java)
            Story.StoryType.liveChat -> Intent(requireContext(), LiveChatActivity::class.java)
            else -> Intent(requireContext(), ArticleActivity::class.java)
        }
        intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, story.containerId)
        intent.putExtra(IntentKeys.INTENT_STORY_TITLE, story.title)
        startActivity(intent)
    }

    inner class ArticleAdapter(private val stories: List<Story>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val holder: View = itemView.findViewById(R.id.row_article_holder)
            val image: ImageView = itemView.findViewById(R.id.row_article_image)
            val title: TextView = itemView.findViewById(R.id.row_article_title)
            val desc: TextView = itemView.findViewById(R.id.row_article_desc)
            val category: TextView = itemView.findViewById(R.id.row_article_category)
            val author: TextView = itemView.findViewById(R.id.row_article_author)
        }

        inner class LiveQuestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val holder: View = itemView.findViewById(R.id.row_livechat_holder)
            val image: ImageView = itemView.findViewById(R.id.row_livechat_image)
            val title: TextView = itemView.findViewById(R.id.row_livechat_text)
        }

        override fun getItemViewType(position: Int): Int =
            if (stories[position].storyType == Story.StoryType.liveQuestions) {
                VIEW_TYPE_LIVE_QUESTIONS
            } else {
                VIEW_TYPE_ARTICLE
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            if (viewType == VIEW_TYPE_LIVE_QUESTIONS) {
                return LiveQuestionsViewHolder(
                    inflater.inflate(R.layout.row_livechat, parent, false)
                )
            }
            return ArticleViewHolder(inflater.inflate(R.layout.row_article, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val story = stories[position]
            if (holder is LiveQuestionsViewHolder) {
                holder.title.text = story.title
                holder.image.setImageResource(R.drawable.icon_question)
                holder.image.visibility = View.VISIBLE
                holder.holder.setOnClickListener { onArticleClicked(story) }
                return
            }

            val articleHolder = holder as ArticleViewHolder
            articleHolder.title.text = story.title
            articleHolder.desc.text = story.description
            articleHolder.category.text = story.category
            articleHolder.author.text = story.author
            Glide.with(articleHolder.image.context)
                .load(story.pictureUrl)
                .into(articleHolder.image)
            articleHolder.holder.setOnClickListener { onArticleClicked(story) }
        }

        override fun getItemCount(): Int = stories.size
    }

    companion object {
        private const val VIEW_TYPE_ARTICLE = 0
        private const val VIEW_TYPE_LIVE_QUESTIONS = 1
    }
}
