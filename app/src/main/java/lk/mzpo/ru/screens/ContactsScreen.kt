package lk.mzpo.ru.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ContactsScreen(
    navHostController: NavHostController,
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
            val bottomSheetState =
                rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
            Log.d("StudyLog", "entered")
            val context = LocalContext.current
            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token_lk", "")
            val uri = LocalUriHandler.current

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
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded))
                    ) {
                        Text(
                            text = "Контакты", fontSize = 24.sp, modifier = Modifier
                                .padding(top = 15.dp)
                                .fillMaxWidth(), textAlign = TextAlign.Center
                        )
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .background(
                                        Primary_Green.copy(0.7f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Учебный корпус:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row {
                                    Text(text = "г. Москва, м. Кузнецкий Мост, ул. Кузнецкий Мост д. 21/5 офис 4002", color = Color.White, modifier = Modifier.clickable {
                                        uri.openUri("https://yandex.ru/maps/-/CDG8fSjD")
                                    }, textDecoration = TextDecoration.Underline)
                                }
                                Text(text = "Розничный отдел, корпоративный отдел, методический отдел, финансовый отдел", color = Color.White, fontSize = 14.sp)

                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .background(
                                        Primary_Green.copy(0.7f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Административный корпус:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row {
                                    Text(text = "г. Москва, м. Кузнецкий Мост, ул. Кузнецкий Мост д. 21/5 офис 2002", color = Color.White, modifier = Modifier.clickable {
                                        uri.openUri("https://yandex.ru/maps/-/CDG8fSjD")
                                    }, textDecoration = TextDecoration.Underline)
                                }
                                Text(text = "Отдел сопровождения и обучения", color = Color.White, fontSize = 14.sp)

                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .background(
                                        Primary_Green.copy(0.7f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Учебный корпус:",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row {
                                    Text(text = "г. Москва, м. Автозаводская, ул. Ленинская Слобода, д.26, корп. С, 2 этаж, бизнес-центр «Омега-2»", color = Color.White, modifier = Modifier.clickable {
                                        uri.openUri("https://yandex.ru/maps/-/CDG8nVKA")
                                    }, textDecoration = TextDecoration.Underline)
                                }

                            }
                            Text(
                                text = "Время работы:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "будни с 9:00 до 20:00")
                            Text(text = "в субботу и воскресенье с 10:00 до 16:00")

                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "ООО \"МИРК\"",
                                fontSize = 12.sp
                            )
                            Text(text = "ИНН 7702399036", fontSize = 12.sp)
                            Text(text = "ОГРН 1167746273925", fontSize = 12.sp)
                            Text(
                                text = "Юридический адрес: 107031, МОСКВА ВН.ТЕР.Г. МУНИЦИПАЛЬНЫЙ ОКРУГ МЕЩАНСКИЙ УЛ КУЗНЕЦКИЙ МОСТ Д.21/5",
                                fontSize = 12.sp
                            )


                        }
                    }
                }
            }

        }
    )
}


