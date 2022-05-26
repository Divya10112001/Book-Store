package com.example.kindle.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import com.razorpay.Checkout
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kindle.R
import com.example.kindle.activity.MainActivity
import com.example.kindle.activity.cart
import com.example.kindle.adapter.FavouriteRecyclerAdapter
import com.example.kindle.database.*
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class FavouriteFragment : Fragment() {

    lateinit var recyclerFav: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var txt: TextView
    lateinit var btn: Button
    private var bookId = 0
    var dbBooklist = ArrayList<BookEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerFav = view.findViewById(R.id.recyclerfavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        txt = view.findViewById(R.id.txt)
        btn = view.findViewById(R.id.cartbtn)
        layoutManager = GridLayoutManager(activity as Context, 2)
        dbBooklist = RetrieveFav(activity as Context).execute().get() as ArrayList<BookEntity>

        if (activity != null) {
            val mobile = activity?.intent?.getStringExtra("mobile")
            val name = activity?.intent?.getStringExtra("name")
            val email = activity?.intent?.getStringExtra("email")
            txt.visibility = View.GONE
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbBooklist,)
            recyclerFav.adapter = recyclerAdapter
            recyclerFav.layoutManager = layoutManager
            val reference: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("user")
            val checkuser: Query = reference.orderByChild("book")
            var sum = 0
            for (i in 0 until dbBooklist.size) {
                sum += dbBooklist[i].bookPrice.substring(4).toInt()

            }
            val total = ("$sum")
            btn.text = "Proceed To Pay (Rs.$total)"
            val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                       if (dbBooklist.isEmpty()) {
                            Toast.makeText(activity as Context, "No orders", Toast.LENGTH_SHORT)
                                .show()
                        }
                     btn.setOnClickListener {
                           val queue = Volley.newRequestQueue(activity as Context)
                            val url = "http://13.235.250.119/v1/book/fetch_books/"
                            /*Creating the json object required for placing the order*/
                            val jsonParams = JSONObject()
                            jsonParams.put(
                                "book_id",
                                dashboard_fragment.bookId?.toString() as String
                            )
                            var finalsum = 0
                            for (i in 0 until dbBooklist.size) {
                                finalsum += dbBooklist[i].bookPrice.substring(4).toInt()
                            }
                            jsonParams.put("total_cost", finalsum.toString())
                            val bookArray = JSONArray()
                            for (i in 0 until dbBooklist.size) {
                                val bookId = JSONObject()
                                bookId.put("book_item_id", dbBooklist[i].book_id)
                                bookArray.put(i, bookId)
                            }
                            jsonParams.put("book", bookArray)

                            val jsonObjectRequest =
                                object : JsonObjectRequest(
                                    Method.POST,
                                    url,
                                    jsonParams,
                                    Response.Listener {
                                       try {
                                            val data = it.getJSONObject("data")
                                            val success = data.getBoolean("success")

                                            if (success) {
                                                val clearCart =
                                                    ClearDBAsync(
                                                        activity as Context,
                                                        bookId.toString()
                                                    ).execute().get()
                                                FavouriteRecyclerAdapter.isCartEmpty = true

                                                val dialog = Dialog(
                                                    activity as Context,
                                                    android.R.style.Theme_Black_NoTitleBar_Fullscreen
                                                )
                                                dialog.setContentView(R.layout.activity_cart)
                                                dialog.show()
                                                dialog.setCancelable(false)
                                                val btnOk = dialog.findViewById<Button>(R.id.okbtn)
                                                btnOk.setOnClickListener {
                                                    dialog.dismiss()
                                                    startActivity(
                                                        Intent(
                                                            activity as Context,
                                                            MainActivity::class.java
                                                        )
                                                    )
                                                }
                                            } else {
                                                Toast.makeText(
                                                    activity as Context,
                                                    "Some Error occurred",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    },
                                    Response.ErrorListener {
                                        Toast.makeText(
                                            activity as Context,
                                            it.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }) {
                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["Content-type"] = "application/json"
                                        headers["token"] = "bb2347be92b5c0"
                                        return headers
                                    }
                                }

                            queue.add(jsonObjectRequest)


                            val finalamount = Math.round((sum).toFloat() * 100)
                            val checkout = Checkout()
                            checkout.setKeyID("rzp_test_7dcCypx4XYYSIQ")
                            try {
                                jsonParams.put("name", name)// to put name
                                jsonParams.put("description", "Test payment")// put description
                                jsonParams.put("theme.color", "")// to set theme color
                                jsonParams.put("currency", "INR")// put the currency
                                jsonParams.put("amount", finalamount)// put amount
                                jsonParams.put("prefill.contact", mobile)// put mobile number
                                jsonParams.put("prefill.email", email)// put email
                                checkout.open(activity, jsonParams) // open razorpay to checkout activity
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                    }
                }


                override fun onCancelled(error: DatabaseError) {
                }
            }
            checkuser.addValueEventListener(postListener)
            @Override
            fun onPaymentSuccess(s: String) {
                // this method is called on payment success.
                Toast.makeText(
                    activity as Context,
                    "Payment successful :" + s,
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(activity as Context, cart::class.java))
            }

            @Override
            fun onPaymentError( s: String) {
                // on payment failed.
                Toast.makeText(
                    activity as Context,
                    "Payment Failed due to error : " + s,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        return view
    }


   class RetrieveFav(val context: Context) : AsyncTask<Void,Void,List<BookEntity>>(){
       override fun doInBackground(vararg p0: Void?): List<BookEntity>{
                     val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db")  .build()
                      return db.bookDao().getAllBooks()
       }

   }

    class ClearDBAsync(context: Context, val bookId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.bookDao().deleteBooks(bookId)
            db.close()
            return true
        }

    }


     fun onSupportNavigateUp(): Boolean {
        if (ClearDBAsync(activity as Context,bookId.toString()).execute().get()) {
            FavouriteRecyclerAdapter.isCartEmpty = true
            onBackPressed()
            return true
        }
        return false
    }

     fun onBackPressed() {
        ClearDBAsync(activity as Context,bookId.toString()).execute().get()
        FavouriteRecyclerAdapter.isCartEmpty = true
        //super.onBackPressed()
    }


}