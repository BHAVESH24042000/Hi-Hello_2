package com.example.hi_hello_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hi_hello_2.Auth.LoginActivity
import com.example.hi_hello_2.homeScreen.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*

// launcher activity
class SplashActivity : AppCompatActivity() {  // this activitry is to check whether our user is predefined or not,
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firstButton.setOnClickListener()
        {
            if(auth.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java)) // if user is not perdefined start from login activity
            }else{
                startActivity(Intent(this, MainActivity::class.java)) // if user predefined start from main activity
            }
            finish()
        }
    }


}