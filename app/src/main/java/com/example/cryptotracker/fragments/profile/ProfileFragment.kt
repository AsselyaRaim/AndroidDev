package com.example.cryptotracker.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.example.cryptotracker.R
import com.example.cryptotracker.data.dao.userDao
import com.example.cryptotracker.data.database.userDatabase
import com.example.cryptotracker.data.models.User
import kotlin.math.log


class ProfileFragment : Fragment() {

    private lateinit var currentUser: User
    private val args: ProfileFragmentArgs by navArgs()

    lateinit var db: userDatabase
    lateinit var userDao: userDao

    lateinit var loginText: TextView
    lateinit var favoritesText: TextView
    lateinit var nameText: TextView
    lateinit var logoutButton: Button
    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        if (args.id > 0) {
            val fragmentContext = context
            if (fragmentContext != null) {
                db = Room.databaseBuilder(fragmentContext, userDatabase::class.java, "user_db")
                    .allowMainThreadQueries().build()
                userDao = db.userDao()

                currentUser = userDao.getUser(args.id)!!

                loginText = view.findViewById(R.id.profileLoginText)
                favoritesText = view.findViewById(R.id.profileFavoritesText)
                nameText = view.findViewById(R.id.profileWelcomeText)
                logoutButton = view.findViewById(R.id.profileLogoutButton)

                loginText.text = loginText.text.toString() + currentUser.login.toString()
                favoritesText.text = favoritesText.text.toString() + currentUser.favorites?.size.toString()
                nameText.text = nameText.text.toString() + currentUser.name.toString()

                //Exit the app
                logoutButton.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View?) {
                        activity?.finish();
                        System.exit(0);
                    }
                })
            }
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}