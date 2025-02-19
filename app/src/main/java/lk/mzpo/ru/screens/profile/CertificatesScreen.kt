package lk.mzpo.ru.screens.profile

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.screens.ProfileHeader
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.CertificatesViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesScreen(
    navHostController: NavHostController,
    certificatesViewModel: CertificatesViewModel = viewModel(),
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
            val test = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token_lk", "")
            LaunchedEffect(key1 = "test") {
                certificatesViewModel.getData(ctx)
            }
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
                    ProfileHeader(navHostController = navHostController)
                    //endregion

                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White, shape = RoundedCornerShape(
                                    topStart = MainRounded, topEnd = MainRounded
                                )
                            )
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded))
                    ) {


                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            Column {
                                Text(
                                    text = "Документы об обучении",
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                )
                                val firstApiFormat =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                val secondFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                val timeFrmtEnd = DateTimeFormatter.ofPattern("HH:mm")

                            }
                            val uri = LocalUriHandler.current
                            LoadableScreen(
                                loaded = certificatesViewModel.loaded,
                                error = certificatesViewModel.err
                            ) {
                                LazyColumn(
                                    Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Top
                                ) {
                                    itemsIndexed(certificatesViewModel.certificates) { _, value ->
                                        Card(
                                            modifier = Modifier.padding(10.dp),
                                            colors = CardColors(
                                                containerColor = Color.White,
                                                contentColor = Color.Black,
                                                disabledContainerColor = Color.LightGray,
                                                disabledContentColor = Color.Black
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                        ) {
                                            AsyncImage(
                                                model = value.image,
                                                contentDescription = value.image
                                            )
                                            Text(
                                                text = value.course!!, textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = "Дата: " + value.date,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 5.dp)
                                            )
                                            Text(
                                                text = "Договор: " + value.number,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 5.dp)
                                            )
                                            if (value.digital != 1) {
                                                Text(
                                                    text = "Статус: " + value.status,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 5.dp)
                                                )
                                            }
                                            if (value.date_given != null) {
                                                Text(
                                                    text = "Документ был отправлен вам на почту",
                                                    fontSize = 12.sp,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 5.dp)
                                                )
                                            }
                                            if (value.debt != 0) {
                                                Text(
                                                    text = "Для получения документа оплатите долг в размере ${value.debt}",
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 5.dp))
                                            } else if (value.digital == 1) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp),
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            uri.openUri(
                                                                "https://trayektoriya.ru/mobile/generate-certificate/${value.id}/${
                                                                    token?.trim(
                                                                        '"'
                                                                    )
                                                                }"
                                                            )
                                                        },
                                                        colors = ButtonColors(
                                                            containerColor = Primary_Green,
                                                            contentColor = Color.White,
                                                            disabledContentColor = Color.White,
                                                            disabledContainerColor = Color.Gray
                                                        ),
                                                        modifier = Modifier
                                                            .height(45.dp)
                                                            .padding(end = 2.dp),
                                                        shape = RoundedCornerShape(10.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.MailOutline,
                                                                contentDescription = "",
                                                                modifier = Modifier.padding(end = 5.dp)
                                                            )
                                                            if (value.date_given == null) {
                                                                Text(
                                                                    text = "Получить документ",
                                                                    color = Color.White
                                                                )
                                                            } else {
                                                                Text(
                                                                    text = "Отправить повторно",
                                                                    color = Color.White
                                                                )
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (certificatesViewModel.certificates.isEmpty()) {
                                        item {
                                            Column(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        top = 50.dp, start = 50.dp, end = 50.dp
                                                    ),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.diploma_svgrepo_com),
                                                    contentDescription = "",
                                                    modifier = Modifier.size(80.dp),
                                                    tint = Primary_Green
                                                )
                                                Text(
                                                    text = "Тут пока ничего нет",
                                                    fontSize = 20.sp,
                                                    modifier = Modifier.padding(top = 10.dp)
                                                )
                                                Text(
                                                    text = "Здесь будут храниться все документы, которые вы получите по окончании обучения",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.padding(vertical = 5.dp)
                                                )
                                                OutlinedButton(
                                                    onClick = {
                                                        navHostController.navigate(
                                                            "categories"
                                                        )
                                                    },
                                                    border = BorderStroke(2.dp, Primary_Green),
                                                    modifier = Modifier.padding(vertical = 10.dp)
                                                ) {
                                                    Text(
                                                        text = "Перейти в каталог",
                                                        color = Primary_Green,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }

                    }
                }
            }
        })
}
