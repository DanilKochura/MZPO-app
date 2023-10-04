package lk.mzpo.ru.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.User
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.PasswordTextField
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
    contractsViewModel: ContractsViewModel = viewModel()
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
        bottomBar = { BottomNavigationMenu(navController = navHostController) },
        content = { padding ->
            Log.d("StudyLog", "entered")
            val context = LocalContext.current
            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token_lk", "")

            contractsViewModel.testAuth(context, token.toString(), navHostController)
            LaunchedEffect(key1 = contractsViewModel.auth_tested.value, block = {
                if(contractsViewModel.auth_tested.value)
                {
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
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded))
                    ) {
                        var list =listOf("Активные", "Завершенные", "Возвраты")

                        TabRow(
                            selectedTabIndex = 0,
                            indicator = {

                            },
                            contentColor = Primary_Green,
                            containerColor = Color.White,
                            modifier = Modifier.fillMaxWidth(),

                        ) {
                            list.forEachIndexed { index, text ->
                                Tab(selected = contractsViewModel.selected.value == text, onClick = {
                                    contractsViewModel.selected.value = text
                                }, text = { Text(text = text) }, selectedContentColor = Aggressive_red, unselectedContentColor = Primary_Green)
                            }
                        }

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
fun Login(token: String?, navHostController: NavHostController)
{
    val password = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val login = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val bl = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Log.d("MyLog", "test1")
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Авторизация", textAlign = TextAlign.Center, fontSize = 28.sp)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly, ) {
            Image(painter = painterResource(id = R.drawable.mirk_logo), contentDescription = "MirkLogo", modifier = Modifier.size(120.dp))
            Image(painter = painterResource(id = R.drawable.mzpo_logo), contentDescription = "MzpoLogo", modifier = Modifier.size(120.dp))
        }
        Text(text = "Для получения доступа к учебным материалам, войдите в аккаунт", textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))


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
                    val test =  context.getSharedPreferences("session", Context.MODE_PRIVATE)
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
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActiveTab(contractsViewModel: ContractsViewModel, navHostController: NavHostController)
{
    val courses = contractsViewModel.courses.value.filter { it.status == 1 }
    val listState: LazyListState = rememberLazyListState()
    if(courses.isNotEmpty())
    {
        LazyRow(content = {
            itemsIndexed(courses)
            {
                    i, contract ->
                if(contract.course !== null)
                {
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
        }, modifier = Modifier.fillMaxSize(),state = listState,flingBehavior = rememberSnapFlingBehavior(listState))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinishedTab(contractsViewModel: ContractsViewModel)
{
    val courses = contractsViewModel.courses.value.filter { it.status == 0 }
    val listState: LazyListState = rememberLazyListState()
    if(courses.isNotEmpty())
    {
        LazyRow(content = {
            itemsIndexed(courses)
            {
                    i, contract ->
                if(contract.course !== null)
                {
                    ContractCard(contract)
                }

            }
        }, modifier = Modifier.fillMaxSize(),state = listState,flingBehavior = rememberSnapFlingBehavior(listState))
    }
}

@Composable
fun RefunedTab(contractsViewModel: ContractsViewModel)
{
    Text(text = "refund")
}

@Composable
fun ContractCard(contract: Contract, modifier: Modifier = Modifier, onClick: () -> Unit = {})
{
    val conf = LocalConfiguration.current
    Card(modifier = modifier

        .padding(25.dp)
        .shadow(2.dp, RoundedCornerShape(10.dp))
        .fillMaxHeight()
        .width(conf.screenWidthDp.dp.minus(50.dp))
        , colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {
//        Image(painter = painterResource(id = R.drawable.masage), contentDescription = "", modifier = Modifier
//            .height(150.dp)
//            .fillMaxWidth(), contentScale = ContentScale.Crop)
        AsyncImage(model = contract.course?.image, contentDescription = contract.course?.id.toString(), modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(), contentScale = ContentScale.Crop)
        Column(modifier = Modifier
            .padding(7.dp)
            .fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(contract.course!!.name, maxLines = 3, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp), textAlign = TextAlign.Center)
            Text(text = contract.course!!.hours.toString()+" ак.ч.", fontWeight = FontWeight.Bold)
            if (contract.status == 1)
            {
                Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
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
                            Text(text = "Файлы: ", color = Primary_Green)
                            Text(text = contract.progress!!.files!!)
                        }
                    }
                    Box(modifier = Modifier.padding(horizontal = 10.dp).size(70.dp))
                    {
                        PieChart(
                            data = mapOf(
                                Pair("Sample-1", contract.progress!!.total!!),
                                Pair("TEst", 100-contract.progress!!.total!!)
                            )
                        )
                        Text(text = contract.progress!!.total!!.toString()+"%", modifier  = Modifier.align(
                            Alignment.Center
                        ))
                    }
                }
            } else
            {
                Text(text = "Вы успешно прошли обучение!")
            }
            Button(onClick = {
                onClick.invoke()
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp), shape = RoundedCornerShape(30), colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red, contentColor = Color.White)) {
                Text("Перейти", color = Color.White)

            }
        }
    }
}