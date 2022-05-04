package com.example.cryptotracker.fragments.register

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.cryptotracker.R
import com.example.cryptotracker.data.dao.userDao
import com.example.cryptotracker.data.database.userDatabase
import com.example.cryptotracker.data.models.User
import com.example.cryptotracker.fragments.login.LoginFragmentDirections
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterFragment : Fragment() {

    private lateinit var db: userDatabase
    private lateinit var userDao: userDao

    private lateinit var usernameText: TextView
    private lateinit var loginText: TextView
    private lateinit var passwordText: TextView
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.register_fragment, container, false)
        val fragmentContext = activity
        if (fragmentContext != null) {
            db = Room.databaseBuilder(fragmentContext, userDatabase::class.java, "user_db")
                .allowMainThreadQueries().build()
            userDao = db.userDao()

            usernameText = view.findViewById(R.id.nameText)
            loginText = view.findViewById(R.id.emailText)
            passwordText = view.findViewById(R.id.passwordText)
            registerButton = view.findViewById(R.id.registerButton)


            registerButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    if (isValidUsername(usernameText.text.toString())
                        && loginText.text.isValidEmail()
                        && isValidPassword(passwordText.text.toString())) {
                        val user = userDao.findUserByLogin(loginText.text.toString())
                        if (user != null) {
                            Toast.makeText(context, "User with this login already exists", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val newUser = User(login=loginText.text.toString(), name=usernameText.text.toString(),
                            password=passwordText.text.toString())
                            userDao.insert(newUser)
                            val newUserId = userDao.findUserByLogin(loginText.text.toString())?.id
                            val action =
                                RegisterFragmentDirections.actionRegisterFragmentToPortfolio(newUserId!!)
                            if (view != null) {
                                Navigation.findNavController(view).navigate(action)
                            }
                            Toast.makeText(context, "Successful registration", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }


    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = ".{8,}"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isValidUsername(username: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val USERNAME_PATTERN = "^[a-zA-Z]+$"
        pattern = Pattern.compile(USERNAME_PATTERN)
        matcher = pattern.matcher(username)
        return matcher.matches()
    }
}