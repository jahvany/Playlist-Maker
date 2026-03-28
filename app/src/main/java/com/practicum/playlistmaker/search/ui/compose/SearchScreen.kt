package com.practicum.playlistmaker.search.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.fragments.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.input.VisualTransformation
import com.practicum.playlistmaker.util.ui.CommonTopBar
import com.practicum.playlistmaker.util.ui.RegularButton
import com.practicum.playlistmaker.util.ui.RegularProgressBar
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavController
) {
    val searchState by viewModel.state.observeAsState()

    val searchDebounce = { query: String ->
        viewModel.textForSave = query
        viewModel.searchDebounce(viewModel.textForSave)
    }

    val onTrackClick = { track: Track ->
        if (viewModel.clickDebounce()) {
            viewModel.saveTrack(track)
            navController.navigate(R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        CommonTopBar(label = stringResource(R.string.search))

        // Search input with icons
        SearchInput(
            searchDebounce = searchDebounce,
            searchCall = viewModel::search
        )

        // Content based on state
        Content(
            state = searchState,
            onTrackClick = onTrackClick,
            onClearHistoryClick = viewModel::clearHistory,
            onUpdateButtonClick = { viewModel.search(viewModel.textForSave) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchInput(
    searchDebounce: (String) -> Unit = {},
    searchCall: (String) -> Unit = {}
) {
    val searchInputState = rememberTextFieldState()

    // On text changed
    LaunchedEffect(searchInputState) {
        snapshotFlow { searchInputState.text }
            .distinctUntilChanged()
            .collect { newText ->
                searchDebounce(newText.toString())
            }
    }

    BasicTextField(
        state = searchInputState,
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 16.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation = InputTransformation.maxLength(30),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        onKeyboardAction = {
            searchCall(searchInputState.text.toString())
        },
        decorator = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = searchInputState.text.toString(),
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                placeholder = {
                    Text(
                        text = stringResource(R.string.search),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.lupa),
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (searchInputState.text.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier
                                .padding(vertical = 12.dp),
                            onClick = { searchInputState.edit { replace(0, length, "") } }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.clear),
                                tint = MaterialTheme.colorScheme.primaryContainer,
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                },
                container = {
                    Box(
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                contentPadding = PaddingValues(vertical = 0.dp)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun Content(
    state: SearchState? = SearchState.Error,
    onTrackClick: (Track) -> Unit = {},
    onClearHistoryClick: () -> Unit = {},
    onUpdateButtonClick: () -> Unit = {}
) {
    when (state) {
        is SearchState.Loading -> {
            RegularProgressBar()
        }

        is SearchState.NothingFound -> {
            Error(R.drawable.nothing, R.string.searchNothing)
        }

        is SearchState.Error -> {
            Column {
                Error(R.drawable.error, R.string.searchError)

                // Update button
                RegularButton(
                    label = stringResource(R.string.update),
                    onClick = onUpdateButtonClick,
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .height(36.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        is SearchState.History -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(top = 24.dp),
                    text = stringResource(R.string.textHistory),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                // RecyclerView equivalent
                Tracks(state.tracks, onTrackClick)

                // Clear history
                RegularButton(
                    label = stringResource(R.string.clearHistory),
                    onClick = onClearHistoryClick,
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .height(36.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        is SearchState.Content -> {
            Spacer(modifier = Modifier.height(12.dp))

            Tracks(state.tracks, onTrackClick)
        }

        else -> {

        }
    }
}

@Composable
fun Error(
    imageRes: Int,
    messageRes: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(top = 86.dp, bottom = 16.dp),
            painter = painterResource(imageRes),
            contentDescription = null,
        )

        Text(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(horizontal = 16.dp),
            text = stringResource(messageRes),
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Tracks(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
         items(
             count = tracks.size,
             key = { index -> tracks[index].trackId },
             contentType = { index -> tracks[index]::class }
         ) { index ->
             TrackItem(tracks[index], onTrackClick)
         }
    }
}

@Composable
fun TrackItem(
    track: Track,
    onTrackClick: (Track) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(start = 12.dp, end = 13.dp)
            .clickable { onTrackClick(track) }
    ) {
        TrackItemInfo(track)
    }
}

@Composable
fun TrackItemInfo(track: Track) {
    val cornerRadius = (2 * LocalResources.current.displayMetrics.density).roundToInt()

    Row(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Cover Image
        AsyncImage(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(cornerRadius.dp)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(track.artworkUrl100)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Track info column
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Track name
            Text(
                modifier = Modifier
                    .padding(end = 8.dp),
                text = track.trackName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Artist and time
            Text(
                text = stringResource(R.string.trackInfo, track.artistName, SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Forward arrow
        Image(
            modifier = Modifier
                .size(24.dp),
            painter = painterResource(id = R.drawable.arrowforward),
            contentDescription = "Select track",
        )
    }
}