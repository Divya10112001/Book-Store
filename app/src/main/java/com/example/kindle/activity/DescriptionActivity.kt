package com.example.kindle.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kindle.R
import com.example.kindle.database.BookDatabase
import com.example.kindle.database.BookEntity
import com.example.kindle.util.ConnectionManagement
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var txtBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    lateinit var sp : SharedPreferences

    var bookId: String? = "100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtAuthorName)
        txtBookPrice = findViewById(R.id.txtPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btnAddToFav = findViewById(R.id.btnAddtoFav)
        progressBar = findViewById(R.id.progressbar)
        progressBar.visibility = View.VISIBLE
        toolbar = findViewById(R.id.toolbardescription)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
           }

        supportActionBar?.title = "Book Details"
        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(this@DescriptionActivity, "Some error  occurred", Toast.LENGTH_SHORT)
                .show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error  occurred",
                Toast.LENGTH_SHORT
            ).show()

        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (!ConnectionManagement().checkConnectivity(this@DescriptionActivity)) {
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }

       else{
            val jsonRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {


                    val bookJsonObject = it.getJSONObject("book_data")
                    progressBar.visibility = View.GONE


                    val bookImageUrl = bookJsonObject.getString("image")
                    Picasso.get().load(bookJsonObject.getString("image"))
                        .error(R.drawable.defaultbook).into(txtBookImage)
                    txtBookName.text = bookJsonObject.getString("name")
                    txtBookAuthor.text = bookJsonObject.getString("author")
                    txtBookPrice.text = bookJsonObject.getString("price")
                    txtBookRating.text = bookJsonObject.getString("rating")
                    txtBookDesc.text = bookJsonObject.getString("description")


                    val bookEntity = BookEntity(
                        bookId?.toInt() as Int,
                        txtBookName.text.toString(),
                        txtBookAuthor.text.toString(),
                        txtBookPrice.text.toString(),
                        txtBookRating.text.toString(),
                        txtBookDesc.text.toString(),
                        bookImageUrl
                    )

                    btnAddToFav.setOnClickListener {
                        val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                        val isFav = checkFav.get()


                        if (isFav) {
                            btnAddToFav.text = "Remove from cart"
                            sp=getSharedPreferences("book", MODE_PRIVATE)
                            val favColor = ContextCompat.getColor(
                                applicationContext,
                                R.color.colorfav
                            )
                            btnAddToFav.setBackgroundColor(favColor)
                        } else {
                            btnAddToFav.text = "Add to cart"
                            val noFavColor =
                                ContextCompat.getColor(applicationContext, R.color.primary_color)
                            btnAddToFav.setBackgroundColor(noFavColor)
                        }



                        if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                .get()
                        ) {


                            val async =
                                DBAsyncTask(applicationContext, bookEntity, 2).execute()
                            val result = async.get()
                            if (result) {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "Book added to cart",
                                    Toast.LENGTH_SHORT
                                ).show()


                                btnAddToFav.text = "Remove from cart"
                                val favColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.colorfav
                                )
                                btnAddToFav.setBackgroundColor(favColor)
                            } else {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "Some error occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            val async =
                                DBAsyncTask(applicationContext, bookEntity, 3).execute()
                            val result = async.get()


                            if (result) {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "Book removed from cart",
                                    Toast.LENGTH_SHORT
                                ).show()


                                btnAddToFav.text = "Add to CART"
                                val noFavColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.primary_color
                                )
                                btnAddToFav.setBackgroundColor(noFavColor)
                            } else {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "Some error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }, Response.ErrorListener {
                    progressBar.visibility = View.GONE

                    Toast.makeText(
                        this@DescriptionActivity,
                        "Volley error occurred $it",
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

            queue.add(jsonRequest)
        }
    }


        class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
            AsyncTask<Void, Void, Boolean>() {
            /*
        Move 1 = Check DB if the book is in cart
        Move 2 = Save the book into Db as item
        Move 3 = Remove the book from cart
         */
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
            override fun doInBackground(vararg p0: Void?): Boolean {
                when (mode) {
                    1 -> {
                        //Check DB if the book is in cart
                        val book: BookEntity? =
                            db.bookDao().getBookById(bookEntity.book_id.toString())
                        db.close()
                        return book != null
                    }
                    2 -> {
                        //Save the book into Db as item
                        db.bookDao().insertBook(bookEntity)
                        db.close()
                        return true
                    }
                    3 -> {
                        // Remove the book from cart
                        db.bookDao().deleteBook(bookEntity)
                        db.close()
                        return true
                    }
                }
                return false
            }

        }

}