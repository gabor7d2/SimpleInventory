package net.gabor7d2.simpleinventory.ui.itemdetails

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import net.gabor7d2.simpleinventory.MobileNavigationDirections
import net.gabor7d2.simpleinventory.R
import net.gabor7d2.simpleinventory.databinding.FragmentItemDetailsBinding
import net.gabor7d2.simpleinventory.model.Item
import net.gabor7d2.simpleinventory.persistence.EntityListener
import net.gabor7d2.simpleinventory.persistence.repository.Repository
import net.gabor7d2.simpleinventory.persistence.repository.RepositoryManager
import net.gabor7d2.simpleinventory.ui.dialog.CategoryPickerDialog
import net.gabor7d2.simpleinventory.ui.dialog.EditTextDialog
import net.gabor7d2.simpleinventory.ui.dialog.ItemPickerDialog

class ItemDetailsFragment(private val itemId: String) : Fragment(), MenuProvider, EntityListener<Item> {

    private var _binding: FragmentItemDetailsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        RepositoryManager.instance.addItemListener(itemId, this)

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RepositoryManager.instance.removeItemListener(this)
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.details_menu, menu)
        menu.findItem(R.id.action_export_barcode).isVisible = true
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_delete -> {
                RepositoryManager.instance.removeItem(itemId)
                true
            }
            else -> false
        }
    }

    override fun onChanged(entity: Item) {
        binding.textViewName.text = entity.name

        binding.editNameButton.setOnClickListener {
            EditTextDialog(getString(R.string.edit_name), prefill = entity.name).show(this) {
                (activity as AppCompatActivity).supportActionBar?.title = it
                RepositoryManager.instance.renameItem(entity, it)
            }
        }


        val category =
            if (entity.categoryId == null) getString(R.string.no_category)
            else RepositoryManager.instance.getCategory(entity.categoryId).name
        binding.textViewCategory.text = category

        binding.editCategoryButton.setOnClickListener {
            CategoryPickerDialog().show(this) {
                RepositoryManager.instance.changeItemCategory(entity, it)
            }
        }

        binding.openCategoryDetailsButton.visibility = if (entity.categoryId == null) View.GONE else View.VISIBLE

        if (entity.categoryId != null) {
            binding.openCategoryDetailsButton.setOnClickListener {
                findNavController().navigate(
                    MobileNavigationDirections.actionGotoCategoryDetailsFragment(category, entity.categoryId)
                )
            }
        }


        val parent =
            if (entity.parentId == null) getString(R.string.no_parent)
            else RepositoryManager.instance.getItem(entity.parentId).name
        binding.textViewParent.text = parent

        binding.editParentButton.setOnClickListener {
            ItemPickerDialog().show(this) {
                RepositoryManager.instance.changeItemParent(entity, it)
            }
        }

        binding.openParentDetailsButton.visibility = if (entity.parentId == null) View.GONE else View.VISIBLE

        if (entity.parentId != null) {
            binding.openParentDetailsButton.setOnClickListener {
                findNavController().navigate(
                    MobileNavigationDirections.actionGotoItemDetailsFragment(parent, entity.parentId)
                )
            }
        }


        val barcodeNumber = entity.barcode.toString().padStart(Repository.BARCODE_LENGTH, '0')
        binding.barcodeText.text = barcodeNumber
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(barcodeNumber, BarcodeFormat.CODE_128,800,300)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            /*bitmap = removeWhiteFromBitmap(bitmap)
            if (requireContext().resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                bitmap = invertBitmap(bitmap)
            }*/
            binding.barcodeImage.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.e("ItemDetailsFragment", "Error while displaying barcode: ", e)
        }
    }

    private fun removeWhiteFromBitmap(bitmap: Bitmap): Bitmap {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (i in pixels.indices) {
            if (pixels[i] == -1) {
                pixels[i] = 0
            }
        }
        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }

    private fun invertBitmap(bitmap: Bitmap): Bitmap {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (i in pixels.indices) {
            pixels[i] = pixels[i] xor 0x00ffffff
        }
        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }

    override fun onRemoved(entity: Item) {
        Toast.makeText(context, getString(R.string.item_deleted), Toast.LENGTH_LONG).show()
        findNavController().popBackStack()
    }
}