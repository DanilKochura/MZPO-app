package lk.mzpo.ru.models

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import lk.mzpo.ru.R
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.ui.theme.Active_Green
import lk.mzpo.ru.ui.theme.Passive_Green
import lk.mzpo.ru.viewModel.CartViewModel
import kotlin.math.log


@Composable
fun BottomNavigationMenu(
    navController: NavHostController, cart: MutableState<Int> = mutableStateOf(0), auth_tested: MutableState<AuthStatus>? = null
)
{
        val cart_val = remember {
            cart
        }
        val ctxx = LocalContext.current
        val test = ctxx.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token", "")
        val auth =  remember {
            mutableStateOf(AuthStatus.TEST)
        }
        if(auth_tested == null || auth_tested.value == AuthStatus.TEST)
        {
            AuthService.testAuth(ctxx, navController, auth, false);
        } else
        {
            auth.value = auth_tested.value
        }
        val token_lk = test.getString("token_lk", "")
        LaunchedEffect(key1 = auth.value, block = {
            Log.d("MyLog", auth.value.name.toString())

            if (auth.value == AuthStatus.AUTH)
            {
                Log.d("CartLog", "LK")
                CartViewModel.getLkCartSum(ctxx, cart_val)
            } else if(auth.value == AuthStatus.GUEST)
            {
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

    BottomNavigation( backgroundColor = Color.White) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        list.forEach { item ->

            var selected = false
            if(currentRoute?.contains(item.route) == true)
            {
                selected = true
            } else if(currentRoute?.contains("catalog") == true)
            {
                if(item.route == "categories" || item.route.contains("course"))
                {
                    selected = true
                }
            }

            BottomNavigationItem(
                selected = selected,
                onClick = {
                     navController.navigate(item.route)
                },
                icon = {
                   if (item.title == "Корзина")
                   {

                       BadgedBox(badge = {Badge{ Text(text = cart_val.value.toString(), color = Color.White)}}) {
                           Icon(
                               painter = painterResource(id = item.icon),
                               contentDescription = "Icon",

                           )
                       }

                   } else
                   {
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

sealed class BottomItem(val title: String, val icon: Int, val route: String)
{
    object  Study: BottomItem("Обучение", R.drawable.baseline_school_24, "contracts")
    object  Cats: BottomItem("Каталог", R.drawable.baseline_shopping_basket_24, "catalog")
    object  Catalog: BottomItem("Каталог", R.drawable.baseline_shopping_basket_24, "categories")
    object  Home: BottomItem("Главная", R.drawable.baseline_home_24, "home")
    object  Cart: BottomItem("Корзина", R.drawable.baseline_shopping_cart_24, "cart")
    object  Profile: BottomItem("Профиль", R.drawable.baseline_person_24, "profile")
    object  Notifications: BottomItem("Уведомления", R.drawable.baseline_notifications_24, "notifications")
}

sealed class ProfileItem(val title: String, val icon: Int, val route: String)
{
    object  Private: ProfileItem("Личные данные", R.drawable.baseline_assignment_ind_24, "profile/user_data")
    object  Bills: ProfileItem("Мои счета", R.drawable.baseline_payment_24, "profile/bills")
    object  Edit: ProfileItem("Редактировать данные", androidx.appcompat.R.drawable.abc_edit_text_material, "profile/edit")
    object  Reviews: ProfileItem("Отзывы", R.drawable.baseline_reviews_24, "profile/reviews")
    object  Jobs: ProfileItem("Трудоустройство", R.drawable.baseline_work_24, "profile/jobs")
    object  Help: ProfileItem("Сообщить о проблеме", R.drawable.devel, "profile/help")
    object  Docs: ProfileItem("Документы", R.drawable.baseline_document_scanner_24, "profile/docs")
    object  Certs: ProfileItem("Документы об обучении", R.drawable.diploma_svgrepo_com, "profile/certs")
    object  Contacts: ProfileItem("Контакты", R.drawable.baseline_map_24, "profile/contacts")
    object  Schedule: ProfileItem("Моё расписание", R.drawable.baseline_calendar_month_24, "profile/schedule")
}
