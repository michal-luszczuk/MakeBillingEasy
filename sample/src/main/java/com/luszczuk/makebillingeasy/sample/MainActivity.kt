package com.luszczuk.makebillingeasy.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.buttonOne).setOnClickListener {
        }

        findViewById<View>(R.id.buttonTwo).setOnClickListener {
        }

        findViewById<View>(R.id.buttonThree).setOnClickListener {

        }
    }
}
