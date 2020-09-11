package com.application.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.application.bookhub.R
import com.application.bookhub.adapter.DashboardRecyclerAdapter
import com.application.bookhub.model.Book
import com.application.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    lateinit var progressBarLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    val bookInfoList = arrayListOf<Book>()

    val ratingComparator = Comparator<Book>{book1, book2 ->
       if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
           book1.bookName.compareTo(book2.bookName, true)
       } else {
           book1.bookRating.compareTo(book2.bookRating, true)
       }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // tell the view to hold the menu options
        setHasOptionsMenu(true)

        // Initialise element inside the view so that it may be created with the parent activity
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        recyclerDashboard.setOnClickListener {
            Toast.makeText(activity as Context, "Clicked", Toast.LENGTH_SHORT).show()
        }

        progressBar = view.findViewById(R.id.progressBar)
        progressBarLayout = view.findViewById(R.id.progressbarlayout)

        // make the progress bar visible only when the view is loading
        // showing the progressbar while the view is loading
        progressBarLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        // variable to store queue of requests
        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v1/book/fetch_books"

        // send api calls only if the device is connected to the internet
        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        // hide the progress bar when the view is completely loaded
                        progressBarLayout.visibility = View.GONE

                        // Handling of the response
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
                                bookInfoList.add(bookObject)

                                recyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                // setting up layoutmanager and adapter
                                recyclerDashboard.layoutManager = layoutManager
                                recyclerDashboard.adapter = recyclerAdapter

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Oops, Some error occurred!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (exception: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occured!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {

                    // Handling of the error
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Not able to access data, some error occured!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "a14841a2aeda14"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
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
        inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if (id == R.id.action_sort) {
            Collections.sort(bookInfoList, ratingComparator)
            bookInfoList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}