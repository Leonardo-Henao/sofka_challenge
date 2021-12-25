package com.sofka.sofkachallenge

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.sofka.sofkachallenge.databinding.ActivityMainBinding
import com.sofka.sofkachallenge.models.modelQuestions
import com.sofka.sofkachallenge.models.modelUser
import com.sofka.sofkachallenge.utils.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private var urlGetQuestion: String? = null
    private var urlCheckQuestion: String? = null
    private var urlSaveDataUser: String? = null

    private lateinit var binding: ActivityMainBinding

    private var idQuestion = 0
    private var user: modelUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createUrls()
        getQuestion(1)
        user = modelUser(null, 1, 0)

        binding.btnNext.setOnClickListener {
            if (user?.name != null) checkQuestion()
            else askNameUser()
        }
        binding.radiogroupQuestion.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId > 1) {
                binding.btnNext.isEnabled = true
            }
        }
        binding.btnSeeScoreUsers.setOnClickListener {
            startActivity(Intent(this, ListUsersActivity::class.java))
        }
        binding.btnExit.setOnClickListener { exitGame() }
    }

    private fun askNameUser() {
        val dialogName = createDialog(R.layout.alert_dialog_ask_name, false)
        with(dialogName) {
            show()
            findViewById<EditText>(R.id.et_ask_name).addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    findViewById<Button>(R.id.btn_ok_dialog_ask_name).isEnabled = count >= 3
                }

                override fun afterTextChanged(s: Editable?) {}
            })
            findViewById<Button>(R.id.btn_ok_dialog_ask_name).setOnClickListener {
                val nameUser = findViewById<EditText>(R.id.et_ask_name).text.toString()
                showToast("${getString(R.string.welcome)} $nameUser")
                modUser(name = nameUser)

                dismiss()
                checkQuestion()
            }
        }
    }

    private fun modUser(name: String? = null, level: Int? = null, score: Int? = null) {
        var userMod = user
        when {
            name != null -> userMod = modelUser(name, user!!.level, user!!.score)
            level != null -> userMod = modelUser(user!!.name, level, user!!.score)
            score != null -> userMod = modelUser(user!!.name, user!!.level, score)
        }
        user = userMod
    }

    private fun checkQuestion() {

        val dialogVerify = startLoadingVerifyQuestion()
        val rbCheck = binding.radiogroupQuestion.checkedRadioButtonId
        val rbValue = findViewById<RadioButton>(rbCheck)

        val strRequest =
            object : StringRequest(POST, urlCheckQuestion, Response.Listener {

                val jsonResponse = JSONObject(it)
                modUser(score = jsonResponse.getInt("score"))
                binding.tvScore.text = jsonResponse.getInt("score").toString()

                stoptLoadingVerifyQuestion(dialogVerify)

                // 0 SI ES CORRECTO | -1 SI ES INCORRECTO | 3 SI FINALIZO EL JUEGO
                when (jsonResponse.getInt("result")) {
                    0 -> responseIsOK(jsonResponse.getInt("level"))
                    3 -> responseIsFinished()
                    else -> responseIsFailed()
                }
            },
                Response.ErrorListener { }) {
                override fun getParams(): MutableMap<String, String> {
                    val params = hashMapOf<String, String>()
                    params["resp"] = rbValue?.text.toString()
                    params["id_q"] = idQuestion.toString()

                    return params
                }
            }
        ControlServer(this).addRq(strRequest)
    }

    private fun responseIsOK(next_level: Int) {

        val mDialog = createDialog(R.layout.alert_dialog_response_ok, false)
        mDialog.show()
        mDialog.findViewById<Button>(R.id.btn_ok_dialog_response_ok).setOnClickListener {
            clearViews()
            getQuestion(next_level)
            mDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun responseIsFinished() {

        binding.tvScore.text = "0"
        saveDataUser()
        val mDialog = createDialog(R.layout.alert_dialog_response_finished, false)
        mDialog.show()
        mDialog.findViewById<TextView>(R.id.tv_msj_alert_response_finish).text =
            "${getString(R.string.congratulation_only)} ${user?.name}, ${getString(R.string.arrived)}"

        mDialog.findViewById<Button>(R.id.btn_ok_dialog_response_finish).setOnClickListener {
            clearViews()
            getQuestion(1)

            mDialog.dismiss()
        }
    }

    private fun responseIsFailed() {

        binding.tvScore.text = "0"
        saveDataUser()
        val mDialog = createDialog(R.layout.alert_dialog_response_failed, false)
        mDialog.show()

        mDialog.findViewById<Button>(R.id.btn_ok_dialog_response_failed).setOnClickListener {
            clearViews()
            showToast("${user?.name}, ${getString(R.string.save_registre)}")
            getQuestion(1)
            showToast(getString(R.string.try_again))

            mDialog.dismiss()
        }
    }

    private fun createUrls() {
        urlGetQuestion = "${getString(R.string.server)}/get_data.php"
        urlCheckQuestion = "${getString(R.string.server)}/check_qst.php"
        urlSaveDataUser = "${getString(R.string.server)}/save_data.php"
    }

    private fun saveDataUser() {

        val stringRequest = object : StringRequest(POST, urlSaveDataUser, Response.Listener {
            clearViews()

        }, Response.ErrorListener {
            MLog.see("Error ${this.localClassName} saveData -> ${it.message}")
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = hashMapOf<String, String>()
                params["user"] = user?.name.toString()
                params["score"] = user?.score.toString()
                params["level"] = user?.level.toString()

                MLog.see("params -> $params")
                return params
            }
        }
        ControlServer(this).addRq(stringRequest)
    }

    private fun getQuestion(level: Int) {

        val dialogLoading = startLoading()

        val strRequest = object : StringRequest(POST, urlGetQuestion, {

            val jsonArray = JSONArray(it)
            val questionSend = modelQuestions(
                jsonArray.optJSONObject(0).getInt("id"),
                jsonArray.optJSONObject(0).getString("question"),
                jsonArray.optJSONObject(0).getString("opt01"),
                jsonArray.optJSONObject(0).getString("opt02"),
                jsonArray.optJSONObject(0).getString("opt03"),
                jsonArray.optJSONObject(0).getString("opt04"),
                jsonArray.optJSONObject(0).getInt("level"),
                jsonArray.optJSONObject(0).getString("category")
            )
            setDataViews(questionSend)
            stoptLoading(dialogLoading)

        }, {
            MLog.see("Error in ${this.localClassName} getQuestion -> $it")
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
        idQuestion = question.id
        binding.tvQuestion.text = question.question
        binding.radiobtOpt01.text = question.opt1
        binding.radiobtOpt02.text = question.opt2
        binding.radiobtOpt03.text = question.opt3
        binding.radiobtOpt04.text = question.opt4
        binding.tvCategory.text = "${getString(R.string.category)}: ${question.category}"
        binding.tvLevel.text = "${getString(R.string.level)}: ${question.level}"
        user?.level = question.level
    }

    private fun clearViews() {
        binding.radiogroupQuestion.clearCheck()
        binding.btnNext.isEnabled = false
    }

    override fun onDestroy() {
        if (user?.name != null) saveDataUser()
        super.onDestroy()
    }

    private fun exitGame() {

        val dialogExit = createDialog(R.layout.alert_dialog_exit_game, true)
        dialogExit.show()

        dialogExit.findViewById<Button>(R.id.btn_cancel_exit_game).setOnClickListener {
            dialogExit.dismiss()
        }
        dialogExit.findViewById<Button>(R.id.btn_ok_exit_game).setOnClickListener {
            onDestroy()
            dialogExit.dismiss()

            finishAffinity()
            exitProcess(0)
        }

    }

}