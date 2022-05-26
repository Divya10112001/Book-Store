package com.example.kindle.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.kindle.R
import com.google.firebase.auth.*


class VerifyPhoneNo : AppCompatActivity() {


        lateinit var auth: FirebaseAuth
        lateinit var mobile: String
        lateinit var password: String


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_verify_phone_no)
            val btn= findViewById<Button>(R.id.submitbtn)
            auth=FirebaseAuth.getInstance()

            // get storedVerificationId from the intent
            val storedVerificationId= intent.getStringExtra("storedVerificationId")
             mobile= intent.getStringExtra("mobile").toString()
             password= intent.getStringExtra("password").toString()

            // fill otp and call the on click on button
            btn.setOnClickListener {
                val otp=findViewById<EditText>(R.id.otp).text.toString()
                if(otp.isNotEmpty()){
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        storedVerificationId.toString(), otp)
                    signInWithPhoneAuthCredential(credential)
                }else{
                    Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this , LoginActivity::class.java)
                    intent.putExtra("mobile",mobile)
                    intent.putExtra("password",password)
                    startActivity(intent)
                    finish()
                } else {
                    // Sign in failed
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

}






