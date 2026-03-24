package com.practicum.playlistmaker.media.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.compose.Error
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.domain.models.FavoriteState
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.fragments.PlayerFragment
import com.practicum.playlistmaker.search.ui.compose.RegularProgressBar
import com.practicum.playlistmaker.search.ui.compose.Tracks

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

    when (state) {
        is FavoriteState.Loading -> {
            RegularProgressBar()
        }

        is FavoriteState.Empty  -> {
            Error(
                imageRes = R.drawable.nothing,
                messageRes = R.string.nothingTracks,
                showUpdateButton = false
            )
        }

        is FavoriteState.Content -> {
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