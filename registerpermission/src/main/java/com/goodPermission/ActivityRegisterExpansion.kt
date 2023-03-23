package com.goodPermission

import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * @Time 2023/3/16 10:52:47
 * @Description
 **/


class ActivityRegisterExpansion<In : Any, Out>(
    private val fragmentManager: FragmentManager,
    private val registerContracts: ActivityResultContract<In, Out>,
    private val registerCallBack: ActivityResultCallback<Out>,
    private val registerValue: In,
) {
    private val requestFragment: Fragment
        get() {
            fragmentManager.findFragmentByTag(RequestFragment::class.java.name)?.let {
                (it as? RequestFragment<In, Out>)?.apply {
                    this.registerContracts = this@ActivityRegisterExpansion.registerContracts
                    this.registerCallBack = this@ActivityRegisterExpansion.registerCallBack
                    this.registerValue = this@ActivityRegisterExpansion.registerValue
                }
                return it
            } ?: run {
                val f = RequestFragment<In, Out>()
                f.apply {
                    this.registerContracts = this@ActivityRegisterExpansion.registerContracts
                    this.registerCallBack = this@ActivityRegisterExpansion.registerCallBack
                    this.registerValue = this@ActivityRegisterExpansion.registerValue
                }
                fragmentManager.beginTransaction()
                    .add(f, RequestFragment::class.java.name)
                    .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
                return f
            }
        }

    class RequestFragment<In, Out> : Fragment() {

        var registerContracts: ActivityResultContract<In, Out>? = null

        var registerCallBack: ActivityResultCallback<Out> = ActivityResultCallback {

        }
        var registerValue: In? = null

        val registerCustomer = registerForActivityResult(registerContracts!!, registerCallBack)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onDestroyView() {
            super.onDestroyView()
            registerCustomer.unregister()
        }

        fun register() {
            registerCustomer.launch(registerValue)
        }
    }

    fun launcherRegister() {
        (requestFragment as? RequestFragment<In, Out>)?.register()
    }
}