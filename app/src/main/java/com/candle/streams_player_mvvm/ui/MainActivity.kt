package com.candle.streams_player_mvvm.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.util.DataState
import com.candle.streams_player_mvvm.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.IOException


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), StreamAdapter.StreamItemListener {
    private var mp: MediaPlayer? = MediaPlayer()
    private var isPLAYING: Boolean = false
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: StreamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        subscribeObservers()
        viewModel.setStateEvent(MainStateEvent.GetStreamEvents)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.setStateEvent(MainStateEvent.GetStreamEvents)
        }

        et_search.doAfterTextChanged {
            viewModel.filterData(et_search.text)
        }

    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.Success<List<Stream>> -> {
                    displayLoading(false)
                    populateRecyclerView(dataState.data)
                }
                is DataState.Loading -> {
                    displayLoading(true)
                }
                is DataState.Error -> {
                    displayLoading(false)
                    displayError(dataState.exception.message)
                }
            }
        })
    }


    private fun displayError(message: String?) {
        if (message.isNullOrEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

    private fun displayLoading(isLoading: Boolean) {
        swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun populateRecyclerView(streams: List<Stream>) {
        if (streams.isNotEmpty()) adapter.setItems(ArrayList(streams))
    }

    private fun setupRecyclerView() {
        adapter = StreamAdapter(this)
        blog_recyclerview.layoutManager = LinearLayoutManager(this)
        blog_recyclerview.adapter = adapter
    }

    override fun onStreamClicked(sentByTitle: CharSequence) {
        Toast.makeText(this, sentByTitle, Toast.LENGTH_SHORT).show()
    }

    override fun onPlayPauseClick(adapterPosition: Int) {

        if (!isPLAYING) {
            isPLAYING = true
             mp = MediaPlayer()
            try {
                mp!!.setDataSource(AppConstants.BASE_URL + adapter.getItemAt(adapterPosition).recording)
                mp!!.prepare()
                mp!!.start()
            } catch (e: IOException) {
                Log.d(MainActivity::class.simpleName,e.message)
            }
        } else {
            isPLAYING = false
            stopPlaying()
        }
    }

    private fun stopPlaying() {
        mp!!.release()
        mp = null
    }

}