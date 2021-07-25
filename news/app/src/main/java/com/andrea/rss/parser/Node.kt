package com.andrea.rss.parser

data class Node(val name: String, val parent: Node?)

enum class ItemTag(private val tagName: String) {
    TITLE("title"),
    LINK("link"),
    GUID("guid"),
    DESCRIPTION("description"),
    PUBLICATION_DATE("pubDate"),
    AUTHOR("author"),
    UNKNOWN("unknown");

    companion object {
        fun toEnum(str: String) = when (str) {
            TITLE.tagName -> TITLE
            LINK.tagName -> LINK
            GUID.tagName -> GUID
            DESCRIPTION.tagName -> DESCRIPTION
            PUBLICATION_DATE.tagName -> PUBLICATION_DATE
            AUTHOR.tagName -> AUTHOR
            else -> UNKNOWN
        }
    }
}

enum class ChannelTag(private val tagName: String) {
    TITLE("title"),
    LINK("link"),
    UNKNOWN("unknown");

    companion object {
        fun toEnum(str: String) = when (str) {
            TITLE.tagName -> TITLE
            LINK.tagName -> LINK
            else -> UNKNOWN
        }
    }
}