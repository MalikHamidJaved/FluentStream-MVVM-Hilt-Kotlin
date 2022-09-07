package com.candle.streams_player_mvvm.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        fun onStreamClicked(blogTitle: CharSequence)
        fun onPlayPauseClick(adapterPosition: Int)
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
        holder.textTitle.text = stream.username_from
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
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener {

    val itemLayout: ConstraintLayout = itemView.stream_layout
    val textTitle: TextView = itemView.text_title
    val textDescription: TextView = itemView.text_description
    val image: AppCompatImageView = itemView.image

    init {
        itemLayout.setOnClickListener(this)
        itemLayout.setOnClickListener({
            listener.onPlayPauseClick(adapterPosition)
        })
    }

    override fun onClick(v: View?) {
        listener.onStreamClicked(textTitle.text)
    }
}

