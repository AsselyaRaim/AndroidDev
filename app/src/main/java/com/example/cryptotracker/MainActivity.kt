package com.example.cryptotracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.cryptotracker.data.dao.userDao
import com.example.cryptotracker.data.database.userDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        db = Room.databaseBuilder(this, userDatabase::class.java, "user_db").
//        allowMainThreadQueries().
//        build()
//        userDao = db.userDao()

//        val favorites1: List<Int> = listOf(1, 1027)
//        val favorites2: List<Int> = listOf(825, 1027, 1839, 3408, 5426)
//
//        val u1 = User(login = "example_user1@gmail.com", name = "User1", password = "123", favorites = favorites1)
//        val u2 = User(login = "example_user2@gmail.com", name = "User2", password = "123", favorites = favorites2)
//        userDao.insert(u1)
//        userDao.insert(u2)


        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.visibility = if(destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.currency_list,
            R.id.portfolio,
            R.id.profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}