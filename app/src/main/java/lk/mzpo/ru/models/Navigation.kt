package lk.mzpo.ru.models

import android.util.Log
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import lk.mzpo.ru.R
import lk.mzpo.ru.ui.theme.Active_Green
import lk.mzpo.ru.ui.theme.Passive_Green


@Composable
fun BottomNavigationMenu(
    navController: NavController
)
{
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
            Log.d("mylog", currentRoute.toString()+" "+item.route.toString())
            if(currentRoute?.contains(item.route) == true)
            {
                selected = true
            } else if(currentRoute?.contains("catalog") == true)
            {
                if(item.route == "categories")
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
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "Icon"
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = Active_Green,
                unselectedContentColor = Passive_Green
            )
        }
    }
}

sealed class BottomItem(val title: String, val icon: Int, val route: String)
{
    object  Study: BottomItem("Обучение", R.drawable.baseline_school_24, "study")
    object  Cats: BottomItem("Каталог", R.drawable.baseline_shopping_basket_24, "catalog")
    object  Catalog: BottomItem("Каталог", R.drawable.baseline_shopping_basket_24, "categories")
    object  Home: BottomItem("Главная", R.drawable.baseline_home_24, "home")
    object  Cart: BottomItem("Корзина", R.drawable.baseline_shopping_cart_24, "cart")
    object  Profile: BottomItem("Профиль", R.drawable.baseline_person_24, "profile")
    object  Notifications: BottomItem("Уведомления", R.drawable.baseline_notifications_24, "notifications")
}
