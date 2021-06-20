package com.andrea.rss.parser

data class Node(val name: String, val parent: Node?)

enum class ItemTag {
    TITLE,
    LINK,
    GUID,
    DESCRIPTION,
    PUBLICATION_DATE,
    UNKNOWN;

    companion object {
        fun toEnum(str: String) = when (str) {
            "title" -> TITLE
            "link" -> LINK
            "guid" -> GUID
            "description" -> DESCRIPTION
            "pubDate" -> PUBLICATION_DATE
            else -> UNKNOWN
        }
    }
}

enum class ChannelTag {
    TITLE,
    LINK,
    UNKNOWN;

    companion object {
        fun toEnum(str: String) = when (str) {
            "title" -> TITLE
            "link" -> LINK
            else -> UNKNOWN
        }
    }
}