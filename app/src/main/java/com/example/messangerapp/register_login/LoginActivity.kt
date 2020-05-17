package com.example.messangerapp.register_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.messangerapp.messages.LatestMessagesActivity
import com.example.messangerapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val firebaseUser = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login.setOnClickListener {
            login_user()
        }

        new_user.setOnClickListener {
            finish()
        }

    }

    private fun login_user() {
        val email = email.text.toString()
        val password = password.text.toString()

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter valid email or password",Toast.LENGTH_LONG).show()
        }
        else{
            firebaseUser.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if (!it.isSuccessful){
                        return@addOnCompleteListener
                    }
                    else{
                        Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show()
                        Log.d("LOGGEDIN","Login with email: ${email} and password: ${password} is successful")

                        val i = Intent(this,
                            LatestMessagesActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
                }
        }
    }
}
