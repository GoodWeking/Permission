package com.goodPermission

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * @Time 2023/3/20 12:05:24
 * @Description
 **/

//inline fun <reified AA : Activity> AppCompatActivity.launcherForResult(crossinline result: (resultCode: Int, data: Intent?) -> Unit) {
//    ActivityRegisterExpansion(fragmentManager = supportFragmentManager,
//        registerContracts = ActivityResultContracts.StartActivityForResult(),
//        registerCallBack = {
//            it?.let {
//                result.invoke(it.resultCode, it.data)
//            }
//        }, registerValue = Intent(this, AA::class.java))
//}