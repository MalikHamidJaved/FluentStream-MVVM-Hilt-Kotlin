package com.candle.streams_player_mvvm.ui

import android.text.Editable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.repository.MainRepository
import com.candle.streams_player_mvvm.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel
@ViewModelInject
constructor(
    private val mainRepository: MainRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _dataState: MutableLiveData<DataState<List<Stream>>> = MutableLiveData()

    val dataState: LiveData<DataState<List<Stream>>>
        get() = _dataState

    fun setStateEvent(mainStateEvent: MainStateEvent) {
        viewModelScope.launch {
            when (mainStateEvent) {
                is MainStateEvent.GetStreamEvents -> {
                    mainRepository.getStream()
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
            mainRepository.getStreamFromLocal(text.toString())
                .onEach { dataState ->
                    _dataState.value = dataState
                }
                .launchIn(viewModelScope)
        }
    }
}


sealed class MainStateEvent {
    object GetStreamEvents : MainStateEvent()
    object None : MainStateEvent()
}

