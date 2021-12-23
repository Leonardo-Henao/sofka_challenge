package com.sofka.sofkachallenge

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.sofka.sofkachallenge.models.modelQuestions
import com.sofka.sofkachallenge.utils.MLog
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Method

class MainActivity : AppCompatActivity() {

    private var listQuestionsAnswered: ArrayList<modelQuestions>? = null
    private var urlGetQuestion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createUrls()
        getQuestion(1)
    }

    private fun createUrls() {
        urlGetQuestion = "${getString(R.string.server)}/get_data.php"
    }

    private fun getQuestion(level: Int) {

        val strRequest = object : StringRequest(POST, urlGetQuestion, {

            val jsonArray = JSONArray(it)
            val questionSend = modelQuestions(
                jsonArray.optJSONObject(0).getString("question"),
                jsonArray.optJSONObject(0).getString("opt01"),
                jsonArray.optJSONObject(0).getString("opt02"),
                jsonArray.optJSONObject(0).getString("opt03"),
                jsonArray.optJSONObject(0).getString("opt04"),
                jsonArray.optJSONObject(0).getInt("level"),
                jsonArray.optJSONObject(0).getString("category")
            )
            setDataViews(questionSend)


        }, {
            MLog.see("Error in ${this.localClassName} getQuestion -> ${it}")
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = hashMapOf<String, String>()
                params["lvl"] = level.toString()

                return params
            }
        }
        ControlServer(this).addRq(strRequest)
    }

    @SuppressLint("SetTextI18n")
    fun setDataViews(question: modelQuestions) {

        findViewById<TextView>(R.id.tv_question).text = question.question
        findViewById<RadioButton>(R.id.radiobt_opt01).text = question.opt1
        findViewById<RadioButton>(R.id.radiobt_opt02).text = question.opt2
        findViewById<RadioButton>(R.id.radiobt_opt03).text = question.opt3
        findViewById<RadioButton>(R.id.radiobt_opt04).text = question.opt4
        findViewById<TextView>(R.id.tv_category).text = "${getString(R.string.category)}: ${question.category}"
        findViewById<TextView>(R.id.tv_level).text = "${getString(R.string.level)}: ${question.level}"
    }


}