package com.example.curity.login

import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.curity.AdminPage.HomePageBrgy
import com.example.curity.MainActivity.HomePage
import com.example.curity.R
import com.example.curity.SignUp.SignUpP1
import com.example.curity.Utility.NetworkChangeListener
import com.example.curity.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var networkChangeListener = NetworkChangeListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.SignUpView.setOnClickListener {
            val intent = Intent(this, SignUpP1::class.java)
            startActivity(intent)
        }

        binding.forgetTextView.setOnClickListener {
            val intent = Intent(this, forgetPassword::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener { authResult -> checkUserAccessLevel(authResult.user!!.uid) }
                        .addOnFailureListener {}
            } else {
                Toast.makeText(this, "Please fill up the empty field(s)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserAccessLevel(uid: String) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid).get()
                .addOnSuccessListener { dataSnapshot ->
                    // Identify the Access level
                    // 1 - Brgy
                    // 2 - User

                    if (dataSnapshot.child("isAdmin").value.toString() == "1") {
                        startActivity(Intent(this, HomePageBrgy::class.java))
                        finish()
                    } else if (dataSnapshot.child("isAdmin").value.toString() == "2") {
                        startActivity(Intent(this, HomePage::class.java))
                        finish()
                    } }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeListener, filter)

        if (firebaseAuth.currentUser != null) {
            checkUserAccessLevel(firebaseAuth.currentUser!!.uid)
            finish()
        }
    }

    override fun onStop() {
        unregisterReceiver(networkChangeListener)
        super.onStop()
    }

    override fun onBackPressed() {
        //Dialog box
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to exit?")
        builder.setIcon(R.drawable.ic_baseline_warning_24)

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener{ dialog, which ->
            finishAffinity()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{ dialog, which ->
            dialog.dismiss()
        })

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}