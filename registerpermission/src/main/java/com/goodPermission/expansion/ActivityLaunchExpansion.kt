package com.goodPermission.expansion

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.goodPermission.mode.ContactMode
import com.goodPermission.mode.PicMode
import com.goodPermission.permission.launchPermission
import com.goodPermission.tools.toContact
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
 */
fun AppCompatActivity.launchContact(result: (ContactMode) -> Unit) {
    launchPermission(Manifest.permission.READ_CONTACTS) { it ->
        if (it) {
            ActivityExpansion(supportFragmentManager).getRequestFragment()?.launchContact {
                val mode = it.toContact(this)
                Log.i("launchContact", "launchContact: $mode")
                result.invoke(mode)
            }
        }
    }
}


/**
 * 选择联系人
 */
fun Fragment.launchContact(result: (ContactMode) -> Unit) {
    launchPermission(Manifest.permission.READ_CONTACTS) { it ->
        if (it) {
            ActivityExpansion(childFragmentManager).getRequestFragment()?.launchContact {
                val mode = it.toContact(requireContext())
                Log.i("launchContact", "launchContact: $mode")
                result.invoke(mode)
            }
        }
    }
}

