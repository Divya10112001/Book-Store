package com.example.kindle.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.kindle.R
import com.example.kindle.loginStatus
import com.example.kindle.util.ConnectionManagement
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var usermobile: String
    lateinit var userpassword: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mobile = findViewById<EditText>(R.id.usermobile)
        val password = findViewById<EditText>(R.id.userpassword)
        val loginbtn = findViewById<Button>(R.id.btnLogin)
        val register = findViewById<TextView>(R.id.register)
        val log = loginStatus()

        if (!ConnectionManagement().checkConnectivity(this@LoginActivity)) {
            val dialog = AlertDialog.Builder(this@LoginActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.create()
            dialog.show()
        } else {
            if(log.getLoggedIn(this) == false) {
                loginbtn.setOnClickListener {
                    if (mobile.text.toString().isEmpty() || password.text.toString().isEmpty()) {
                        Toast.makeText(
                            this@LoginActivity, "Please fill all credentials",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        usermobile = mobile.text.toString().trim()
                        userpassword = password.text.toString().trim()

                        val reference: DatabaseReference =
                            FirebaseDatabase.getInstance().getReference("user")
                        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        val checkUser: Query = reference.orderByChild("mobile").equalTo(usermobile)
                        val postListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    val passwordfromDB =
                                        dataSnapshot.child(usermobile).child("password")
                                            .getValue(true)
                                    if (passwordfromDB != null && passwordfromDB.equals(userpassword)) {

                                        val namefromDB =
                                            dataSnapshot.child(usermobile).child("username")
                                                .getValue(true)
                                        editor.putString("username", namefromDB.toString())
                                        val mobilefromDB =
                                            dataSnapshot.child(usermobile).child("mobile")
                                                .getValue(true)
                                        editor.putString("mobile", mobilefromDB.toString())
                                        val emailfromDB =
                                            dataSnapshot.child(usermobile).child("email")
                                                .getValue(true)
                                        editor.putString("email", emailfromDB.toString())
                                        val addressfromDB =
                                            dataSnapshot.child(usermobile).child("address")
                                                .getValue(true)
                                        editor.putString("address", addressfromDB.toString())
                                        editor.apply()

                                        log.setLoggedIn(this@LoginActivity, true)
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                MainActivity::class.java
                                            )
                                        )
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Wrong Password",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        password.requestFocus()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "No such User exist",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    mobile.requestFocus()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        }

                        checkUser.addValueEventListener(postListener)


                    }
                }
            }else{
                startActivity(Intent(this,MainActivity::class.java))
            }

            register.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        RegistrationActivity::class.java
                    )
                )
            }

        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}