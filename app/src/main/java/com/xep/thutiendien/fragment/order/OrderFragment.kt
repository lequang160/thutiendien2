package com.xep.thutiendien.fragment.order

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xep.thutiendien.*
import com.xep.thutiendien.activity.MainActivity
import com.xep.thutiendien.activity.PrinterActivity
import com.xep.thutiendien.base.BaseFragment
import com.xep.thutiendien.models.OrderModel
import java.text.SimpleDateFormat
import java.util.*


class OrderFragment : BaseFragment() {

    private lateinit var orderViewModel: OrderViewModel
    lateinit var mOrderRecyclerView: RecyclerView
    val mAdapter: OrderAdapter = OrderAdapter(item = null)
    lateinit var mOrderSelected: OrderModel
    var mOrderListTemp: MutableList<OrderModel> = arrayListOf()
    var mCurrentPage = 1
    var isLoadMore = false
    var isSearching = false

    lateinit var textViewBill: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        orderViewModel =
            ViewModelProviders.of(this).get(OrderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_order, container, false)
        orderViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).mSearchListener = { query ->
            isSearching = true
            orderViewModel.fetchOrder("", query).run { showLoading() }
            /*if (query.isEmpty()) {
                mAdapter.data.clear()
                mAdapter.data.addAll(mOrderListTemp)
                mAdapter.notifyDataSetChanged()
            } else {
                val temp = mOrderListTemp.filter { orderModel ->
                    orderModel.address.toUpperCase().contains(query.toUpperCase()) ||
                            orderModel.customerName.toUpperCase().contains(query.toUpperCase()) ||
                            orderModel.customerId.contains(query) ||
                            orderModel.phoneNumber.contains(query) ||
                            orderModel.transaction.contains(query)
                }
                mAdapter.data.clear()
                mAdapter.data.addAll(temp)
                mAdapter.notifyDataSetChanged()
            }*/
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOrderRecyclerView = view.findViewById(R.id.fragment_home_order_rv)
        textViewBill = view.findViewById(R.id.textView)

        val layoutManage = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mOrderRecyclerView.layoutManager = layoutManage
        mOrderRecyclerView.adapter = mAdapter
        mAdapter.isLoadMoreEnable = true

        subscribeData()

        orderViewModel.fetchOrder(mCurrentPage.toString()).run {
            showLoading()
        }

        actionAdapter()

        orderViewModel.fetchheroku()
    }

    fun subscribeData() {
        orderViewModel.orderLiveData.observe(viewLifecycleOwner, Observer {
            if (isSearching) {
                isSearching = false
                mAdapter.data.clear()
                mAdapter.data.addAll(it)
                mAdapter.notifyDataSetChanged()
                hideLoading()
                return@Observer
            }
            if (isLoadMore) {
                mAdapter.data.addAll(it)
                mAdapter.notifyDataSetChanged()
            } else {
                mAdapter.data.clear()
                mAdapter.data.addAll(it)
                mAdapter.notifyDataSetChanged()
            }
            if (it.isNullOrEmpty() || it.size < 10) {
                isLoadMore = false
                mAdapter.isLoadMoreEnable = false
            }
            mOrderListTemp.clear()
            mOrderListTemp.addAll(mAdapter.data)
            hideLoading()
        })

        orderViewModel.printLiveData.observe(viewLifecycleOwner, Observer {
            if (ElectricityApplication.mBluetoothSocket != null && ElectricityApplication.mBluetoothSocket!!.isConnected) {
                ElectricityApplication.print(textViewBill, mOrderSelected)
            } else {
                val intent = Intent(requireContext(), PrinterActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("data", mOrderSelected)
                intent.putExtras(bundle)
                startActivity(intent)
            }

        })

        orderViewModel.text.observe(viewLifecycleOwner, Observer {
            if(it == "Liên hệ để được hỗ trợ")
            {
                mAdapter.data.clear()
                mAdapter.notifyDataSetChanged()
                Toast.makeText(activity,it.toString(), Toast.LENGTH_SHORT).show()
            }else {
                if (mAdapter.data.size >= 10) {
                    for (i in 1..10) {
                        mAdapter.data.removeAt(mAdapter.data.size - 1)
                    }
                }
                orderViewModel.fetchOrder(mCurrentPage.toString())
            }
        })
    }

    fun actionAdapter() {

        mAdapter.mPaymentListener = { order ->

            val alertDialog: AlertDialog =
                android.app.AlertDialog.Builder(requireContext()) //set icon
                    .setTitle("Bạn có chắc chắn muốn thu tiền đơn này ?") //set message
                    .setMessage("") //set positive button
                    .setPositiveButton("Thu tiền",
                        DialogInterface.OnClickListener { dialogInterface, i -> //set what would happen when positive button is clicked
                            mOrderSelected = order
                            orderViewModel.updateOrder(order.id).run {
                                showLoading()
                            }
                            setupBillPreperPrint(mOrderSelected)
                        }) //set negative button
                    .setNegativeButton("Không",
                        DialogInterface.OnClickListener { dialogInterface, i -> //set what would happen when positive button is clicked

                        })
                    .show()

        }

        mAdapter.onLoadMoreListener = {
            if (mAdapter.data.size >= 10) {
                isLoadMore = true
                mCurrentPage += 1
                orderViewModel.fetchOrder(mCurrentPage.toString())
            }
        }

        mAdapter.mPrintListener = { order ->
            val intent = Intent(requireContext(), PrinterActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("data", order)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun setupBillPreperPrint(orderModel: OrderModel) {
        val BILL = """
            <p style="font-family: Arial, Helvetica, sans-serif">
<b>Biên nhận thanh toán tiền điện</b><br>
Kỳ thanh toán: ${orderModel.date}<br><br>

<b>Tên KH: ${orderModel.customerName}</b><br><br>

Địa chỉ: ${orderModel.address}<br>
Mã KH: ${orderModel.customerId}<br><br>

<b>TỔNG TIỀN: ${orderModel.amount} đ</b><br><br>
 
Ngày in: ${curentDate("dd/MM/yyyy")}<br>
Ngày thanh toán: ${curentDate()}<br>
HĐ điện tử: http://cskh.evnspc.vn<br>
Nơi thanh toán<br>
<b>Tên cửa hàng: Điểm thu Minh Hiền</b><br>
<b>SDT: 0382708876</b><br>
Địa chỉ: Khu Phước Hải<br>
Hotline: 1900 1006 - 1900 6906<br><br>

<b>ĐÃ THANH TOÁN</b><br><br>

Xác nhận của điểm giao dịch<br>
...........................<br>
...........................<br>
Cảm ơn quý khách và hen gặp lại! </p>
"""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textViewBill.text = Html.fromHtml(BILL, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textViewBill.text = Html.fromHtml(BILL);
        }
    }



    fun curentDate(parttern: String = "dd-MM-yyyy HH:mm:ss"): String {
        val dateFormat = SimpleDateFormat(parttern)
        return dateFormat.format(Date())
    }

    override fun onDetach() {
        super.onDetach()
        mOrderListTemp = arrayListOf()

    }

    /*public fun removeAccents(str: String): String {
        val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        val str1 =  pattern.matcher(nfdNormalizedString).replaceAll("")
        return str1
    }*/

}
