package lk.mzpo.ru.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.User
import lk.mzpo.ru.models.study.UserSchedule
import lk.mzpo.ru.screens.ProfileHeader
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ScheduleScreen(
    user: User,
    navHostController: NavHostController,
    scheduleViewModel: ScheduleViewModel = viewModel(),
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

            scheduleViewModel.getData(context = ctx)
            val isRefreshing = remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing.value,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing.value = true
                        // Здесь вы можете добавить логику обновления данных
                        delay(2000) // Задержка для демонстрации
                        scheduleViewModel.getData(ctx)
                        isRefreshing.value = false
                    }
                }
            )

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



                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp), verticalArrangement = Arrangement.SpaceBetween) {

                           Column {
                               Text(
                                   text = "Моё расписание",
                                   textAlign = TextAlign.Center,
                                   fontSize = 20.sp,
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(vertical = 10.dp)
                               )
                               Divider()
                               Box(
                                   modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)
                               ) {
                                   LazyColumn(
                                       content = {
                                           itemsIndexed(scheduleViewModel.schedule)
                                           { index, item ->

//                                       Row (Modifier, horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically){
//                                           val date = LocalDateTime.parse(item.date , firstApiFormat)
//                                           Text(text = item.course.toString(), modifier = Modifier.weight(2f))
//
//                                           Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(2f)){
//                                               Text(text = date.format(secondFormatter).toString())
//                                               Text(text = date.format(timeFrmtEnd).toString())
//                                           }
//                                           Text(text = item.auditory.toString(), Modifier.weight(1f))
//                                           Text(text = item.teacher.toString(), modifier =  Modifier.weight(1f))
//                                       }
//                                       Divider()
                                               MyGroup(
                                                   group = item,
                                                   navHostController = navHostController
                                               )
                                           }

                                           if (scheduleViewModel.schedule.isEmpty()) {
                                               item {
                                                   Column(
                                                       Modifier
                                                           .fillMaxWidth()
                                                           .padding(
                                                               top = 50.dp,
                                                               start = 50.dp,
                                                               end = 50.dp
                                                           ),
                                                       verticalArrangement = Arrangement.Center,
                                                       horizontalAlignment = Alignment.CenterHorizontally
                                                   ) {
                                                       Icon(
                                                           painter = painterResource(id = R.drawable.calendar_1__traced_),
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
                                                           text = "Здесь будет храниться расписание ваших курсов",
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
                                                           border = BorderStroke(
                                                               2.dp,
                                                               Primary_Green
                                                           ),
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

                                       }, modifier = Modifier
                                           .fillMaxSize()
                                   )
                                   PullRefreshIndicator(
                                       refreshing = isRefreshing.value,
                                       state = pullRefreshState,
                                       modifier = Modifier.align(Alignment.TopCenter)
                                   )
                               }
                           }


                        }

                    }
                }
            }
        }
    )
}

@Composable
fun MyGroup(
    group: UserSchedule,
    navHostController: NavHostController
) {
    val ctx = LocalContext.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(150.dp)
            .border(1.dp, Primary_Green, RoundedCornerShape(10))
            .background(
                Brush.linearGradient(listOf(Primary_Green, Primary_Green)),
                RoundedCornerShape(10),
                0.5f
            )
            .clip(RoundedCornerShape(10.dp)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            Modifier
                .padding(5.dp)
                .weight(4f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                    {
                        append("Курс: ")
                    }
                    append(group.course)
                }, color = Color.White
            , maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                    {
                        append("Начало: ")
                    }
                    append(group.date)
                }, color = Color.White
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                    {
                        append("Аудитория: ")
                    }
                    append(group.auditory)
                }, color = Color.White
            )
//            Text(
//                buildAnnotatedString {
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
//                    {
//                        append("Время начала: ")
//                    }
//                    append(group.timeStart)
//                }, color = Color.White
//            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                    {
                        append("Преподаватель: ")
                    }
                    append(group.teacher)
                }, color = Color.White
            )

        }

        if (!group.teacher_image.isNullOrEmpty())
        {

            AsyncImage(model = group.teacher_image, contentDescription = "", modifier = Modifier.weight(2f))
        }
    }
}