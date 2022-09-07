package com.candle.streams_player_mvvm.ui

import android.text.Editable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.repository.StreamRepository
import com.candle.streams_player_mvvm.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel
@ViewModelInject
constructor(
    private val streamRepository: StreamRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var selectedAdapterPosition: Int = -1
    private val _dataState: MutableLiveData<DataState<List<Stream>>> = MutableLiveData()
    private val playingStream: MutableLiveData<Stream> = MutableLiveData()

    val dataState: LiveData<DataState<List<Stream>>>
        get() = _dataState

    fun setStateEvent(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {
                is MainStateEvent.GetStreamEvents -> {
                    streamRepository.getStream()
                        .onEach { dataState ->
                            _dataState.value = dataState
                        }
                        .launchIn(viewModelScope)
                }

                is MainStateEvent.None -> {
                    // No action
                }
            }
        }
    }

    fun filterData(text: Editable?) {
        viewModelScope.launch {
            streamRepository.getStreamFromLocal(text.toString())
                .onEach { dataState ->
                    _dataState.value = dataState
                }
                .launchIn(viewModelScope)
        }
    }

    fun setPlayingItem(itemAt: Stream) {
        playingStream.value = itemAt
    }
}


sealed class MainStateEvent {
    object GetStreamEvents : MainStateEvent()
    object None : MainStateEvent()
}

