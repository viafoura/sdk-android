package com.viafourasample.src.managers

import com.viafourasample.src.model.Story

class StoryManager {
    val storyList: MutableList<Story> = mutableListOf(
        Story(
            "https://www.datocms-assets.com/55856/1636663477-blognewheights.jpg?fit=crop&fm=webp&h=428&w=856",
            "Live Q&A",
            "Join our live questions session and ask editors your most pressing questions about the future of news",
            "Editorial Team",
            "LIVE",
            "https://viafoura-mobile-demo.vercel.app",
            "600042387794",
            Story.StoryType.liveQuestions
        ),
        Story(
            "https://www.datocms-assets.com/67251/1701970811-tacos.jpg?fit=crop&fm=webp&h=428&w=856",
            "Moving Staff to Cover the Coronavirus",
            "Here Are What Media Companies Are Doing to Deal With COVID-19 Information Overload",
            "Norman Phillips",
            "ECONOMY",
            "https://viafoura-mobile-demo.vercel.app/posts/here-are-what-media-companies-are-doing-with-covid-19-overload",
            "72c86bde-e162-11ee-b6d0-e3e7190ad965",
            Story.StoryType.comments
        ),
        Story(
            "https://www.datocms-assets.com/55856/1636663477-blognewheights.jpg?fit=crop&fm=webp&h=428&w=856",
            "Grow civility",
            "Don't shut out your community, instead guide them towards civility",
            "Tom Hardington",
            "ECONOMY",
            "https://viafoura-mobile-demo.vercel.app/posts/dont-shut-out-your-community-guide-them-to-civility",
            "101113531",
            Story.StoryType.comments
        ),
        Story(
            "https://www.datocms-assets.com/67251/1701970811-tacos.jpg?fit=crop&fm=webp&h=428&w=856",
            "Korean Fusion Delight",
            "Homemade Bulgogi Tacos Recipe",
            "Tom Hardington",
            "RECIPE",
            "https://viafoura-mobile-demo.vercel.app/posts/brexit-to-cost-gbp1-200-for-each-person-in-uk",
            "1231293123",
            Story.StoryType.reviews
        ),
        Story(
            "https://www.datocms-assets.com/55856/1636663477-blognewheights.jpg?fit=crop&fm=webp&h=428&w=856",
            "Live Chat: Breaking News Coverage",
            "Chat live with our reporters as they cover today's top stories in real time",
            "News Desk",
            "LIVE",
            "https://viafoura-mobile-demo.vercel.app",
            "test-livechat",
            Story.StoryType.liveChat
        )
    )

    fun getStory(containerId: String): Story =
        storyList.firstOrNull { it.containerId == containerId } ?: Story.randomStory(containerId)

    companion object {
        private var singleton: StoryManager? = null

        fun getInstance(): StoryManager = singleton ?: StoryManager().also { singleton = it }
    }
}
