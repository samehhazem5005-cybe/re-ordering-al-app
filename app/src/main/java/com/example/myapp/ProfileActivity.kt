package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = Firebase.auth.currentUser?.uid
        if (uid == null) {
            // Not logged in, go back to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        userRef = Firebase.database.reference.child("Users").child(uid)

        // Load existing data
        userRef.get().addOnSuccessListener { snap ->
            binding.etName.setText(snap.child("name").value?.toString() ?: "")
            binding.etPhone.setText(snap.child("phone").value?.toString() ?: "")
        }

        // Save edits
        binding.btnSave.setOnClickListener {
            val updates = mapOf(
                "name" to binding.etName.text.toString().trim(),
                "phone" to binding.etPhone.text.toString().trim()
            )
            userRef.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            }
        }

        // Log out
        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}