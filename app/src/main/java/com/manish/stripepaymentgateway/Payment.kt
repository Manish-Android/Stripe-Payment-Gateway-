package com.manish.stripepaymentgateway

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject

// go to this "https://stripe.com/docs/payments/accept-a-payment" url for customerUrl,ephemeralUrl,paymentIntentUrl
//  "https://dashboard.stripe.com/test/apikeys"  hwere you get secretkey and publickkey

class Payment(val contex: Context,val progress_bar : ProgressBar, var paymentSheet : PaymentSheet)  {

    var SECRETKEY = "put your secret key here"
    var PUBLICKEY = "put your publishable key here"

    var customerUrl = "https://api.stripe.com/v1/customers"
    var ephemeralUrl = "https://api.stripe.com/v1/ephemeral_keys"
    var paymentIntentUrl = "https://api.stripe.com/v1/payment_intents"
    var amount = "10"
    var currencyType = "usd"
    var automatic_payment_methods = "true"
    var clientid = ""
    var ephemeralid = ""
    var clientSecret = ""



    fun paymentStart() {
        PaymentConfiguration.init(contex, PUBLICKEY)

        progress_bar.visibility = View.VISIBLE
        val requestQueue = Volley.newRequestQueue(contex)

        val stringRequest = object : StringRequest(Method.POST, customerUrl,
            Response.Listener { response ->

                val responsejson = JSONObject(response)
                Log.d("TAG", "<<responsejson :: $responsejson")
                clientid = responsejson.getString("id")
                Log.d("TAG", "<<clientid :: $clientid")
                ephemeralKeys(clientid)


            },
            Response.ErrorListener { error ->
                Log.d("TAG", "<<error :: $error")

            }) {

            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRETKEY"

                return headers

            }

        }

        requestQueue.add(stringRequest)

        // paymentSheet = PaymentSheet(contex, ::onPaymentSheetResult)
    }



    fun ephemeralKeys(clientid: String) {

        val requestQueue = Volley.newRequestQueue(contex)

        val stringRequest = object : StringRequest(
            Method.POST, ephemeralUrl,
            Response.Listener { response ->

                val responsejson = JSONObject(response)
                ephemeralid = responsejson.getString("id")
                paymentIntent(clientid)
                Log.d("TAG", "<<responseephemeralKeys :: $responsejson")


            },
            Response.ErrorListener { error ->
                Log.d("TAG", "<<errorephemeralKeys :: $error")

            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["customer"] = clientid
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRETKEY"
                headers["Stripe-Version"] = "2023-10-16"

                return headers

            }

        }

        requestQueue.add(stringRequest)
    }

    fun paymentIntent(clientid: String) {

        val requestQueue = Volley.newRequestQueue(contex)

        val stringRequest = object : StringRequest(
            Method.POST, paymentIntentUrl,
            Response.Listener { response ->

                val responsejson = JSONObject(response)
                Log.d("TAG", "<<responsepaymentIntent :: $responsejson")
                clientSecret = responsejson.getString("client_secret")
                Log.d("TAG", "<<clientSecret :: $clientSecret")
                progress_bar.visibility = View.GONE

            },
            Response.ErrorListener { error ->
                Log.d("TAG", "<<errorpaymentIntent :: $error")

            }) {

            override fun getParams(): Map<String, String> {

                val params = HashMap<String, String>()
                params["customer"] = clientid
                params["amount"] = "${amount}00"
                params["currency"] = currencyType
                params["automatic_payment_methods[enabled]"] = automatic_payment_methods
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $SECRETKEY"
                return headers

            }

        }

        requestQueue.add(stringRequest)
    }

    fun paymentFlow() {
        val googlePayConfiguration = PaymentSheet.GooglePayConfiguration(
            PaymentSheet.GooglePayConfiguration.Environment.Test,
            "US",
            "USD",
            1000,
            "Gpay"
        )
        val configuration = PaymentSheet.Configuration("hello",null,googlePayConfiguration)

        val data = PaymentSheet.Configuration(
            "ABC Company",
            PaymentSheet.CustomerConfiguration(clientid, ephemeralid)
        )
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration)

    }


}