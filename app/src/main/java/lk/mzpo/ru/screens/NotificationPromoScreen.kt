package lk.mzpo.ru.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.network.retrofit.Data2Amo
import lk.mzpo.ru.network.retrofit.SendDataToAmo
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.components.NameTextField
import lk.mzpo.ru.ui.components.PhoneTextField
import lk.mzpo.ru.ui.components.Privacy
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.NotificationPromoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPromoScreen(
    navHostController: NavHostController,
    url: String, cart_sum: MutableState<Int> = mutableStateOf(0),
    notificationPromoViewModel: NotificationPromoViewModel = viewModel()
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
            val context = LocalContext.current
            notificationPromoViewModel.getData(context, url)
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

                    //region Search
                    ProfileHeader(navHostController = navHostController, true)
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
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LoadableScreen(loaded = notificationPromoViewModel.loaded)
                        {
                            if (notificationPromoViewModel.story.value.id !== null) {
                                val promo = notificationPromoViewModel.story.value
                                var name  = remember { mutableStateOf(TextFieldValue("")) }
                                var email  = remember { mutableStateOf(TextFieldValue("")) }
                                var phone  = remember { mutableStateOf("") }
                                val context = LocalContext.current
                                var nameError = remember {
                                    mutableStateOf(false)
                                }
                                var emailError = remember {
                                    mutableStateOf(false)
                                }
                                var phoneError = remember {
                                    mutableStateOf(false)
                                }
                                Column(
                                    Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())) {
                                    Text(
                                        text = promo.name!!,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                    if (promo.banner !== null) {
                                        AsyncImage(
                                            model = promo.banner,
                                            contentDescription = "promoBanner",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        )
                                    }
                                    if(promo.description !== "")
                                    {
                                        Html(text = promo.description!!)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        NameTextField(name = name, nameError)
                                        PhoneTextField(phone = phone, phoneError)
                                        EmailTextField(email = email, emailError)
                                        Button(
                                            modifier = Modifier
                                                .fillMaxWidth(0.8f)
                                                .padding(top = 20.dp, bottom = 10.dp)
                                                .height(45.dp)
                                            ,
                                            onClick = {
                                                if(phone.value.length != 10)
                                                {
                                                    phoneError.value = true
                                                    return@Button
                                                }else
                                                {
                                                    phoneError.value = false

                                                }
                                                if(name.value.text.isEmpty())
                                                {
                                                    nameError.value = true
                                                    return@Button
                                                } else
                                                {
                                                    nameError.value = false
                                                }
                                                SendDataToAmo.sendDataToAmo(
                                                    Data2Amo(
                                                        "Записаться на "+promo.name+" с мобильного приложения",
                                                        email = email.value.text,
                                                        "Записаться на "+promo.name+" с мобильного приложения",
                                                        name.value.text.toString(),
                                                        phone = phone.value,
                                                        event = promo.event,
                                                        event_name = promo.event_name,
                                                        pipeline = promo.pipeline
                                                    ), ctx = context)
                                                 },
                                            colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Оставить заявку", color = Color.White)
                                        }
                                        Privacy()
                                        Spacer(modifier = Modifier.height(10.dp))

                                }
                            }

                        }

                    }
                }
            }
        } }
    )
}

