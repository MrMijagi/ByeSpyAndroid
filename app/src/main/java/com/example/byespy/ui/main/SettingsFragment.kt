package com.example.byespy.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.byespy.databinding.FragmentSettingsBinding
import com.example.byespy.network.SessionManager
import com.example.byespy.ui.StartActivity
import com.example.byespy.ui.chat.ChatActivity

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val idValue = binding.idValue
        val emailValue = binding.emailValue
        val button = binding.button
        val chat = binding.buttonChat

        mainViewModel.getProfile(requireContext())

        mainViewModel.profileResponse.observe(viewLifecycleOwner, Observer {
            val profileResponse = it ?: return@Observer

            idValue.text = profileResponse.id.toString()
            emailValue.text = profileResponse.email
        })

        button.setOnClickListener {
            // reset both tokens
            val sessionManager = SessionManager(requireContext())
            sessionManager.logout()

            // go to start activity
            val intent = Intent(requireContext(), StartActivity::class.java)
            startActivity(intent)

            //Complete and destroy login activity once successful
            activity?.setResult(AppCompatActivity.RESULT_OK)
            activity?.finish()
        }

        chat.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}