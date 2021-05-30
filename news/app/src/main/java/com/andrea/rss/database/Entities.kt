package com.andrea.rss.database

import androidx.room.*
import com.andrea.rss.domain.RssFeed

/*
 * TODO Basic rss item model. This is used to make the DatabaseRssFeed work.
 *  It will be updated in a later implementation to include more properties.
 */
@Entity(tableName = "rss_item")
data class DatabaseRssItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String
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
    var rssItemId: Long = -1
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