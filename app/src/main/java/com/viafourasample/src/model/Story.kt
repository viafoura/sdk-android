package com.viafourasample.src.model

class Story(
    val pictureUrl: String,
    val title: String,
    val description: String,
    val author: String,
    val category: String,
    val link: String,
    val containerId: String,
    var storyType: StoryType
) {
    enum class StoryType {
        comments, reviews, liveQuestions, liveChat
    }

    companion object {
        fun randomStory(containerId: String): Story = Story(
            "https://www.datocms-assets.com/55856/1636753460-information-overload.jpg?crop=focalpoint&fit=crop&fm=webp&fp-x=0.86&fp-y=0.47&h=428&w=856",
            "Moving Staff to Cover the Coronavirus",
            "Here Are What Media Companies Are Doing to Deal With COVID-19 Information Overload",
            "Norman Phillips",
            "ECONOMY",
            "https://viafoura-mobile-demo.vercel.app/posts/here-are-what-media-companies-are-doing-with-covid-19-overload",
            containerId,
            StoryType.comments
        )
    }
}
