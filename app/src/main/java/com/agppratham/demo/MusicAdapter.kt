package com.agppratham.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*

class MusicAdapter(var context: Context, var list: ArrayList<MusicList>, var onClick: OnClick) : RecyclerView.Adapter<MusicAdapter.viewVH>() {

    class viewVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewVH {
        var view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false)
        return viewVH(view)
    }

    override fun onBindViewHolder(holder: viewVH, position: Int) {
        holder.itemView.txtName.setText(list.get(position).title)

        holder.itemView.setOnClickListener {
            onClick.onClick(list.get(position).path)
            holder.itemView.txtName.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClick{
        fun onClick(text : String)
    }
}