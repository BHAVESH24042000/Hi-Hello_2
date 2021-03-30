package com.example.hi_hello_2.Auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
//import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.hi_hello_2.Auth.LoginActivity
import com.example.hi_hello_2.Auth.SignUpActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit
import com.example.hi_hello_2.R

const val PHONE_NUMBER = "phoneNumber"
private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
private var mCounterDown: CountDownTimer? = null
private lateinit var auth: FirebaseAuth
private var phoneNumber: String? = null
private var mVerificationId: String? = null
private lateinit var progressDialog: ProgressDialog
private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null



class OtpActivity : AppCompatActivity(), View.OnClickListener {
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        auth = Firebase.auth

        initView()
        startVerify()

    }

    private fun startVerify() { // code from firebase code authentication //https://firebase.google.com/docs/auth/android/phone-auth?authuser=1#kotlin+ktx

        //val auth=auth.setLanguageCode("fr")
        val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber!!)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        showTimer(60000) // to call countdown timer

    }

    private fun initView() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER) // to receive phonenumber from login activity

        verifyTv.text = getString(R.string.verify_number, phoneNumber) // to set first line of otp activity xml
        waitingTv.text = getString(R.string.waiting_text, phoneNumber) // to set second line of otp activity xml

        verificationBtn.setOnClickListener(this) // button listener of verification button
        resendBtn.setOnClickListener(this)



        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {  // code from firebase code authentication //https://firebase.google.com/docs/auth/android/phone-auth?authuser=1#kotlin+ktx

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //Log.d(TAG, "onVerificationCompleted:$credential")
               if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                val smsMessageSent = credential.smsCode // to store the sms we receive for authentication
                if (!smsMessageSent.isNullOrBlank())
                    sentcodeEt.setText(smsMessageSent)   // to automatically set sms in sentcodeEt textview

                Toast.makeText(getApplicationContext(),"Verifying code Automatically",LENGTH_LONG).show();

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                // Log.w(TAG, "onVerificationFailed", e)
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
                notifyUserAndRetry("Your Phone Number might be wrong or connection error.Retry again!")
                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Log.d(TAG, "onCodeSent:$verificationId")
               // progressDialog.dismiss()
                counterTv.isVisible = false
                // Save verification ID and resending token so we can use them later
                Log.e("onCodeSent==", "onCodeSent:$verificationId")
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token

                // ...
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        if (::progressDialog.isInitialized) {
                            progressDialog.dismiss()
                        }
                        startActivity(Intent(this, SignUpActivity::class.java)) // after code sent and fill up, navigate to next activity to create account
                    }
                    else
                    {
                        if (::progressDialog.isInitialized) {
                            progressDialog.dismiss()
                        }
                        notifyUserAndRetry("Your Phone Number Verification Failed. Try Again")
                    }

                }

    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)

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


    private fun showTimer(milliSecInFuture: Long) {  // function to set countdown timer //https://developer.android.com/reference/android/os/CountDownTimer

        resendBtn.isVisible = false
        mCounterDown = object : CountDownTimer(milliSecInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                counterTv.isVisible = true
                counterTv.text = getString(R.string.seconds_remaining, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                resendBtn.isEnabled = true
                counterTv.isVisible = false
            }

        }.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCounterDown != null) {
            mCounterDown!!.cancel()
        }
    }

    private fun showLoginActivity() {  // to migrate to login activity
        startActivity(
                Intent(this, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    override fun onClick(v: View) {  // fuction on clicking verification button and on clicking resend button
        when (v) {
            verificationBtn -> {

                var code = sentcodeEt.text.toString()
                if (code.isNotEmpty() && !mVerificationId.isNullOrEmpty()) {
                    progressDialog = createProgressDialog("Please wait...", false)
                    progressDialog.show()

                    val credential =
                            PhoneAuthProvider.getCredential(mVerificationId!!, code.toString())
                    signInWithPhoneAuthCredential(credential)
                }

            }

            resendBtn -> {
                if (mResendToken != null) {
                    resendVerificationCode(phoneNumber.toString(), mResendToken)
                    showTimer(60000)
                   progressDialog = createProgressDialog("Sending a verification code", false)
                    progressDialog.show()

                } else {
                    //Toast.makeText("Sorry, You Can't request new code now, Please wait ...")
                }
            }
        }


    }


    private fun resendVerificationCode(phoneNumber: String,
                                       mResendToken: PhoneAuthProvider.ForceResendingToken?) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                callbacks, // OnVerificationStateChangedCallbacks
                mResendToken
        ) // ForceResendingToken from callbacks

    }

    fun Context.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
        return ProgressDialog(this).apply {
            setCancelable(isCancelable)
            setCanceledOnTouchOutside(false)
            setMessage(message)
        }


    }

}




