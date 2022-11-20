package hu.bme.aut.android.chat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import hu.bme.aut.android.chat.session.SessionProvider
import hu.bme.aut.android.chat.contacts.AddContactDialog
import hu.bme.aut.android.chat.databinding.ActivityMainBinding
import hu.bme.aut.android.chat.network.NetworkManager
import hu.bme.aut.android.chat.network.socket.WebsocketManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Websocket must be initialized before Preferences (object init block runs after this block)
        NetworkManager.init()

        // Preferences only available from activity
        SessionProvider.injectPreferences(getPreferences(MODE_PRIVATE))
    }

    override fun onStart() {
        super.onStart()
        SessionProvider.listeners.add(::onSessionChange)
    }

    override fun onStop() {
        super.onStop()
        SessionProvider.listeners.remove(::onSessionChange)
    }

    private fun onSessionChange() {
        runOnUiThread {
            invalidateMenu()
            if (SessionProvider.session == null) {
                // binding.root also works
                binding.hostFragment.findNavController().navigate(R.id.action_global_welcomeFragment)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Menu should only be visible with a session
        if (SessionProvider.session != null) {
            menuInflater.inflate(R.menu.menu_action, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_signout -> {
                SessionProvider.logout(binding.root, ::getString)
                true
            }
            R.id.menu_action_addcontact -> {
                showAddContact()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Shows dialog fragment for adding contact
     */
    private fun showAddContact() {
        AddContactDialog(binding.root, ::getString).show(supportFragmentManager, AddContactDialog.TAG)
    }
}