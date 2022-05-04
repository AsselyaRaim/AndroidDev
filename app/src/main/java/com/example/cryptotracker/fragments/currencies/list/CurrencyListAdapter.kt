package com.example.cryptotracker.fragments.currencies.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptotracker.R
import com.example.cryptotracker.data.models.currency.Currency
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyListAdapter(private val currencyList: Array<Currency>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<CurrencyListAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
                                    View.OnClickListener {
        val rankView: TextView
        val nameView: TextView
        val symbolView: TextView
        val priceView: TextView
        val marketCapView: TextView
        val percentChangeView: TextView

        init {
            view.setOnClickListener(this)
            // Define click listener for the ViewHolder's View.
            rankView = view.findViewById(R.id.rankText)
            nameView = view.findViewById(R.id.nameText)
            symbolView = view.findViewById(R.id.symbolText)
            priceView = view.findViewById(R.id.priceText)
            marketCapView = view.findViewById(R.id.marketCapText)
            percentChangeView = view.findViewById(R.id.percentChangeText)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_currencies, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currency = currencyList[position]
        viewHolder.rankView.text = currency.cmcRank.toString()
        viewHolder.nameView.text = currency.name
        viewHolder.symbolView.text = currency.symbol
        viewHolder.priceView.text = '$' + BigDecimal(currency.quote.dataUSD.price).setScale(2, RoundingMode.HALF_EVEN).toString()
        viewHolder.marketCapView.text = '$' + BigDecimal(currency.quote.dataUSD.marketCap).setScale(2, RoundingMode.HALF_EVEN).toString()
        viewHolder.percentChangeView.text = BigDecimal(currency.quote.dataUSD.percentChange24h).setScale(2, RoundingMode.HALF_EVEN).toString() + '%'

        //Color to percentages
        if (currency.quote.dataUSD.percentChange24h < 0.001){
            //red color
            viewHolder.percentChangeView.setTextColor(Color.parseColor("#FFEF5050"))
        }
        else {
            //green color
            viewHolder.percentChangeView.setTextColor(Color.parseColor("#FF5DB657"))
        }
    }


    override fun getItemCount(): Int {
        return currencyList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


}