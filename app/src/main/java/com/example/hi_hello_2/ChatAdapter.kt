package com.example.hi_hello_2

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.hi_hello_2.R
import com.example.hi_hello_2.models.ChatEvent
import com.example.hi_hello_2.models.DateHeader
import com.example.hi_hello_2.models.Message
import com.example.hi_hello_2.utlis.formatAsTime
import kotlinx.android.synthetic.main.list_item_chat_recv_message.view.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*

class ChatAdapter(private val list: MutableList<ChatEvent>, private val mCurrentUser: String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = { layout: Int ->
            LayoutInflater.from(parent.context)
                    .inflate(layout, parent, false)
        }

        return when (viewType) {
            TEXT_MESSAGE_RECEIVED -> {
                MessageHolder(
                        inflate(R.layout.list_item_chat_recv_message)
                )
            }
            TEXT_MESSAGE_SENT -> {
                MessageHolder(
                        inflate(R.layout.list_item_chat_sent_message)
                )
            }
            DATE_HEADER -> {
                DateHeaderHolder(
                        inflate(R.layout.list_item_date_header)
                )
            }
            else -> {
                MessageHolder(
                        inflate(R.layout.list_item_chat_recv_message)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = list[position]) {
            is DateHeader -> {
                holder.itemView.DatetextView.text = item.date
            }
            is Message -> {
                holder.itemView.content.text = item.msg
                holder.itemView.time.text = item.sentAt.formatAsTime()


                /*when (getItemViewType(position)) {
                    TEXT_MESSAGE_RECEIVED -> {
                        holder.itemView.messageCardView.setOnClickListener(object :
                                DoubleClickListener() {
                            override fun onDoubleClick(v: View?) {
                                highFiveClick?.invoke(item.msgId, !item.liked)
                            }
                        })
                        holder.itemView.highFiveImg.apply {
                            isVisible = position == itemCount - 1 || item.liked
                            isSelected = item.liked
                            setOnClickListener {
                                highFiveClick?.invoke(item.msgId, !isSelected)
                            }
                        }
                    }

                    TEXT_MESSAGE_SENT -> {
                        holder.itemView.highFiveImg.apply {
                            isVisible = item.liked
                        }
                    }
                }*/
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (val event = list[position]) {
            is Message -> {
                if (event.senderId == mCurrentUser) {
                    TEXT_MESSAGE_SENT
                } else {
                    TEXT_MESSAGE_RECEIVED
                }
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }

    class DateHeaderHolder(view: View) : RecyclerView.ViewHolder(view)

    class MessageHolder(view: View) : RecyclerView.ViewHolder(view)


    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }

}