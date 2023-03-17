package com.goodPermission.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.myapplication.R

/**
 * 弹窗被强制拒绝后膜默认弹窗
 **/
private fun compulsionRefuseDialog(context: Context) {
    context.apply {
        AlertDialog.Builder(context)
            .setMessage(resources.getString(R.string.register_permission_dialog_msg))
            .setPositiveButton(resources.getString(R.string.register_permission_dialog_confirm)) { _, _ ->
                openSettingsPermission()
            }
            .setNeutralButton(
                resources.getString(R.string.register_permission_dialog_cancel),
                null
            ).show()
    }
}

/**
 * 跳转到系统设置也开启权限
 */
fun Context.openSettingsPermission() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
        startActivity(this)
    }
}


/**
 * request one permission
 */
//再Activity第一次请求单项权限
private var isFirstRequestPermission = true
fun AppCompatActivity.launchPermission(
    permissions: String,
    tipDialog: AppCompatActivity.(String) -> Unit = {
        compulsionRefuseDialog(this)
    },
    permissionResult: (Boolean) -> Unit,
) {
    //判断是否被强制拒绝了，弹窗提示用户
    if (ContextCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_DENIED) {
        val isCompulsionRefuse =
            ActivityCompat.shouldShowRequestPermissionRationale(this, permissions)
        if (!isCompulsionRefuse) {
            if (!isFirstRequestPermission) {
                tipDialog.invoke(this, permissions)
            }
            isFirstRequestPermission = false
        }
    }
    ActivityResult(supportFragmentManager).requestFragment.launchPermission(
        permissions,
    ) {
        permissionResult.invoke(it)
        if (it) {
            isFirstRequestPermission = true
        }
    }
}
/**
 * request one permission
 */

//再fragment第一次请求单项权限
private var isFirstRequestFragmentPermission = true
fun Fragment.launchPermission(
    permissions: String,
    tipDialog: Fragment.(String) -> Unit = {
        compulsionRefuseDialog(requireContext())
    },
    permissionResult: (Boolean) -> Unit,
) {
    //判断是否被强制拒绝了，弹窗提示用户
    if (ContextCompat.checkSelfPermission(requireContext(),
            permissions) == PackageManager.PERMISSION_DENIED
    ) {
        val isCompulsionRefuse =
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permissions)
        if (!isCompulsionRefuse) {
            if (!isFirstRequestFragmentPermission) {
                tipDialog.invoke(this, permissions)
            }
            isFirstRequestFragmentPermission = false
        }
    }
    ActivityResult(childFragmentManager).requestFragment.launchPermission(
        permissions,
    ) {
        permissionResult.invoke(it)
        if (it) {
            isFirstRequestFragmentPermission = true
        }
    }
}

/**
 * request more permission
 */

//再Activity第一次请求多项项权限
private var isFirstRequestMultiplePermission = true
fun AppCompatActivity.launchPermission(
    permissions: Array<String>,
    tipDialog: AppCompatActivity.(MutableList<String>) -> Unit = {
        compulsionRefuseDialog(this)
    },
    permissionResult: (Map<String, Boolean>) -> Unit,
) {
    val compulsionRefusePermission = mutableListOf<String>()
    permissions.forEach {
        //判断是否被强制拒绝了，弹窗提示用户
        if (ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED) {
            val isCompulsionRefuse =
                ActivityCompat.shouldShowRequestPermissionRationale(this, it)
            if (!isCompulsionRefuse) {
                if (!isFirstRequestMultiplePermission) {
                    compulsionRefusePermission.add(it)
                }
            }
        }
    }
    //被强制拒绝的权限是否为空
    if (compulsionRefusePermission.isNotEmpty()) {
        tipDialog.invoke(this, compulsionRefusePermission)
    }
    isFirstRequestMultiplePermission = false
    ActivityResult(supportFragmentManager).requestFragment.launchPermission(
        permissions,
    ) {
        permissionResult.invoke(it)
        if (it.filter { it.value }.isEmpty()) {
            isFirstRequestMultiplePermission = true
        }
    }
}

//再fragment第一次请求多项权限
private var isFirstRequestMultipleFragmentPermission = true

fun Fragment.launchPermission(
    permissions: Array<String>,
    tipDialog: Fragment.(MutableList<String>) -> Unit = {
        compulsionRefuseDialog(requireContext())
    },
    permissionResult: (Map<String, Boolean>) -> Unit,
) {
    val compulsionRefusePermission = mutableListOf<String>()
    permissions.forEach {
        //判断是否被强制拒绝了，弹窗提示用户
        if (ContextCompat.checkSelfPermission(requireContext(),
                it) == PackageManager.PERMISSION_DENIED
        ) {
            val isCompulsionRefuse =
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it)
            if (!isCompulsionRefuse) {
                if (!isFirstRequestMultipleFragmentPermission) {
                    compulsionRefusePermission.add(it)
                }
            }
        }
    }
    //被强制拒绝的权限是否为空
    if (compulsionRefusePermission.isNotEmpty()) {
        tipDialog.invoke(this, compulsionRefusePermission)
    }
    isFirstRequestMultipleFragmentPermission = false
    ActivityResult(childFragmentManager).requestFragment.launchPermission(
        permissions,
    ) {
        permissionResult.invoke(it)
        if (it.filter { !it.value }.isEmpty()) {
            isFirstRequestMultipleFragmentPermission = true
        }
    }
}
