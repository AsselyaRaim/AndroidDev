package com.example.cryptotracker.fragments.currencies.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptotracker.R
import com.example.cryptotracker.data.api.ApiService
import com.example.cryptotracker.data.models.currency.Currency
import com.example.cryptotracker.fragments.currencies.details.CurrencyDetailsFragmentArgs
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CurrencyListFragment : Fragment(), CurrencyListAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = CurrencyListFragment()
    }

    private lateinit var viewModel: CurrencyListViewModel
    private lateinit var currencyArray: Array<Currency>
    private val args: CurrencyListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val apiService = ApiService()
        val view: View = inflater.inflate(R.layout.currency_list_fragment, container, false)
//        Log.d("ID of logged user: ", args.id.toString())

        findNavController().graph.findNode(R.id.portfolio)
            ?.addArgument("id", NavArgument.Builder()
                .setDefaultValue(args.userId).build())


        apiService.getCurrencyList().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val rawString = response.body()?.string()
                val rawJson = JSONObject(rawString)
                val rawDataNode = rawJson.get("data").toString()
                val gson = Gson()
                currencyArray = gson.fromJson(rawDataNode, Array<Currency>::class.java)

                createRecyclerView(view)
            }
        })

        return view
    }

    //Creating recyclerView and passing it currencyArray
    fun createRecyclerView(view: View) {
        val recyclerView: RecyclerView =  view.findViewById(R.id.recyclerView)
        val adapter = CurrencyListAdapter(currencyArray, this)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        val clickedItem = currencyArray[position]
        val clickedItemId = clickedItem.id
        view?.let { showCurrencyDetail(it, clickedItemId) }
    }

    private fun showCurrencyDetail(view: View, id: Int) {
        val actionDetail = CurrencyListFragmentDirections.actionCurrencyListToCurrencyDetailsFragment(id, args.userId)
        Navigation.findNavController(view).navigate(actionDetail)
    }

}