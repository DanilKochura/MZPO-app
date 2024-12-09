package lk.mzpo.ru.screens

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.study.StudyModule
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.components.PickImageFromGallery
import lk.mzpo.ru.ui.theme.Active_Green
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.Blue_BG
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Orange
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.StudyViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                        if (contract.docs_errors > 0 && false)
                        {
                            BadgedBox(badge = { Badge { Text(contract.docs_errors.toString()) } }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "",
                                        modifier = Modifier.padding(end = 5.dp)
                                    )
                                    Text(text = "Загрузить документы", color = Color.White)
                                }
                            }
                        } else
                        {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "",
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                                Text(text = "Загрузить документы", color = Color.White)
                            }
                        }
                    }
                    if ((studyViewModel.practiceData.isNotEmpty() || studyViewModel.practiceOcno.isNotEmpty()) && !studyViewModel.verify_docs.value) {
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
                            Column(Modifier.weight(2f)) {
                                Text(
                                    text = contract.course!!.name,
                                    modifier = Modifier,
                                    fontSize = 18.sp,
                                    maxLines = 2,
                                    fontWeight = FontWeight.Bold
                                )
                                if (studyViewModel.examNew.size > 0)
                                {
                                    if (!studyViewModel.examNew[0].accessed.isNullOrEmpty())
                                    {
                                        Text(
                                            buildAnnotatedString {
                                                append("Экзамен: ")
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Aggressive_red)) {
                                                    append(studyViewModel.examNew[0].accessed)
                                                }
                                            }, modifier = Modifier.padding(bottom = 5.dp), fontSize = 12.sp
                                        )
                                    } else if (studyViewModel.examNew[0].many.isNotEmpty())
                                    {
                                        if (studyViewModel.examNew[0].many.size > 1)
                                        {
                                            Text(
                                                buildAnnotatedString {
                                                    append("Экзамены: ")
                                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Aggressive_red)) {
                                                        for (i in 0..studyViewModel.examNew[0].many.size - 1)
                                                        {
                                                            append(studyViewModel.examNew[0].many[i])
                                                            if (i < studyViewModel.examNew[0].many.size-1)
                                                            {
                                                                append(", ")
                                                            }
                                                        }
                                                    }
                                                }, modifier = Modifier.padding(bottom = 5.dp), fontSize = 12.sp)
                                        } else
                                        {
                                            Text(
                                                buildAnnotatedString {
                                                    append("Окончание обучения: ")
                                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Aggressive_red)) {
                                                            append(studyViewModel.examNew[0].many[0])
                                                        }
                                                }, modifier = Modifier.padding(bottom = 5.dp), fontSize = 12.sp)
                                        }
                                    } else if (!studyViewModel.examNew[0].corp.isNullOrEmpty())
                                    {
                                        Text(
                                            buildAnnotatedString {
                                                append("Окончание обучения: ")
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Aggressive_red)) {
                                                    append(studyViewModel.examNew[0].corp)
                                                }
                                            }, modifier = Modifier.padding(bottom = 5.dp), fontSize = 12.sp
                                        )
                                    } else if (!studyViewModel.examNew[0].close.isNullOrEmpty())
                                    {
                                        Text(
                                            buildAnnotatedString {
                                                append("С итоговым экзаменом: ")
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Aggressive_red)) {
                                                    append(studyViewModel.examNew[0].close)
                                                }
                                            }, modifier = Modifier.padding(bottom = 5.dp), fontSize = 12.sp
                                        )
                                    }

                                }


                            }
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
                        if (studyViewModel.contract.status == 7) {
                            Column (Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .background(Aggressive_red.copy(.1f))
                                        .border(1.dp, Aggressive_red, RoundedCornerShape(5.dp))
                                        .clip(
                                            RoundedCornerShape(5.dp)
                                        )
                                ) {
                                    Text(
                                        text = "Ваше заявление на возврат принято",
                                        color = Aggressive_red,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .padding(vertical = 5.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                        Divider()
                        LoadableScreen(loaded = studyViewModel.loaded, error = studyViewModel.error)
                        {
                            if (!studyViewModel.verify_docs.value) {
                                if (studyViewModel.contract.course!!.prices.sale15 !== null || studyViewModel.passedModules.isNotEmpty() || studyViewModel.exam.isNotEmpty() || studyViewModel.schedules.isNotEmpty()) {
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
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Для доступа к курсу, пожалуйста, загрузите документы для проверки на соответствие к требованию к курсу",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(
                                                Blue_BG.copy(0.1f),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                2.dp, Blue_BG, RoundedCornerShape(10.dp)
                                            )
                                            .padding(10.dp)
                                    )

                                }
                            }
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
                        var verify = 0
                        if (studyViewModel.verify_docs.value) {
                            verify = 1
                            val list =
                                studyViewModel.admissions.filter { it.pivot!!.for_access == "1" }
                            studyViewModel.admissions.clear()
                            studyViewModel.admissions.addAll(list)
                        }
                        LazyColumn(content = {
                            itemsIndexed(studyViewModel.admissions)
                            { index, admission ->
                                val loaded: ArrayList<String> = arrayListOf()
                                var comment: String? = null
                                var status: String? = null
                                var count = 0
                                for (j in studyViewModel.documents) {
                                    if (admission.id == j.admissionId) {
                                        if (j.file !== null)
                                        {
                                            if (!loaded.contains(j.file!!))
                                            {
                                                loaded.add(j.file!!)
                                            }
                                        }
                                        status = j.docCondition
                                        if (j.comment !== null)
                                        {
                                            comment = j.comment
                                        }
                                    }
                                }
                                count = studyViewModel.documents.filter { it.docCondition === "0"  }.size
                                Text(
                                    text = admission.name.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (loaded.isNotEmpty()) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        when (status) {
                                            null -> Text("")
                                            "0" -> Text(
                                                text = "Документ на проверке", modifier = Modifier
                                                    .clip(
                                                        RoundedCornerShape(3.dp)
                                                    )
                                                    .background(Color.Blue.copy(0.3f))
                                                    .padding(3.dp)
                                            )

                                            "1" -> Text(
                                                text = "Документ принят", modifier = Modifier
                                                    .padding(3.dp)
                                                    .clip(
                                                        RoundedCornerShape(3.dp)
                                                    )
                                                    .background(Color.Green.copy(0.3f))
                                                    .padding(3.dp)
                                            )

                                            "2" -> {
                                                Text(
                                                    text = comment.toString(), modifier = Modifier
                                                        .padding(3.dp)
                                                        .clip(
                                                            RoundedCornerShape(3.dp)
                                                        )
                                                        .background(Aggressive_red.copy(0.3f))
                                                        .padding(3.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                PickImageFromGallery(
                                    contract.id,
                                    admission.id!!,
                                    loaded,
                                    status,
                                    count,
                                    verify
                                )


                                HorizontalDivider()
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

                            if (studyViewModel.practiceData.isNotEmpty()) {
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
                                            Button(
                                                onClick = {
                                                    uri.openUri("https://lk.mzpo-s.ru/" + item.path)
                                                },
                                                colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green),
                                                modifier = Modifier.padding(vertical = 5.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.doc),
                                                        contentDescription = "",
                                                        modifier = Modifier
                                                            .padding(end = 5.dp)
                                                            .height(20.dp),
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
                                if (studyViewModel.practiceData.isNotEmpty())
                                {
                                    if (!studyViewModel.practiceData[0].courses.isNullOrEmpty()) {
                                        Text(
                                            text = "Рекомендуем курсы для платной практики",
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 5.dp)
                                        )
                                        LazyRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp)
                                        ) {
                                            itemsIndexed(studyViewModel.practiceData[0].courses)
                                            { _, index ->
                                                CourseCard(index,
                                                    Modifier
                                                        .width(300.dp)
                                                        .clickable {
                                                            navHostController.navigate("course/" + index.id)
                                                        })
                                            }
                                        }

                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(5.dp))
                            } else if (studyViewModel.practiceOcno.isNotEmpty()) {
                                Text(text = studyViewModel.practiceOcno[0].text.toString())
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            uri.openUri("tel:" + studyViewModel.practiceOcno[0].phone)
                                        },
                                        colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.width(200.dp)
                                    ) {
                                        Text(text = "Позвонить", color = Color.White)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            uri.openUri(studyViewModel.practiceOcno[0].blank.toString())
                                        },
                                        colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green),
                                        modifier = Modifier.padding(vertical = 5.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.doc),
                                                contentDescription = "",
                                                modifier = Modifier
                                                    .padding(end = 5.dp, top = 10.dp)
                                                    .height(20.dp),
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
    var files = 0;
    var tests = 0;
    var videos = 0;
    for (i in studyModule.activeMaterials) {
        if (i.activeFile !== null)
        {
            if (i.activeFile!!.type == "video") {
                sum += i.activeFile!!.size ?: 0
                videos+=1;
            } else if (i.activeFile!!.type == "file") {
                sum += (i.activeFile!!.size ?: 0) * 120
                files+=1;

            } else if (i.activeFile!!.type == "test" || i.activeFile!!.type == "final_test") {
                sum += 1200
                tests+=1;

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
            Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "~ ${sum.div(60).toInt()} мин.")

                Row {
                    if (files > 0) {
                        Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp)) {
                            Icon(painter = painterResource(id = R.drawable.baseline_library_books_24), contentDescription = "", Modifier.size(20.dp), tint = Primary_Green)
                            Text(text = files.toString())
                        }
                    }
                    if (videos > 0)
                    {
                        Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp)){
                            Icon(painter = painterResource(id = R.drawable.baseline_video_library_24), contentDescription = "", Modifier.size(20.dp), tint = Primary_Green)
                            Text(text = videos.toString())
                        }
                    }
                    if (tests > 0)
                    {
                        Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp)) {
                            Icon(painter = painterResource(id = R.drawable.baseline_quiz_24), contentDescription = "", Modifier.size(20.dp), tint = Primary_Green)
                            Text(text = tests.toString())
                        }
                    }
                }

            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (checked == 2) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "",
                    tint = Primary_Green,
                    modifier = Modifier.align(
                        Alignment.Center
                    ).fillMaxSize()
                )
            } else if (checked == 1) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "",
                    tint = Orange,
                    modifier = Modifier.align(
                        Alignment.Center
                    ).fillMaxSize()
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
        if (studyViewModel.passedModules.isNotEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(text = "Вы успешно прошли", fontWeight = FontWeight.Bold)
                HorizontalDivider()
                for (item in studyViewModel.passedModules) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "",
                            tint = Active_Green,
                        )
                        Text(
                            text = item.module.toString(),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }
                HorizontalDivider()
            }
        }

        if (studyViewModel.exam.isNotEmpty()) {
            //region Билеты экзамен
            if (studyViewModel.exam[0].answered != 1) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Экзаменационный билет №" + studyViewModel.exam[0].num,
                        modifier = Modifier
                            .background(
                                Primary_Green, RoundedCornerShape(10.dp)
                            )
                            .padding(5.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    )
                    {
                        Text(
                            text = "Необходимо отправить ответы с компьютера до " + studyViewModel.exam[0].send_answer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Blue_BG.copy(0.1f), RoundedCornerShape(10.dp)
                                )
                                .padding(5.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                        for (i in 0 until studyViewModel.exam[0].ticket.size) {
                            Text(
                                text = "Вопрос №" + (i + 1) + " " + studyViewModel.exam[0].ticket[i].question.toString(),
                                fontWeight = FontWeight.Bold
                            )

                        }
                    HorizontalDivider()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Ваш ответ принят!", modifier = Modifier
                            .background(
                                Primary_Green, RoundedCornerShape(10.dp)
                            )
                            .padding(5.dp)
                            .fillMaxWidth(), textAlign = TextAlign.Center, color = Color.White
                    )
                }
            }
            //endregion
        }
        if (studyViewModel.schedules.isNotEmpty()) {
            for (item in studyViewModel.schedules) {


                if(item.group !== null)
                {
                    Text(
                        text = "Группа " + item.group.title,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    if (!item.group.exam.isNullOrEmpty())
                    {
                        val exam = LocalDate.parse(item.group.exam)
                        Text(
                            text = "Дата экзамена: " + exam.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                            Text(
                                text = if (i.teacher !== null) i.teacher?.name.toString() else "Преподаватель не указан",
                                modifier = Modifier.fillMaxWidth(0.4f)
                            )
                        }
                        Divider()
                    }
                    Divider(thickness = 3.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                }



            }
        } else {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .background(Aggressive_red.copy(.1f))
                    .border(1.dp, Aggressive_red, RoundedCornerShape(5.dp))
                    .clip(
                        RoundedCornerShape(5.dp)
                    )
            )
            {
                Text(
                    text = "На данный момент вы не записаны в группу. В ближайшее время вас запишут и вы сможете увидеть расписание вашей группы.",
                    color = Orange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(70.dp))
    }
}

@Composable
fun Materials(
    studyViewModel: StudyViewModel, navHostController: NavHostController, contract: Contract
) {
    val ctx = LocalContext.current
    LazyColumn(
        content = {

            if (studyViewModel.contract.status == 10) {
                item {
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(Aggressive_red.copy(.1f))
                            .border(1.dp, Aggressive_red, RoundedCornerShape(5.dp))
                            .clip(
                                RoundedCornerShape(5.dp)
                            )
                    )
                    {
                        Text(
                            text = "Доступ к материалам будет открыт после подтверждения оплаты",
                            color = Aggressive_red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
            } else {
                itemsIndexed(studyViewModel.studyModules.value) { i, item ->

                    module(studyModule = item, int = i, navHostController, onClick = {
                        try {
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
                        } catch (e: Exception)
                        {
                            Toast.makeText(ctx, "Произошла ошибка. Попробуйте позже!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            item {
                Spacer(modifier = Modifier.padding(50.dp))
            }
        }, modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    )
}