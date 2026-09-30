package com.viafourasample.src.model

sealed class ArticleBlock(val text: String) {
    class Heading(text: String) : ArticleBlock(text)
    class Paragraph(text: String) : ArticleBlock(text)
}
