package com.kuit.conet.UI.User

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kuit.conet.R
import com.kuit.conet.databinding.ActivityInquiryBinding


class InquiryActivity : AppCompatActivity() {
    lateinit var binding: ActivityInquiryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInquiryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ivInquiryBackBtn.setOnClickListener {
            finish()
        }

        binding.vInquiryEmailBtn.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {
        val email = "helpconet@gmail.com"
        val subject = "Conet 문의사항"
        val body = "문의사항을 적어주세요.."
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } else {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://mail.google.com/mail/?view=cm&fs=1&to=$email&su=${Uri.encode(subject)}&body=${Uri.encode(body)}")
            )
            startActivity(webIntent)
        }
    }
}
