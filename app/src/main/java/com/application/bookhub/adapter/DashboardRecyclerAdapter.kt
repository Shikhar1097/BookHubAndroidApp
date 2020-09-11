package com.application.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.application.bookhub.R
import com.application.bookhub.activity.DescriptionActivity
import com.application.bookhub.model.Book
import com.squareup.picasso.Picasso
import java.util.ArrayList

class DashboardRecyclerAdapter(val context: Context, val itemList: ArrayList<Book>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName: TextView = view.findViewById(R.id.txtRecyclerbookname)
        val txtBookAuthor: TextView = view.findViewById(R.id.txtRecyclerauthorname)
        val txtBookPrice: TextView = view.findViewById(R.id.txtRecyclerprice)
        val txtBookRating: TextView = view.findViewById(R.id.txtRecyclerstars)
        val imgBookImage: ImageView = view.findViewById(R.id.imgRecyclerbookcover)
        val rlContent: RelativeLayout = view.findViewById(R.id.rlContent)
    }

    // responsible for creation of viewHolders that would be recycled to show all the data, i.e.,
    // if there are total of 100 rows and only 10 are visible on screen at a time
    // so, this creates those 10 rows that are recycled to show the data when user scrolls.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(view)
    }

    // responsible to recycle and reuse of viewholders created in onCreateViewHolder
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val bookInfo = itemList[position]
        holder.txtBookName.text = bookInfo.bookName
        holder.txtBookAuthor.text = bookInfo.bookAuthor
        holder.txtBookPrice.text = bookInfo.bookPrice
        holder.txtBookRating.text = bookInfo.bookRating
        Picasso.get().load(bookInfo.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)

        holder.rlContent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id", bookInfo.bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}