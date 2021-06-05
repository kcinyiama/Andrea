package com.andrea.rss.database

import androidx.room.*
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.network.ParsedRssFeed
import com.andrea.rss.network.ParsedRssItem

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
    var enabled: Int = 1, //item enabled,
    var group: String,
    var iconUrl: String? = null,
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
    var title: String,
    var link: String,
    var description: String,
    var guid: String,
    @ColumnInfo(name = "publication_date")
    var publicationDate: String? = null,
    @ColumnInfo(name = "rss_item_id", index = true)
    var rssItemId: Int = -1
)

data class DatabaseRssItemWithFeeds(
    @Embedded val item: DatabaseRssItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "rss_item_id"
    )
    val feeds: List<DatabaseRssFeed>
)

// TODO Delete later
val images = arrayListOf(
    "https://images.pexels.com/photos/7688377/pexels-photo-7688377.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=150&w=150",
    "https://images.pexels.com/photos/7560130/pexels-photo-7560130.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=150&w=150",
    "https://images.pexels.com/photos/7987629/pexels-photo-7987629.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=150&w=150",
    "https://images.pexels.com/photos/8096623/pexels-photo-8096623.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=150",
    "https://images.pexels.com/photos/7750026/pexels-photo-7750026.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=150",
    "https://images.pexels.com/photos/7175583/pexels-photo-7175583.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=150"
)

fun List<DatabaseRssItemWithFeeds>.toDomainModel(): List<RssFeed> {
    return flatMap {
        it.feeds.map { feed ->
            RssFeed(
                id = feed.id,
                title = feed.title,
                fullStoryLink = feed.link,
                publicationDate = feed.publicationDate,
                description = feed.description,
                imageUrl = images.shuffled().first(),
                rssItemIconUrl = it.item.iconUrl,
                rssItemGroup = it.item.group,
                rssItemTitle = it.item.name
            )
        }
    }
}

fun List<ParsedRssFeed>.toDatabaseModel(item: DatabaseRssItem): List<DatabaseRssFeed> {
    return map {
        DatabaseRssFeed(
            title = it.title!!,
            link = it.link!!,
            description = it.description!!,
            guid = it.guid!!,
            publicationDate = it.publicationDate,
            rssItemId = item.id
        )
    }
}

fun ParsedRssItem.toDatabaseModel(): DatabaseRssItem {
    return DatabaseRssItem(
        name = title!!,
        url = link!!,
        group = group!!,
        iconUrl = iconUrl
    )
}