package com.example.cryptotracker.fragments.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.cryptotracker.R
import com.example.cryptotracker.data.dao.userDao
import com.example.cryptotracker.data.database.userDatabase
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginFragment : Fragment() {

    private lateinit var db: userDatabase
    private lateinit var userDao: userDao

    private lateinit var loginText: TextView
    private lateinit var passwordText: TextView
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        val fragmentContext = activity
        if (fragmentContext != null) {
            db = Room.databaseBuilder(fragmentContext, userDatabase::class.java, "user_db")
                .allowMainThreadQueries().build()
            userDao = db.userDao()

            loginText = view.findViewById(R.id.loginFragmentLoginText)
            passwordText = view.findViewById(R.id.loginFragmentPasswordText)
            loginButton = view.findViewById(R.id.loginFragmentLoginButton)
            registerButton = view.findViewById(R.id.loginFragmentRegisterButton)

            loginButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    if (loginText.text.isValidEmail()){
                        val loggedUser = userDao.findUser(loginText.text.toString(), passwordText.text.toString())
                        if (loggedUser != null) {
                            val loggedUserId = loggedUser.id
                            if (loggedUserId != null) {
                                val action =
                                    LoginFragmentDirections.actionLoginFragmentToPortfolio(loggedUserId)
                                if (view != null) {
                                    Navigation.findNavController(view).navigate(action)
                                }
                            }
                        }
                        else {
                            Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            loginText.text = ""
                            passwordText.text = ""
                        }
                    }
                    else {
                        Toast.makeText(context, "Invalid login", Toast.LENGTH_SHORT).show()
                        loginText.text = ""
                        passwordText.text = ""
                    }
                }


            })

            registerButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                    if (view != null) {
                        Navigation.findNavController(view).navigate(action)
                    }
                }
            })
        }



        return view
    }

    //Functions to check the validity of username

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}