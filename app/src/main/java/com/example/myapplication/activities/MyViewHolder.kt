package com.example.myapplication

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder : RecyclerView.ViewHolder {

    val imageView: ImageView

    val nameView: TextView

    val emailView: TextView

    constructor(itemView: View) : super(itemView) {
        imageView = itemView.findViewById(R.id.imageview)
        nameView = itemView.findViewById(R.id.name)
        emailView = itemView.findViewById(R.id.email)
    }
}