package com.xep.thutiendien.fragment.order

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.xep.thutiendien.R
import com.xep.thutiendien.inflate
import com.xep.thutiendien.models.OrderModel

class OrderAdapter constructor(val data: MutableList<OrderModel> = arrayListOf(), @LayoutRes val item: Int?) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    var dathu = false
    var mPrintListener: ((order: OrderModel) -> Unit)? = null
    var mPaymentListener: ((order: OrderModel) -> Unit)? = null

    var isLoadMoreEnable = false
    var mContext: Context? = null
    var onLoadMoreListener: ((adapter: RecyclerView.Adapter<*>) -> Unit)? =
        null
    var onItemClickListener: ((adapter: RecyclerView.Adapter<*>, view: View, position: Int) -> Unit)? =
        null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        if (item != null) {
            val view = parent.inflate(item, false)
            return OrderViewHolder.MainViewHolder(view)
        } else {
            val view = parent.inflate(R.layout.item_order, false)
            return OrderViewHolder.MainViewHolder(view)
        }

    }

    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = data[position]
        when (holder) {
            is OrderViewHolder.MainViewHolder -> {
                holder.view.setOnClickListener {
                    onItemClickListener?.invoke(this, it, position )
                }
                holder.orderNo.text = "${order.customerId} / ${order.transaction}"
                holder.address.text = order.address
                holder.amountTv.text = "${order.amount} VND"
                holder.customerName.text = "${order.customerName} - ${order.phoneNumber}"
                holder.noteTv.text = order.note
                holder.status.text = order.status
                when(order.status){
                    "ĐÃ THU" -> holder.paymentBtn.text = "Hủy"
                    "HỦY" -> holder.paymentBtn.text = "Phục hồi"
                    "CHƯA THU" -> holder.paymentBtn.text = "Thu tiền"
                }
                /*if(order.status == "ĐÃ THU") {
                    holder.paymentBtn.text = "Hủy"
                    //holder.paymentBtn.setTextColor(Color.GRAY)
                }else{
                    holder.paymentBtn.isEnabled = true
                    holder.paymentBtn.setTextColor(Color.parseColor("#FB9300"))
                }*/

                holder.paymentBtn.setOnClickListener {
                    mPaymentListener?.invoke(order)
                }
                holder.printBtn.setOnClickListener {
                    mPrintListener?.invoke(order)
                }
            }
        }

        if (position == data.size - 1 && isLoadMoreEnable) {
            onLoadMoreListener?.invoke(this)
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (this.mContext == null) {
            mContext = recyclerView.context
        }

    }

    sealed class OrderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        class MainViewHolder(val view: View) : OrderViewHolder(view) {
            val status = view.findViewById<TextView>(R.id.dathu)
            val orderNo = view.findViewById<TextView>(R.id.item_order_no)
            var customerName = view.findViewById<TextView>(R.id.item_order_customer_name_tv)
            val address = view.findViewById<TextView>(R.id.item_order_address_tv)
            val amountTv = view.findViewById<TextView>(R.id.item_order_amount_tv)
            val noteTv = view.findViewById<TextView>(R.id.item_order_note_tv)
            val paymentBtn = view.findViewById<Button>(R.id.item_order_payment_btn)
            val printBtn = view.findViewById<Button>(R.id.item_order_print_btn)
        }

        class EmptyViewHolder(view: View) : OrderViewHolder(view) {

        }
    }
}