package net.gabor7d2.simpleinventory.ui.item

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentListItemsBinding
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.ui.ListItemRecyclerViewAdapter

class ItemsFragment(private val itemId: String? = null) : Fragment(), MenuProvider {

    private var _binding: FragmentListItemsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ListItemRecyclerViewAdapter<Item>

    override fun onAttach(context: Context) {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onAttach: itemId=$itemId")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onCreate: itemId=$itemId")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onCreateView: itemId=$itemId")

        _binding = FragmentListItemsBinding.inflate(inflater, container, false)

        adapter = ListItemRecyclerViewAdapter(findNavController())
        RepositoryManager.instance.addItemChildrenListener(itemId, adapter)
        binding.list.adapter = adapter

        binding.fab.setOnClickListener {
            val newItem = RepositoryManager.instance.addOrUpdateItem(Item(null, "New Item", null, itemId))
            findNavController().navigate(
                MobileNavigationDirections.actionGotoItemDetailsFragment(newItem.name, newItem.id!!)
            )
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onStart() {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onStart: itemId=$itemId")
        super.onStart()
    }

    override fun onResume() {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onResume: itemId=$itemId")
        super.onResume()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onCreateMenu: itemId=$itemId")
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search items..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }

    override fun onPause() {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onPause: itemId=$itemId")
        super.onPause()
    }

    override fun onStop() {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onStop: itemId=$itemId")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onDestroyView: itemId=$itemId")
        RepositoryManager.instance.removeItemChildrenListener(adapter)
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onDestroy: itemId=$itemId")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d("ItemsFragment", "${System.identityHashCode(this)} onDetach: itemId=$itemId")
        super.onDetach()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return menuItem.itemId == R.id.action_search
    }
}