package com.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.goodPermission.expansion.launchCamera
import com.goodPermission.expansion.launchContact
import com.goodPermission.permission.launchPermission
import com.goodPermission.expansion.launchPic
import com.goodPermission.launch.launch
import com.goodPermission.launch.launchForResult

/**
 * @Time 2023/3/16 16:40:33
 * @Description
 **/
class TakeFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val content = inflater.inflate(R.layout.fragment_teke, null)
        val iv = content.findViewById<ImageView>(R.id.ivFImage)
        content.findViewById<Button>(R.id.btnFCamera).setOnClickListener {
            //拍照
            launchCamera("camera" to "camera_${System.currentTimeMillis()}.jpg") {
                iv.setImageURI(it.uri)
            }
        }
        content.findViewById<Button>(R.id.btnFPic).setOnClickListener {
            //相册
            launchPic("camera" to "pic_${System.currentTimeMillis()}.jpg") {
                iv.setImageURI(it.uri)
            }
        }
        content.findViewById<AppCompatButton>(R.id.btnFSinglePermission).setOnClickListener {
            //单项权限
            launchPermission(android.Manifest.permission.CAMERA) {
                Toast.makeText(requireContext(), "以获取拍摄权限", Toast.LENGTH_SHORT).show()
                Log.i("打印", "onCreateView 单项权限: $it")
            }
        }

        content.findViewById<AppCompatButton>(R.id.btnFMorePermission).setOnClickListener {
            //多项权限
            launchPermission(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_CONTACTS
            )) {
                Log.i("打印", "onCreateView 多项权限: $it")
            }
        }

        content.findViewById<Button>(R.id.btnFLacunch).setOnClickListener {
            //启动
            launch<ContentActivity>()
        }

        content.findViewById<Button>(R.id.btnFLacunchResult).apply {
            setOnClickListener {
                //启动回传
                launchForResult<ContentActivity>("customer" to "mainActivity") { _: Int, data: Intent? ->
                    this.text = data?.getStringExtra("result")
                }
            }
        }
        val tvContent = content.findViewById<AppCompatTextView>(R.id.tvTextFContent)
        content.findViewById<Button>(R.id.btnFContact).setOnClickListener {
            //启动
            launchContact {
                tvContent.text = it.toString()
            }
        }
        return content
    }
}