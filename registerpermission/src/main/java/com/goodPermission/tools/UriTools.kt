package com.goodPermission.tools

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Debug
import android.os.Environment
import android.provider.ContactsContract
import android.util.Log
import androidx.core.content.FileProvider
import com.goodPermission.mode.ContactMode
import com.goodPermission.mode.PicMode
import java.io.*

/**
 * @Time 2023/3/16 11:19:33
 * @Description
 **/
fun Context.cameraUri(path: String, name: String): PicMode {
    val file =
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
            "$path/${name}")
    return PicMode(FileProvider.getUriForFile(this, "${this.packageName}.FileProvider", file.apply {
        parentFile?.mkdirs()
        createNewFile()
    }), file)
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
            val file =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
                    "$path/${name}").apply {
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

/**
 * Intent action 方式读取联系人从Uri读取
 */
@SuppressLint("Range")
fun Uri.toContactPick(context: Context): ContactMode {
    val contactMode = ContactMode()
    try {
        val cursor: Cursor? = context.contentResolver.query(this, null, null, null, null)
        if (cursor == null) {
            contactMode.isError = true
            return contactMode
        }
        val first = cursor?.moveToFirst()
        Log.i("打印", "toContactPick first: $first")
        if (first == true) {
            cursor.columnNames?.sorted()?.forEach {
                Log.i("打印", "toContactPick columnName: $it")
            }
            cursor.getColumnIndex(ContactsContract.Contacts.Data.DATA1).let {
                if (it > -1)
                    contactMode.phone = cursor.getString(it)
            }
            cursor.getColumnIndex(ContactsContract.Contacts.Data._ID).let {
                if (it > -1) {
                    contactMode.contactId = cursor.getString(it)
                }
            }
            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY).let {
                if (it > -1)
                    contactMode.name = cursor.getString(it)
            }
            cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP).let {
                if (it > -1) {
                    contactMode.saveDate = cursor.getString(it)
                }
            }
            Log.i("toContactPick", "cursor - name: ${contactMode.name}")
            Log.i("toContactPick", "cursor - saveTime: ${contactMode.saveDate}")
            Log.i("toContactPick", "cursor - phone: ${contactMode.phone}")
            Log.i("toContactPick", "cursor - id: ${contactMode.contactId}")
            cursor.close()
        }
    } catch (e: Exception) {
        contactMode.isError = true
        Log.i("toContactPick", "cursor error: $e")
    }
    return contactMode
}
