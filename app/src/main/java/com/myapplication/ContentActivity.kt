package com.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

/**
 * @Time 2023/3/17 10:05:52
 * @Description
 **/
class ContentActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        findViewById<TextView>(R.id.tvContent).apply {
            text = intent.getStringExtra("customer")
        }
        findViewById<AppCompatButton>(R.id.btnResult).setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra("result", "contentActivity")
            })
            finish()
        }
    }
}