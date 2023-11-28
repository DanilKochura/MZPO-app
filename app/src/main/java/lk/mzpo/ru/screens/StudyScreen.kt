package lk.mzpo.ru.screens

import android.content.Context
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.material.tooltip.TooltipDrawable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.gson.Gson
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.study.StudyModule
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.components.PickImageFromGallery
import lk.mzpo.ru.ui.components.PieChart
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Orange
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.ContractsViewModel
import lk.mzpo.ru.viewModel.StudyViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun StudyScreen(
    contract: Contract,
    navHostController: NavHostController,
    studyViewModel: StudyViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0)

) {
    val context = LocalContext.current
    val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token_lk", "")

    studyViewModel.contract = contract
    studyViewModel.getData(context)

    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val bottomPracticeState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
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
        floatingActionButton = {
            if (bottomSheetState.targetValue == ModalBottomSheetValue.Hidden && bottomPracticeState.targetValue == ModalBottomSheetValue.Hidden) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                bottomSheetState.show()
                            }
                        },
                        containerColor = Primary_Green,
                        contentColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .weight(1f)
                            .height(45.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "",
                                modifier = Modifier.padding(end = 5.dp)
                            )
                            Text(text = "Загрузить документы", color = Color.White)
                        }
                    }
                    if (studyViewModel.practiceData.isNotEmpty() || studyViewModel.practiceOcno.isNotEmpty()) {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    bottomPracticeState.show()
                                }
                            },
                            containerColor = Aggressive_red,
                            contentColor = Color.White,
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .weight(1f)
                                .height(45.dp)
                                .padding(start = 10.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "",
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                                Text(text = "Практика", color = Color.White)
                            }
                        }
                    }
                }

            }
        },
        bottomBar = { BottomNavigationMenu(navController = navHostController, cart = cart_sum) },
        content = { padding ->
            Log.d("StudyLog", "entered")

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
                                color = Color.White, shape = RoundedCornerShape(
                                    topStart = MainRounded, topEnd = MainRounded
                                )
                            )
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded))
                    ) {


                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = contract.course!!.name,
                                modifier = Modifier.weight(2f),
                                fontSize = 20.sp,
                                maxLines = 3,
                                fontWeight = FontWeight.Bold
                            )
                            AsyncImage(
                                model = contract.course!!.image,
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                            )
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .height(25.dp)
                        ) {
                            val progress = if (contract.progress!!.total!!.toFloat()
                                    .div(100f) > 0.1f
                            ) contract.progress!!.total!!.toFloat().div(100f) else 0.1f
                            LinearProgressIndicator(
                                progress = progress,
                                Modifier
                                    .fillMaxSize()
                                    .clip(
                                        RoundedCornerShape(50)
                                    ), color = Primary_Green, trackColor = Color.LightGray
                            ) //70% progress
                            Text(
                                text = contract.progress!!.total!!.toInt().toString() + "%",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 7.dp)
                            )
                        }
                        if (contract.course!!.prices.dist !== null && contract.course!!.prices.dist !== 0) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp, bottom = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "warning",
                                    modifier = Modifier.padding(end = 5.dp),
                                    tint = Aggressive_red
                                )
                                Text(
                                    text = "Для прохождения курса необходимо просмотреть более 70% материалов",
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                        Divider()
                        if (studyViewModel.contract.course!!.prices.sale15 !== null) {
                            var list = listOf("Расписание", "Материалы")
                            TabRow(
                                selectedTabIndex = 0,
                                indicator = {

                                },
                                contentColor = Primary_Green,
                                containerColor = Color.White,
                                modifier = Modifier.fillMaxWidth(),

                                ) {
                                list.forEachIndexed { index, text ->
                                    Tab(
                                        selected = studyViewModel.selected.value == text,
                                        onClick = {
                                            studyViewModel.selected.value = text
                                        },
                                        text = { Text(text = text) },
                                        selectedContentColor = Aggressive_red,
                                        unselectedContentColor = Primary_Green
                                    )
                                }
                            }
                            when (studyViewModel.selected.value) {
                                "Расписание" -> Schedule(studyViewModel = studyViewModel)
                                "Материалы" -> Materials(
                                    studyViewModel = studyViewModel,
                                    navHostController = navHostController,
                                    contract = contract
                                )

                            }
                        } else {
                            Materials(
                                studyViewModel = studyViewModel,
                                navHostController = navHostController,
                                contract = contract
                            )
                        }

                    }
                }
            }

            //region Попап для
            ModalBottomSheetLayout(
                sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), sheetContent = {
                    Column(
                        Modifier
                            .fillMaxWidth()

                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Для выписки документа о прохождении обучения, загрузите Ваши документы",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Divider()
                        LazyColumn(content = {
                            itemsIndexed(studyViewModel.admissions)
                            {
                                index, admission ->
                                var loaded: String? = null
                                var comment: String? = null
                                var status: String? = null
                                for (j in studyViewModel.documents) {
                                    if (admission.id == j.admissionId) {
                                        loaded = j.file
                                        status = j.docCondition
                                        comment = j.comment
                                        break
                                    }

                                }
                                Text(text = admission.name.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    when(status)
                                    {
                                        null -> Text("")
                                        "0" -> Text(text = "Документ на проверке", modifier = Modifier
                                            .clip(
                                                RoundedCornerShape(3.dp)
                                            )
                                            .background(Color.Blue.copy(0.3f))
                                            .padding(3.dp))
                                        "1" -> Text(text = "Документ принят", modifier = Modifier
                                            .padding(3.dp)
                                            .clip(
                                                RoundedCornerShape(3.dp)
                                            )
                                            .background(Color.Green.copy(0.3f))
                                            .padding(3.dp))
                                        "2" -> Text(text = comment.toString(), modifier = Modifier
                                            .padding(3.dp)
                                            .clip(
                                                RoundedCornerShape(3.dp)
                                            )
                                            .background(Aggressive_red.copy(0.3f))
                                            .padding(3.dp))
                                    }
                                }
                                PickImageFromGallery(contract.id, admission.id!!, loaded)
                                Divider()
                            }
                        })
                    }

                }, sheetState = bottomSheetState, modifier = Modifier.padding(padding)
            ) {

            }
            //endregion

            //region Попап для практики
            if (studyViewModel.practiceData.isNotEmpty() || studyViewModel.practiceOcno.isNotEmpty()) {
                ModalBottomSheetLayout(
                    sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    sheetContent = {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Практика",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Divider()
                            val uri = LocalUriHandler.current

                            if (studyViewModel.practiceData.isNotEmpty())
                            {
                                Text(
                                    buildAnnotatedString {
                                        append("По вашему курсу предусмотрена производственная практика – ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(studyViewModel.practiceData[0].duration.toString())
                                        }
                                        append(" ак. часов (если еще не проходили ее и не сдавали заполненные бланки).")
                                    }, modifier = Modifier.padding(bottom = 5.dp)
                                )
                                Text(text = "Её можно пройти на месте вашей работы или в любом другом учреждении или ИП, где есть деятельность по направлению вашего обучения;")
                                Divider()
                                if (studyViewModel.practiceData[0].blanks.isNotEmpty()) {
                                    Text(
                                        text = "Бланки для бесплатной практики",
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    )
                                    LazyColumn(content = {
                                        itemsIndexed(studyViewModel.practiceData[0].blanks) { index, item ->
                                            Button(onClick = {
                                                uri.openUri("https://lk.mzpo-s.ru/" + item.path)
                                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green), modifier = Modifier.padding(vertical = 5.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.doc),
                                                        contentDescription = "",
                                                        modifier = Modifier.padding(end = 5.dp).height(20.dp),
                                                        tint = Color.White
                                                    )
                                                    Text(text = item.file, color = Color.White)
                                                }
                                            }
                                        }
                                    })
                                    Spacer(modifier = Modifier.height(5.dp))
                                }
                                Divider()
                                if (studyViewModel.practiceData[0].blanks.isNotEmpty()) {
                                    Text(
                                        text = "Рекомендуем курсы для платной практики",
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    )
                                    LazyRow(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                    ) {
                                        itemsIndexed(studyViewModel.practiceData[0].courses)
                                        {
                                                _, index ->
                                            CourseCard(index,
                                                Modifier
                                                    .width(300.dp)
                                                    .clickable {
                                                        navHostController.navigate("course/" + index.id)
                                                    })
                                        }
                                    }

                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(5.dp))
                            } else if(studyViewModel.practiceOcno.isNotEmpty())
                            {
                                Text(text = studyViewModel.practiceOcno[0].text.toString())
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    Button(onClick = {
                                        uri.openUri("tel:" + studyViewModel.practiceOcno[0].phone)
                                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red), shape = RoundedCornerShape(10.dp), modifier = Modifier.width(200.dp)) {
                                        Text(text = "Позвонить", color = Color.White)
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    Button(onClick = {
                                        uri.openUri(studyViewModel.practiceOcno[0].blank.toString())
                                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green), modifier = Modifier.padding(vertical = 5.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.doc),
                                                contentDescription = "",
                                                modifier = Modifier.padding(end = 5.dp, top = 10.dp).height(20.dp),
                                                tint = Color.White
                                            )
                                            Text(text = "Скачать бланк", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    },
                    sheetState = bottomPracticeState,
                    modifier = Modifier.padding(padding)
                ) {

                }
            }
            //endregion

        })
}


@Composable
fun module(
    studyModule: StudyModule,
    int: Int,
    navHostController: NavHostController,
    onClick: () -> Unit = {}
) {

    var sum = 0;
    var checked = 0;
    for (i in studyModule.activeMaterials) {
        if (i.activeFile!!.type == "video") {
            sum += i.activeFile!!.size ?: 0
        } else if (i.activeFile!!.type == "file") {
            sum += (i.activeFile!!.size ?: 0) * 120

        } else if (i.activeFile!!.type == "test") {
            sum += 1200

        }

        if (i.activeFile!!.userProgress !== null) {
            if (checked != 1) {
                if (i.activeFile!!.userProgress?.viewed == "1") {
                    checked = 1;
                }
                if (i.activeFile!!.userProgress?.viewed == "2") {
                    checked = 2;
                }
            }
        } else {
            if (checked != 0) {
                checked = 1
            }
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 20.dp)
        ) {
            Text(
                text = "${int + 1}.  ${studyModule.name} ",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(text = "~ ${sum.div(60).toInt()} мин.")

        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (checked == 2) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "",
                    tint = Primary_Green,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            } else if (checked == 1) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "",
                    tint = Orange,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )

            }
        }
    }
    Divider()


}

@Composable
fun Schedule(studyViewModel: StudyViewModel) {
    val firstApiFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val secondFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val timeFrmtEnd = DateTimeFormatter.ofPattern("HH:mm")
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        for (item in studyViewModel.schedules) {
            val exam = LocalDate.parse(item.group.exam)
            Text(
                text = "Группа " + item.group.title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Дата экзамена: " + exam.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Divider()
            for (i in item.group.allSchedules) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val date = LocalDateTime.parse(i.date + " " + i.timeStart, firstApiFormat)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = date.format(secondFormatter).toString())
                        Text(text = date.format(timeFrmtEnd).toString())
                    }
                    Text(text = i.auditory.toString())
                    Text(text = i.teacher?.name.toString(), modifier = Modifier.fillMaxWidth(0.4f))
                }
                Divider()
            }
            Divider(thickness = 3.dp)
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(70.dp))
    }
}

@Composable
fun Materials(
    studyViewModel: StudyViewModel, navHostController: NavHostController, contract: Contract
) {
    LazyColumn(
        content = {


            itemsIndexed(studyViewModel.studyModules.value) { i, item ->

                module(studyModule = item, int = i, navHostController, onClick = {
                    val gson = Gson()
                    val contractJson = gson.toJson(
                        contract, Contract::class.java
                    )
                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                        "Contract", contractJson
                    )
                    val smJson = gson.toJson(
                        item, StudyModule::class.java
                    )
                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                        "StudyModule", smJson
                    )

                    navHostController.navigate("study/module")
                })
            }
            item {
                Spacer(modifier = Modifier.padding(50.dp))
            }
        }, modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    )
}