package com.andrea.rss.network

open class RssParserException(str: String) : IllegalArgumentException(str)

class InvalidRssItemException(str: String) : RssParserException(str)