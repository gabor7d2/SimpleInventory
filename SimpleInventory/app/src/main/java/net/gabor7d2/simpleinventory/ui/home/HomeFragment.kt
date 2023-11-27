package net.gabor7d2.simpleinventory.ui.home

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import net.gabor7d2.simpleinventory.BarcodeUtils
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.model.ListItem
import net.gabor7d2.simpleinventory.persistence.Preferences
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.ListItemFragmentBase
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter

class HomeFragment : ListItemFragmentBase<ListItem>() {

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null) {
            val barcode = result.contents.toIntOrNull()
            if (result.contents.length != BarcodeUtils.BARCODE_LENGTH || result.contents.any { !it.isDigit() } || barcode == null) {
                Toast.makeText(requireContext(), getString(R.string.barcode_invalid), Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
            val item = RepositoryManager.instance.getItemByBarcode(barcode)
            if (item == null) {
                Toast.makeText(requireContext(), getString(R.string.barcode_item_not_found), Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(
                    MobileNavigationDirections.actionGotoItemDetailsFragment(item.name, item.id!!)
                )
            }
        }
    }

    override val searchHint by lazy {
        getString(R.string.search_globally)
    }

    override fun registerAdapterAsListener(adapter: ListItemRecyclerViewAdapter<ListItem>) {
        RepositoryManager.instance.addFavouritesListener(adapter)
    }

    override fun unregisterAdapterAsListener(adapter: ListItemRecyclerViewAdapter<ListItem>) {
        RepositoryManager.instance.removeFavouritesListener(adapter)
    }

    override fun onFabClicked() {
        val newItem = RepositoryManager.instance.addOrUpdateItem(Item(null, getString(R.string.new_item), null, null))
        findNavController().navigate(
            MobileNavigationDirections.actionGotoItemDetailsFragment(newItem.name, newItem.id!!)
        )
    }

    override fun onSearchQueryChanged(newQuery: String?) {
        val query = newQuery ?: ""
        if (query.isEmpty() && adapter.searchQuery.isNotEmpty()) {
            adapter.filter(query)
            RepositoryManager.instance.removeGlobalListener(adapter)
            adapter.clear()
            RepositoryManager.instance.addFavouritesListener(adapter)
        } else if (query.isNotEmpty() && adapter.searchQuery.isEmpty()) {
            adapter.filter(query)
            RepositoryManager.instance.removeFavouritesListener(adapter)
            adapter.clear()
            RepositoryManager.instance.addGlobalListener(adapter)
        } else {
            adapter.filter(query)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateMenu(menu, menuInflater)
        //menu.findItem(R.id.action_log_out).isVisible = true
        menu.findItem(R.id.action_scan_barcode).isVisible = true
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_log_out) {
            Preferences(requireActivity()).clearLoginCredentials()
            //startActivity(Intent(requireContext(), LoginActivity::class.java))
            return true
        }
        if (menuItem.itemId == R.id.action_scan_barcode) {
            val options = ScanOptions()
            with(options) {
                setDesiredBarcodeFormats(ScanOptions.CODE_128)
                setPrompt(getString(R.string.scan_barcode_activity_title))
                setBeepEnabled(false)
                setBarcodeImageEnabled(false)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
            return true
        }
        return super.onMenuItemSelected(menuItem)
    }
}