package com.manish.stripepaymentgateway

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class MainActivity : AppCompatActivity() {

    lateinit var makePaymentButton : Button
    lateinit var paymentSheet: PaymentSheet
    lateinit var progressbar : ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makePaymentButton = findViewById(R.id.button)
        progressbar = findViewById(R.id.progressbar)

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        val obj = Payment(this@MainActivity,progressbar,paymentSheet)
        PaymentConfiguration.init(this@MainActivity,obj.PUBLICKEY)

        makePaymentButton.setOnClickListener {
            makePaymentButton.visibility = View.GONE
            obj.paymentStart()
            Handler(Looper.getMainLooper()).postDelayed({
                obj.paymentFlow()
            },3000)

        }


    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                makePaymentButton.visibility = View.VISIBLE
                progressbar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Canceled", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Failed -> {
                makePaymentButton.visibility = View.VISIBLE
                progressbar.visibility = View.GONE

                Toast.makeText(this@MainActivity, "${paymentSheetResult.error.localizedMessage}", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, "first check Payment class", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                makePaymentButton.visibility = View.VISIBLE
                progressbar.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Completed", Toast.LENGTH_SHORT).show()

            }
        }
    }


}