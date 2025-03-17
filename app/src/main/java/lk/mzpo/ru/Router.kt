package lk.mzpo.ru

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.gson.Gson
import lk.mzpo.ru.models.BottomItem
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.Gift
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.study.ActiveFile
import lk.mzpo.ru.models.study.NewMaterials
import lk.mzpo.ru.models.study.StudyModule
import lk.mzpo.ru.screens.CartScreen
import lk.mzpo.ru.screens.CatalogScreen
import lk.mzpo.ru.screens.CategoriesScreen
import lk.mzpo.ru.screens.ContactsScreen
import lk.mzpo.ru.screens.ContractsScreen
import lk.mzpo.ru.screens.CourseScreen
import lk.mzpo.ru.screens.GiftScreen
import lk.mzpo.ru.screens.LoginScreen
import lk.mzpo.ru.screens.Main
import lk.mzpo.ru.screens.NotificationPromoScreen
import lk.mzpo.ru.screens.NotificationsScreen
import lk.mzpo.ru.screens.PdfGiftScreen
import lk.mzpo.ru.screens.PdfScreen
import lk.mzpo.ru.screens.ProfileScreen
import lk.mzpo.ru.screens.RegisterScreen
import lk.mzpo.ru.screens.StudyModuleScreen
import lk.mzpo.ru.screens.StudyScreen
import lk.mzpo.ru.screens.TestScreen
import lk.mzpo.ru.screens.VideoGiftScreen
import lk.mzpo.ru.screens.VideoScreen
import lk.mzpo.ru.screens.profile.BillsScreen
import lk.mzpo.ru.screens.profile.CertificatesScreen
import lk.mzpo.ru.screens.profile.HelpScreen
import lk.mzpo.ru.screens.profile.JobsScreen
import lk.mzpo.ru.screens.profile.PrivateScreen
import lk.mzpo.ru.screens.profile.ScheduleScreen
import lk.mzpo.ru.screens.profile.UserDataScreen
import lk.mzpo.ru.services.CustomTabHelper
import lk.mzpo.ru.ui.components.WebViewPage

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(
    navHostController: NavHostController,
    cart_sum: MutableState<Int>,
    customTabHelper: CustomTabHelper
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavHost(navController = navHostController, startDestination = "home") {
            composable("profile") {
                val context = LocalContext.current
                val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token = test.getString("token_lk", "")
                if (token.isNullOrBlank() || token.isEmpty()) {

                    LaunchedEffect(key1 = token, block = {
                        Log.d("MyLog", "Study to login!")
                        navHostController.navigate("login")
                    })


                } else {
                    ProfileScreen(
                        token = token,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )

                }
            }
            composable(
                "profile/private"
            ) {
//                val userJson =  it.arguments?.getString("user")
//
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if (userObject !== null) {
                    PrivateScreen(
                        user = userObject,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
                }
            }
            composable(
                "profile/bills"
            ) {
//                val userJson =  it.arguments?.getString("user")
//
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if (userObject !== null) {
                    BillsScreen(
                        user = userObject,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
                }
            }
            composable(
                "profile/certs"
            ) {
//                val userJson =  it.arguments?.getString("user")
                    CertificatesScreen(
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
            }
            composable(
                "profile/reviews"
            ) {
//                val userJson =  it.arguments?.getString("user")
//
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if (userObject !== null) {
                    BillsScreen(
                        user = userObject,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
                }
            }
            composable(
                "profile/jobs"
            ) {
//                val userJson =  it.arguments?.getString("user")
//
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if (userObject !== null) {
                    JobsScreen(
                        user = userObject,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
                }
            }
            composable(
                "profile/help"
            ) {
//                val userJson =  it.arguments?.getString("user")
//
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if (userObject !== null) {
                    HelpScreen(
                        user = userObject,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
                }
            }
            composable(
                "profile/schedule"
            ) {
//                val userJson =  it.arguments?.getString("user")
//
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if (userObject !== null) {
                    ScheduleScreen(
                        user = userObject,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
                }
            }
            composable(
                "page"
            ) {
//                val userJson =  it.arguments?.getString("user")
//
                val link =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("link")

                if (link !== null) {
                    NotificationPromoScreen(
                        navHostController = navHostController,
                        url = link,
                        cart_sum = cart_sum
                    )
                }
            }
            composable(
                "profile/contacts"
            ) {
//                val userJson =  it.arguments?.getString("user")
                ContactsScreen(navHostController = navHostController, cart_sum = cart_sum)
//
//                val uriHandler = LocalUriHandler.current
//                uriHandler.openUri("https://www.mzpo-s.ru/contacts")
            }
            composable(
                "profile/user_data"
            ) {
//                val userJson =  it.arguments?.getString("user")
//
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("USER")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, User::class.java)
                if (userObject !== null) {
                    UserDataScreen(
                        user = userObject,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
                }
            }
            composable("categories")
            {
                CategoriesScreen(navHostController = navHostController, cart_sum = cart_sum)
            }
            composable("contracts") {
                val context = LocalContext.current
                val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token = test.getString("token_lk", "")
                if (token.isNullOrBlank() || token.isEmpty()) {

                    LaunchedEffect(key1 = token, block = {
                        Log.d("MyLog", "Study to login!")
                        navHostController.navigate("login")
                    })


                } else {
                    Log.d("MyLog", token.toString())
                    ContractsScreen(navHostController = navHostController, cart_sum = cart_sum)
                }

            }
            composable("study")
            {

                val contractJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("Contract")
                val gson = Gson()
                val contract = gson.fromJson(contractJson, Contract::class.java)
                if (contract !== null) {
                    StudyScreen(contract = contract, navHostController = navHostController)
                } else {
                    ContractsScreen(navHostController = navHostController, cart_sum = cart_sum)
                }
            }
            composable("gift")
            {

                val contractJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("Gift")
                val gson = Gson()
                val gift = gson.fromJson(contractJson, Gift::class.java)
                if (gift !== null) {
                    GiftScreen(gift = gift, navHostController = navHostController)
                } else {
                    ContractsScreen(navHostController = navHostController, cart_sum = cart_sum)
                }
            }
            composable("study/module")
            {

                val gson = Gson()
                var contract: Contract? = null
                var module: StudyModule? = null
                try {
                    val contractJson =
                        navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("Contract")
                    contract = gson.fromJson(contractJson, Contract::class.java)

                    val moduleJson =
                        navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("StudyModule")
                    module = gson.fromJson(moduleJson, StudyModule::class.java)
                } catch (_: Exception){}

                if (contract !== null && module !== null) {
                    StudyModuleScreen(
                        module = module,
                        contract = contract,
                        navHostController = navHostController,
                        cart_sum = cart_sum
                    )
                } else {
                    ContractsScreen(navHostController = navHostController, cart_sum = cart_sum)
                }
            }
            composable("notifications") {
                InDev()
            }
            composable("home",
                deepLinks = listOf(

                )
            )
            {
                Main(navHostController, cart_sum = cart_sum)
            }
            composable("login",
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://trayektoriya.ru/login"
                        action = Intent.ACTION_VIEW
                    }
                ))
            {
                LoginScreen(navHostController, cart_sum = cart_sum)
            }
            composable("register")
            {
                RegisterScreen(navHostController, cart_sum = cart_sum)
            }
            composable("cart")
            {
                CartScreen(navHostController = navHostController, cart_sum = cart_sum, customTabHelper = customTabHelper, fromDeep =  false)
            }
            composable("cart-deep", deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://trayektoriya.ru/mobile/cart"
                    action = Intent.ACTION_VIEW
                },
                navDeepLink {
                    uriPattern = "https://trayektoriya.ru/category/cart"
                    action = Intent.ACTION_VIEW
                }
            ))
            {
                CartScreen(navHostController = navHostController, cart_sum = cart_sum, customTabHelper = customTabHelper, fromDeep =  true)
            }
            composable("notifications")
            {
                NotificationsScreen(navHostController = navHostController, cart_sum = cart_sum)
            }
            composable("contacts")
            {
                ContactsScreen(navHostController = navHostController, cart_sum = cart_sum)
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
                    CourseScreen(
                        navHostController = navHostController,
                        id = id,
                        cart_sum = cart_sum
                    )
//                CoursePage(id = id, navController = navHostController)
                }
            }
            composable(
                route = "material/{contract}/{material}/{token}",
                arguments = listOf(
                    navArgument("contract")
                    {
                        type = NavType.StringType
                    },
                            navArgument ("material")
                    {
                        type = NavType.StringType
                    },
                            navArgument ("token")
                    {
                        type = NavType.StringType
                    }
                )
            ) {
                val material = it.arguments?.getString("material")
                val token = it.arguments?.getString("token")
                val contract = it.arguments?.getString("contract")
                if (material != null) {
                    WebViewPage(
                        url = "https://trayektoriya.ru/mobile/study/${contract}/${material}/${token}"
                    )
//                CoursePage(id = id, navController = navHostController)
                }
            }
            composable(
                "video",
            ) {
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("video")
                val contract =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("contract")
                val gson = Gson()
                val material = gson.fromJson(userJson, NewMaterials::class.java)
                if (material !== null && contract !== null) {
                    VideoScreen(navHostController = navHostController, video = material, contract.toInt())
                } else
                {
                    ContractsScreen(navHostController = navHostController, cart_sum = cart_sum)
                }
            }
            composable(
                "gift/video",
            ) {
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("video")
                val gift =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("gift")
                val gson = Gson()
                val material = gson.fromJson(userJson, ActiveFile::class.java)
                if (material !== null && gift !== null) {
                    VideoGiftScreen(navHostController = navHostController, video = material, gift.toInt())
                } else
                {
                    ContractsScreen(navHostController = navHostController, cart_sum = cart_sum)
                }
            }
            composable(
                "pdf",
            ) {
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("MATERIAL")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, NewMaterials::class.java)
                val contract =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<Int>("CONTRACT")
                if (userObject !== null && contract !== null) {
                    PdfScreen(navHostController = navHostController, material = userObject, contract = contract)
                } else
                {
                    navHostController.navigate("study")
                }
            }
            composable(
                "gift/pdf",
            ) {
                val userJson =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("MATERIAL")
                val gson = Gson()
                val userObject = gson.fromJson(userJson, ActiveFile::class.java)
                val contract =
                    navHostController.previousBackStackEntry?.savedStateHandle?.get<Int>("gift")
                if (userObject !== null && contract !== null) {
                    PdfGiftScreen(navHostController = navHostController, material = userObject, gift = contract)
                } else
                {
                    navHostController.navigate("study")
                }
            }
            composable(
                route = "test/{contract}/{test}",
                arguments = listOf(
                    navArgument("contract") { type = NavType.IntType },
                    navArgument("test") { type = NavType.IntType },
                )
            ) { backStackEntry ->
                val contract = backStackEntry.arguments?.getInt("contract")
                val test = backStackEntry.arguments?.getInt("test")
                if (test !== null && contract !== null) {
                    TestScreen(
                        navHostController = navHostController,
                        test = test,
                        contract = contract,
                        cart_sum = cart_sum
                    )
                } else {
                    navHostController.navigateUp()
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
            ) { entry ->
                CatalogScreen(
                    navHostController = navHostController,
                    entry.arguments?.getString("name").toString(),
                    cart_sum = cart_sum
                )
            }
            composable(
                route = BottomItem.Cats.route + "?search={search}",
                arguments = listOf(navArgument("search")
                {
                    type = NavType.StringType
                    defaultValue = "main"
                    nullable = true
                })
            ) { entry ->

                CatalogScreen(
                    navHostController = navHostController,
                    entry.arguments?.getString("search").toString(),
                    cart_sum = cart_sum
                )
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
//            Text(
//                text = "Куда полезли? Я еще до $text не добрался \nОтрисуйте и сделаю)))",
//                textAlign = TextAlign.Center
//            )
//            if(text !== null)
//            {
//                Text(text = text)
//            }
        }

    }
}

