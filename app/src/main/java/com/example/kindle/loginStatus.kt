package com.example.kindle

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class loginStatus {

    fun getPreferences(context: Context): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun setLoggedIn(context: Context,loggedIn:Boolean){
        val editor: SharedPreferences.Editor? = getPreferences(context)?.edit()
        editor?.putBoolean("LoggedInStatus", loggedIn);
        editor?.apply();
    }
    fun getLoggedIn(context: Context): Boolean? {
         return getPreferences(context)?.getBoolean("LoggedInStatus", false)
    }
}