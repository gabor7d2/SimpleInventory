package net.gabor7d2.simpleinventory.ui.categorydetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentCategoryDetailsBinding
import net.gabor7d2.simpleinventory.model.Category
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.dialog.CategoryPickerDialog
import net.gabor7d2.simpleinventory.ui.dialog.EditTextDialog
import net.gabor7d2.simpleinventory.ui.dialog.EditTextDialogArgs

class CategoryDetailsFragment : Fragment(), MenuProvider, EntityListener<Category> {

    private var _binding: FragmentCategoryDetailsBinding? = null

    private val binding get() = _binding!!

    private val categoryId: String by lazy { CategoryDetailsFragmentArgs.fromBundle(requireArguments()).categoryId }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDetailsBinding.inflate(inflater, container, false)
        RepositoryManager.instance.addCategoryListener(categoryId, this)

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RepositoryManager.instance.removeCategoryListener(this)
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.details_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete -> {
                RepositoryManager.instance.removeCategory(categoryId)
                true
            }
            else -> false
        }
    }

    override fun onChanged(entity: Category) {
        binding.textViewName.text = entity.name

        binding.editNameButton.setOnClickListener {
            EditTextDialog().apply {
                arguments = EditTextDialogArgs(
                    title = this@CategoryDetailsFragment.getString(R.string.edit_name),
                    prefill = entity.name
                ).toBundle()
            }.show(this) {
                (activity as AppCompatActivity).supportActionBar?.title = it
                RepositoryManager.instance.renameCategory(entity, it)
            }
        }


        val parent =
            if (entity.parentId == null) getString(R.string.no_parent)
            else RepositoryManager.instance.getCategory(entity.parentId).name
        binding.textViewParent.text = parent

        binding.editParentButton.setOnClickListener {
            CategoryPickerDialog().show(this) {
                RepositoryManager.instance.changeCategoryParent(entity, it)
            }
        }

        binding.openParentDetailsButton.visibility = if (entity.parentId == null) View.GONE else View.VISIBLE

        if (entity.parentId != null) {
            binding.openParentDetailsButton.setOnClickListener {
                findNavController().navigate(
                    MobileNavigationDirections.actionGotoCategoryDetailsFragment(parent, entity.parentId)
                )
            }
        }
    }

    override fun onRemoved(entity: Category) {
        Toast.makeText(context, getString(R.string.category_deleted), Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }
}