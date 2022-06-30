package pk.mosafir.travsol.ui.account

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import coil.api.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import pk.mosafir.travsol.R
import pk.mosafir.travsol.databinding.FragmentUserInfoBinding
import pk.mosafir.travsol.model.UpdateProfileModel
import pk.mosafir.travsol.utils.FileUtils
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

    var name: String? = null
    var number: String? = null
    var address: String? = null
    var email: String? = null
    var updateCHECK: String? = null
    var userID: String? = null
    var finalFile: File? = null

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

                userID = binding.userInfoBinding!!.user_id.toString()
                updateCHECK = "1"
                name = binding.userInfoBinding!!.full_name

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

                userID = binding.userInfoBinding!!.user_id.toString()
                email = binding.userInfoBinding!!.email!!
                updateCHECK = "2"
                name = binding.userInfoBinding!!.full_name

                //disable image field
                //disable name field
                binding.changeImg.isEnabled = false
                binding.userNameProfile.isClickable = false
                binding.userNameProfile.isFocusable = false
                binding.userNameProfile.isFocusableInTouchMode = false
                binding.userNameProfile.isCursorVisible = false
            }

        })


        binding.update.setOnClickListener {
            name = binding.userNameProfile.text?.toString()
            number = binding.phoneNumberProfile.text?.toString()
            address = binding.addressProfile.text?.toString()

            //check if the EditText have values or not
            if(name!!.trim().isNotEmpty()) {
                userID = binding.userInfoBinding!!.user_id.toString()
                name = binding.userNameProfile.text?.toString()
                address = binding.addressProfile.text?.toString()
                number = binding.phoneNumberProfile.text?.toString()

                //fill model class
                var updateProfileModel = UpdateProfileModel(updateCHECK, userID, finalFile, name, email, address)

                Log.e("data", "check" + updateCHECK + " userID " + userID + "file" + finalFile + " name" + name + " email" + email + "add" + address)

                viewModel.updateProfileData(updateProfileModel)
                viewModel.updateProfile.observe(viewLifecycleOwner, Observer {
                    when {
                        it.equals("1") ->
                        {
                            requireContext().toast("Update success")
                        }
                        it.equals("0") ->
                        {
                            requireContext().toast("Update failed")
                        }
                        else ->
                        {
                            requireContext().toast("Other error")
                        }
                    }
                })

            }else{
                requireContext().toast("Please type your name to update.")
            }

        }

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
                    try {

                        val photo = data!!.extras!!["data"] as Bitmap?
                        //binding.img.setImageBitmap(photo)

                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        val tempUri: Uri = getImageUri(requireContext(), photo)

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        finalFile = File(getRealPathFromURI(tempUri))
                        Log.e("path_file",finalFile.toString())

                        //we are using coroutine image loader (coil)
                        binding.img.load(tempUri) {
                            crossfade(true)
                            crossfade(1000)
                            transformations(CircleCropTransformation())
                        }

                        /*bitmap = data?.extras?.get("data") as Bitmap
//                        var intent:Intent = data?.get as Intent
                        var path = data?.extras?.get("data") as Uri
                        var pathString = FileUtils.getPath(requireContext(), path)
                        Log.e("path",pathString)

                        if (pathString.isNotEmpty()){
                            var file = File(pathString)
                            bitmap = MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                path
                            )
                            binding.img.setImageBitmap(bitmap)
                        }else{
                            requireContext().toast("path empty")
                        }*/

                       /* var file = File(pathString)

                        Log.e("file",file.toString())


                        var bitmaps = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            path
                        )*/

                        //we are using coroutine image loader (coil)
                        /*binding.img.load(bitmap) {
                            crossfade(true)
                            crossfade(1000)
                            transformations(CircleCropTransformation())
                        }*/

                    } catch (e: Exception) {
                        Log.e("image load", e.toString())
                    }
                }

                GALLERY_PICTURE_REQUEST -> {

                    val uri = data?.data as Uri
                    //var path = data?.extras?.get("data") as Uri
                    var pathString = FileUtils.getPath(requireContext(), uri)
                    Log.e("path",pathString)

                    finalFile = File(pathString)

                    Log.e("path_file",finalFile.toString())

                    //we are using coroutine image loader (coil)
                    binding.img.load(uri) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }

                }
            }
        }
    }

    private fun getRealPathFromURI(tempUri: Uri): String {
        var path = ""
        if (requireContext().contentResolver != null) {
            val cursor: Cursor? = requireContext().contentResolver.query(tempUri, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        Log.e("path",path)
        return path

    }

    private fun getImageUri(requireContext: Context, photo: Bitmap?): Uri {

        val bytes = ByteArrayOutputStream()
        photo?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(requireContext.contentResolver, photo, "Title", null)
        return Uri.parse(path)
    }

    private fun convertImgFileToBase64(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        Log.e("base64",Base64.encodeToString(b, Base64.DEFAULT))
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


}