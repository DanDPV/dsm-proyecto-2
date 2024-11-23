package com.udb.comunidad_dsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.udb.comunidad_dsm.ui.BottomNavigationBar
import com.udb.comunidad_dsm.ui.EventsScreen
import com.udb.comunidad_dsm.ui.HomeScreen
import com.udb.comunidad_dsm.ui.LoginScreen
import com.udb.comunidad_dsm.ui.UserScreen
import com.udb.comunidad_dsm.ui.theme.Proyecto2DSMTheme

class MainActivity : ComponentActivity() {

    private lateinit var navHostController: NavHostController

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Proyecto2DSMTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(
                        this,
                        googleSignInLauncher
                    ) {
                        navHostController = it
                    }
                }
            }
        }
    }

    fun firebaseAuthWithGoogle(
        account: GoogleSignInAccount?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful, proceed to HomeScreen
                    onSuccess()
                } else {
                    // Handle failed sign-in
                    val errorMessage =
                        task.exception?.localizedMessage ?: "Hubo un error en el inicio de sesión con google"
                    onError(errorMessage)
                }
            }
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account,
                onSuccess = {
                    navHostController.navigateSingleTopTo(Home.route)
                },
                onError = { errorMessage ->
                    Log.i("ERRRRRROOOORRR", "EEEEEEEERRRROORORR")
                }
            )
        } catch (e: ApiException) {
            if (e.statusCode == 12501) {

            }

            Log.d("TEST", "Google sign-in failed: ${e.statusCode}")
        }
    }
}

// Start Google Sign-In
fun signInWithGoogle(
    googleSignInClient: GoogleSignInClient,
    googleSignInLauncher: ActivityResultLauncher<Intent>
) {
    val signInIntent = googleSignInClient.signInIntent
    googleSignInLauncher.launch(signInIntent)
}

@Composable
fun App(
    mainActivity: ComponentActivity,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    initializeNavController: (NavHostController) -> Unit
) {
    Proyecto2DSMTheme {
        val navController = rememberNavController()

        LaunchedEffect(key1 = Unit) {
            initializeNavController(navController)
        }

        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1052594127252-ui7d1r7up91g1d9hc5968m7rbnl720kq.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(mainActivity, gso)

        // Change the variable to this and use Overview as a backup screen if this returns null
        val currentScreen = screens.find { it.route == currentDestination?.route } ?: Login
        Scaffold(
            bottomBar = {
                if (currentScreen.route in menuScreens.map { it.route }) {
                    BottomNavigationBar(
                        navigateTo = { route ->
                            navController.navigateSingleTopTo(route)
                        },
                        currentRoute = currentScreen,
                        screens = menuScreens,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        secondaryColor = MaterialTheme.colorScheme.inversePrimary,
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (auth.currentUser != null) Home.route else Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Home.route) {
                    HomeScreen(
                        navigateTo = { route ->
                            navController.navigateSingleTopTo(route)
                        },
                        auth = auth
                    )
                }

                composable(route = Login.route) {
                    LoginScreen(
                        navigateTo = { route ->
                            navController.navigateSingleTopTo(route)
                        },
                        onGoogleSignInClick = {
                            signInWithGoogle(
                                googleSignInClient,
                                googleSignInLauncher
                            )
                        }
                    )
                }

                composable(route = Events.route) {
                    EventsScreen(
                        navigateTo = { route ->
                            navController.navigateSingleTopTo(route)
                        },
                        auth = auth
                    )
                }

                composable(route = Configuration.route) {
                    UserScreen(
                        navigateTo = { route ->
                            navController.navigateSingleTopTo(route)
                        },
                        auth = auth
                    )
                }

            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }