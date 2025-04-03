package com.example.iotbazar

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    val userName = user?.displayName ?: "Unknown User"
    val userEmail = user?.email ?: "No Email Available"
    val profilePic = user?.photoUrl?.toString()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = "profile") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile Picture
            if (!profilePic.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(profilePic)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info
            Text(userName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(userEmail, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Tagline
            Text(
                text = "🚀 We provide ready-to-use projects!\nClick on 'Custom Project' to explore.\nContact us using email and whatsapp only.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Options
            listOf(
                Triple("Request Custom Project", Icons.Filled.Build, "Custom Project Inquiry"),
                Triple("Bulk Order Inquiry", Icons.Filled.ShoppingCart, "Bulk Order Inquiry"),
                Triple("Give Feedback", Icons.Filled.Info, "Feedback for IoT Bazar"),
            ).forEach { (label, icon, subject) ->
                CustomEmailButton(label, icon, "iotbazar.support@gmail.com", subject, context)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // WhatsApp Contact Button
            Button(
                onClick = { openWhatsApp(context, "8679******") }, // ✅ WhatsApp Number
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // ✅ Theme Color
            ) {
                Image(
                    painter = painterResource(id = R.drawable.whatsapp), // ✅ WhatsApp Logo from Drawable
                    contentDescription = "WhatsApp",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text("Contact Us on WhatsApp")
            }
        }
    }
}

@Composable
fun CustomEmailButton(label: String, icon: ImageVector, email: String, subject: String, context: Context) {
    Button(
        onClick = { sendEmail(context, email, subject, "") },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // ✅ Theme Color
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.padding(end = 8.dp))
        Text(label)
    }
}

// ✅ Function to Open WhatsApp Chat
private fun openWhatsApp(context: Context, phoneNumber: String) {
    val formattedNumber = "+918679930799" // Ensure country code is included
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = android.net.Uri.parse("https://wa.me/$formattedNumber")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp is not installed!", Toast.LENGTH_SHORT).show()
    }
}

// ✅ Function to Send Email
private fun sendEmail(context: Context, to: String, subject: String, body: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Send Email"))
    } catch (e: Exception) {
        Toast.makeText(context, "No email app found!", Toast.LENGTH_SHORT).show()
    }
}
