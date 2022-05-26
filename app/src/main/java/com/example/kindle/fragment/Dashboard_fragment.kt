package com.example.kindle.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kindle.R
import com.example.kindle.adapter.DashboardAdapter
import com.example.kindle.model.Book
import com.example.kindle.util.ConnectionManagement
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class dashboard_fragment : Fragment() {

    lateinit var recyclerdashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recycleraAdapter: DashboardAdapter
    lateinit var progressLayout  : RelativeLayout
    lateinit var progressBar: ProgressBar
    companion object {
        @SuppressLint("StaticFieldLeak")
        var bookId: Int? = 0
    }
    val bookInfolist = arrayListOf<Book>()
    var ratingComparator = Comparator<Book>{book1,book2 ->
        if(book1.bookRating.compareTo(book2.bookRating,true)==0){

        book1.bookName.compareTo(book2.bookName,true)}
        else{
                book1.bookRating.compareTo(book2.bookRating,true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard_fragment, container, false)
        //to add menu to a fragment
        setHasOptionsMenu(true)
        recyclerdashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout.visibility= View.VISIBLE
        //Volley to fetch data from internet
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

      if (ConnectionManagement().checkConnectivity(activity as Context)) {
              val jsonObjectRequest =
                   object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                       println("Response is $it")
                       try{
                           progressLayout.visibility=View.GONE
                       val success = it.getBoolean("success")

                       if (success) {
                           val data = it.getJSONArray("data")
                           for (i in 0 until data.length()) {
                               val bookJsonObject = data.getJSONObject(i)
                               val bookObject = Book(
                                   bookJsonObject.getString("book_id"),
                                   bookJsonObject.getString("name"),
                                   bookJsonObject.getString("author"),
                                   bookJsonObject.getString("rating"),
                                   bookJsonObject.getString("price"),
                                   bookJsonObject.getString("image")
                               )
                               bookInfolist.add(bookObject)
                               recycleraAdapter =
                                   DashboardAdapter(activity as Context, bookInfolist)

                               recyclerdashboard.adapter = recycleraAdapter
                               recyclerdashboard.layoutManager = layoutManager



                           }
                       } else {
                           Toast.makeText(
                               activity as Context,
                               "Some Error Occurred !!!",
                               Toast.LENGTH_SHORT
                           )
                               .show()
                       }
                   }
                       catch(e:JSONException){
                           Toast.makeText(activity as Context, "Some Unexpected Error Occurred !!", Toast.LENGTH_SHORT).show()
                       }
                   }, Response.ErrorListener {
                       //Here error will be handled
                       if(activity!=null){
                       Toast.makeText(activity as Context, "Volley error occurred !!", Toast.LENGTH_SHORT).show()
                   }}) {
                       override fun getHeaders(): MutableMap<String, String> {
                           val headers = HashMap<String, String>()
                           headers["Content-type"] = "application/json"
                           headers["token"] = "bb2347be92b5c0"
                           return headers
                       }

                   }
               queue.add(jsonObjectRequest)
           } else {
               val dialog = AlertDialog.Builder(activity as Context)
               dialog.setTitle("Error")
               dialog.setMessage("Internet Connection is not Found")
               dialog.setPositiveButton("Open Settings") { text, listener ->
                   val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                   startActivity(settingIntent)
                   activity?.finish()
               }
               dialog.setNegativeButton("Exit") { text, listener ->
                   ActivityCompat.finishAffinity(activity as Activity)
               }
               dialog.create()
               dialog.show()
           }


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboarf,menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         val id =item.itemId
        if(id == R.id.action_sort){
          Collections.sort(bookInfolist,ratingComparator)//sort the list in ascending order
            bookInfolist.reverse()

        }

        recycleraAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}

