package com.example.cryptotracker.fragments.currencies.details

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavArgument
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.cryptotracker.R
import com.example.cryptotracker.data.api.ApiService
import com.example.cryptotracker.data.dao.userDao
import com.example.cryptotracker.data.database.userDatabase
import com.example.cryptotracker.data.models.User
import com.example.cryptotracker.data.models.currency.Currency
import com.example.cryptotracker.data.models.currencyMetadata.CurrencyMetadata
import com.example.cryptotracker.fragments.login.LoginFragmentDirections
import com.google.gson.Gson
import com.google.gson.JsonElement
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

class CurrencyDetailsFragment : Fragment() {

    private lateinit var currency: Currency
    private lateinit var currencyMetadata: CurrencyMetadata
    private val args: CurrencyDetailsFragmentArgs by navArgs()

    private lateinit var db: userDatabase
    private lateinit var userDao: userDao

    companion object {
        fun newInstance() = CurrencyDetailsFragment()
    }

    private lateinit var viewModel: CurrencyDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.currency_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().graph.findNode(R.id.portfolio)
            ?.addArgument("id", NavArgument.Builder()
                .setDefaultValue(args.userId).build())
        val apiService = ApiService()
        val currencyId = args.id
        getCurrencyDetails(view, apiService, currencyId)
    }

    private fun getCurrencyDetails(view: View, apiService: ApiService, currencyId: Int) {
        apiService.getCurrencyDetails(currencyId.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val rawString = response.body()?.string()
                val rawJson = JSONObject(rawString)
                val rawDataNode = rawJson.get("data") as JSONObject
                val rawIdDataNode = rawDataNode.get(currencyId.toString()).toString()
                val gson = Gson()
                currency = gson.fromJson(rawIdDataNode, Currency::class.java)

                getCurrencyMetadata(view, apiService, currencyId)
            }
        })
    }

    private fun getCurrencyMetadata(view: View, apiService: ApiService, currencyId: Int) {
        apiService.getCurrencyMetadata(currencyId.toString()).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val rawString = response.body()?.string()
                val rawJson = JSONObject(rawString)
                val rawDataNode = rawJson.get("data") as JSONObject
                val rawIdDataNode = rawDataNode.get(currencyId.toString()).toString()
                val gson = Gson()
                currencyMetadata = gson.fromJson(rawIdDataNode, CurrencyMetadata::class.java)

                bindData(view)
            }
        })
    }

    fun bindData(view: View) {
        //Finding views
        val logoImage: ImageView = view.findViewById(R.id.cryptoLogo)
        val symbolText: TextView = view.findViewById(R.id.cryptoSymbol)
        val nameText: TextView = view.findViewById(R.id.cryptoName)
        val rankText: TextView = view.findViewById(R.id.cryptoRank)
        val supplyText: TextView = view.findViewById(R.id.cryptoSupply)
        val priceText: TextView = view.findViewById(R.id.cryptoPrice)
        val volumeText: TextView = view.findViewById(R.id.cryptoVolume)
        val marketCapText: TextView = view.findViewById(R.id.cryptoMarketCap)
        val percent24hText: TextView = view.findViewById(R.id.cryptoPercent24h)
        val percent7dText: TextView = view.findViewById(R.id.cryptoPercent7d)
        val descText: TextView = view.findViewById(R.id.cryptoDesc)
        val buyButton: Button = view.findViewById(R.id.BuyButton)

        val fragmentContext = activity
        if (fragmentContext != null) {
            db = Room.databaseBuilder(fragmentContext, userDatabase::class.java, "user_db")
                .allowMainThreadQueries().build()
            userDao = db.userDao()

            buyButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val user: User? = userDao.getUser(args.userId)
                    if (user != null){
                        val userFavorites = user.favorites
                        if (userFavorites != null) {
                            if (args.id in userFavorites){
                                Toast.makeText(fragmentContext, "You already follow this currency", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                userFavorites.add(args.id)
                                userDao.updateFavorites(user.id!!, userFavorites)
                                Toast.makeText(fragmentContext, "Now you follow this currency", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            val userFavorites = mutableListOf<Int>()
                            userFavorites.add(args.id)
                            userDao.updateFavorites(user.id!!, userFavorites)
                            Toast.makeText(fragmentContext, "Now you follow this currency", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }


        val supplyTextToAdd: String
        if (abs(currency.maxSupply - 0) < 0.001) {
            supplyTextToAdd = currency.circulatingSupply.toInt().toString() +
                    "/no limit"
        }
        else {
            supplyTextToAdd = currency.circulatingSupply.toInt().toString() +
                    '/' + currency.maxSupply.toInt().toString()
        }
        //Binding views with data
        val logoImageUrl = currencyMetadata.logo
        Glide.with(this).load(logoImageUrl).into(logoImage)
        symbolText.text = currency.symbol
        nameText.text = currency.name
        rankText.text = rankText.text.toString() + currency.cmcRank.toString()
        supplyText.text = supplyText.text.toString() + supplyTextToAdd
        priceText.text = priceText.text.toString() + BigDecimal(currency.quote.dataUSD.price.toString()).setScale(2, RoundingMode.HALF_EVEN).toString()
        volumeText.text = volumeText.text.toString() + BigDecimal(currency.quote.dataUSD.volume24h.toString()).setScale(2, RoundingMode.HALF_EVEN).toString()
        marketCapText.text = marketCapText.text.toString() + BigDecimal(currency.quote.dataUSD.marketCap.toString()).setScale(2, RoundingMode.HALF_EVEN).toString()
        percent24hText.text = percent24hText.text.toString() + BigDecimal(currency.quote.dataUSD.percentChange24h.toString())
            .setScale(2, RoundingMode.HALF_EVEN).toString() + '%'
        percent7dText.text = percent7dText.text.toString() + BigDecimal(currency.quote.dataUSD.percentChange7d.toString())
            .setScale(2, RoundingMode.HALF_EVEN).toString() + '%'
        descText.text = currencyMetadata.description

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrencyDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}