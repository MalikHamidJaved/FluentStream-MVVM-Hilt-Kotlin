package com.candle.streams_player_mvvm.ui

import android.app.ProgressDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.io.IOException


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), StreamAdapter.StreamItemListener {
    private var maxSeekValue: Int = 100
    private lateinit var mSeekBar:SeekBar
    private lateinit var dialog: ProgressDialog
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
            viewModel.selectedAdapterPosition = -1
            stopPlaying()
            viewModel.setStateEvent(MainStateEvent.GetStreamEvents)
        }

        et_search.doAfterTextChanged {
            viewModel.selectedAdapterPosition = -1
            stopPlaying()
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
        if(adapter.itemCount>0) {
            swipeRefreshLayout.isRefreshing = isLoading
        }else{
            displayLoadingDialog(isLoading)
        }
    }
    private fun displayLoadingDialog(isLoading: Boolean) {
        if(!this::dialog.isInitialized) {
            dialog = ProgressDialog( this)
            dialog.setTitle("Downloading Data")
        }
        if(isLoading){
            if(!dialog.isShowing){
                dialog.show()
            }
        }else{
            if(dialog.isShowing){
                dialog.dismiss()
            }
        }

    }

    private fun populateRecyclerView(streams: List<Stream>) {
        if (streams.isNotEmpty()) adapter.setItems(ArrayList(streams))
    }

    private fun setupRecyclerView() {
        adapter = StreamAdapter(this)
        blog_recyclerview.layoutManager = LinearLayoutManager(this)
        blog_recyclerview.adapter = adapter
    }


    override fun onPlayPauseClick(adapterPosition: Int,seekBar:SeekBar) {

        CoroutineScope(Dispatchers.Main).launch {
            if(adapter.getItemAt(adapterPosition).recording.isNullOrEmpty()){
                displayError("Invalid Data")

            }else {
                if (adapterPosition != viewModel.selectedAdapterPosition
                    && viewModel.selectedAdapterPosition != -1
                ) {
                    adapter.getItemAt(viewModel.selectedAdapterPosition).isPlaying = false
                    stopPlaying()
                }

                adapter.getItemAt(adapterPosition).isPlaying =
                    !adapter.getItemAt(adapterPosition).isPlaying
                viewModel.setPlayingItem(adapter.getItemAt(adapterPosition))
                viewModel.selectedAdapterPosition = adapterPosition

                adapter.notifyDataSetChanged()

                if (!isPLAYING) {
                    isPLAYING = true
                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(AppConstants.BASE_URL + adapter.getItemAt(adapterPosition).recording)
                        mp!!.prepare()
                        mp!!.start()
                        mp!!.setOnCompletionListener {
                            stopPlaying()
                            adapter.getItemAt(viewModel.selectedAdapterPosition).isPlaying = false
                            viewModel.selectedAdapterPosition = -1
                        }
                    } catch (e: IOException) {
                        Log.d(MainActivity::class.simpleName, e.message)
                    }
                } else {
                    isPLAYING = false
                    stopPlaying()
                }
            }
        }

    }



    private fun stopPlaying() {
        if(mp != null)
        mp!!.release()
        mp = null
        isPLAYING = false

    }

}