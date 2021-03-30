package com.example.hi_hello_2.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.example.hi_hello_2.Auth.OtpActivity
//import com.example.hi_hello_2.homeScreen.OtpActivity.P
import com.example.hi_hello_2.R
import com.example.hi_hello_2.Auth.PHONE_NUMBER
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var phoneNumber: String
    private lateinit var countryCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        phoneNumberEt.addTextChangedListener {
            nextBtn.isEnabled =
                !(it.isNullOrEmpty() || it.length < 10) // this will check whether our number is 10 digit and should not be null

        }

        nextBtn.setOnClickListener {
            checkNumber()
        }

    }

    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + phoneNumberEt.text.toString()

        notifyUser()
    }

    private fun notifyUser()  // to create a dialog box before verification of phone number //https://material.io/components/dialogs/android
    {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(
                "We will be verifying the phone number:$phoneNumber\n" +
                        "Is this OK, or would you like to edit the number?"
            )
            setPositiveButton("Ok") { dialog, _ ->
                showLoginActivity()
            }

            setNegativeButton("Edit") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }


    private fun showLoginActivity() // to migrate form login activity to OTP activity with phoneNumber
    {
        startActivity(
            Intent(this, OtpActivity::class.java).putExtra(PHONE_NUMBER, phoneNumber)
        )
    }
}