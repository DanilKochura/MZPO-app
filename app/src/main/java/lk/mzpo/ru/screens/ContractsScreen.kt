package lk.mzpo.ru.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.extractor.text.webvtt.WebvttCssStyle.FontSizeUnit
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.User
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.ui.components.CircularProgressbar2
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.components.PickImageFromGallery
import lk.mzpo.ru.ui.components.PieChart
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.ContractsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractsScreen(
    navHostController: NavHostController,
    contractsViewModel: ContractsViewModel = viewModel(),
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
            Log.d("StudyLog", "entered")
            val context = LocalContext.current
            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token_lk", "")

            AuthService.testAuth(context, navHostController, contractsViewModel.auth_tested)

            LaunchedEffect(key1 = contractsViewModel.auth_tested.value, block = {
                if (contractsViewModel.auth_tested.value == AuthStatus.AUTH) {
                    if (token != null) {
                        contractsViewModel.getData(token, context = context)
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
                        var list = listOf("Активные", "Завершенные", "Возвраты")
                        TabRow(
                            selectedTabIndex = 0,
                            indicator = {

                            },
                            contentColor = Primary_Green,
                            containerColor = Color.White,
                            modifier = Modifier.fillMaxWidth(),

                            ) {

                            Tab(
                                selected = contractsViewModel.selected.value == "Активные",
                                onClick = {
                                    contractsViewModel.selected.value = "Активные"
                                },
                                text = { Text(text = "Активные", fontSize = 13.sp) },
                                selectedContentColor = Aggressive_red,
                                unselectedContentColor = Primary_Green
                            )
                            Tab(
                                selected = contractsViewModel.selected.value == "Завершенные",
                                onClick = {
                                    contractsViewModel.selected.value = "Завершенные"
                                },
                                text = { Text(text = "Завершенные", fontSize = 13.sp) },
                                selectedContentColor = Aggressive_red,
                                unselectedContentColor = Primary_Green
                            )
                            val courses_refund =
                                contractsViewModel.contracts.value.filter { it.status == 2 }
                            if(courses_refund.isNotEmpty())
                            {
                                Tab(
                                    selected = contractsViewModel.selected.value == "Возвраты",
                                    onClick = {
                                        contractsViewModel.selected.value = "Возвраты"
                                    },
                                    text = { Text(text = "Возвраты", fontSize = 13.sp) },
                                    selectedContentColor = Aggressive_red,
                                    unselectedContentColor = Primary_Green
                                )
                            }
                        }
                        LoadableScreen(loaded = contractsViewModel.loaded)


                        when (contractsViewModel.selected.value) {
                            "Активные" -> ActiveTab(contractsViewModel, navHostController)
                            "Завершенные" -> FinishedTab(contractsViewModel)
                            "Возвраты" -> RefunedTab(contractsViewModel)

                        }
                    }
                }
            }
        }
    )
}


@Composable
fun Login(token: String?, navHostController: NavHostController) {
    val password = remember {
        mutableStateOf(TextFieldValue("mkCF4CVa4iLyTd8"))
    }
    val login = remember {
        mutableStateOf(TextFieldValue("kochura2017@yandex.ru"))
    }
    val bl = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Log.d("MyLog", "test1")
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Авторизация", textAlign = TextAlign.Center, fontSize = 28.sp)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(
                painter = painterResource(id = R.drawable.mirk_logo),
                contentDescription = "MirkLogo",
                modifier = Modifier.size(120.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.mzpo_logo),
                contentDescription = "MzpoLogo",
                modifier = Modifier.size(120.dp)
            )
        }
        Text(
            text = "Для получения доступа к учебным материалам, войдите в аккаунт",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )


        EmailTextField(email = login, isError = bl)
        PasswordTextField(password = password, isError = bl)
        Row(
            modifier = Modifier.padding(all = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
//                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val data = AuthData(login.value.text, password.value.text, token.toString())
                    val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                    val gson = Gson()
                    test.edit().putString("auth_data", gson.toJson(data)).apply()
                    AuthService.login(data, context, navHostController)

                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(200.dp)
            ) {
                Text("Войти", color = Color.White)
            }
        }
        Row(
            modifier = Modifier.padding(all = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val urlHandler = LocalUriHandler.current
            Text(text = "Забыли пароль?", color= Color.Blue, textDecoration = TextDecoration.Underline,modifier = Modifier.clickable {
                urlHandler.openUri("https://lk.mzpo-s.ru/password/reset")
            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActiveTab(contractsViewModel: ContractsViewModel, navHostController: NavHostController) {
    val courses =
        contractsViewModel.contracts.value.filter { it.status != 0 && it.status != 2 && it.status != 3 && it.status != 4 }
    val listState: LazyListState = rememberLazyListState()
    if (courses.isNotEmpty()) {
        LazyRow(
            content = {
                itemsIndexed(courses)
                { i, contract ->
                    if (contract.course !== null) {
                        ContractCard(contract, onClick = {
                            val gson = Gson()
                            val contractJson = gson.toJson(
                                contract,
                                Contract::class.java
                            )
                            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                "Contract",
                                contractJson
                            )

                            navHostController.navigate("study")
                        })
                    }

                }
            },
            modifier = Modifier.fillMaxSize(),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinishedTab(contractsViewModel: ContractsViewModel) {
    val courses = contractsViewModel.contracts.value.filter { it.status == 0 || it.status == 4 }
    val listState: LazyListState = rememberLazyListState()
    if (courses.isNotEmpty()) {
        LazyRow(
            content = {
                itemsIndexed(courses)
                { i, contract ->
                    if (contract.course !== null) {
                        ContractCard(contract)
                    }

                }
            },
            modifier = Modifier.fillMaxSize(),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RefunedTab(contractsViewModel: ContractsViewModel) {
    val courses = contractsViewModel.contracts.value.filter { it.status == 2 }
    val listState: LazyListState = rememberLazyListState()
    if (courses.isNotEmpty()) {
        LazyRow(
            content = {
                itemsIndexed(courses)
                { i, contract ->
                    if (contract.course !== null) {
                        ContractCard(contract)
                    }

                }
            },
            modifier = Modifier.fillMaxSize(),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState)
        )
    }
//    PickImageFromGallery()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractCard(contract: Contract, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val conf = LocalConfiguration.current
    Card(
        modifier = modifier

            .padding(25.dp)
            .shadow(2.dp, RoundedCornerShape(10.dp))
            .fillMaxHeight()
            .width(conf.screenWidthDp.dp.minus(50.dp)), colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
//        Image(painter = painterResource(id = R.drawable.masage), contentDescription = "", modifier = Modifier
//            .height(150.dp)
//            .fillMaxWidth(), contentScale = ContentScale.Crop)
        AsyncImage(
            model = contract.course?.image,
            contentDescription = contract.course?.id.toString(),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .padding(7.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                contract.course!!.name,
                maxLines = 2,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
            Text(text = contract.course!!.hours.toString() + " ак.ч.", fontWeight = FontWeight.Bold)
            if (contract.status != 0 && contract.status != 2 && contract.status != 3 && contract.status != 4) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row {
                            Text(text = "Тесты: ", color = Primary_Green)
                            Text(text = contract.progress!!.tests!!)
                        }
                        Row {
                            Text(text = "Видео: ", color = Primary_Green)
                            Text(text = contract.progress!!.video!!)
                        }
                        Row {
                            Text(text = "Пособия: ", color = Primary_Green)
                            Text(text = contract.progress!!.files!!)
                        }
                    }
                    CircularProgressbar2(contract.progress!!.total!!.toFloat(), size = conf.screenHeightDp.dp.div(10))

                }
            } else if (contract.status!! > 4) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Вы успешно прошли обучение!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 7.dp)
                    )
                    if (contract.certs.isNotEmpty()) {
                        Text(
                            text = "Документ ${contract.certs[0]}",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                    }
                    if (contract.debt != 0) {
                        Text(
                            text = "Для получения документа, Вам необходимо оплатить долг в размере ${contract.debt} руб.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 7.dp)
                        )
                    }
                }
            }
            if (contract.notPassed === null && contract.status!! > 4) {
                Button(
                    onClick = {
                        onClick.invoke()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Aggressive_red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Перейти", color = Color.White)

                }
            } else {
                Button(
                    onClick = {
                        onClick.invoke()

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.LightGray,
                        contentColor = Color.White
                    ),
                    enabled = false
                ) {
                    Text("Доступ закрыт", color = Color.White)

                }
            }
        }
    }
}