package dk.itu.moapd.x9.diko.ui.main.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.Profile
import dk.itu.moapd.x9.diko.databinding.FragmentProfileBinding
import dk.itu.moapd.x9.diko.ui.settings.SettingsActivity



private const val TAG = "Profile"

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    // Defines the Profile Fragment.
    // Displays user information and reefers to the Settings Activity.
    private lateinit var binding: FragmentProfileBinding

    private val settingsLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK) {
            setupProfileInfo()
            }

            /*
            Snackbar.make(
                binding.root,
                "Profile updated",
                Snackbar.LENGTH_SHORT
            ).show()
             */
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentProfileBinding.bind(view)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        settingsLauncher.launch(
                            Intent(
                                requireContext(),
                                SettingsActivity::class.java
                            )
                        )
                        true
                    }
                    else -> false
                }
            }


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        if (!Profile.profileEmpty()) {
            setupProfileInfo()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
    }

    private fun setupProfileInfo()=
        // Used for setting up the Profile Fragment. It makes it easier to reflect changes to the user information.
        with(binding){
            val userdata = Profile.getProfile()
            textFirstName.text = userdata.first_name
            textLastName.text = userdata.last_name
            textUsername.text = userdata.username
            textEmail.text = userdata.email
            val stats_message = "You have submitted ${userdata.num_reports} reports so far! Thank you for being part of the community!"
            textReportStats.text = stats_message
    }


}


