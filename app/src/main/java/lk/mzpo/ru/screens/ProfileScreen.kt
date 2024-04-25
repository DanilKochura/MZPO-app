package lk.mzpo.ru.screens

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.ProfileItem
import lk.mzpo.ru.models.User
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    token: String,
    navHostController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0)
) {
    Scaffold(
//            bottomBar = { BottomNavigationMenu(navController = nav)  },
//            topBar = {
//                if (!topBarDisabled.value) {
//                    TopAppBar(backgroundColor = MaterialTheme.colorScheme.background)
//                    {
//                        Text(
//                            title.value,
//                            fontSize = 22.sp,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                    }
//                }
//            },
        bottomBar = { BottomNavigationMenu(navController = navHostController, cart = cart_sum) },
        content = { padding ->
            val ctx = LocalContext.current
            AuthService.testAuth(ctx, navHostController, profileViewModel.auth_tested)
            LaunchedEffect(key1 = profileViewModel.auth_tested.value, block = {
                if (profileViewModel.auth_tested.value == AuthStatus.AUTH  ) {
                    if (profileViewModel.user.value.id == 0) {
                        profileViewModel.getData(token = token, ctx)
                    }
                }
            })






            Box(
                Modifier
                    .background(color = Primary_Green)
                    .fillMaxSize()
            ) {
                //region Top
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.lebed),
                        contentDescription = "lebed_back",
                        modifier = Modifier.padding(1.dp)
                    )
                }
                //endregion

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    //region Header
                    ProfileHeader(navHostController = navHostController)
                    //endregion

                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(
                                    topStart = MainRounded,
                                    topEnd = MainRounded
                                )
                            )
                            .verticalScroll(rememberScrollState())
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded))
                    ) {

                        val context = LocalContext.current

                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp)
                        ) {
                            LoadableScreen(loaded = profileViewModel.loaded, error = profileViewModel.error)
                            {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(Modifier.padding(start = 10.dp)) {
                                        Text(
                                            text = profileViewModel.user.value.name,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = profileViewModel.user.value.phone)
                                        Text(text = "Редактировать профиль",
                                            color = Color.LightGray,
                                            modifier = Modifier
                                                .padding(vertical = 10.dp)
                                                .clickable {
                                                    val gson = Gson()
                                                    val user = gson.toJson(
                                                        profileViewModel.user.value,
                                                        User::class.java
                                                    )
                                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "USER",
                                                        user
                                                    )

                                                    navHostController.navigate("profile/private")
                                                })
                                    }
                                    Column(Modifier) {
                                        if (profileViewModel.user.value.avatar !== null) {
                                            AsyncImage(
                                                model = "https://lk.mzpo-s.ru/build/images/" + profileViewModel.user.value.avatar,
                                                contentDescription = "",
                                                modifier = Modifier

                                                    .size(80.dp)
                                                    .clip(
                                                        CircleShape
                                                    )
                                            )
                                        } else {
                                            Box(modifier = Modifier
                                                .clip(CircleShape)
                                                .border(1.dp, Color.LightGray, CircleShape)
                                                .padding(5.dp))
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = "",
                                                    tint = Color.LightGray,
                                                    modifier = Modifier
                                                        .size(70.dp)
                                                        .padding(10.dp)

                                                )
                                            }
                                        }
                                    }
                                }
                                Divider()
                                val Nav = listOf(
//                                    ProfileItem.Private,
                                    ProfileItem.Bills,
                                    ProfileItem.Jobs,

                                    ProfileItem.Schedule,
                                    ProfileItem.Contacts
                                );

                                Column {
                                    for (i in Nav) {
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val gson = Gson()
                                                    val user = gson.toJson(
                                                        profileViewModel.user.value,
                                                        User::class.java
                                                    )
                                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "USER",
                                                        user
                                                    )

                                                    navHostController.navigate(i.route)
                                                }
                                                .padding(horizontal = 5.dp, vertical = 15.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween) {
                                            Row {
                                                Icon(
                                                    painter = painterResource(id = i.icon),
                                                    contentDescription = i.title,
                                                    tint = Color.Gray
                                                )
                                                Text(text = i.title, Modifier.padding(start = 5.dp))
                                            }
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = ""
                                            )
                                        }
                                        Divider()
                                    }
                                }
                            }


                            val NavError = listOf(
                                ProfileItem.Help,
                                ProfileItem.Reviews,
                            )
                            Column {
                                for (i in NavError) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val gson = Gson()
                                                val user = gson.toJson(
                                                    profileViewModel.user.value,
                                                    User::class.java
                                                )
                                                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                    "USER",
                                                    user
                                                )

                                                navHostController.navigate(i.route)
                                            }
                                            .padding(horizontal = 5.dp, vertical = 15.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween) {
                                        Row {
                                            Icon(
                                                painter = painterResource(id = i.icon),
                                                contentDescription = i.title,
                                                tint = Color.Gray
                                            )
                                            Text(text = i.title, Modifier.padding(start = 5.dp))
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = ""
                                        )
                                    }
                                    Divider()
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {

                                        val test = context.getSharedPreferences(
                                            "session",
                                            Context.MODE_PRIVATE
                                        )
                                        test.edit().remove("token_lk").apply()
                                        test.edit().remove("auth_data").apply()
                                        FirebaseHelpers.getCartSum(token, cart_sum)

                                        navHostController.navigate("profile")

                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Aggressive_red)
                                ) {
                                    Text(text = "Выйти")
                                }
//                                Text(text = "Версия:" + System.getProperty("lk.mzpo-s.com.app-version"))
                            }

                        }

                    }
                }
            }
        }
    )
}


@Composable
fun ProfileHeader(navHostController: NavHostController, auth: Boolean = false) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navHostController.navigateUp() }, modifier = Modifier.weight(1f)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "", tint = Color.White)
        }
        Row(modifier = Modifier.weight(7f), horizontalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.mirk_logo),
                contentDescription = "MirkLogo",
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.mzpo_logo),
                contentDescription = "MzpoLogo",
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
        IconButton(onClick = {
                             if(auth)
                             {
                                 navHostController.navigate("notifications")
                             }

        }, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "bell",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}