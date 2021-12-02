package com.example.byespy.ui.main

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.byespy.ByeSpyApplication
import com.example.byespy.databinding.FragmentSettingsBinding
import com.example.byespy.network.SessionManager
import com.example.byespy.ui.StartActivity
import com.example.byespy.ui.invitations.InvitationsActivity
import com.example.byespy.ui.new_contact.NewContactActivity
import com.example.byespy.ui.profile.ProfileActivity

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val mainViewModel by activityViewModels<MainViewModel> {
        MainViewModelFactory(
            (activity?.application as ByeSpyApplication).database.mainActivityDao()
        )
    }
    private lateinit var startAddContactActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val usernameValue = binding.usernameValue
        val emailValue = binding.emailValue
        val logoutButton = binding.buttonLogout
        val contactButton = binding.buttonAddContact
        val invitationsButton = binding.buttonResetPassword
        val editButton = binding.buttonEditProfile

        mainViewModel.profileResponse.observe(viewLifecycleOwner, Observer {
            val profileResponse = it ?: return@Observer

            usernameValue.text = profileResponse.username
            emailValue.text = profileResponse.email
        })

        startAddContactActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val email = result.data?.getStringExtra("email") ?: ""
                    Log.d("EMAIL2", email)
                    Log.d("EMAIL3", (email == "").toString())

                    if (email != "") {
                        mainViewModel.sendInvitation(requireContext(), email)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Email incorrect",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        mainViewModel.sendInvitationLiveData.observe(viewLifecycleOwner, Observer { returned ->
            Toast.makeText(
                requireContext(),
                if (returned) "Invitation sent" else "Email incorrect / already exists",
                Toast.LENGTH_LONG
            ).show()
        })

        logoutButton.setOnClickListener {
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

        contactButton.setOnClickListener {
            val intent = Intent(requireContext(), NewContactActivity::class.java)
            startAddContactActivity.launch(intent)
        }

        invitationsButton.setOnClickListener {
            val intent = Intent(requireContext(), InvitationsActivity::class.java)
            startActivity(intent)
        }

        editButton.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        mainViewModel.getProfile(requireContext())
    }
}