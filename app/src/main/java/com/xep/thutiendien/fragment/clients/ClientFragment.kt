package com.xep.thutiendien.fragment.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xep.thutiendien.activity.MainActivity
import com.xep.thutiendien.R
import com.xep.thutiendien.base.BaseFragment
import com.xep.thutiendien.models.OrderModel

class ClientFragment : BaseFragment() {

    private lateinit var clientViewModel: ClientViewModel
    lateinit var mOrderRecyclerView: RecyclerView
    val mAdapter: ClientAdapter = ClientAdapter()
     var mOrderListTemp: MutableList<OrderModel> = arrayListOf()
    var mCurrentPage = 1
    var isLoadMore = false
    var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        clientViewModel =
            ViewModelProviders.of(this).get(ClientViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_client, container, false)

        clientViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).mSearchListener = { query ->
            isSearching = true
            clientViewModel.fetchCustomer("", query).run {
                showLoading()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mOrderRecyclerView = view.findViewById(R.id.fragment_home_order_rv)
        val layoutManage = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mOrderRecyclerView.layoutManager = layoutManage
        mOrderRecyclerView.adapter = mAdapter
        mAdapter.isLoadMoreEnable = true


        clientViewModel.orderLiveData.observe(viewLifecycleOwner, Observer {
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
        clientViewModel.fetchCustomer(mCurrentPage.toString(), "")

        mAdapter.onLoadMoreListener = {
            if (mAdapter.data.size >= 10) {
                isLoadMore = true
                mCurrentPage += 1
                clientViewModel.fetchCustomer(mCurrentPage.toString(), "")
            }
        }
    }

    override fun onDetach() {
        super.onDetach()

            mOrderListTemp = arrayListOf()

    }
}
