package com.xep.thutiendien.fragment

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
import com.xep.thutiendien.ElectricityApplication
import com.xep.thutiendien.R
import com.xep.thutiendien.activity.MainActivity
import com.xep.thutiendien.activity.PrinterActivity
import com.xep.thutiendien.base.BaseFragment
import com.xep.thutiendien.fragment.order.OrderAdapter
import com.xep.thutiendien.models.OrderModel
import java.text.SimpleDateFormat
import java.util.*

class OrderCancelFragment : BaseFragment() {
    private lateinit var textViewBill: TextView
    lateinit var mOrderRecyclerView: RecyclerView
    val mAdapter: OrderAdapter = OrderAdapter(item = null)
    var mOrderListTemp: MutableList<OrderModel> = arrayListOf()
    var mCurrentPage = 1
    var isLoadMore = false
    var isSearching = false
    lateinit var mNumberOrderPaidTv: TextView
    lateinit var mMoneyPaidTv: TextView

    companion object {
        fun newInstance() = OrderCancelFragment()
    }

    private lateinit var viewModel: OrderCancelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(OrderCancelViewModel::class.java)
        return inflater.inflate(R.layout.order_cancel_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        (activity as MainActivity).mSearchListener = { query ->
            isSearching = true
            viewModel.fetchOrderCancel("", query).run { showLoading() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOrderRecyclerView = view.findViewById(R.id.fragment_home_order_rv)
        val layoutManage = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mOrderRecyclerView.layoutManager = layoutManage
        mAdapter.dathu = true
        mAdapter.isLoadMoreEnable = true
        mOrderRecyclerView.adapter = mAdapter

        mMoneyPaidTv = view.findViewById(R.id.paid_amount_tv)
        mNumberOrderPaidTv = view.findViewById(R.id.paid_number_tv)
        textViewBill = view.findViewById(R.id.textView)

        viewModel.fetchOrderCancel(mCurrentPage.toString(), "")
        viewModel.fetchSumMoney()
        viewModel.fetchNumberOrder()




        viewModel.moneyLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.length > 4) {
                val amount = it.toDouble()
                val s = String.format("%,.0f", amount)
                mMoneyPaidTv.text = "${s} VND"
            } else {
                mMoneyPaidTv.text = "$it VND"
            }
        })

        viewModel.numberOrderLiveData.observe(viewLifecycleOwner, Observer {
            mNumberOrderPaidTv.text = "$it đơn"
        })

        viewModel.text.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()

            mCurrentPage = 1
            mAdapter.data.clear()

            viewModel.fetchOrderCancel(mCurrentPage.toString(), "")
            viewModel.fetchSumMoney()
            viewModel.fetchNumberOrder()
        })
        viewModel.orderLiveData.observe(viewLifecycleOwner, Observer {
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



        mAdapter.onLoadMoreListener = {
            if (mAdapter.data.size >= 10) {
                isLoadMore = true
                mCurrentPage += 1
                viewModel.fetchOrderCancel(mCurrentPage.toString(), "")
            }
        }

        mAdapter.mPrintListener = { order ->
            setupBillPreperPrint(order)
            if (ElectricityApplication.mBluetoothSocket != null && ElectricityApplication.mBluetoothSocket!!.isConnected) {
                ElectricityApplication.print(textViewBill, order)
            } else {
                val intent = Intent(requireContext(), PrinterActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("data", order)
                intent.putExtras(bundle)
                startActivity(intent)
            }

        }

        mAdapter.mPaymentListener = { order ->

            val alertDialog: AlertDialog =
                android.app.AlertDialog.Builder(requireContext()) //set icon
                    .setTitle("Bạn muốn cập nhật đơn hàng này về Chưa Thu ?") //set message
                    .setMessage("") //set positive button
                    .setPositiveButton("Cập nhật",
                        DialogInterface.OnClickListener { dialogInterface, i -> //set what would happen when positive button is clicked
                            viewModel.updateOrder(order.id).run {
                                showLoading()
                            }
                        }) //set negative button
                    .setNegativeButton("Không",
                        DialogInterface.OnClickListener { dialogInterface, i -> //set what would happen when positive button is clicked

                        })
                    .show()

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

}
