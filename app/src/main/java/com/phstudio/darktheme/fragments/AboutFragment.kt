package com.phstudio.darktheme.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.phstudio.darktheme.R

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvApps = view.findViewById<TextView>(R.id.tvApps)

        val tvShare = view.findViewById<TextView>(R.id.tvShare)

        val tvPrivacy = view.findViewById<TextView>(R.id.tvPrivacy)




        tvApps.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.google_play))
            )
            startActivity(browserIntent)
        }



        tvShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_mail))
            intent.putExtra(
                Intent.EXTRA_TEXT,
                (resources.getString(R.string.email_text) + "\n" + resources.getString(R.string.email_text2) + "\n" + resources.getString(
                    R.string.email_text3
                ))
            )
            intent.type = "message/rfc822"
            startActivity(Intent.createChooser(intent, resources.getString(R.string.select_email)))
        }



        tvPrivacy.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.privacy))
            )
            startActivity(browserIntent)
        }


    }
}