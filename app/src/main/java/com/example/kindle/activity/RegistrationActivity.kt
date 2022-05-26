package com.example.kindle.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kindle.R
import com.example.kindle.database.userdatabase
import com.example.kindle.fragment.dashboard_fragment
import com.example.kindle.util.ConnectionManagement
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RegistrationActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var number : String
    lateinit var storedVerificationId:String
    lateinit var mobile:EditText
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        var rootNode : FirebaseDatabase
        var references : DatabaseReference

        val name =findViewById<EditText>(R.id.name)
        mobile =findViewById(R.id.mobile)
        val email =findViewById<EditText>(R.id.Email)
        val address =findViewById<EditText>(R.id.address)
        val password =findViewById<EditText>(R.id.password)
        val registerbtn =findViewById<Button>(R.id.btnRegister)
        val login =findViewById<TextView>(R.id.login)
        if (ConnectionManagement().checkConnectivity(this@RegistrationActivity)) {
            registerbtn.setOnClickListener {

                if (name.text.toString().isEmpty() || mobile.text.toString()
                        .isEmpty() || email.text.toString()
                        .isEmpty()
                    || address.text.toString().isEmpty() || password.text.toString().isEmpty()
                  ) {
                    Toast.makeText(
                        this@RegistrationActivity, "Please fill all credentials",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    rootNode = FirebaseDatabase.getInstance()
                    references = rootNode.getReference("user")
                    val username = name.text.toString()
                    val usermobile = mobile.text.toString()
                    val useremail = email.text.toString()
                    val useraddress = address.text.toString()
                    val userpassword = password.text.toString()

                    val helper =
                        userdatabase(username, useremail, usermobile, useraddress, userpassword)
                    references.child(usermobile).setValue(helper)
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Your Account has been created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                   auth = FirebaseAuth.getInstance()
                    // Callback function for Phone Auth
                    callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        // This method is called when the verification is completed
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            startActivity(
                                Intent(
                                    this@RegistrationActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Verification Completed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // Called when verification is failed add log statement to see the exception
                        override fun onVerificationFailed(e: FirebaseException) {
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Verification Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // On code is sent by the firebase this method is called
                        // in here we start a new activity where user can enter the OTP
                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            Log.d("M", "onCodeSent: $verificationId")
                            storedVerificationId = verificationId
                            resendToken = token


                                        val intent = Intent(
                                            this@RegistrationActivity,
                                            VerifyPhoneNo::class.java
                                        )
                                        intent.putExtra(
                                            "storedVerificationId",
                                            storedVerificationId
                                        )
                                        intent.putExtra("mobile", mobile.text.toString())
                                        intent.putExtra("password",password.text.toString())
                                        startActivity(intent)
                                        finish()
                                    }
                                }


                            }
                            otpverification()
                        }
                    }

        else{
            val dialog = AlertDialog.Builder(this@RegistrationActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.create()
            dialog.show()
        }
        login.setOnClickListener { startActivity(Intent(this@RegistrationActivity,LoginActivity::class.java)) }
    }
    fun otpverification() {

       number = mobile.text.trim().toString()

        if (number.isNotEmpty()) {
            number = "+91$number"
            sendotp(number)
        } else {
            Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendotp(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)                    // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                        // Activity (for callback binding)
            .setCallbacks(callbacks)                 // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("M","Auth Started")
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
}