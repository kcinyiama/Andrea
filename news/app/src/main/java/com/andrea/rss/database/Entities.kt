package com.andrea.rss.database

import androidx.room.*
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.network.ParsedRssFeed

/*
 * TODO Basic rss item model. This is used to make the DatabaseRssFeed work.
 *  It will be updated in a later implementation to include more properties.
 */
@Entity(
    tableName = "rss_item",
    indices = [Index(value = ["url"], unique = true)]
)
data class DatabaseRssItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var url: String,
    /*
     * TODO
     * Used to test fetching of rss feeds via the RssRepository since we do not have to way to
     * marked rss item feeds which have been fetched or not. This will be removed in the future.
     * If fetched = 0, flow will return the entity so that the feeds can be fetched
     * If fetched = 1, flow will not return it.
     */
    var fetched: Int = 0
)

@Entity(
    tableName = "rss_feed",
    foreignKeys = [ForeignKey(
        entity = DatabaseRssItem::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("rss_item_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["guid"], unique = true)]
)
data class DatabaseRssFeed(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var guid: String = "",
    var title: String = "",
    var link: String = "",
    @ColumnInfo(name = "publication_date")
    var publicationDate: String = "",
    var description: String = "",
    @ColumnInfo(name = "rss_item_id", index = true)
    var rssItemId: Int = 0
)

data class DatabaseRssItemWithFeeds(
    @Embedded val item: DatabaseRssItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "rss_item_id"
    )
    val feeds: List<DatabaseRssFeed>
)

fun List<DatabaseRssItemWithFeeds>.toDomainModel(): List<RssFeed> {
    return flatMap {
        it.feeds.map { feed ->
            RssFeed(
                id = feed.id,
                title = feed.title,
                fullStoryLink = feed.link,
                publicationDate = feed.publicationDate,
                description = feed.description
            )
        }
    }
}

fun List<ParsedRssFeed>.toDatabaseModel(item: DatabaseRssItem): List<DatabaseRssFeed> {
    return map {
        DatabaseRssFeed(
            guid = it.guid,
            title = it.title,
            link = it.link,
            publicationDate = it.publicationDate,
            description = it.description,
            rssItemId = item.id
        )
    }
}