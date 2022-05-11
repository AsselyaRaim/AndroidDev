package com.example.cryptotracker.fragments.portfolio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgument
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgument
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.cryptotracker.R
import com.example.cryptotracker.data.api.ApiService
import com.example.cryptotracker.data.dao.userDao
import com.example.cryptotracker.data.database.userDatabase
import com.example.cryptotracker.data.models.User
import com.example.cryptotracker.data.models.currency.Currency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PortfolioFragment : Fragment(), PortfolioAdapter.OnItemClickListener, PortfolioAdapter.onButtonClickListener{

    lateinit var followingArray: MutableList<Currency>
    private val args: PortfolioFragmentArgs by navArgs()
    private lateinit var currentUser: User

    lateinit var db: userDatabase
    lateinit var userDao: userDao

    companion object {
        fun newInstance() = PortfolioFragment()
    }


    private lateinit var viewModel: PortfolioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        followingArray = mutableListOf()
        val apiService = ApiService()
        val view: View = inflater.inflate(R.layout.portfolio_fragment, container, false)
        val fragmentContext = context
        if (fragmentContext != null) {
            db = Room.databaseBuilder(fragmentContext, userDatabase::class.java, "user_db")
                .allowMainThreadQueries().build()
            userDao = db.userDao()

            currentUser = userDao.getUser(args.id)!!
            if (currentUser != null) {
                findNavController().graph.findNode(R.id.profile)
                    ?.addArgument("id", NavArgument.Builder()
                        .setDefaultValue(currentUser.id).build())

                findNavController().graph.findNode(R.id.currencyDetailsFragment)
                    ?.addArgument("userId", NavArgument.Builder()
                        .setDefaultValue(currentUser.id).build())

                findNavController().graph.findNode(R.id.currency_list)
                    ?.addArgument("userId", NavArgument.Builder()
                        .setDefaultValue(currentUser.id).build())

                if (currentUser.favorites != null && currentUser.favorites?.size!! > 0) {

                    val favoritesSize = currentUser.favorites?.size!!
                    var parameterList = ""
                    if (favoritesSize > 1) {
                        for (i in 0..(favoritesSize - 2)) {
                            parameterList += currentUser.favorites!![i].toString() + ","
                        }
                        parameterList += currentUser.favorites!![favoritesSize - 1].toString()
                    } else if (favoritesSize == 1) {
                        parameterList = currentUser.favorites!![0].toString()
                    }

                    apiService.getCurrencyDetails(parameterList)
                        .enqueue(object : Callback<ResponseBody> {
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
                                val type = object : TypeToken<Map<String, Currency>>() {}.type
                                val followingMap =
                                    gson.fromJson<Map<String, Currency>>(rawDataNode, type)
                                //createRecyclerView(view)
                                for ((k, v) in followingMap) {
                                    followingArray.add(v)
                                }
                                createRecyclerView(view)
                            }
                        })
                }
            }
        }

        return view
    }

    //Creating recyclerView and passing it followingArrayJSON
    fun createRecyclerView(view: View) {
        val recyclerView: RecyclerView =  view.findViewById(R.id.recyclerViewPortfolio)
        val adapter = PortfolioAdapter(followingArray, this, this)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        val clickedItem = followingArray[position]
        val clickedItemId = clickedItem.id
        view?.let { showCurrencyDetail(it, clickedItemId) }
    }

    private fun showCurrencyDetail(view: View, id: Int) {
        val actionDetail = PortfolioFragmentDirections.actionPortfolioToCurrencyDetailsFragment(id, currentUser.id!!)
        Navigation.findNavController(view).navigate(actionDetail)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PortfolioViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onButtonClick(position: Int) {
        val clickedButton = followingArray[position]
        val clickedButtonId = clickedButton.id
        var userFavorites = currentUser.favorites
        userFavorites?.remove(clickedButtonId)
        if (userFavorites?.size == 0) {
            userFavorites = null
        }
        userDao.updateFavorites(currentUser.id!!, userFavorites)
        Toast.makeText(context, "Currency is no longer followed", Toast.LENGTH_SHORT).show()
    }

}