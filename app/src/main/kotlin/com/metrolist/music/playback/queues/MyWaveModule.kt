package com.metrolist.music.playback.queues

import androidx.media3.common.MediaItem
import com.metrolist.innertube.YouTube
import com.metrolist.innertube.models.WatchEndpoint
import com.metrolist.music.extensions.toMediaItem
import com.metrolist.music.models.MediaMetadata
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class MyWaveModule(
    private var endpoint: WatchEndpoint = WatchEndpoint(playlistId = "RDAMVM"),
    override val preloadItem: MediaMetadata? = null,
) : Queue {
    private var continuation: String? = null

    override fun hasNextPage(): Boolean = true

    override suspend fun getInitialStatus(): Queue.Status = withContext(IO) {
        val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
        val items = nextResult.items
        continuation = nextResult.continuation

        return@withContext Queue.Status(
            title = nextResult.title,
            items = items.map { it.toMediaItem() },
            mediaItemIndex = nextResult.currentIndex ?: 0,
        )
    }

    override suspend fun nextPage(): List<MediaItem> = withContext(IO) {
        val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
        continuation = nextResult.continuation

        return@withContext nextResult.items.map { it.toMediaItem() }
    }
}
