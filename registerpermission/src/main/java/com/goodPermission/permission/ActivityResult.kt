package com.goodPermission.permission

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class ActivityResult(private val fragmentManager: FragmentManager) {
    val requestFragment: RequestFragment
        get() {
            (fragmentManager.findFragmentByTag(RequestFragment::class.java.name) as? RequestFragment?)?.let {
                return it
            } ?: run {
                val activityResultFragment = RequestFragment()
                fragmentManager.beginTransaction().add(
                    activityResultFragment,
                    RequestFragment::class.java.name
                ).commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
                return activityResultFragment
            }
        }

    class RequestFragment : Fragment() {
        var onResult: ((resultCode: Int, data: Intent?) -> Unit)? = null

        //启动页面并带有回传值
        private val launcherResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                onResult?.invoke(it.resultCode, it.data)
            }

        private var permissionResult: (Boolean) -> Unit = {}

        private var permissionMultipleResult: (Map<String, Boolean>) -> Unit = {}

        //单条权限请求
        private val launchPermissionSingle =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                permissionResult.invoke(it)
            }

        //多要权限请求
        private val launchPermissionMutable =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                permissionMultipleResult.invoke(it)
            }

        override fun onDestroyView() {
            super.onDestroyView()
            launcherResult.unregister()
            launchPermissionSingle.unregister()
            launchPermissionMutable.unregister()
        }

        fun startForResult(
            intent: Intent?,
            onResult: (resultCode: Int, data: Intent?) -> Unit,
        ) {
            this.onResult = onResult
            launcherResult.launch(intent)
        }

        fun launchPermission(permission: String, permissionResult: (Boolean) -> Unit) {
            this.permissionResult = permissionResult
            launchPermissionSingle.launch(permission)
        }

        fun launchPermission(
            permission: Array<String>,
            permissionResult: (Map<String, Boolean>) -> Unit,
        ) {
            this.permissionMultipleResult = permissionResult
            launchPermissionMutable.launch(permission)
        }
    }
}

