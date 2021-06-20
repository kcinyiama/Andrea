package com.andrea.rss.parser

import java.lang.Exception

open class RssParserException(str: String) : IllegalArgumentException(str)

class RssFetchException(str: String, e: Throwable): Exception(str, e)

class InvalidRssItemException(str: String) : RssParserException(str)