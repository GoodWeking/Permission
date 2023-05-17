package com.goodPermission.expansion

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.goodPermission.mode.ContactMode
import com.goodPermission.mode.PicMode
import com.goodPermission.permission.launchPermission
import com.goodPermission.tools.toContact
import com.goodPermission.tools.toContactPick
import com.goodPermission.tools.toFile

/**
 * 相册选择
 **/
fun AppCompatActivity.launchPic(path: Pair<String, String>, img: (PicMode) -> Unit) {
    launchPermission(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    ) { it ->
        if (it) {
            ActivityExpansion(supportFragmentManager).getRequestFragment()?.launchPic {
                it.toFile(applicationContext, path.first, path.second)?.let(img)
            }
        }
    }
}

fun Fragment.launchPic(path: Pair<String, String>, img: (PicMode) -> Unit) {
    launchPermission(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
    ) { it ->
        if (it) {
            ActivityExpansion(childFragmentManager).getRequestFragment()?.launchPic {
                it.toFile(requireContext().applicationContext, path.first, path.second)?.let(img)
            }
        }
    }
}

/**
 * 相机拍摄
 * path.first : 保存文件夹路径
 *      second：保存文件名称记得加图片后缀名
 */
fun AppCompatActivity.launchCamera(path: Pair<String, String>, img: (PicMode) -> Unit) {
    launchPermission(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
        } else {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        }
    ) {
        if (it.filter { !it.value }.isEmpty()) {
            ActivityExpansion(supportFragmentManager).getRequestFragment()
                ?.launchCamera(path.first, path.second, img)
        }
    }
}

fun Fragment.launchCamera(path: Pair<String, String>, img: (PicMode) -> Unit) {
    launchPermission(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
        } else {
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        }
    ) {
        if (it.filter { !it.value }.isEmpty()) {
            ActivityExpansion(childFragmentManager).getRequestFragment()
                ?.launchCamera(path.first, path.second, img)
        }
    }
}

/**
 * 选择联系人
 * 需要添加权限 <uses-permission android:name="android.permission.READ_CONTACTS" />
 */
fun AppCompatActivity.launchContact(result: (ContactMode) -> Unit) {
    launchPermission(Manifest.permission.READ_CONTACTS) {
        //判断是否有联系人读取权限
        if (it) {
            ActivityExpansion(supportFragmentManager).getRequestFragment()
                ?.launchContact { contactUri ->
                    val mode = contactUri.toContact(this)
                    result.invoke(mode)
                }
        }
    }
}

/**
 * 选择联系人
 */
fun Fragment.launchContact(result: (ContactMode) -> Unit) {
    launchPermission(Manifest.permission.READ_CONTACTS) {
        if (it) {
            ActivityExpansion(childFragmentManager).getRequestFragment()
                ?.launchContact { contactUri ->
                    val mode = contactUri.toContact(requireContext())
                    result.invoke(mode)
                }
        }
    }
}

/**
 * 选择联系人 无需 联系人读取权限
 */
fun AppCompatActivity.launchContact2(result: (ContactMode) -> Unit) {
    launchIntentForResult(Intent(Intent.ACTION_PICK).apply {
        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
    }) {
        if (it != null && it.data != null) {
            val contact = it.data?.data?.toContactPick(this)
            if (contact != null) {
                result.invoke(contact)
            } else {
                result.invoke(ContactMode().apply {
                    isError = true
                })
            }
        }
    }
}

/**
 * 选择联系人 无需 联系人读取权限
 */
fun Fragment.launchContact2(result: (ContactMode) -> Unit) {
    launchIntentForResult(Intent(Intent.ACTION_PICK).apply {
        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
    }) {
        if (it != null && it.data != null) {
            val contact = it.data?.data?.toContactPick(requireContext())
            if (contact != null) {
                result.invoke(contact)
            } else {
                result.invoke(ContactMode().apply {
                    isError = true
                })
            }
        }
    }
}


/**
 * 自定义 intent 启动
 */
fun AppCompatActivity.launchIntentForResult(intent: Intent, result: (ActivityResult?) -> Unit) {
    ActivityExpansion(supportFragmentManager).getRequestFragment()
        ?.launchIntentForResult(intent, result)
}


/**
 * 自定义 intent 启动
 */
fun Fragment.launchIntentForResult(intent: Intent, result: (ActivityResult?) -> Unit) {
    ActivityExpansion(childFragmentManager).getRequestFragment()
        ?.launchIntentForResult(intent, result)
}

