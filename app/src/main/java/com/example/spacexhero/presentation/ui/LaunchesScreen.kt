package com.example.spacexhero.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.spacexhero.R
import com.example.spacexhero.domain.model.Launch
import com.example.spacexhero.presentation.launches.LaunchesEffect
import com.example.spacexhero.presentation.launches.LaunchesEvent
import com.example.spacexhero.presentation.launches.LaunchesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun LaunchesScreen(
    viewModel: LaunchesViewModel = hiltViewModel(),
    onNavigateToDetails: (String) -> Unit
) {
    val launches: LazyPagingItems<Launch> = viewModel.launchPagingData.collectAsLazyPagingItems()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LaunchesEffect.NavigateToDetails -> onNavigateToDetails(effect.launchId)
                is LaunchesEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    fontWeight = FontWeight.Bold
                )
            }
        )

        val isRefreshing = launches.loadState.refresh is LoadState.Loading

        when {
            // Initial full-screen spinner
            isRefreshing && launches.itemCount == 0 -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            // Initial load error
            launches.loadState.refresh is LoadState.Error && launches.itemCount == 0 -> {
                val msg = (launches.loadState.refresh as LoadState.Error).error.message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = msg ?: "Unknown error", color = Color.Red, fontSize = 16.sp)
                }
            }

            else -> {
                val pullState = rememberPullRefreshState(
                    refreshing = isRefreshing,
                    onRefresh = {
                        launches.refresh()
                        viewModel.handleEvent(LaunchesEvent.RefreshLaunches)
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullState)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(launches.itemCount) { index ->
                            val launch = launches[index]
                            if (launch != null) {
                                LaunchListItem(
                                    launch = launch,
                                    onItemClick = {
                                        viewModel.handleEvent(
                                            LaunchesEvent.OnLaunchClicked(launch.id)
                                        )
                                    }
                                )
                                HorizontalDivider()
                            }
                        }

                        // Footer: loading next page
                        when (val appendState = launches.loadState.append) {
                            is LoadState.Loading -> item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }
                            is LoadState.Error -> item {
                                Text(
                                    text = appendState.error.message ?: "Load error",
                                    color = Color.Red,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                            else -> Unit
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = pullState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun LaunchListItem(
    launch: Launch,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(16.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (launch.imageUrl.isBlank()) {
            LaunchImagePlaceholder()
        } else {
            LaunchImage(launch.imageUrl)
        }

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = launch.name,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun LaunchImage(imageUrl: String) {
    GlideImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .size(48.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(corner = CornerSize(8.dp))),
        contentScale = ContentScale.FillBounds,
        loading = placeholder(R.drawable.startup_rocket_launch_icon)
    )
}

@Composable
private fun LaunchImagePlaceholder() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                Color.hsl(200f, 0.7f, 0.5f),
                shape = RoundedCornerShape(corner = CornerSize(8.dp))
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🚀", fontSize = 24.sp)
    }
}
