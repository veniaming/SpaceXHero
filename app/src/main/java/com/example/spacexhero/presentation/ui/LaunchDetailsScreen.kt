package com.example.spacexhero.presentation.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spacexhero.presentation.launchdetails.LaunchDetailsEffect
import com.example.spacexhero.presentation.launchdetails.LaunchDetailsEvent
import com.example.spacexhero.presentation.launchdetails.LaunchDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchDetailsScreen(
    launchId: String,
    viewModel: LaunchDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(launchId) {
        viewModel.handleEvent(LaunchDetailsEvent.LoadLaunchDetails(launchId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LaunchDetailsEffect.NavigateBack -> {
                    onNavigateBack()
                }

                is LaunchDetailsEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Launch Details",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        viewModel.handleEvent(LaunchDetailsEvent.OnBackPressed)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        when {
            state.value.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.value.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.value.error ?: "Unknown error",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }

            state.value.launchDetails != null -> {
                val launchDetails = state.value.launchDetails ?: return
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = launchDetails.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (launchDetails.videoUrl.isNotBlank()) {
                        Button(
                            modifier = Modifier.padding(
                                vertical = 16.dp,
                            ),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, launchDetails.videoUrl.toUri())
                                context.startActivity(intent)
                            }
                        ) {
                            Text("Watch on YouTube")
                        }
                    }
                }
            }
        }
    }
}

