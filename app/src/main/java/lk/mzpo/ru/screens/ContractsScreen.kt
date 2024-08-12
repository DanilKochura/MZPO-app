package lk.mzpo.ru.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.exceptions.NoConnectionException
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.Gift
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.network.retrofit.BuyExtendRequest
import lk.mzpo.ru.network.retrofit.ExtendRequest
import lk.mzpo.ru.network.retrofit.RecoveryPostBody
import lk.mzpo.ru.ui.components.CircularProgressbar2
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.ContractsViewModel
import lk.mzpo.ru.viewModel.ProfileViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
            val bottomSheetState =
                rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
            Log.d("StudyLog", "entered")
            val context = LocalContext.current
            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token_lk", "")

            try {
                AuthService.testAuth(context, navHostController, contractsViewModel.auth_tested)
            } catch (_: NoConnectionException) {
                contractsViewModel.error.value = true
                contractsViewModel.loaded.value = true
            }

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
                                contractsViewModel.contracts.value.filter { it.status == 5 || it.status == 2 }
                            if (courses_refund.isNotEmpty()) {
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
                        LoadableScreen(
                            loaded = contractsViewModel.loaded,
                            error = contractsViewModel.error
                        )


                        when (contractsViewModel.selected.value) {
                            "Активные" -> ActiveTab(
                                contractsViewModel,
                                navHostController,
                                bottomSheetState
                            )

                            "Завершенные" -> FinishedTab(contractsViewModel, navHostController)
                            "Возвраты" -> RefunedTab(contractsViewModel, navHostController)

                        }

                    }
                }
            }
            ModalBottomSheetLayout(
                sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                sheetContent = {

                    Column {
                        Text(
                            text = "Перенести окончание обучения/экзамен на",
                            modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 5.dp)
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp, horizontal = 10.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        for (i in contractsViewModel.accessDates) {
                            Column(
                                modifier = Modifier
                                    .height(45.dp)
                                    .padding(end = 7.dp)
                                    .clip(
                                        RoundedCornerShape(20)
                                    )
                                    .background(if (contractsViewModel.selectedDate.value == i) Primary_Green else Color.Transparent)
                                    .border(
                                        width = 1.dp,
                                        color = if (contractsViewModel.selectedDate.value == i) Primary_Green else Color.LightGray,
                                        RoundedCornerShape(20)
                                    )
                                    .clickable {
                                        contractsViewModel.selectedDate.value = i
//                                    if (scrollstate.value > 800) {
//                                        scope.launch {
//                                            scrollstate.animateScrollTo(800)
//                                        }
//                                    }
                                    }, verticalArrangement = Arrangement.Center
                            )
                            {
                                Text(
                                    text = i,
                                    Modifier.padding(10.dp),
                                    color = if (contractsViewModel.selectedDate.value == i) Color.White else Color.Black
                                )
                            }
                        }
                    }
                    val pref = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                    val token_ = pref.getString("token_lk", "")
                    Button(
                        onClick = {


                            if (contractsViewModel.accessFree.value == "0") {
                                BuyExtendRequest().send(
                                    "Bearer " + token_?.trim('"'),
                                    BuyExtendRequest.PostBody(
                                        exam_date = contractsViewModel.selectedDate.value,
                                        module_uid = contractsViewModel.accessModuleUid.value,
                                        contract_uid = contractsViewModel.accessContractUid.value,
                                        course_hours = contractsViewModel.accessCourseHours.value
                                    )

                                ).enqueue(object : retrofit2.Callback<ResponseBody> {
                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Log.e(
                                            "API Request",
                                            "I got an error and i don't know why :("
                                        )
                                        Log.e("API Request", t.message.toString())
                                        Toast.makeText(
                                            context,
                                            "Произошла ошибка. Попробуйте позже!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        Log.d("API Request", response.body().toString())
                                        Log.d("API Request", response.message())
                                        Log.d("API Request", response.errorBody().toString())
                                        Log.d("API Request", response.raw().body.toString())
                                        if (response.isSuccessful) {
                                            navHostController.navigate("cart")
                                        }
                                    }
                                })
                            } else {
                                ExtendRequest().send(
                                    "Bearer " + token_?.trim('"'),
                                    ExtendRequest.PostBody(
                                        contract_id = contractsViewModel.accessOrder.intValue,
                                        module_id = contractsViewModel.accessModule.intValue,
                                        exam_date = contractsViewModel.selectedDate.value
                                    )

                                ).enqueue(object : retrofit2.Callback<ResponseBody> {
                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Log.e(
                                            "API Request",
                                            "I got an error and i don't know why :("
                                        )
                                        Log.e("API Request", t.message.toString())
                                        Toast.makeText(
                                            context,
                                            "Произошла ошибка. Попробуйте позже!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        Log.d("API Request", response.body().toString())
                                        Log.d("API Request", response.message())
                                        Log.d("API Request", response.errorBody().toString())
                                        Log.d("API Request", response.raw().body.toString())
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Вы успешно продлили курс",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navHostController.navigate("study")


                                        }
                                    }
                                })
                            }


                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
//                        .padding(vertical = 5.dp),
                        shape = RoundedCornerShape(30),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Aggressive_red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Продлить доступ", color = Color.White)
                    }

                },
                sheetState = bottomSheetState,
                modifier = Modifier.padding(padding)
            ) {

            }
        }
    )
}


@Composable
fun Login(token: String?, navHostController: NavHostController) {
    val password = remember {
        mutableStateOf(TextFieldValue(""))
//        mutableStateOf(TextFieldValue("12345"))
//        mutableStateOf(TextFieldValue("mkCF4CVa4iLyTd8"))
    }
    val login = remember {
        mutableStateOf(TextFieldValue(""))
//        mutableStateOf(TextFieldValue("d.kochura@mzpo.info"))
//        mutableStateOf(TextFieldValue("cooliyev@gmail.com"))
    }
    val bl = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
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
            Text(
                text = "Забыли пароль?",
                color = Color.Blue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    urlHandler.openUri("https://lk.mzpo-s.ru/password/reset")
                })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ActiveTab(
    contractsViewModel: ContractsViewModel,
    navHostController: NavHostController,
    bottomsheetState: ModalBottomSheetState
) {
    val courses =
        contractsViewModel.contracts.value.filter {
            it.status!! in intArrayOf(
                1,
                6,
                7,
                8,
                9,
                10,
                11
            )
        }
    if (courses.isNotEmpty() || contractsViewModel.gifts.value.isNotEmpty()) {
        val listState: LazyListState = rememberLazyListState()
        LazyRow(
            content = {
                itemsIndexed(courses)
                { i, contract ->
                    if (contract.course !== null) {
                        ContractCard(
                            contract, onClick = {
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

                            },
                            onAccess = {
                                contractsViewModel.accessDates.clear()
                                contractsViewModel.accessDates.addAll(contract.notPassed?.exams!!.toMutableList())
                                contractsViewModel.accessOrder.intValue = contract.id
                                contractsViewModel.accessModule.intValue =
                                    contract.notPassed?.moduleId!!
                                contractsViewModel.selectedDate.value =
                                    contract.notPassed!!.exams[0]
                                contractsViewModel.accessContractUid.value = contract.uid!!
                                contractsViewModel.accessModuleUid.value =
                                    contract.notPassed!!.moduleUid!!
                                contractsViewModel.accessCourseHours.value = contract.course!!.hours
                                contractsViewModel.accessFree.value = contract.notPassed!!.free!!
                                contractsViewModel.accessPrice.value = contract.extendPrice!!
                            },
                            bottom = bottomsheetState,
                            navHostController = navHostController
                        )
                    }

                }
                itemsIndexed(contractsViewModel.gifts.value)
                { i, gift ->
                    if (gift.name !== null) {
                        GiftCard(gift = gift, navHostController = navHostController, onClick = {
                            val gson = Gson()
                            val contractJson = gson.toJson(
                                gift,
                                Gift::class.java
                            )
                            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                "Gift",
                                contractJson
                            )

                            navHostController.navigate("gift")
                        })
                    }

                }
            },
            modifier = Modifier.fillMaxSize(),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState)
        )
    } else {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Тут пока ничего нет")
            Text(text = "Самое время что-нибудь подобрать")
            OutlinedButton(
                onClick = { navHostController.navigate("categories") },
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun FinishedTab(contractsViewModel: ContractsViewModel, navHostController: NavHostController) {
    val courses =
        contractsViewModel.contracts.value.filter { it.status == 0 || it.status == 4 || it.status == 17 }
    val listState: LazyListState = rememberLazyListState()
    if (courses.isNotEmpty()) {
        LazyRow(
            content = {
                itemsIndexed(courses)
                { i, contract ->
                    if (contract.course !== null) {
                        ContractCard(contract, Modifier, navHostController)
                    }

                }
            },
            modifier = Modifier.fillMaxSize(),
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun RefunedTab(contractsViewModel: ContractsViewModel, navHostController: NavHostController) {
    val courses = contractsViewModel.contracts.value.filter { it.status == 5 || it.status == 2 }
    val listState: LazyListState = rememberLazyListState()
    if (courses.isNotEmpty()) {
        LazyRow(
            content = {
                itemsIndexed(courses)
                { i, contract ->
                    if (contract.course !== null) {
                        ContractCard(contract, Modifier, navHostController)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ContractCard(
    contract: Contract,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    onClick: () -> Unit = {},
    onAccess: () -> Unit = {},
    bottom: ModalBottomSheetState? = null
) {
    val conf = LocalConfiguration.current
    val context = LocalContext.current
    val metodCallText = buildAnnotatedString {
        val mStr =
            "Если у вас есть вопросы по документам, свяжитесь с Методическим отделом +7(495)278-11-09 (доб. 302)"

        // word and span to be hyperlinked
        val mStartIndex = mStr.indexOf("+7(495)278-11-09")
        val mEndIndex = mStartIndex + 17
        addStyle(
            style = ParagraphStyle(textAlign = TextAlign.Center),
            start = 0,
            end = mStr.length
        )
        append(mStr)
        addStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            ), start = mStartIndex, end = mEndIndex
        )

        // attach a string annotation that
        // stores a URL to the text "link"
        addStringAnnotation(
            tag = "URL",
            annotation = "tel:84952781109",
            start = mStartIndex,
            end = mEndIndex
        )

    }
    val uriHandler = LocalUriHandler.current
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
                .fillMaxWidth()
                .clickable {
                    if (contract.notPassed === null && contract.status!! in intArrayOf(
                            1,
                            6,
                            7,
                            9,
                            10,
                            11
                        )
                    ) {
                        onClick.invoke()
                    }
                },
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
            if (contract.status!! in intArrayOf(
                    1,
                    6,
                    7,
                    8,
                    9,
                    10,
                    11
                )
            ) {
                Text(
                    text = contract.course!!.hours.toString() + " ак.ч.",
                    fontWeight = FontWeight.Bold
                )
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
                    CircularProgressbar2(
                        contract.progress!!.total!!.toFloat(),
                        size = conf.screenHeightDp.dp.div(10)
                    )

                }
            } else if (contract.status!! in intArrayOf(
                    0,
                    3
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .verticalScroll(
                            rememberScrollState()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (contract.status!! == 0) {
                        Text(
                            text = "Вы успешно прошли обучение!",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 7.dp),
                            color = Aggressive_red
                        )
                    }
                    Text(
                        buildAnnotatedString {
                            append("Документ ")
                            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                if (contract.certs.isNotEmpty()) {
                                    append(contract.certs[0])
                                } else {
                                    append("изготавливается")
                                }
                            }
                        }, modifier = Modifier.padding(bottom = 5.dp), textAlign = TextAlign.Center

                    )
                    if (contract.certs.isNotEmpty() && contract.debt == 0 && !contract.need_docs) {
                        OutlinedButton(
                            onClick = {
                                navHostController.navigate(
                                    "profile/certs"
                                )
                            },
                            border = BorderStroke(2.dp, Primary_Green),
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Получить документ",
                                color = Primary_Green,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (contract.debt != 0) {
                        Text(
                            text = "Для получения документа, Вам необходимо оплатить долг в размере ${contract.debt} руб.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 7.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (contract.need_docs) {
                        Text(
                            text = "Чтобы получить документ об образовании загрузите документы с компьютера и ожидайте уведомление о статусе проверки",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 7.dp),
                        )
                        ClickableText(text = metodCallText, onClick = {
                            metodCallText
                                .getStringAnnotations("URL", it, it)
                                .firstOrNull()?.let { stringAnnotation ->
                                    uriHandler.openUri(stringAnnotation.item)
                                }
                        })
                    }

                }
            }
            if (contract.status == 5 || contract.status == 2) {
                Text(text = "Отказ от курса", color = Aggressive_red, textAlign = TextAlign.Center)

                if (contract.amount !== null) {
                    Text(text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Aggressive_red))
                        {
                            if (contract.status == 2) {
                                append("Денежные средства возвращены ")
                            } else {
                                append("Частичный возврат денежных средств ")
                            }
                        }
                        append(contract.dateCancel)
                    }, textAlign = TextAlign.Center)
                }
            }
            if (contract.whyCancel !== null) {
                Text(text = contract.whyCancel!!, textAlign = TextAlign.Center)
            }
            if (contract.notPassed === null && contract.status!! in intArrayOf(
                    1,
                    6,
                    7,
//                    8,
                    9,
                    10,
                    11
                )
            ) {
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
            } else if (contract.status!! in intArrayOf(1, 6, 7, 9, 10, 11)) {


                if (!contract.notPassed?.free.isNullOrEmpty() && contract.notPassed?.free != "0") {
                    Text(
                        text = "У вас " + contract.notPassed?.extendTimes + " бесплатно продлить доступ. Используйте " + contract.notPassed?.left + " до " + contract.notPassed?.extendTill,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 5.dp, bottom = 0.dp)
                    )
                }
                val scope = rememberCoroutineScope()
                Button(
                    onClick = {
                        if (contract.status != 10) {
                            scope.launch {
                                bottom?.show()
                                onAccess.invoke()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Вы сможете продлить доступ после полной оплаты курса.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Aggressive_red,
                        contentColor = Color.White
                    ),
                ) {
                    if (contract.notPassed!!.free == "0") {
                        Text(
                            "Продлить доступ за " + contract.extendPrice + " руб.",
                            color = Color.White
                        )
                    } else {
                        Text("Продлить доступ", color = Color.White)
                    }
                }

            } else if (contract.status!! in intArrayOf(8)) {

                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.LightGray,
                        contentColor = Color.White
                    ),
                    enabled = false
                ) {
                    Text("Обучение приостановлено", color = Color.White)
                }

            } else if (contract.status!! in intArrayOf(17)) {


                if (!contract.notPassed?.free.isNullOrEmpty() && contract.notPassed?.free != "0") {
                    Text(
                        text = "Восстановить доступ к курсу за 50% стоимости - " + contract.extendPrice,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 5.dp, bottom = 0.dp)
                    )
                }
                val scope = rememberCoroutineScope()
                val pref = context.getSharedPreferences("session", Context.MODE_PRIVATE)
                val token_ = pref.getString("token_lk", "")
                Button(
                    onClick = {
                        BuyExtendRequest().sendRecovery(
                            "Bearer " + token_?.trim('"'),
                            RecoveryPostBody(
                                contract_id = contract.id
                            )

                        ).enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.e("API Request", "I got an error and i don't know why :(")
                                Log.e("API Request", t.message.toString())
                                Toast.makeText(
                                    context,
                                    "Произошла ошибка. Попробуйте позже!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                Log.d("API Request", response.body().toString())
                                Log.d("API Request", response.message())
                                Log.d("API Request", response.errorBody().toString())
                                Log.d("API Request", response.raw().body.toString())
                                if (response.isSuccessful) {
                                    navHostController.navigate("cart")
                                }
                            }
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .padding(vertical = 5.dp),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Aggressive_red,
                        contentColor = Color.White
                    ),
                ) {

                    Text("Восстановиться", color = Color.White)
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun GiftCard(
    gift: Gift,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    onClick: () -> Unit = {},
) {
    val conf = LocalConfiguration.current
    val context = LocalContext.current
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
            model = gift.image,
            contentDescription = gift.id.toString(),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clickable {
                        onClick.invoke()
                    },
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
                gift.name!!,
                maxLines = 2,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
            Icon(
                painter = painterResource(id = R.drawable.gift_lfzdpfhdv6ka),
                contentDescription = "",
                Modifier
                    .size(120.dp)
                    .padding(end = 10.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "Доступно до " + gift.dateTo, modifier = Modifier
                    .fillMaxWidth()

                    .padding(10.dp), textAlign = TextAlign.Center, color = Aggressive_red
            )

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

        }
    }
}