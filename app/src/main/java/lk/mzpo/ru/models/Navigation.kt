package lk.mzpo.ru.models

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lk.mzpo.ru.R
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.ui.theme.Active_Green
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.Passive_Green
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.CartViewModel


@Composable
fun BottomNavigationMenuVVV(
    navController: NavHostController,
    cart: MutableState<Int> = mutableStateOf(0),
    auth_tested: MutableState<AuthStatus>? = null
) {
    val cart_val = remember {
        cart
    }
    val ctxx = LocalContext.current
    val test = ctxx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token", "")
    val auth = remember {
        mutableStateOf(AuthStatus.TEST)
    }
    if (auth_tested == null || auth_tested.value == AuthStatus.TEST) {
        AuthService.testAuth(ctxx, navController, auth, false);
    } else {
        auth.value = auth_tested.value
    }
    val token_lk = test.getString("token_lk", "")
    LaunchedEffect(key1 = auth.value, block = {
        Log.d("MyLog", auth.value.name.toString())

        if (auth.value == AuthStatus.AUTH) {
            Log.d("CartLog", "LK")
            CartViewModel.getLkCartSum(ctxx, cart_val)
        } else if (auth.value == AuthStatus.GUEST) {
            FirebaseHelpers.getCartSum(token, cart_val)
        }
    })
    val list = listOf(
        BottomItem.Home,
        BottomItem.Catalog,
        BottomItem.Cart,
        BottomItem.Study,
        BottomItem.Profile,
    )

    BottomNavigation(backgroundColor = Color.White) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        list.forEach { item ->

            var selected = false
            if (currentRoute?.contains(item.route) == true) {
                selected = true
            } else if (currentRoute?.contains("catalog") == true) {
                if (item.route == "categories" || item.route.contains("course")) {
                    selected = true
                }
            }

            BottomNavigationItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                    if (item.title == "Корзина") {

                        BadgedBox(badge = {
                            Badge {
                                Text(
                                    text = cart_val.value.toString(),
                                    color = Color.White
                                )
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = "Icon",

                                )
                        }

                    } else {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = "Icon",


                            )
                    }

                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = Active_Green,
                unselectedContentColor = Passive_Green,
            )
        }
    }
}

sealed class BottomItem(val title: String, val icon: Int, val route: String) {
    object Study : BottomItem("Обучение", R.drawable.baseline_school_24, "contracts")
    object Cats : BottomItem("Каталог", R.drawable.baseline_shopping_basket_24, "catalog")
    object Catalog : BottomItem("Каталог", R.drawable.baseline_shopping_basket_24, "categories")
    object Home : BottomItem("Главная", R.drawable.baseline_home_24, "home")
    object Cart : BottomItem("Корзина", R.drawable.baseline_shopping_cart_24, "cart")
    object Profile : BottomItem("Профиль", R.drawable.baseline_person_24, "profile")
    object Notifications :
        BottomItem("Уведомления", R.drawable.baseline_notifications_24, "notifications")
}

sealed class ProfileItem(val title: String, val icon: Int, val route: String) {
    object Private :
        ProfileItem("Личные данные", R.drawable.baseline_assignment_ind_24, "profile/user_data")

    object Bills : ProfileItem("Мои счета", R.drawable.baseline_payment_24, "profile/bills")
    object Edit : ProfileItem(
        "Редактировать данные",
        androidx.appcompat.R.drawable.abc_edit_text_material,
        "profile/edit"
    )

    object Reviews : ProfileItem("Отзывы", R.drawable.baseline_reviews_24, "profile/reviews")
    object Jobs : ProfileItem("Трудоустройство", R.drawable.baseline_work_24, "profile/jobs")
    object Help : ProfileItem("Сообщить о проблеме", R.drawable.devel, "profile/help")
    object Docs : ProfileItem("Документы", R.drawable.baseline_document_scanner_24, "profile/docs")
    object Certs :
        ProfileItem("Документы об обучении", R.drawable.diploma_svgrepo_com, "profile/certs")

    object Contacts : ProfileItem("Контакты", R.drawable.baseline_map_24, "profile/contacts")
    object Schedule :
        ProfileItem("Моё расписание", R.drawable.baseline_calendar_month_24, "profile/schedule")
}



@Composable
fun BottomNavigationMenu(
    navController: NavHostController,
    cart: MutableState<Int> = mutableStateOf(0),
    auth_tested: MutableState<AuthStatus>? = null
) {
    val cart_val = remember {
        cart
    }
    val ctxx = LocalContext.current
    val test = ctxx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token", "")
    val auth = remember {
        mutableStateOf(AuthStatus.TEST)
    }
    if (auth_tested == null || auth_tested.value == AuthStatus.TEST) {
        AuthService.testAuth(ctxx, navController, auth, false);
    } else {
        auth.value = auth_tested.value
    }
    val token_lk = test.getString("token_lk", "")
    LaunchedEffect(key1 = auth.value, block = {
        Log.d("MyLog", auth.value.name.toString())

        if (auth.value == AuthStatus.AUTH) {
            Log.d("CartLog", "LK")
            CartViewModel.getLkCartSum(ctxx, cart_val)
        } else if (auth.value == AuthStatus.GUEST) {
            FirebaseHelpers.getCartSum(token, cart_val)
        }
    })
    val list = listOf(
        BottomItem.Home,
        BottomItem.Catalog,
        BottomItem.Cart,
        BottomItem.Study,
        BottomItem.Profile,
    )

    Box {
        BottomNavigation(
            backgroundColor = Color.White,
            modifier = Modifier
                .height(64.dp) // Увеличиваем высоту меню
                .zIndex(0f) // Меню должно быть под корзиной
        ) {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            list.forEach { item ->
                val selected = currentRoute?.contains(item.route) == true ||
                        (currentRoute?.contains("catalog") == true &&
                                (item.route == "categories" || item.route.contains("course")))

                if (item.title != "Корзина") {
                    BottomNavigationItem(
                        selected = selected,
                        onClick = { navController.navigate(item.route) },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = "Icon",
                            )
                        },
                        label = {
                            Text(text = item.title, fontSize = 9.sp)
                        },
                        selectedContentColor = Active_Green,
                        unselectedContentColor = Passive_Green,
                    )
                } else {
                    BottomNavigationItem(
                        selected = false,
                        onClick = {  },
                        label = {
                            Text(text = "", fontSize = 9.sp)
                        },
                        selectedContentColor = Color.Transparent,
                        unselectedContentColor = Color.Transparent,
                        icon = {}
                    )
                }
            }
        }

        // Кнопка корзины поверх меню
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter) // Центрируем по нижнему краю
                .absoluteOffset(y = -10.dp) // Выносим корзину вверх
                .size(64.dp) // Делаем кнопку крупнее
                .background(Primary_Green, CircleShape)
                .clip(CircleShape)
                .clickable {
                     navController.navigate(BottomItem.Cart.route)
                }
                .zIndex(1f) // Размещаем корзину выше BottomNavigation
        ) {


                BadgedBox(
                    badge = {
                        if (cart_val.value > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Aggressive_red, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cart_val.value.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = BottomItem.Cart.icon),
                        contentDescription = "Cart Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp) // Делаем иконку больше
                    )
                }

        }
    }

}


@Preview
@Composable
fun menu_test() {
    val nav = rememberNavController();
    val cart = remember {
        mutableStateOf(2)
    }
//    BottomNavigationMenu(navController = nav)
    Scaffold (
        content = { Text(text = "")},
        bottomBar = {
//            BottomNavigationMenuTest(navController = nav, cart = cart)
        }
    )
}