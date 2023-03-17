package com.goodPermission.tools

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.ContactsContract
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import com.goodPermission.mode.ContactMode
import com.goodPermission.mode.PicMode
import com.myapplication.R
import java.io.*

/**
 * @Time 2023/3/16 11:19:33
 * @Description
 **/
fun Context.cameraUri(path: String, name: String): PicMode {
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .absolutePath,
        "$path/${name}"
    )
    return PicMode(
        FileProvider.getUriForFile(
            this,
            "${this.packageName}.FileProvider",
            file.apply {
                parentFile?.mkdirs()
                createNewFile()
            }
        ), file)
}

/**
 * 将uri目录的文件读取到自定义file中
 */
fun Uri.toFile(context: Context, path: String, name: String): PicMode? {
    //android10以上转换
    when (this.scheme) {
        ContentResolver.SCHEME_FILE -> {
            return PicMode(this, this.path?.let { File(it) })
        }
        ContentResolver.SCHEME_CONTENT -> {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .absolutePath,
                "$path/${name}"
            ).apply {
                parentFile?.mkdirs()
                createNewFile()
            }
            //把文件复制到沙盒目录
            val contentResolver: ContentResolver = context.contentResolver
            return try {
                val br = BufferedInputStream(contentResolver.openInputStream(this))
                val bs = BufferedOutputStream(FileOutputStream(file))
                val b = ByteArray(1024)
                while ((br.read(b)) != -1) {
                    bs.write(b)
                    bs.flush()
                }
                br.close()
                bs.close()
                PicMode(this, file)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
        else -> {
            return null
        }
    }
}


/**
 * 从uri读取单个联系人
 */
@SuppressLint("Range")
fun Uri.toContact(context: Context): ContactMode {
    val contactMode = ContactMode()
    val cursor = context.contentResolver.query(this, null, null, null, null)
    if (cursor?.moveToFirst() == true) {
        contactMode.name =
            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
        val hasPhone =
            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
        val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
        contactMode.contactId = id
        if (hasPhone == "1") {
            val phones =
                context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                    null,
                    null)
            while (phones?.moveToNext() == true) {
                contactMode.phone =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .replace(" ", "")
                //联系人更新时间
                contactMode.saveDate =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP))
            }
            phones?.close()
        }
        cursor.close()
    }
    return contactMode
}