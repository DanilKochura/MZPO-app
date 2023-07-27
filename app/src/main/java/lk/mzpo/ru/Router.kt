package lk.mzpo.ru

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import lk.mzpo.ru.models.BottomItem
import lk.mzpo.ru.screens.CartScreen
import lk.mzpo.ru.screens.CatalogScreen
import lk.mzpo.ru.screens.CategoriesScreen
import lk.mzpo.ru.screens.Main
import lk.mzpo.ru.screens.ProfileScreen
import lk.mzpo.ru.screens.StudyScreen

@Composable
fun NavGraph(
    navHostController: NavHostController,

    ) {
    NavHost(navController = navHostController, startDestination = "home") {
        composable("profile") {
            ProfileScreen(navHostController = navHostController)
        }
        composable("categories")
        {
            CategoriesScreen(navHostController = navHostController)
        }
        composable("study") {
            StudyScreen(navHostController = navHostController)
        }
        composable("notifications") {
            InDev()
        }
        composable("home")
        {
            Main(navHostController)
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
                InDev()
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
            Text(text = "Страница находится в разработке: ")
            if(text !== null)
            {
                Text(text = text)
            }
        }

    }
}

