package com.example.fishdatfinal.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fishdat.data.AppData
import com.example.fishdat.data.FishDataItem
import com.example.fishdatfinal.R

class FishDataListAdapter(): RecyclerView.Adapter<FishDataListAdapter.FishDataListViewHolder>()
{
    class FishDataListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    var onItemChecked: (() -> Unit)? = null
    var onListModified: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishDataListViewHolder {
        //Nastavenie funkcie pre notifikaciu o zmene v zozname
        AppData.Data.FishData.onListModified = {
            notifyDataSetChanged()
            onListModified?.invoke()
        }

        AppData.Data.FishData.onItemAdded = {
            notifyItemInserted(AppData.Data.FishData.GetItemCount() - 1)
        }

        return FishDataListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fish_list_item,
                parent,
                false
            )
        )
    }


    private fun OnFishDataItemChecked( isChecked:Boolean) {
        onItemChecked?.invoke()
    }
    override fun onBindViewHolder(holder: FishDataListViewHolder, position: Int) {
        val currentItem = AppData.Data.FishData.Items[position]

        val revirNumber: TextView = holder.itemView.findViewById<TextView>(R.id.tvRevirNumber)
        val fishName: TextView = holder.itemView.findViewById<TextView>(R.id.tvFishName)
        val fishLength: TextView = holder.itemView.findViewById<TextView>(R.id.tvFishLength)
        val fishWeight: TextView = holder.itemView.findViewById<TextView>(R.id.tvFishWeight)
        val selected: CheckBox = holder.itemView.findViewById<CheckBox>(R.id.chbSelected)

        revirNumber.text = currentItem.RevirNumber
        fishName.text = currentItem.FishName
        fishLength.text = currentItem.FishLength.toString() + " cm"
        fishWeight.text = currentItem.FishWeight.toString() + " kg"
        selected.isChecked = currentItem.IsSelected


        OnFishDataItemChecked(currentItem.IsSelected)

        selected.setOnCheckedChangeListener {_, isChecked ->
            currentItem.IsSelected = isChecked
            OnFishDataItemChecked(isChecked)
        }
    }

    override fun getItemCount(): Int {
        return AppData.Data.FishData.GetItemCount()
    }

    fun AddItem(item: FishDataItem){
        AppData.Data.FishData.Items.add(item);
        notifyItemInserted(AppData.Data.FishData.GetItemCount() - 1);
    }

}
