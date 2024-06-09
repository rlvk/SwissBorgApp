package com.example.swissborgapp.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.swissborgapp.R
import com.example.swissborgapp.domain.model.Ticker
import com.example.swissborgapp.presentation.FailureDialogState
import com.example.swissborgapp.presentation.MainViewModel
import com.example.swissborgapp.ui.theme.SwissBorgAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.persistentListOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SwissBorgAppTheme {
                // A surface container using the 'background' color from the theme
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    MainScreenContent(
        modifier = modifier,
        state = state,
        onSearchTextChanged = viewModel::onSearchTextChanged,
        onAcknowledgeFailureDialog = viewModel::onAcknowledgeFailureDialog
    )
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    state: MainViewModel.State,
    onSearchTextChanged: (String) -> Unit,
    onAcknowledgeFailureDialog: () -> Unit,
) {

    if (state.isLoading) {
        ProgressBar()
    } else {
        Scaffold(
            modifier = modifier
                .fillMaxSize(),
            topBar = {
                Column {
                    ConnectivityStatusBox(isConnected = state.isConnected)

                    TextField(
                        value = state.searchText,
                        onValueChange = onSearchTextChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                        ,
                        placeholder = { Text(text = stringResource(id = R.string.search)) }
                    )
                }
            }
        ) { contentPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            ) {
                items(state.publicTickers) { ticker ->
                    TickerListItem(
                        ticker = ticker
                    )
                }
            }
        }
    }

    when (val dialogState = state.failureDialogState) {
        is FailureDialogState.Closed -> {}
        is FailureDialogState.Open -> {
            AlertDialog(
                onDismissRequest = onAcknowledgeFailureDialog,
                title = {
                    Text(
                        text = stringResource(id = R.string.error)
                    )
                },
                text = {
                    Text(
                        text = dialogState.errorText.asString()
                    )
                },
                confirmButton = {
                    onAcknowledgeFailureDialog()
                },
                dismissButton = {
                    onAcknowledgeFailureDialog()
                },
            )
        }
    }
}

@Composable
fun TickerListItem(
    modifier: Modifier = Modifier,
    ticker: Ticker
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = ticker.symbol,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(id = R.string.price_usd, ticker.price),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProgressBar() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenContentPreview() {
    SwissBorgAppTheme {
        MainScreenContent(
            state = MainViewModel.State(
                isLoading = true,
                tickers = persistentListOf(),
                error = null
            ),
            onSearchTextChanged = {},
            onAcknowledgeFailureDialog = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TickerListItemPreview() {
    SwissBorgAppTheme {
        TickerListItem(
            ticker = Ticker(
                symbol = "BTC",
                price = "19,200"
            )
        )
    }
}