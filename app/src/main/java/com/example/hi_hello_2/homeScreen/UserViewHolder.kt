package com.example.hi_hello_2.homeScreen

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.hi_hello_2.R
import com.example.hi_hello_2.models.User
import com.squareup.picasso.Picasso
//import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(user: User, onClick: (name: String, photo: String, id: String)->Unit)= with(itemView){

        countTv.isVisible=false;
        timeTv.isVisible=false

        titleTv.text=user.name
        subTitleTv.text=user.status

        Picasso.get()
            .load(user.thumbImage)
            .placeholder(R.drawable.defaultavatar)
            .error(R.drawable.defaultavatar)
            .into(userImgView)

        setOnClickListener{
            onClick.invoke(user.name, user.thumbImage, user.uid)
        }
    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}