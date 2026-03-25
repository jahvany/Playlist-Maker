package com.practicum.playlistmaker.media.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.compose.Error
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.FavoriteState
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.fragments.PlayerFragment
import com.practicum.playlistmaker.search.ui.compose.Tracks
import com.practicum.playlistmaker.util.ui.RegularProgressBar

@Composable
fun FavoritesTab(
    viewModel: FavoriteTracksViewModel,
    navController: NavController
) {
    val state by viewModel.observeState().observeAsState()

    val onTrackClick = { track: Track ->
        if (viewModel.clickDebounce()) {
            navController.navigate(
                R.id.action_mediaFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        when (state) {
            is FavoriteState.Loading -> {
                RegularProgressBar()
            }

            is FavoriteState.Empty -> {
                Error(
                    imageRes = R.drawable.nothing,
                    messageRes = R.string.nothingTracks
                )
            }

            is FavoriteState.Content -> {
                Spacer(modifier = Modifier.height(16.dp))
                Tracks(
                    // Smart cast to 'FavoriteState.Content' is impossible, because 'state' is a delegated property
                    tracks = (state as FavoriteState.Content).tracks,
                    onTrackClick = onTrackClick
                )
            }

            else -> {

            }
        }
    }
}