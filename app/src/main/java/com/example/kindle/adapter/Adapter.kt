package com.example.kindle.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kindle.R
import com.example.kindle.activity.DescriptionActivity
import com.example.kindle.model.Book
import com.squareup.picasso.Picasso

class DashboardAdapter(val context: Context, val itemList: ArrayList<Book>) : RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recyclerdashboard,parent,false)
        return DashboardViewHolder(view)
    }
    override fun getItemCount(): Int {

        return itemList.size
    }
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
          val book = itemList[position]
        holder.txtBookname.text=book.bookName
        holder.txtBookAuthor.text=book.bookAuthor
        holder.txtBookPrice.text=book.bookPrice
        holder.txtBookRating.text=book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.defaultbook).into(holder.bookimage)
//to go to description activity bind the holder
        holder.liContent.setOnClickListener{
           val intent = Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }
    }

    class DashboardViewHolder(view:View) : RecyclerView.ViewHolder(view){
       val txtBookname : TextView =  view.findViewById(R.id.textBookname)
        val txtBookAuthor : TextView =  view.findViewById(R.id.textAuthorname)
        val txtBookPrice: TextView =  view.findViewById(R.id.textPrice)
        val txtBookRating : TextView =  view.findViewById(R.id.Bookrating)
        val bookimage : ImageView =  view.findViewById(R.id.bookImage)
        val liContent : LinearLayout = view.findViewById(R.id.liContent)
    }
}