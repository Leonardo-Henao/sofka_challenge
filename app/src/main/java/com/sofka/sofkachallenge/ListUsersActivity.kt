package com.sofka.sofkachallenge

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.sofka.sofkachallenge.adapters.AdapterListUsers
import com.sofka.sofkachallenge.databinding.ActivityListUsersBinding
import com.sofka.sofkachallenge.models.modelUsersFinished
import org.json.JSONArray
import org.json.JSONObject

class ListUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListUsersBinding
    private var listUsers = ArrayList<modelUsersFinished>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getDataUsers()
    }

    override fun navigateUpTo(upIntent: Intent?): Boolean {
        onBackPressed()
        return true
    }

    private fun getDataUsers() {

        val url = "${getString(R.string.server)}/get_all_users.php"
        val stringRecuest = object : StringRequest(POST, url, Response.Listener {
            val jsonArray = JSONArray(it)

            for (i in 0 until jsonArray.length()) {
                parserDataUsers(jsonArray.getJSONObject(i))
            }
            showData()

            binding.progressbarListUsers.visibility = View.GONE

        }, Response.ErrorListener { }) {}
        ControlServer(this).addRq(stringRecuest)
    }

    private fun showData() {
        binding.recyclerviewListUsers.adapter = AdapterListUsers(listUsers, this)
        binding.recyclerviewListUsers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun parserDataUsers(jsonObject: JSONObject) {
        listUsers.add(
            modelUsersFinished(
                jsonObject.getString("name_user"),
                jsonObject.getString("level_max"),
                jsonObject.getString("score_max"),
                jsonObject.getString("date")
            )
        )
    }

}