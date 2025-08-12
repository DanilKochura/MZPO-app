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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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
import lk.mzpo.ru.models.ExamData
import lk.mzpo.ru.models.Gift
import lk.mzpo.ru.models.canAccessCourse
import lk.mzpo.ru.models.canExtendAccess
import lk.mzpo.ru.models.isActiveCourse
import lk.mzpo.ru.models.isCanceled
import lk.mzpo.ru.models.isCompleted
import lk.mzpo.ru.models.isSuspended
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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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


        EmailTextField(email = login, isError = bl, title = "Email/Телефон")
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
                    urlHandler.openUri("https://trayektoriya.ru/password/reset")
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
                11,
                14
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
//                                val gson = Gson()
//                                val contractJson = gson.toJson(
//                                    contract,
//                                    Contract::class.java
//                                )
//                                navHostController.currentBackStackEntry?.savedStateHandle?.set(
//                                    "Contract",
//                                    contractJson
//                                )

                                navHostController.navigate("study/" + contract.id)

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "Активные курсы",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Primary_Green
            )
            Image(
                painter = painterResource(id = R.drawable.books__1_),
                contentDescription = "lebed_back",
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            Text(
                "Тут пока ничего нет...",
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
//            Text("Самое время что-нибудь подобрать",  fontSize = 22.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    navHostController.navigate("categories")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(200.dp)
            ) {
                Text(text = "За покупками", color = Color.White)
            }

        }

    }
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun FinishedTab(contractsViewModel: ContractsViewModel, navHostController: NavHostController) {
    val courses =
        contractsViewModel.contracts.value.filter { it.status == 0 || it.status == 4 || it.status == 17 || it.status == 15 }

    val active_courses =
        contractsViewModel.contracts.value.filter {
            it.status!! in intArrayOf(
                1,
                6,
                7,
                8,
                9,
                10,
                11,
                14
            )
        }
    val listState: LazyListState = rememberLazyListState()
    if (courses.isNotEmpty()) {
        LazyRow(
            content = {
                itemsIndexed(courses)
                { i, contract ->
                    if (contract.course !== null) {
                        ContractCard(
                            contract, Modifier, navHostController,
                            onClick = {
                                navHostController.navigate("study/" + contract.id)

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                "Завершенные курсы",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Primary_Green
            )
            Image(
                painter = painterResource(id = R.drawable.books__1_),
                contentDescription = "lebed_back",
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            Text(
                "Вы еще не завершили ни одного курса",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            if (active_courses.isNotEmpty()) {
                Button(
                    onClick = {
                        contractsViewModel.selected.value = "Активные"
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = "Перейти к учебе", color = Color.White)
                }
            } else {
                Button(
                    onClick = {
                        navHostController.navigate("categories")
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = "Выбрать курс", color = Color.White)
                }
            }

        }
//        Icon(painter = painterResource(R.drawable.books__1_))
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
        val mStartIndex = mStr.indexOf("+7(495)278-11-09")
        val mEndIndex = mStartIndex + 17
        addStyle(style = ParagraphStyle(textAlign = TextAlign.Center), start = 0, end = mStr.length)
        append(mStr)
        addStyle(
            style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline),
            start = mStartIndex,
            end = mEndIndex
        )
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
//            .fillMaxHeight()
            .heightIn(min = 450.dp) // Минимальная высота
            .width(conf.screenWidthDp.dp.minus(50.dp)), colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
//        Image(painter = painterResource(id = R.drawable.masage), contentDescription = "", modifier = Modifier
//            .height(150.dp)
//            .fillMaxWidth(), contentScale = ContentScale.Crop)
        Column(
            modifier = Modifier
                .fillMaxSize(),

            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable(
                        enabled = contract.notPassed == null && contract.status!! in intArrayOf(
                            1,
                            6,
                            7,
                            9,
                            10,
                            11,
                            14
                        )
                    ) {
                        onClick()
                    }
            ) {
                AsyncImage(
                    model = contract.course?.image,
                    contentDescription = contract.course?.id?.toString() ?: "course-image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.55f to Color(0x66000000),
                                1f to Color(0xB3000000)
                            )
                        )
                )
                Text(
                    text = contract.course?.name ?: "Курс",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    maxLines = 2
                )
            }
            Box(
                modifier = Modifier
                    .padding(7.dp)
                    .weight(1f) // Это заставит блок занимать всё доступное пространство
                    .fillMaxWidth()
            ) {


                if (contract.isActiveCourse()) {
                    ProgressBlockPretty(
                        contract.progress!!.tests!!,
                        contract.progress!!.video!!,
                        contract.progress!!.files!!,
                        contract.progress!!.total!!.toFloat(),
                        contract.examData,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (contract.isCompleted()) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .verticalScroll(
                                rememberScrollState()
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (contract.status!! == 0 || contract.status!! == 15) {
                            Text(
                                text = "Вы успешно прошли обучение!",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 7.dp),
                                color = Aggressive_red
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
                                Text("Смотреть материалы", color = Color.White)

                            }
                        }
                        Text(
                            buildAnnotatedString {
                                append("Документ ")
                                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                    if (contract.certs.isNotEmpty()) {
                                        append(contract.certs[0])
                                    } else if (contract.legal == "2") {
                                        append("будет передан в вашу организацию.")
                                    } else {
                                        append("изготавливается")
                                    }
                                }
                            },
                            modifier = Modifier.padding(bottom = 5.dp),
                            textAlign = TextAlign.Center

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
//                    if (contract.debt != 0 && contract.legal !== "2") {
//                        Text(
//                            text = "Для получения документа, Вам необходимо оплатить долг в размере ${contract.debt} руб.",
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.padding(bottom = 7.dp),
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
                        if (contract.need_docs) {
                            Text(
                                text = "Чтобы получить документ об образовании загрузите документы и ожидайте уведомление о статусе проверки",
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
                if (contract.isCanceled()) {
                    Text(
                        text = "Отказ от курса",
                        color = Aggressive_red,
                        textAlign = TextAlign.Center
                    )

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
                if (contract.canAccessCourse()) {
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

                //region Продление
                else if (contract.canExtendAccess()) {


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

                }
                //endregion

                else if (contract.isSuspended()) {

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

@Composable
fun ProgressBlockPretty(
    tests: String,
    video: String,
    files: String,
    total: Float, // 0..100
    examData: List<ExamData>,
    freeExtends: String? = null,
    extendTimes: String? = null,
    left: String? = null,
    extendTill: String? = null,
    modifier: Modifier = Modifier
) {
    val conf = LocalConfiguration.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
            .padding(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Заголовок + бейдж процента
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Прогресс",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                // Небольшой бейдж справа
//                Box(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(10.dp))
//                        .background(Color(0xFFE5F5E9)) // светло-зелёный фон
//                        .padding(horizontal = 8.dp, vertical = 4.dp)
//                ) {
//                    Text(
//                        text = "${total.toInt()}%",
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Medium,
//                        color = Primary_Green
//                    )
//                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Левая колонка с метриками
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricLine(label = "Тесты", value = tests)
                    MetricLine(label = "Видео", value = video)
                    MetricLine(label = "Пособия", value = files)
                }

                Spacer(Modifier.width(12.dp))

                // Круг прогресса справа
                CircularProgressbar2(
                    number = total,
                    size = conf.screenHeightDp.dp.div(9),
                    thickness = 13.dp,
                    numberStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    foregroundIndicatorColor = Primary_Green,
                    backgroundIndicatorColor = Primary_Green.copy(alpha = 0.25f),
                    extraSizeForegroundIndicator = 6.dp,
                    animationDuration = 700,
                    animationDelay = 100
                )
            }

            // Подсказка о бесплатных продлениях (если есть)
            if (!freeExtends.isNullOrEmpty() && freeExtends != "0") {
                Text(
                    text = "У вас $extendTimes бесплатно продлить доступ. Используйте $left до $extendTill",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (examData.size > 0) {
                var selectedExam = examData.find { it.passed == "0" }
                if (selectedExam == null) selectedExam = examData[0]
                ExamBanner(selectedExam.exam, selectedExam.passed == "1", {})
            }
        }
    }
}

@Composable
private fun MetricLine(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF3F4F6))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // маленький маркер
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Primary_Green)
        )
        Spacer(Modifier.width(8.dp))
        Text(text = "$label:", color = Primary_Green, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(6.dp))
        Text(text = value, color = Color(0xFF111827))
    }
}

@Composable
fun dateExam(examDate: String) {
    val formattedDate = try {
        val dateTime = ZonedDateTime.parse(examDate) // Парсим строку в ZonedDateTime
        dateTime.format(
            DateTimeFormatter.ofPattern(
                "dd MMMM yyyy, HH:mm",
                Locale("ru")
            )
        ) // Форматируем
    } catch (e: Exception) {
        "Дата недоступна" // В случае ошибки парсинга
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(Primary_Green.copy(0.2f))
            .border(1.dp, Primary_Green, RoundedCornerShape(5.dp))
            .padding(6.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.calendar),
            contentDescription = "Дата экзамена",
            tint = Primary_Green.copy(0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Экзамен: $formattedDate",
            color = Primary_Green.copy(0.7f),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }

}

@Composable
fun ExamBanner(
    examDateIso: String?, // например "2025-08-10T12:00:00+03:00"
    passed: Boolean,
    onOpenExam: (() -> Unit)? = null
) {
    val (bg, fg) = if (passed) Color(0xFFE6F9EE) to Color(0xFF1E7F4C) else Color(0xFFFFF6E6) to Color(
        0xFF8A5A00
    )
    val iconTint = fg.copy(alpha = 0.85f)

    val text = remember(examDateIso, passed) {
        val formatted = try {
            val dt = java.time.ZonedDateTime.parse(examDateIso)
            dt.format(
                java.time.format.DateTimeFormatter.ofPattern(
                    "dd MMMM yyyy, HH:mm",
                    java.util.Locale("ru")
                )
            )
        } catch (_: Exception) {
            null
        }

        when {
            passed -> "Экзамен: сдан"
            formatted != null -> "Экзамен: $formatted"
            else -> "Экзамен: дата не назначена"
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, fg.copy(0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        if (passed) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = iconTint
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = null,
                tint = iconTint
            )
        }

        Spacer(Modifier.width(8.dp))
        Text(
            text,
            color = fg,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        if (!passed && onOpenExam != null) {
            Text(
                "Перейти",
                color = Aggressive_red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenExam() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun InfoChipsRow(
    hours: Int,
    format: String?,        // например: "видео, тесты"
    certReady: Boolean,     // есть certs и нет need_docs/долга
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoChip(text = "$hours ак.ч.")
        format?.takeIf { it.isNotBlank() }?.let { InfoChip(text = it) }
        InfoChip(
            text = if (certReady) "Документ готов" else "Документ в обработке",
            filled = certReady
        )
    }
}

@Composable
private fun InfoChip(text: String, filled: Boolean = false) {
    val bg = if (filled) Primary_Green.copy(0.12f) else Color(0xFFF3F4F6)
    val fg = if (filled) Primary_Green else Color(0xFF111827)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(
                1.dp,
                if (filled) Primary_Green.copy(0.4f) else Color(0xFFE5E7EB),
                RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            color = fg,
            fontSize = 12.sp,
            fontWeight = if (filled) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
fun AccessUntilBadge(untilIso: String?) {
    if (untilIso.isNullOrBlank()) return
    val text = try {
        val end = java.time.ZonedDateTime.parse(untilIso)
        val now = java.time.ZonedDateTime.now(end.zone)
        val days = java.time.Duration.between(now, end).toDays().coerceAtLeast(0)
        "Доступ: ещё $days дн."
    } catch (_: Exception) {
        "Доступ: дата неизвестна"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEFF6FF))
            .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) { Text(text, fontSize = 12.sp, color = Color(0xFF1D4ED8)) }
}
