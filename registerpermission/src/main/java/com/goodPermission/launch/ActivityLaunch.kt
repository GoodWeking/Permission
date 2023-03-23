package com.goodPermission.launch

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.goodPermission.ActivityRegisterExpansion
import com.goodPermission.permission.ActivityResult

/**
 * @Time 2023/3/15 18:31:24
 * @Description
 **/

/**
 * launch activity for result
 */

fun launchForResultCustomer(
    context: Context,
    fm: FragmentManager,
    vararg value: Pair<String, Any?>,
    nextPage: Class<*>,
    onResult: (resultCode: Int, data: Intent?) -> Unit,
) {
//    ActivityResult(fm).requestFragment.startForResult(
//        builderValue(Intent(context, nextPage), *value), onResult
//    )
    ActivityRegisterExpansion(fragmentManager = fm,
        registerContracts = ActivityResultContracts.StartActivityForResult(),
        registerCallBack = {
            it?.let {
                onResult.invoke(it.resultCode, it.data)
            }
        },
        registerValue = builderValue(Intent(context, nextPage), *value)
    ).launcherRegister()
}

inline fun <reified AA> AppCompatActivity.launchForResult(
    vararg value: Pair<String, Any?>,
    noinline onResult: (resultCode: Int, data: Intent?) -> Unit,
) {
    launchForResultCustomer(context = this,
        fm = supportFragmentManager,
        value = value,
        nextPage = AA::class.java,
        onResult = onResult)

}

/**
 * launch activity for result
 */
inline fun <reified AA> Fragment.launchForResult(
    vararg value: Pair<String, Any?>,
    noinline onResult: (resultCode: Int, data: Intent?) -> Unit,
) {
    launchForResultCustomer(context = requireContext(),
        fm = childFragmentManager,
        value = value,
        nextPage = AA::class.java,
        onResult = onResult)
}

/**
 * launch activity
 */
inline fun <reified AA> Context.launch(vararg value: Pair<String, Any?>) {
    startActivity(builderValue(Intent(this, AA::class.java), *value))
}

/**
 * launch activity
 */
inline fun <reified AA> Fragment.launch(vararg value: Pair<String, Any?>) {
    requireContext().launch<AA>(*value)
}

/**
 * group values to intent
 */
fun builderValue(intent: Intent, vararg value: Pair<String, Any?>): Intent {
    return intent.apply {

        value.forEach {
            it.second?.let { sec ->
                when (sec) {
                    is String -> putExtra(it.first, sec)
                    is Int -> putExtra(it.first, sec)
                    is Boolean -> putExtra(it.first, sec)
                    is Double -> putExtra(it.first, sec)
                    is Float -> putExtra(it.first, sec)
                    is Parcelable -> putExtra(it.first, sec)
                    is ArrayList<*> -> putParcelableArrayListExtra(
                        it.first,
                        sec as java.util.ArrayList<out Parcelable>?
                    )
                }
            }
        }
    }
}
