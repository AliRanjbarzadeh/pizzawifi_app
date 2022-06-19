package ir.atriatech.pizzawifi.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.pizzawifi.common.SMS_CODE
import ir.atriatech.pizzawifi.common.SMS_RECEIVED


class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        d("SMSReceived 11-- > ${intent?.action}", "TAG")

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val status: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            d("SMSReceived -- > ${extras[SmsRetriever.EXTRA_STATUS]}", "TAG")

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    d(message, "TAG")

                    if (message != null) {
                        val firstLine = message.split("\n")
                        if (firstLine.isNotEmpty()) {
                            val code = firstLine[0].split(":")
                            if (code.isNotEmpty()) {
                                val codeLastOne = code[code.size - 1].trim()
                                d("code ----- ${codeLastOne}", "TAG")

                                val smsIntent = Intent(SMS_RECEIVED)
                                smsIntent.putExtra(SMS_CODE, codeLastOne)
                                context?.sendBroadcast(smsIntent)
                            }
                        }
                    }
                }
            }
        }
    }
}