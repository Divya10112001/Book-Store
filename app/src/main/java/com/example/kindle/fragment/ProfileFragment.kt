package com.example.kindle.fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.kindle.R
import com.example.kindle.loginStatus


class ProfileFragment : Fragment() {

    lateinit var pmob: String
    lateinit var pname:String
    lateinit var paddress: String
    lateinit var pemail: String
    val log= loginStatus()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val sharedPreferences  :SharedPreferences? = activity?.getSharedPreferences("user",Context.MODE_PRIVATE)
        val view=inflater.inflate(R.layout.fragment_profile, container, false)

             pname=sharedPreferences?.getString("username","").toString()
             pmob=sharedPreferences?.getString("mobile","").toString()
             paddress=sharedPreferences?.getString("address","").toString()
             pemail=sharedPreferences?.getString("email","").toString()

           if(activity?.let { log.getLoggedIn(it.applicationContext) } ==true) {
                val name = view.findViewById<TextView>(R.id.profilename)
                name.text = pname
                val mob = view.findViewById<TextView>(R.id.profilemob)
                mob.text = pmob
                val email = view.findViewById<TextView>(R.id.profileemail)
                email.text = pemail
                val address = view.findViewById<TextView>(R.id.profileadd)
                address.text = paddress
             }
    return view
    }
}