package com.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.goodPermission.*
import com.goodPermission.expansion.launchCamera
import com.goodPermission.expansion.launchContact
import com.goodPermission.expansion.launchPic
import com.goodPermission.launch.launch
import com.goodPermission.launch.launchForResult
import com.goodPermission.permission.launchPermission

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ivImage = findViewById<ImageView>(R.id.ivDemo)

        findViewById<AppCompatButton>(R.id.btnGetContact).setOnClickListener {
            //选择联系人
            launchContact {
            }
        }
        findViewById<AppCompatButton>(R.id.btnLaunch).setOnClickListener {
            //启动页面
            launch<ContentActivity>()
        }

        findViewById<AppCompatButton>(R.id.btnLaunchResult).apply {
            setOnClickListener {
                launchForResult<ContentActivity>("customer" to "mainActivity") { _: Int, data: Intent? ->
                    this.text = data?.getStringExtra("result")
                }
            }
        }

        findViewById<AppCompatButton>(R.id.btnAlbum).setOnClickListener {
            launchPic("camera" to ("${System.currentTimeMillis()}.jpg")) {
                ivImage.setImageURI(it.uri)
            }
        }
        findViewById<AppCompatButton>(R.id.btnCamera).setOnClickListener {
            launchCamera("camera" to ("${System.currentTimeMillis()}.jpg")) {
                ivImage.setImageURI(it.uri)

            }
        }
        findViewById<AppCompatButton>(R.id.btnSinglePermission).setOnClickListener {
            launchPermission(permissions = android.Manifest.permission.ACCESS_FINE_LOCATION,
                tipDialog = {
                    //自定义提示
                }) {
                Toast.makeText(this, "是否成功：$it", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<AppCompatButton>(R.id.btnMorePermission).setOnClickListener {
            launchPermission(
                permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_SMS),
                tipDialog = {
                    //自定义提示引导
                }) {

                Toast.makeText(this, "是否成功：$it", Toast.LENGTH_SHORT).show()
            }
        }

        supportFragmentManager.beginTransaction().add(R.id.flContent, TakeFragment())
            .commitAllowingStateLoss()
    }
}
