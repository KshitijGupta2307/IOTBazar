package com.example.iotbazar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Logged out!", Toast.LENGTH_SHORT).show()
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
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Picture
            user?.photoUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info
            Text(user?.displayName ?: "Unknown User", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(user?.email ?: "No Email Available", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Tagline
            Text(
                "🚀 Ready-to-use projects. Contact us via email or WhatsApp.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            ContactButtons(context)
        }
    }
}

@Composable
fun ContactButtons(context: Context) {
    val buttonSpacing = 12.dp

    Column(modifier = Modifier.fillMaxWidth()) {
        OpenWebsiteButton(
            label = "Request Custom Project",
            icon = Icons.Filled.Build,
            url = "https://kshitijgupta2307.github.io/IOT-BAZAAR/",
            context = context
        )

        Spacer(modifier = Modifier.height(buttonSpacing))

        FeedbackButton(context)

        Spacer(modifier = Modifier.height(buttonSpacing))

        WhatsAppButton(context)
    }
}

@Composable
fun OpenWebsiteButton(label: String, icon: ImageVector, url: String, context: Context) {
    Button(
        onClick = { openWebsite(context, url) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.padding(end = 8.dp))
        Text(label)
    }
}

@Composable
fun FeedbackButton(context: Context) {
    Button(
        onClick = { openFeedbackForm(context) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(Icons.Filled.Info, contentDescription = "Give Feedback", modifier = Modifier.padding(end = 8.dp))
        Text("Give Feedback")
    }
}

@Composable
fun WhatsAppButton(context: Context) {
    Button(
        onClick = { openWhatsApp(context, "+917831864073") },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(id = R.drawable.whatsapp),
            contentDescription = "WhatsApp",
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        Text("Contact Us on WhatsApp")
    }
}

// Utilities
private fun openWebsite(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open the website.", Toast.LENGTH_SHORT).show()
    }
}

private fun openWhatsApp(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${phoneNumber.replace("+", "")}"))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp is not installed!", Toast.LENGTH_SHORT).show()
    }
}

private fun openFeedbackForm(context: Context) {
    val url = "https://forms.gle/3NCwebZd23KNhhKYA"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open the form.", Toast.LENGTH_SHORT).show()
    }
}
