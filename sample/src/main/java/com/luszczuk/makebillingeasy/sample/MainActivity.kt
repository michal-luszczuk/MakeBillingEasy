package com.luszczuk.makebillingeasy.sample

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.luszczuk.makebillingeasy.BillingConnector
import com.luszczuk.makebillingeasy.lifecycle.BillingConnectionLifecycleManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var connector: BillingConnector

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycle.addObserver(BillingConnectionLifecycleManager(connector))

        findViewById<View>(R.id.buttonOne).setOnClickListener {
            viewModel.onGetPurchasesPressed()
        }

        findViewById<View>(R.id.buttonTwo).setOnClickListener {
        }

        findViewById<View>(R.id.buttonThree).setOnClickListener {

        }
    }
}
