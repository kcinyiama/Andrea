package com.andrea.rss.database

import androidx.room.*
import com.andrea.rss.domain.RssFeed
import com.andrea.rss.domain.RssItem
import com.andrea.rss.parser.ParsedRssFeed
import com.andrea.rss.parser.ParsedRssItem

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

fun DatabaseRssItem.isEnabled(): Boolean = enabled == 1

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
    var imageUrls: String? = null,
    var author: String? = null,
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

data class DatabaseFeedWithItem(
    @Embedded val feed: DatabaseRssFeed,
    @Relation(
        parentColumn = "rss_item_id",
        entityColumn = "id"
    )
    val item: DatabaseRssItem
)

@JvmName("toRssFeedModel")
fun List<DatabaseRssItemWithFeeds>.toDomainModel(): List<RssFeed> {
    return flatMap {
        it.feeds.map { feed ->
            RssFeed(
                id = feed.id,
                title = feed.title,
                fullStoryLink = feed.link,
                author = feed.author ?: it.item.group,
                publicationDate = feed.publicationDate,
                description = feed.description,
                imageUrls = feed.imageUrls?.split(",")?.toList() ?: emptyList(),
                rssItem = RssItem(
                    it.item.id,
                    it.item.name,
                    it.item.group,
                    it.item.isEnabled(),
                    it.item.iconUrl
                )
            )
        }
    }
}

fun DatabaseFeedWithItem.toDomainModel(): RssFeed {
    return RssFeed(
        id = feed.id,
        title = feed.title,
        fullStoryLink = feed.link,
        author = feed.author ?: item.group,
        publicationDate = feed.publicationDate,
        description = feed.description,
        imageUrls = feed.imageUrls?.split(",")?.toList() ?: emptyList(),
        rssItem = RssItem(item.id, item.name, item.group, item.isEnabled(), item.iconUrl)
    )
}

@JvmName("toRssItemModel")
fun List<DatabaseRssItem>.toDomainModel(): List<RssItem> = map { RssItem(it.id, it.name, it.group, it.isEnabled(), it.iconUrl) }

fun List<ParsedRssFeed>.toDatabaseModel(item: DatabaseRssItem): List<DatabaseRssFeed> {
    return map {
        DatabaseRssFeed(
            title = it.title!!,
            link = it.link!!,
            description = it.description!!,
            guid = it.guid!!,
            publicationDate = it.publicationDate,
            imageUrls = it.imageUrls.joinToString(","),
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