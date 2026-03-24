package com.practicum.playlistmaker.media.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.MediaPlaylistState
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.fragments.FragmentPlaylist
import com.practicum.playlistmaker.media.ui.view_model.MediaPlaylistsViewModel
import com.practicum.playlistmaker.search.ui.compose.RegularProgressBar
import com.practicum.playlistmaker.search.ui.compose.Error
import com.practicum.playlistmaker.search.ui.compose.RegularButton

@Composable
fun PlaylistsTab(
    viewModel: MediaPlaylistsViewModel,
    navController: NavController
) {
    val state by viewModel.observeState().observeAsState()

    val onPlaylistClick = { playlist: Playlist ->
        if (viewModel.clickDebounce()) {
            navController.navigate(R.id.action_mediaFragment_to_fragmentPlaylist,
                FragmentPlaylist.createArgs(playlist))
        }
    }

    val onNewPlaylistButtonClick = {
        navController.navigate(R.id.action_mediaFragment_to_fragmentNewPlaylist)
    }

    // New playlist button
    RegularButton(
        label = stringResource(R.string.newPlaylist),
        onClick = onNewPlaylistButtonClick
    )

    when (state) {
        is MediaPlaylistState.Loading -> {
            RegularProgressBar()
        }

        is MediaPlaylistState.Empty  -> {
            Error(
                imageRes = R.drawable.nothing,
                messageRes = R.string.nothingPlaylists,
                showUpdateButton = false
            )
        }

        is MediaPlaylistState.Content -> {
            PlaylistsGrid(
                playlists = (state as MediaPlaylistState.Content).playlists,
                onItemClick = onPlaylistClick
            )
        }

        else -> {

        }
    }
}

@Composable
fun PlaylistsGrid(
    playlists: List<Playlist> = listOf(),
    onItemClick: (Playlist) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(playlists.size) { index ->
            PlaylistGridItem(playlists[index], onItemClick)
        }
    }
}

@Composable
fun PlaylistGridItem(
    playlist: Playlist,
    onClick: (Playlist) -> Unit
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .height(196.dp)
            .padding(start = 4.dp, end = 4.dp)
            .clickable { onClick(playlist) },
        horizontalAlignment = Alignment.Start
    ) {
        // Cover Image
        AsyncImage(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(8.dp)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(playlist.cover)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            contentDescription = null,
            contentScale = Crop
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Playlist name
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Tracks number
        Text(
            text = pluralStringResource(
                id = R.plurals.track_count,
                count = playlist.numbersOfTracks,
                playlist.numbersOfTracks
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}