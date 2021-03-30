package com.example.hi_hello_2.Auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hi_hello_2.R
import com.example.hi_hello_2.models.User
import com.example.hi_hello_2.homeScreen.MainActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.auth.User
//import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.FirebaseStorage.*
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val database by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var downloadUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        userImgView.setOnClickListener{
            checkPermissionForImage() // function to set image of DP
            // Firebase Thumbnail
        }

        nextBtn.setOnClickListener {
            val name = nameEt.text.toString()

            if (!::downloadUrl.isInitialized) { // if photo is not inserted
                Toast.makeText(this, "Photo cannot be empty", Toast.LENGTH_SHORT ).show()
            } else if (name.isEmpty()) { // if name is not inserted
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT ).show()
                //toast("Name cannot be empty")
            } else {
                val user = User(name, downloadUrl, downloadUrl/*Needs to thumbnai url*/, auth.uid!!) // to send data of this user to cloud

                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    nextBtn.isEnabled = true
                }
            }
        }
    }


    private fun checkPermissionForImage() {   // code for function to ask for user permission for accessing input storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(
                    permission,
                    1001
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(
                    permissionWrite,
                    1002
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                pickImageFromGallery() // user defined function to set image image
            }
        }
    }

    private fun pickImageFromGallery() {  // function to create intent to navigate from activity to gallery to pick up image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            1000
        ) // GIVE AN INTEGER VALUE FOR IMAGE_PICK_CODE LIKE 1000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) { // function to set image as Dp
            data?.data?.let {
                userImgView.setImageURI(it)
                startUpload(it)  // user defined function to upload image to firebase store
            }
        }
    }

    private fun startUpload(it: Uri) { // to upload DP to firebase storage
        nextBtn.isEnabled = false
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUrl = task.result.toString()
                nextBtn.isEnabled = true
            } else {
                nextBtn.isEnabled = true
                // Handle failures
            }
        }.addOnFailureListener {

        }
    }
}