package com.example.hi_hello_2

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.hi_hello_2.models.Inbox
import com.example.hi_hello_2.utlis.formatAsListItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Inbox, onClick: (name: String, photo: String, id: String) -> Unit) =
            with(itemView) {
                countTv.isVisible = item.count > 0
                countTv.text = item.count.toString()
                timeTv.text = item.time.formatAsListItem(context)

                titleTv.text = item.name
                subTitleTv.text = item.msg
                Picasso.get()
                        .load(item.image)
                        .placeholder(R.drawable.defaultavatar)
                        .error(R.drawable.defaultavatar)
                        .into(userImgView)
                setOnClickListener {
                    onClick.invoke(item.name, item.image, item.from)
                }
            }
}