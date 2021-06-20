package com.andrea.rss

val RSS_SAMPLE_DATA = """
    <rss xmlns:media="http://example.com/rss/" xmlns:atom="http://www.w3.org/2005/Atom" version="2.0">
        <channel>
            <link>https://www.example.com/world</link>
            <title>World</title>
            <description>Latest world news from Example</description>
            <image>
                <url>https://thewest.com.au/static/media/googlenews-logo-250px.png</url>
                <title>The West Australian</title>
                <link>https://thewest.com.au/travel</link>
            </image>
            <atom:link type="application/rss+xml" rel="self" href="https://www.example.com/rss/world"/>
            <item>
                <title>Headline 1</title>
                <link>https://www.example.com/fullstory/1</link>
                <guid isPermaLink="false">1</guid>
                <pubDate>Sun, 23 May 2021 14:34:05 GMT</pubDate>
                <description readingtime="45">Lorem ipsum dolor sit amet, consectetur adipiscing elit</description>
            </item>
            <item>
                <title>Headline 2</title>
                <link>https://www.example.com/fullstory/2</link>
                <guid isPermaLink="false">2</guid>
                <pubDate>Sun, 23 May 2021 14:15:37 GMT</pubDate>
                <description readingtime="113">&lt;img src="https://www.example.com/2.jpg"&gt;Lorem ipsum dolor sit amet, consectetur adipiscing elit</description>
            </item>
            <item>
                <title>Headline 3</title>
                <link>https://www.example.com/fullstory/3</link>
                <guid isPermaLink="false">3</guid>
                <pubDate>Sun, 23 May 2021 13:29:40 GMT</pubDate>
                <description readingtime="45">&lt;img src="https://www.example.com/2.jpg"&gt;&lt;img src="https://www.example.com/3.jpg"&gt;Lorem ipsum dolor sit amet, consectetur adipiscing elit</description>
            </item>
        </channel>
    </rss>
""".trimIndent()

val RSS_INCOMPLETE_SAMPLE_DATA = """
    <rss xmlns:media="http://example.com/rss/" xmlns:atom="http://www.w3.org/2005/Atom" version="2.0">
        <channel>
            <link>https://www.example.com/world</link>
            <title>World</title>
            <description>Latest world news from Example</description>
            <atom:link type="application/rss+xml" rel="self" href="https://www.example.com/rss/world"/>
            <item>
                <link>https://www.example.com/fullstory/1</link>
                <pubDate>Sun, 23 May 2021 14:34:05 GMT</pubDate>
                <description readingtime="45">Lorem ipsum dolor sit amet, consectetur adipiscing elit</description>
            </item>
            <item>
                <title>Headline 2</title>
                <link>https://www.example.com/fullstory/2</link>
                <guid isPermaLink="false">2</guid>
                <pubDate>Sun, 23 May 2021 14:15:37 GMT</pubDate>
                <description readingtime="113">&lt;img src="https://www.example.com/2.jpg"&gt;Lorem ipsum dolor sit amet, consectetur adipiscing elit</description>
            </item>
            <item>
                <title>Headline 3</title>
                <guid isPermaLink="false">3</guid>
                <pubDate>Sun, 23 May 2021 13:29:40 GMT</pubDate>
                <description readingtime="45">Lorem ipsum dolor sit amet, consectetur adipiscing elit</description>
            </item>
        </channel>
    </rss>
""".trimIndent()

val ICON_DATA_1 = """
    <html>
        <head>
            <link rel="apple-touch-icon" href="www.example.com/1.png" />
            <link rel="shortcut icon" href="www.example.com/2.png" />
        </head>
    </html>
""".trimIndent()

val ICON_DATA_2 = """
    <html>
        <head>
            <link rel="apple-touch-icon" href="www.example.com/alternative1.png" />
            <link rel="icon" href="www.example.com/alternative2.png" />
        </head>
    </html>
""".trimIndent()