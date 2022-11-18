package hu.bme.aut.android.chat

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import hu.bme.aut.android.chat.connection.SessionProvider
import hu.bme.aut.android.chat.contacts.AddContactDialog
import hu.bme.aut.android.chat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionProvider.injectPreferences(getPreferences(MODE_PRIVATE))
        SessionProvider.listeners.add(::onSessionChange)
    }

    private fun onSessionChange() {
        invalidateMenu()
        if (SessionProvider.session == null) {
            // binding.root also works
            binding.hostFragment.findNavController().navigate(R.id.action_global_welcomeFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        SessionProvider.listeners.remove(::onSessionChange)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (SessionProvider.session != null) {
            menuInflater.inflate(R.menu.menu_action, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_signout -> {
                SessionProvider.logout()
                true
            }
            R.id.menu_action_addcontact -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}