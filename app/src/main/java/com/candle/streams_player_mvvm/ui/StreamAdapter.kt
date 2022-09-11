package com.candle.streams_player_mvvm.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.candle.streams_player_mvvm.R
import com.candle.streams_player_mvvm.model.Stream
import com.candle.streams_player_mvvm.util.DateUtils
import kotlinx.android.synthetic.main.item_stream.view.*
import kotlin.collections.ArrayList

class StreamAdapter(private val listener: StreamItemListener) : RecyclerView.Adapter<StreamViewHolder>() {

    interface StreamItemListener {
        fun onPlayPauseClick(adapterPosition: Int,seekbar: SeekBar)
    }

    private val items = ArrayList<Stream>()
    private lateinit var stream: Stream

    fun setItems(items: ArrayList<Stream>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stream, parent, false)
        return StreamViewHolder(view, listener)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: StreamViewHolder, position: Int) {
        stream = items[position]
        val stream = items[position]
        holder.text_title.text

        holder.text_title.text = "Sender: "+ if(stream.username_from.isNullOrEmpty()) {
            "NA"
        }else{
            stream.username_from
        }
        holder.text_receiver.text = "Receiver: "+if(stream.username_to.isNullOrEmpty()) {
            "NA"
        }else{
            stream.username_to
        }

        if(stream.isPlaying){
            holder.ivPlayPause.setImageResource(R.drawable.ic_pause)
            holder.seekBar.visibility = View.VISIBLE
            holder.text_title.visibility = View.INVISIBLE
            holder.text_receiver.visibility = View.INVISIBLE
            try{
                holder.seekBar.postDelayed({
                    holder.seekBar.setProgress(40,true)
                    holder.seekBar.postDelayed({
                        holder.seekBar.setProgress(( 70),true)
                        holder.seekBar.postDelayed({
                            holder.seekBar.setProgress(80,true)

                        },2000)

                    },2000)
                },1000)
            }catch (e:Exception){

            }

        }else{

            holder.ivPlayPause.setImageResource(R.drawable.ic_play)
            holder.seekBar.visibility = View.INVISIBLE
            holder.text_title.visibility = View.VISIBLE
            holder.text_receiver.visibility = View.VISIBLE

        }
        if(stream != null)
        stream.timestamp.let {
            if(it != null)
            DateUtils.getDateTime(it).let {
                holder.textDescription.text = it
            }
        }


    }




    fun getItemAt(adapterPosition: Int): Stream {
        return items[adapterPosition]
    }
}

class StreamViewHolder(itemView: View, private val listener: StreamAdapter.StreamItemListener) :
    RecyclerView.ViewHolder(itemView) {

    val itemLayout: ConstraintLayout = itemView.stream_layout
    val seekBar: SeekBar = itemView.seekbar
    val textDescription: TextView = itemView.text_description
    val text_title: TextView = itemView.text_title
    val text_receiver: TextView = itemView.text_receiver
    val ivPlayPause: AppCompatImageView = itemView.playPause

    init {
        itemLayout.setOnClickListener {
            listener.onPlayPauseClick(adapterPosition, seekBar)
        }
    }


}

