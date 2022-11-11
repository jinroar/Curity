package com.example.curity.login

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.curity.MainActivity.HomePage
import com.example.curity.R
import com.example.curity.SignUp.SignUpP1
import com.example.curity.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.SignUpView.setOnClickListener{
            val intent = Intent(this, SignUpP1::class.java)
            startActivity(intent)
        }

        binding.forgetTextView.setOnClickListener{
            val intent = Intent(this, forgetPassword::class.java)
            startActivity(intent)
        }

        binding.brgyLogin.setOnClickListener {
            val intent = Intent(this,LoginBrgy::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()



            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Please fill up the empty field(s)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser !=null){
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            finish();
        }
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