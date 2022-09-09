package com.candle.streams_player_mvvm.ui

import android.app.ProgressDialog
import android.media.MediaPlayer
import android.os.Bundle
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
import com.candle.streams_player_mvvm.model.LoggedInUser
import com.candle.streams_player_mvvm.util.PreferenceHelper
import com.candle.streams_player_mvvm.util.PreferenceHelper.isAdmin
import com.candle.streams_player_mvvm.util.PreferenceHelper.user
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
    private lateinit var userData: LoggedInUser
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
        userData = LoggedInUser(PreferenceHelper.customPreference(this).user!!
            ,PreferenceHelper.customPreference(this).isAdmin)
        viewModel.setStateEvent(MainStateEvent.GetStreamEvents,userData)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.selectedAdapterPosition = -1
            stopPlaying()
            viewModel.setStateEvent(MainStateEvent.GetStreamEvents,userData)
        }

        et_search.doAfterTextChanged {
            viewModel.selectedAdapterPosition = -1
            stopPlaying()
            viewModel.filterData(et_search.text,userData)
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
            swipeRefreshLayout.isRefreshing = false
            displayLoadingDialog(isLoading)
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

        CoroutineScope(Dispatchers.IO).launch {
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

                viewModel.selectedAdapterPosition = adapterPosition


                if (!isPLAYING) {
                    isPLAYING = true
                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(AppConstants.BASE_URL + adapter.getItemAt(adapterPosition).recording)
                        mp!!.prepare()
                        mp!!.start()
                        mp!!.setOnCompletionListener {
                            CoroutineScope(Dispatchers.Main).launch {
                                stopPlaying()
                                adapter.getItemAt(viewModel.selectedAdapterPosition).isPlaying =
                                    false
                                viewModel.selectedAdapterPosition = -1
                            }
                        }

                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.setPlayingItem(adapter.getItemAt(adapterPosition))
                            adapter.notifyDataSetChanged()

                        }


                    } catch (e: IOException) {
                        isPLAYING = false
                        Log.d(MainActivity::class.simpleName, e.message)
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        stopPlaying()
                    }
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