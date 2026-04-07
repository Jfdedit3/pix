package com.jfdedit3.pix.ui

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jfdedit3.pix.auth.SessionStore
import com.jfdedit3.pix.auth.UserSession
import com.jfdedit3.pix.data.Artwork
import com.jfdedit3.pix.data.DemoData

private sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "Home")
    data object Search : Screen("search", "Search")
    data object Ranking : Screen("ranking", "Ranking")
    data object Bookmarks : Screen("bookmarks", "Bookmarks")
    data object Profile : Screen("profile", "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixApp() {
    val context = LocalContext.current
    val store = remember { SessionStore(context) }
    var session by remember { mutableStateOf(store.read()) }

    if (session == null || session?.isConnected != true) {
        EmbeddedPixivLoginScreen(
            onContinue = {
                val newSession = UserSession(
                    displayName = "Pixiv Web User",
                    sessionValue = "embedded_web_session",
                    refreshValue = "",
                    isConnected = true
                )
                store.save(newSession)
                session = newSession
            }
        )
        return
    }

    val navController = rememberNavController()
    val screens = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Ranking,
        Screen.Bookmarks,
        Screen.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Pix") })
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(screen.label) },
                        icon = { Text("", modifier = Modifier.size(1.dp)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                ArtworkListScreen(title = "Following", artworks = DemoData.feed)
            }
            composable(Screen.Search.route) {
                SearchScreen()
            }
            composable(Screen.Ranking.route) {
                ArtworkListScreen(title = "Daily Ranking", artworks = DemoData.ranking)
            }
            composable(Screen.Bookmarks.route) {
                ArtworkListScreen(title = "Bookmarks", artworks = DemoData.bookmarks)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    session = session!!,
                    onLogout = {
                        store.clear()
                        session = null
                    }
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun EmbeddedPixivLoginScreen(onContinue: () -> Unit) {
    var currentUrl by rememberSaveable { mutableStateOf("https://accounts.pixiv.net/login") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Pixiv web login", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("The Pixiv website is displayed directly inside the app.")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { webViewRef?.goBack() }, modifier = Modifier.weight(1f), enabled = webViewRef?.canGoBack() == true) {
                Text("Back")
            }
            Button(onClick = { webViewRef?.reload() }, modifier = Modifier.weight(1f)) {
                Text("Reload")
            }
        }
        Text(currentUrl, style = MaterialTheme.typography.bodySmall)
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewRef = this
                    CookieManager.getInstance().setAcceptCookie(true)
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.loadsImagesAutomatically = true
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            currentUrl = url ?: currentUrl
                            super.onPageFinished(view, url)
                        }
                    }
                    loadUrl(currentUrl)
                }
            },
            update = { view ->
                webViewRef = view
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text("Continue to app")
        }
    }
}

@Composable
private fun ArtworkListScreen(title: String, artworks: List<Artwork>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        items(artworks) { artwork ->
            ArtworkCard(artwork)
        }
    }
}

@Composable
private fun ArtworkCard(artwork: Artwork) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(artwork.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("by ${artwork.author}")
            Text(artwork.tags.joinToString(prefix = "#", separator = " #"))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${artwork.likes} likes")
                Text(if (artwork.bookmarked) "Bookmarked" else "Not bookmarked")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    val results = DemoData.feed.filter {
        query.isBlank() ||
            it.title.contains(query, ignoreCase = true) ||
            it.author.contains(query, ignoreCase = true) ||
            it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            active = false,
            onActiveChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search illustrations, artists, tags") }
        ) {}
        ArtworkListScreen(title = "Results", artworks = results)
    }
}

@Composable
private fun ProfileScreen(session: UserSession, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(session.displayName)
        Text("Connected: ${session.isConnected}")
        Text("Following: 128")
        Text("Bookmarks: ${DemoData.bookmarks.size}")
        Text("Web login shown inside the app")
        TextButton(onClick = onLogout) {
            Text("Log out")
        }
    }
}
