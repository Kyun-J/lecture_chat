package com.kyun.testchat.Main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import com.kyun.testchat.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {


    var user_name : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_name = resources.getString(R.string.user_name)
        val perference : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val edit : SharedPreferences.Editor = perference.edit()

        val s = perference.getString(user_name,null)

        if(s == null) {
            setTheme(R.style.AppTheme)
            setContentView(R.layout.activity_splash)
            splash_edit.setOnEditorActionListener { textView, i, keyEvent ->
                if(i == EditorInfo.IME_ACTION_DONE) {
                    edit.putString(user_name,splash_edit.text.toString())
                    edit.commit()
                    startActivity(Intent(this, MainActivity::class.java).putExtra(user_name,splash_edit.text.toString()))
                    finish()
                    true
                } else
                    false
            }
            splash_button.setOnClickListener {
                edit.putString(user_name,splash_edit.text.toString())
                edit.commit()
                startActivity(Intent(this, MainActivity::class.java).putExtra(user_name,splash_edit.text.toString()))
                finish()
            }

        } else {
            startActivity(Intent(this, MainActivity::class.java).putExtra(user_name,s))
            finish()
        }
    }
}