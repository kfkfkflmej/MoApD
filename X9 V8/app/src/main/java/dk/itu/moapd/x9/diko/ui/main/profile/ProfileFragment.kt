package dk.itu.moapd.x9.diko.ui.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.UserRepository
import dk.itu.moapd.x9.diko.databinding.FragmentProfileBinding
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import dk.itu.moapd.x9.diko.ui.main.UserViewModel
import dk.itu.moapd.x9.diko.ui.settings.SettingsActivity
import androidx.core.net.toUri

private const val TAG = "Profile"

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    // Defines the Profile Fragment.
    // Displays user information and refers to the Settings Activity.
    private lateinit var binding: FragmentProfileBinding
    private val userRepository by lazy { UserRepository() }

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_PROFILE_PICTURE = "profile_picture_uri_"
    }

    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userRepository) as T
            }
        }
    }

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // Update the profile picture when returning from SettingsActivity.
        changeProfilePicture()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentProfileBinding.bind(view)

        setupMenu()

        val userId = userRepository.currentUserId() ?: return

        userViewModel.loadUser(userId)
        Log.d(TAG, "userViewModel.loadUser() called")

        // Observe each LiveData individually to ensure UI updates as soon as data is available
        userViewModel.username.observe(viewLifecycleOwner) { username ->
            if (binding.textFieldUsername.editText?.text.toString() != username) {
                binding.textFieldUsername.editText?.setText(username)
            }
        }

        userViewModel.email.observe(viewLifecycleOwner) { email ->
            binding.textEmail.text = email ?: ""
        }

        userViewModel.numReports.observe(viewLifecycleOwner) { numReports ->
            binding.textReportStats.text = getString(R.string.num_of_reports_blank, numReports?.toString() ?: "0")
        }

        setupUI()
        changeProfilePicture()
    }

    private fun setupMenu() {
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
    }

    private fun setupUI() = with(binding) {
        textFieldUsername.editText?.doOnTextChanged { inputText, _, _, _ ->
            userViewModel.setUsername(inputText.toString())
        }

        buttonCheck.setOnClickListener {
            val userId = userRepository.currentUserId() ?: return@setOnClickListener
            // Use .value to get the actual data from LiveData instead of calling .toString() on the LiveData object
            userRepository.updateUserInfo(
                userId = userId,
                username = userViewModel.username.value ?: "Guest",
                numReports = userViewModel.numReports.value ?: 0,
                email = userViewModel.email.value ?: ""
            )
            showSnackBar(getString(R.string.new_name))
        }
    }

    /**
     * Updates the profile picture from the saved URI in SharedPreferences.
     */
    private fun changeProfilePicture() {
        val userId = userRepository.currentUserId() ?: return
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val uriString = sharedPreferences.getString(KEY_PROFILE_PICTURE + userId, null)
        if (uriString != null) {
            try {
                val uri = uriString.toUri()
                binding.imageProfile.setImageURI(uri)
                binding.iconCamera.visibility = View.GONE
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load profile picture", e)
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
    }

    private fun showSnackBar(message: String) {
        binding.root.showSnackBar(message)
    }
}
