package com.sofka.sofkachallenge.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sofka.sofkachallenge.R
import com.sofka.sofkachallenge.adapters.AdapterListUsers.ViewHolder
import com.sofka.sofkachallenge.models.modelUsersFinished

class AdapterListUsers(val mItems: ArrayList<modelUsersFinished>, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_list_users, parent, false)
        )

    override fun getItemCount() = mItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(mItems[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItems(item: modelUsersFinished) {

            if (item.level == "5")
                itemView.findViewById<ImageView>(R.id.card_list_start).visibility = View.VISIBLE

            itemView.findViewById<TextView>(R.id.card_list_tv_name).text = item.name
            itemView.findViewById<TextView>(R.id.card_list_tv_max_score).text =
                "${context.getString(R.string.score_max_user)}: ${item.score}"
            itemView.findViewById<TextView>(R.id.card_list_tv_max_level).text =
                "${context.getString(R.string.level_max_user)}: ${item.level}"

            itemView.findViewById<TextView>(R.id.card_list_tv_max_date).text = item.date
        }
    }
}