package com.example.kindle.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kindle.R
import com.example.kindle.database.BookEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter (val context: Context, val bookList: List<BookEntity>): RecyclerView.Adapter<FavouriteRecyclerAdapter.FavViewHolder>(){
    class FavViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtBookName : TextView = view.findViewById(R.id.favbookTitle)
        val txtbookAuthor : TextView =view.findViewById(R.id.favbookAuthor)
        val txtbookPrice : TextView =view.findViewById(R.id.favbookPrice)
        val txtbookRating : TextView =view.findViewById(R.id.favbookrating)
        val favimage : ImageView = view.findViewById(R.id.imgFavBookImage)
       // val  lifavcontent: LinearLayout =view.findViewById(R.id.lifavcontent)
    }
    companion object {
        var isCartEmpty = true
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recyclerfavourite,parent,false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val book = bookList[position]
        holder.txtBookName.text=book.bookName
        holder.txtbookAuthor.text=book.bookAuthor
        holder.txtbookPrice.text=book.bookPrice
        holder.txtbookRating.text= book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.defaultbook).into(holder.favimage)


    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}