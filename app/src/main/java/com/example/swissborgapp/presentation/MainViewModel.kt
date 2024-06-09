package com.example.swissborgapp.presentation

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swissborgapp.domain.ConnectivityObserver
import com.example.swissborgapp.domain.Status
import com.example.swissborgapp.domain.model.Ticker
import com.example.swissborgapp.domain.use_case.GetPublicTickersUseCase
import com.example.swissborgapp.presentation.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getPublicTickersUseCase: GetPublicTickersUseCase,
    connectivityObserver: ConnectivityObserver
): ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getPublicTickersUseCase(),
                connectivityObserver.observe(),
            ) { result, connectivityStatus ->
                if (result.isSuccess) {
                    State(
                        isConnected = connectivityStatus == Status.Available,
                        isLoading = false,
                        searchText = state.value.searchText,
                        tickers = result.getOrNull()?.toPersistentList() ?: persistentListOf()
                    )
                } else {
                    State(
                        isConnected = connectivityStatus == Status.Available,
                        isLoading = false,
                        searchText = state.value.searchText,
                        error = result.exceptionOrNull()?.message
                    )
                }
            }.collect { state ->
                _state.update {
                    state
                }
            }
        }
    }

    fun onSearchTextChanged(searchText: String) {
        _state.update {
            it.copy(
                searchText = searchText
            )
        }
    }

    fun onAcknowledgeFailureDialog() {
        _state.update {
            it.copy(
                error = null
            )
        }
    }

    @Parcelize
    data class State(
        val isConnected: Boolean = true,
        val isLoading: Boolean = true,
        private val tickers: List<Ticker> = emptyList(),
        val searchText: String = "",
        private val error: String? = null
    ): Parcelable {
        val publicTickers: ImmutableList<Ticker>
            get() = if (searchText.isNotBlank()) {
                tickers.filter {
                    it.doesMatchQuery(searchText)
                }.toPersistentList()
            } else tickers.toPersistentList()

        val failureDialogState: FailureDialogState
            get() = if (error == null) {
                FailureDialogState.Closed
            } else {
                FailureDialogState.Open(
                    error.let { message ->
                        UiText.DynamicString(message)
                    }
                )
            }
    }
}

sealed interface FailureDialogState {
    data object Closed : FailureDialogState
    data class Open(val errorText: UiText) : FailureDialogState
}