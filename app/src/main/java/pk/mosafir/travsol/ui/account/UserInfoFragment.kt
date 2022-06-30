package pk.mosafir.travsol.ui.account

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import coil.api.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk.getApplicationContext
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentUserInfoBinding
import pk.mosafir.travsol.utils.toast
import pk.mosafir.travsol.viewmodel.UserInfoViewModel
import java.io.ByteArrayOutputStream
import java.io.File


class UserInfoFragment : Fragment() {
    private lateinit var _binding: FragmentUserInfoBinding
    private val binding get() = _binding

/*    private var phoneNum: String? = null
    private var name: String? = null*/

    protected val CAMERA_REQUEST = 101
    protected val GALLERY_PICTURE_REQUEST = 102

    var bitmap: Bitmap? = null


    private val viewModel: UserInfoViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        viewModel.getUserInfoData()
        viewModel.userInfoData.observe(viewLifecycleOwner, Observer {
            binding.userInfoBinding = it
            if (it.auth_type.isNullOrBlank()){
                requireContext().toast("native login")
                binding.userNameProfile.setText(binding.userInfoBinding!!.full_name)
                binding.phoneNumberProfile.setText(binding.userInfoBinding!!.mobile)
                binding.userInfoBinding!!.profile_image?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .into(binding.img)
                }
                binding.changeImg.isEnabled = true
                //disable phone no & country code field
                binding.phoneNumberProfile.isClickable = false
                binding.phoneNumberProfile.isFocusable = false
                binding.phoneNumberProfile.isFocusableInTouchMode = false
                binding.phoneNumberProfile.isCursorVisible = false

                binding.profileSpinnerCountry.setCcpClickable(false)

            }else{
                requireContext().toast("social login")
                binding.userNameProfile.setText(binding.userInfoBinding!!.full_name)

                //disable image field
                //disable name field
                binding.changeImg.isEnabled = false
                binding.userNameProfile.isClickable = false
                binding.userNameProfile.isFocusable = false
                binding.userNameProfile.isFocusableInTouchMode = false
                binding.userNameProfile.isCursorVisible = false
            }

        })

        //go back
        binding.back.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(
                R.id.nav_host_fragment,
                LoggedInFragment()
            )
            transaction.commit()
        }

        //requestForSpecificPermission()
        binding.changeImg.setOnClickListener {

            startImagePickrDialog();
        }

     return binding.root
    }

    private fun startImagePickrDialog() {
        val pictureDialog = AlertDialog.Builder(requireContext())
        pictureDialog.setTitle("Select Action")
        val pictureDialogItem = arrayOf("Select photo from Gallery",
            "Capture photo from Camera")
        pictureDialog.setItems(pictureDialogItem) { dialog, which ->

            when (which) {
                0 -> gallery()
                1 -> cameraCheckPermission()
            }
        }

        pictureDialog.show()
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_PICTURE_REQUEST)
    }


    private fun cameraCheckPermission() {

        Dexter.withContext(requireContext())
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(requireContext())
            .setMessage("It looks like you have turned off permissions"
                    + " required for this feature. It can be enable under App settings!!!")

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", "pk.mosafir.travsol", null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST -> {

                    val bitmap = data?.extras?.get("data") as Bitmap

                    //we are using coroutine image loader (coil)
                    binding.img.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                }

                GALLERY_PICTURE_REQUEST -> {

                    val uri = data?.data

                    binding.img.load(uri) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                    Log.e("uri",uri.toString())
                }
            }
        }
    }

    private fun getImageUri(requireContext: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(requireContext.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun getRealPathFromURI(tempUri: Uri): String {
        val cursor: Cursor? = getApplicationContext().contentResolver.query(tempUri, null, null,
            null, null)
        cursor?.moveToFirst()
        val idx: Int = cursor!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
        Log.e("img_link", cursor?.toString())

    }


}