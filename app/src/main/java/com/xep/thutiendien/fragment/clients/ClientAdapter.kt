package com.xep.thutiendien.fragment.clients

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xep.thutiendien.R
import com.xep.thutiendien.inflate
import com.xep.thutiendien.models.OrderModel

class ClientAdapter constructor(val data: MutableList<OrderModel> = arrayListOf()) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {
    var dathu = false
    var onLoadMoreListener: ((adapter: RecyclerView.Adapter<*>) -> Unit)? =
        null
    var isLoadMoreEnable = false
    var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {

        val view = parent.inflate(R.layout.item_client, false)
        return ClientViewHolder.MainViewHolder(view)


    }

    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val order = data[position]
        when (holder) {
            is ClientViewHolder.MainViewHolder -> {
                holder.clientAddressTv.text = "Địa chỉ : ${order.address}"
                holder.clientNameTv.text = "Tên: ${order.customerName}"
                holder.clientPhoneTv.text = "SDT: ${order.phoneNumber}"
                holder.clientAvatarTv.text = "${order.customerName[0]}"
            }
        }

        if (position == data.size - 1 && isLoadMoreEnable) {
            onLoadMoreListener?.invoke(this)
        }
    }

    sealed class ClientViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        class MainViewHolder(view: View) : ClientViewHolder(view) {
            val clientNameTv = view.findViewById<TextView>(R.id.item_client_name_tv)
            val clientPhoneTv = view.findViewById<TextView>(R.id.item_client_phone_tv)
            var clientAddressTv = view.findViewById<TextView>(R.id.item_client_address_tv)
            val clientAvatarTv = view.findViewById<TextView>(R.id.item_client_avatar_tv)
        }

        class EmptyViewHolder(view: View) : ClientViewHolder(view) {

        }
    }
}