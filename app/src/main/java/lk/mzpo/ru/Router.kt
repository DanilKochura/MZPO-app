package lk.mzpo.ru

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import lk.mzpo.ru.models.BottomItem
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.UserData
import lk.mzpo.ru.models.study.StudyModule
import lk.mzpo.ru.screens.CartScreen
import lk.mzpo.ru.screens.CatalogScreen
import lk.mzpo.ru.screens.CategoriesScreen
import lk.mzpo.ru.screens.ContractsScreen
import lk.mzpo.ru.screens.CourseScreen
import lk.mzpo.ru.screens.LoginScreen
import lk.mzpo.ru.screens.Main
import lk.mzpo.ru.screens.ProfileScreen
import lk.mzpo.ru.screens.StudyModuleScreen
import lk.mzpo.ru.screens.StudyScreen
import lk.mzpo.ru.screens.VideoScreen
import lk.mzpo.ru.screens.profile.BillsScreen
import lk.mzpo.ru.screens.profile.PrivateScreen
import lk.mzpo.ru.screens.profile.UserDataScreen

@Composable
fun NavGraph(
    navHostController: NavHostController
    ) {
    Column(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.navigationBars)) {
        NavHost(navController = navHostController, startDestination = "profile") {
            composable("profile") {
                val context = LocalContext.current
                val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token = test.getString("token_lk", "")
                if(token.isNullOrBlank() || token.isEmpty())
                {

                    LaunchedEffect(key1 = token, block = {
                        Log.d("MyLog", "Study to login!")
                        navHostController.navigate("login")
                    })


                } else
                {
                    ProfileScreen(token = token, navHostController = navHostController)

                }
            }
            composable("profile/private"
            ){
//                val userJson =  it.arguments?.getString("user")
//
                val userJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if(userObject !== null)
                {
                    PrivateScreen(user = userObject, navHostController = navHostController)
                }
            }
            composable("profile/bills"
            ){
//                val userJson =  it.arguments?.getString("user")
//
                val userJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if(userObject !== null)
                {
                    BillsScreen(user = userObject, navHostController = navHostController)
                }
            }
            composable("profile/reviews"
            ){
//                val userJson =  it.arguments?.getString("user")
//
                val userJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if(userObject !== null)
                {
                    BillsScreen(user = userObject, navHostController = navHostController)
                }
            }
            composable("profile/jobs"
            ){
//                val userJson =  it.arguments?.getString("user")
//
                val userJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if(userObject !== null)
                {
                    BillsScreen(user = userObject, navHostController = navHostController)
                }
            }
            composable("profile/help"
            ){
//                val userJson =  it.arguments?.getString("user")
//
                val userJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if(userObject !== null)
                {
                    BillsScreen(user = userObject, navHostController = navHostController)
                }
            }
            composable("profile/docs"
            ){
//                val userJson =  it.arguments?.getString("user")
//
                val userJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if(userObject !== null)
                {
                    BillsScreen(user = userObject, navHostController = navHostController)
                }
            }
            composable("profile/contacts"
            ){
//                val userJson =  it.arguments?.getString("user")
//
                val uriHandler = LocalUriHandler.current
                uriHandler.openUri("https://www.mzpo-s.ru/contacts")
            }
            composable("profile/user_data"
            ){
//                val userJson =  it.arguments?.getString("user")
//
                val userJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if(userObject !== null)
                {
                    UserDataScreen(user = userObject, navHostController = navHostController)
                }
            }
            composable("categories")
            {
                CategoriesScreen(navHostController = navHostController)
            }
            composable("contracts") {
                val context = LocalContext.current
                val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token = test.getString("token_lk", "")
                if(token.isNullOrBlank() || token.isEmpty())
                {

                       LaunchedEffect(key1 = token, block = {
                           Log.d("MyLog", "Study to login!")
                           navHostController.navigate("login")
                       })


                } else
                {
                    Log.d("MyLog", token.toString())
                    ContractsScreen(navHostController = navHostController)
                }

            }
            composable("study")
            {
                val contractJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("Contract")
                val gson = Gson()
                val  contract = gson.fromJson( contractJson, Contract::class.java)
                if(contract !== null)
                {
                    StudyScreen(contract = contract, navHostController = navHostController)
                } else
                {
                    ContractsScreen(navHostController = navHostController)
                }
            }
            composable("study/module")
            {
                val contractJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("Contract")
                val gson = Gson()
                val  contract = gson.fromJson( contractJson, Contract::class.java)

                val moduleJson = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("StudyModule")
                val  module = gson.fromJson( moduleJson, StudyModule::class.java)
                if(contract !== null && module !== null)
                {
                    StudyModuleScreen(module = module, contract = contract, navHostController = navHostController)
                } else
                {
                    ContractsScreen(navHostController = navHostController)
                }
            }
            composable("notifications") {
                InDev()
            }
            composable("home")
            {
                Main(navHostController)
            }
            composable("login")
            {
                LoginScreen(navHostController)
            }
            composable("cart")
            {
                CartScreen(navHostController = navHostController)
            }

            composable(
                route = "course/{id}",
                arguments = listOf(navArgument("id")
                {
                    type = NavType.IntType
                })
            ) {
                val id = it.arguments?.getInt("id")
                if (id != null) {
                    CourseScreen(navHostController = navHostController, id = id)
//                CoursePage(id = id, navController = navHostController)
                }
            }
            composable(
                route = "video?url={url}",
                arguments = listOf(navArgument("url")
                {
                    type = NavType.StringType
                    defaultValue = "url"
                    nullable = true
                })
            ) {
                val url = it.arguments?.getString("url")
                if (url != null) {
                    VideoScreen(navHostController = navHostController, url = url)
//                CoursePage(id = id, navController = navHostController)
                }
            }
            composable(
                route = BottomItem.Cats.route + "?name={name}",
                arguments = listOf(navArgument("name")
                {
                    type = NavType.StringType
                    defaultValue = "main"
                    nullable = true
                })
            ) {entry ->
                CatalogScreen(navHostController = navHostController, entry.arguments?.getString("name").toString())
            }
            composable(
                route = BottomItem.Cats.route + "?search={search}",
                arguments = listOf(navArgument("search")
                {
                    type = NavType.StringType
                    defaultValue = "main"
                    nullable = true
                })
            ) {entry ->

                CatalogScreen(navHostController = navHostController, entry.arguments?.getString("search").toString())
            }
        }
    }
}

@Composable
fun Profile() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "InDev: Profile")

    }
}


@Composable
fun Notifications() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "InDev: Notifications")

    }
}


@Composable
fun Study() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "InDev: Study")

    }
}

@Composable
fun InDev(text: String? = null) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.devel),
                contentDescription = "dev",
                modifier = Modifier.padding(5.dp)
            )
//            Text(text = "Страница находится в разработке: \nОтрисуйте и сделаю)))", textAlign = TextAlign.Center)
            Text(text = "Куда полезли? Я еще до $text не добрался \nОтрисуйте и сделаю)))", textAlign = TextAlign.Center)
//            if(text !== null)
//            {
//                Text(text = text)
//            }
        }

    }
}

