package com.goodPermission.expansion

import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.goodPermission.mode.PicMode
import com.goodPermission.tools.cameraUri

/**
 * @Time 2023/3/16 10:52:47
 * @Description
 **/


class ActivityExpansion(private val fragmentManager: FragmentManager) {

    private val requestFragment: Fragment
        get() {
            fragmentManager.findFragmentByTag(RequestFragment::class.java.name)?.let {
                return it
            } ?: run {
                val requestFragment = RequestFragment()
                fragmentManager.beginTransaction()
                    .add(requestFragment, requestFragment::class.java.name)
                    .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
                return requestFragment
            }
        }

    class RequestFragment : Fragment() {
        //相册选择图片
        private var picResult: (Uri) -> Unit = {}
        private val launcherPic = registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                picResult.invoke(it)
            }
        }

        //相机拍摄
        private var cameraUri: PicMode? = null
        private var cameraResult: (PicMode) -> Unit = {}
        private val launcherCamera =
            registerForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it) {
                    cameraUri?.let(cameraResult)
                }
            }

        //选择联系人
        private var contactResult: (Uri) -> Unit = {}
        private val launcherContact =
            registerForActivityResult(ActivityResultContracts.PickContact()) {
                it?.let(contactResult)
            }

        override fun onDestroyView() {
            super.onDestroyView()
            launcherPic.unregister()
            launcherCamera.unregister()
            launcherContact.unregister()
        }

        fun launchPic(picResult: (Uri) -> Unit) {
            this.picResult = picResult
            launcherPic.launch("image/*")
        }

        fun launchCamera(path: String, name: String, camera: (PicMode) -> Unit) {
            cameraUri = requireContext().cameraUri(path, name)
            cameraResult = camera
            launcherCamera.launch(cameraUri?.uri)
        }

        fun launchContact(contactResult: (Uri) -> Unit) {
            this.contactResult = contactResult
            launcherContact.launch(null)
        }
    }

    fun getRequestFragment(): RequestFragment? {
        return requestFragment as? RequestFragment?
    }

}