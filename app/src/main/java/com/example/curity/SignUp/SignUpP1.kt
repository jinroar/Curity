package com.example.curity.SignUp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.curity.Login
import com.example.curity.databinding.ActivitySignUpP1Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class SignUpP1 : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpP1Binding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpP1Binding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView1.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.button1.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val fName = binding.fNameEt.text.toString()
            val lName = binding.lNameEt.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()
                && fName.isNotEmpty() && lName.isNotEmpty()) {
                if (pass == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(this, SignUpP2::class.java)

                            val user = FirebaseAuth.getInstance().currentUser
                            val fullName = fName.plus(" ").plus(lName)

                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName).build()

                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "User profile updated.")
                                    }
                                }
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "Password is not matched", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please fill Up the empty field(s)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}