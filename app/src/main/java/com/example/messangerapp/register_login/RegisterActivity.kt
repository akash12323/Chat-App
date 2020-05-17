package com.example.messangerapp.register_login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.messangerapp.messages.LatestMessagesActivity
import com.example.messangerapp.R
import com.example.messangerapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    val firebaseUser = FirebaseAuth.getInstance()
    var selected_photo:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        signin_text.setOnClickListener {
            startActivity(Intent(this,
                LoginActivity::class.java))
        }

        register_button.setOnClickListener {
            register_user()
        }
        photo_selector.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null && data.data!=null){
            Log.d("RegisterActivity","photo is selected")

            selected_photo = data.data
            Picasso.get().load(selected_photo.toString()).into(photo_selector)
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uri)
//            val bitmapDrawable = BitmapDrawable(bitmap)

//            photo_selector.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun register_user() {
        val email = email_edittext.text.toString()
        val password = password_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Enter appropriate email and password",Toast.LENGTH_SHORT).show()
            return
        }
        else {
            firebaseUser.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (!it.isSuccessful){
                        return@addOnCompleteListener
                    } else {
                        Log.d("RegisterActivity","Email is: ${email}    Password is:${password}")
                        uploadImageToFirebaseStorage()
                    }
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity","Register failed because: ${it.message}")
                    Toast.makeText(this,"${it.message} !! Please Try Again",Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun uploadImageToFirebaseStorage() {

        if (selected_photo == null) {
            return
        }
        else{
            val filename = UUID.randomUUID().toString()  // To Create a random string as key for the image
            val dbStorageRef = FirebaseStorage.getInstance().getReference("Images/$filename")

            dbStorageRef.putFile(selected_photo!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity","Image uploaded successfully with id: ${it.metadata?.path}")
                    dbStorageRef.downloadUrl.addOnSuccessListener {
                        Log.d("RegisterActivity","Image Location is: ${it}")

                        saveUserToDatabase(it.toString())
                    }
                }
                .addOnFailureListener{
                    Log.d("RegisterActivity","Image was not uploaded successfully ")
                }
        }
    }

    private fun saveUserToDatabase(imageUrl: String) {
        val dbRef = FirebaseDatabase.getInstance().reference
        val uid = FirebaseAuth.getInstance().uid.toString()

        dbRef.child("users").child(uid).setValue(
            User(
                uid,
                username_edittext.text.toString(),
                imageUrl
            )
        )
            .addOnSuccessListener {
                Log.d("RegisterActivity","Finally the user is saved to the database")
                val i = Intent(this,
                    LatestMessagesActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
            .addOnFailureListener {
                Log.d("RegisterActivity","Some error occured while savind userdata to the firebase database")
            }
    }
}


