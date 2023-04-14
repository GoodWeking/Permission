package com.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.goodPermission.expansion.launchContact2


/**
 * @Time 2023/4/13 17:40:25
 * @Description
 **/
class ContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_contact)
        val tvContact = findViewById<TextView>(R.id.tvContact)
        findViewById<Button>(R.id.btnContact).setOnClickListener {
            launchContact2 { contact ->
                tvContact.text =
                    "name:${contact?.name}\nphone:${contact?.phone}\nsaveDate:${contact?.saveDate}\ncontactId:${contact?.contactId}"
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.flCC, TakeFragment()).commit()
    }
}