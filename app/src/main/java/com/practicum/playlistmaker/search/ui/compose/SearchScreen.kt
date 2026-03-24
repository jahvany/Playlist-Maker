package com.practicum.playlistmaker.search.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.practicum.playlistmaker.util.ui.CommonTopBar
import com.practicum.playlistmaker.util.ui.RegularButton
import com.practicum.playlistmaker.util.ui.RegularProgressBar
import java.text.SimpleDateFormat
import java.util.Locale

val track1 = Track(trackName="Perfect", artistName="Ed Sheeran", trackTimeMillis=263400, artworkUrl100="https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/15/e6/e8/15e6e8a4-4190-6a8b-86c3-ab4a51b88288/190295851286.jpg/100x100bb.jpg", trackId=1193701400, collectionName="÷ (Deluxe)", releaseDate="2017-03-03T08:00:00Z", primaryGenreName="Pop", country="USA", previewUrl="https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/c7/ba/bc/c7babc66-f598-aaa6-bcf6-307281795817/mzaf_16337361235117168274.plus.aac.p.m4a", isFavorite=false)

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

    LaunchedEffect(searchInputState) {
        snapshotFlow { searchInputState.text }
            .distinctUntilChanged()
            .collect { newText ->
                searchDebounce(newText.toString())
            }
    }

    // Background for EditText
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            state = searchInputState,
            placeholder = { Text(stringResource(R.string.search)) },
            textStyle = MaterialTheme.typography.bodyMedium,
            inputTransformation = InputTransformation.maxLength(30),
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onKeyboardAction = {
                searchCall(searchInputState.text.toString())
            },
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .padding(start = 14.dp, end = 4.dp),
                    painter = painterResource(R.drawable.lupa),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (searchInputState.text.isNotEmpty()) {
                    IconButton(
                        modifier = Modifier
                            .padding(end = 4.dp),
                        onClick = { searchInputState.edit { replace(0, length, "") } }
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(vertical = 12.dp).padding(end = 14.dp),
                            painter = painterResource(R.drawable.clear),
                            contentDescription = stringResource(R.string.clear)
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Content(
    state: SearchState? = SearchState.Content(tracks = listOf(track1)),
    onTrackClick: (Track) -> Unit = {},
    onClearHistoryClick: () -> Unit = {},
    onUpdateButtonClick: () -> Unit = {}
) {
    when (state) {
        is SearchState.Loading -> {
            RegularProgressBar()
        }

        is SearchState.NothingFound -> {
            Error(R.drawable.nothing, R.string.searchNothing, false)
        }

        is SearchState.Error -> {
            Error(R.drawable.error, R.string.searchError, true, onUpdateButtonClick)
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
                        .padding(top = 8.dp),
                    text = stringResource(R.string.textHistory),
                    fontSize = 19.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
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
            Tracks(state.tracks, onTrackClick)
        }

        else -> {

        }
    }
}

@Composable
fun Error(
    imageRes: Int,
    messageRes: Int,
    showUpdateButton: Boolean,
    onUpdateButtonClick: () -> Unit = {}
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
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium)),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        if (showUpdateButton) {
            RegularButton(
                label = stringResource(R.string.update),
                onClick = onUpdateButtonClick
            )
        }
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
             key = { index -> tracks[index].trackId }
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
    val cornerRadius = (2 * LocalResources.current.displayMetrics.density).roundToInt()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(start = 12.dp, end = 13.dp)
            .clickable { onTrackClick(track) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
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
}