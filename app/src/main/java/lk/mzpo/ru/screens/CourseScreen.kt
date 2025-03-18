package lk.mzpo.ru.screens

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.GroupCart
import lk.mzpo.ru.network.firebase.FirebaseHelpers
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.network.retrofit.AuthStatus
import lk.mzpo.ru.network.retrofit.Data2Amo
import lk.mzpo.ru.network.retrofit.SendDataToAmo
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.NameTextField
import lk.mzpo.ru.ui.components.PhoneTextField
import lk.mzpo.ru.ui.components.Privacy
import lk.mzpo.ru.ui.components.isValidEmail
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.Gold
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.CartViewModel
import lk.mzpo.ru.viewModel.CourseViewModel
import java.math.RoundingMode
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CourseScreen(
    navHostController: NavHostController,
    id: Int,
    courseViewModel: CourseViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0)

) {
    val ctxx = LocalContext.current
    if (courseViewModel.courses.value.isEmpty()) {
        Log.d("MyCourseLog", id.toString())
        courseViewModel.getData(id, ctxx)
    }
    var ok = 0
    val test = ctxx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token", "")
    val secondsToDisappear = remember { mutableStateOf(5) }
    Log.d("TimerLog", "timer started")
//    LaunchedEffect(Unit) {
//        while (secondsToDisappear.value > 0) {
//            delay(1.seconds)
//            secondsToDisappear.value -= 1
//        }
//        val viewed = test.getString("viewed", "")
//        val viewed_arr = viewed?.split(',')?.toTypedArray() ?: listOf<Int>()
//        val new_arr = listOf(courseViewModel.courses.value[0].id) + viewed_arr
//        test.edit().putString("viewed", new_arr.joinToString(",")).apply()
//    }

    AuthService.testAuth(ctxx, navHostController, courseViewModel.auth_tested, false);

    Scaffold(
        bottomBar = { BottomNavigationMenu(navController = navHostController, cart_sum) },
        content = { padding ->

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
                val co = courseViewModel.courses.value.firstOrNull()
                if (co != null) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navHostController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "",
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = courseViewModel.courses.value.get(0)?.name.toString(),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 20.sp,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )

                            IconButton(onClick = {
                                if (courseViewModel.auth_tested.value == AuthStatus.AUTH) {
                                    navHostController.navigate("notifications")
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "",
                                    tint = Color.White
                                )
                            }
                        }
                        /* Row(
                             horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp)
                         ) {
                             SearchViewPreview();
                             IconButton(onClick = { TODO }) {
                                 Icon(
                                     imageVector = Icons.Default.Notifications,
                                     contentDescription = "bell",
                                     tint = Color.White,
                                     modifier = Modifier.size(40.dp)
                                 )
                             }
                         }*/

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
                                .clip(
                                    RoundedCornerShape(
                                        topStart = MainRounded,
                                        topEnd = MainRounded
                                    )
                                )
                        ) {
//                Log.d("MyLog", "Here");
//                Log.d("MyLog", course.value.toString());
//

//                    Text(text = courses.toString())


                            CourseTab(co, courseViewModel, navHostController);
                        }
                    }

                }

            }
            ModalForm(mainViewModel = courseViewModel)
        },
        floatingActionButton = {
            val ctx = LocalContext.current
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FloatingActionButton(
                    onClick = { courseViewModel.modalForm.value = true },
                    containerColor = Primary_Green,
                    contentColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(45.dp)
                        .padding(end = 2.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "",
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(text = "Задать вопрос", color = Color.White)
                    }
                }
                FloatingActionButton(
                    onClick = {

                        if (courseViewModel.auth_tested.value == AuthStatus.AUTH) {
                            var group_type = 1;
                            if (courseViewModel.selectedType.value == "weekend")
                            {
                                group_type = 2
                            }
                            val group = courseViewModel.courses.value[0].groups?.filter { it.group_type == group_type }
                            if (group.isNullOrEmpty())
                            {
                                CartViewModel.addToCart(ctxx, courseViewModel.courses.value[0].id)
                            } else
                            {
                                CartViewModel.addToCart(ctxx, courseViewModel.courses.value[0].id, group.first().id)
                            }
                        } else if (courseViewModel.auth_tested.value == AuthStatus.GUEST) {
                            FirebaseHelpers.addToCart(
                                token, hashMapOf(
                                    "id" to courseViewModel.courses.value.get(0).id.toString(),
                                    "type" to courseViewModel.selectedType.value
                                )
                            )
//                            navHostController.navigate("register")
                        }

                        if (ok === 0) {
                            cart_sum.value++
                            ok = 1
                            val vibrator =
                                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            if (vibrator.hasVibrator()) { // Vibrator availability checking
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        200,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                ) // New vibrate method for API Level 26 or higher
                            }

                        }


                    },
                    containerColor = Aggressive_red,
                    contentColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .height(45.dp)
                        .padding(start = 2.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "",
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(text = "Добавить в корзину", color = Color.White)
                    }
                }

            }

        },
        floatingActionButtonPosition = FabPosition.Center
    )
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class)
@Composable
fun CourseTab(
    course: Course,
    courseViewModel: CourseViewModel,
    navHostController: NavHostController
) {


    var list = mutableListOf("Инфо", "Документы", "Расписание", "Отзывы")
    if (course.modules.isNotEmpty()) {
        list = mutableListOf("Инфо", "Модули", "Документы", "Расписание", "Отзывы")
    }
    if (courseViewModel.isDist.value) {
        list.remove("Расписание")
    }
    val scrollstate = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollstate)) {


        val state = com.google.accompanist.pager.rememberPagerState()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {

            Box(modifier = Modifier.fillMaxWidth()) {
                SliderView(state = state)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp)
                ) {
                    DotsIndicator(
                        totalDots = course.images.size,
                        selectedIndex = state.currentPage
                    )
                }

            }
//            LaunchedEffect(key1 = state.currentPage) {
//                delay(3000)
//                var newPosition = state.currentPage + 1
//                if (newPosition > courseViewModel.courseListResponse.size - 1) newPosition = 0
//                // scrolling to the new position.
//                state.animateScrollToPage(newPosition)
//            }
        }


        ScrollableTabRow(
            selectedTabIndex = 0,
            indicator = {},
            edgePadding = 0.dp,
            contentColor = Color.White,
            containerColor = Primary_Green

        ) {
            list.forEachIndexed { index, text ->
                Tab(selected = courseViewModel.tabIndex.value == text, onClick = {
                    courseViewModel.tabIndex.value = text
                }, text = { Text(text = text) })
            }
        }

        when (courseViewModel.tabIndex.value) {
            "Инфо" -> CourseInfo(courseViewModel, scrollstate)
            "Модули" -> CourseModules(course = course)
            "Документы" -> CourseDocs(course)
            "Расписание" -> CourseGroups(
                course = course,
                courseViewModel,
                navHostController = navHostController
            )

            "Отзывы" -> CourseReviews(courseViewModel)

        }

    }
}

@Composable
fun CourseReviews(courseViewModel: CourseViewModel) {

    if (courseViewModel.reviews.isEmpty()) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_reviews_24),
                    contentDescription = "No Schedule Icon",
                    Modifier.size(80.dp),
                    tint = Color.Gray
                )
                Text(text = "Отзывов пока нет", fontSize = 22.sp)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            for (review in courseViewModel.reviews) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .background(Primary_Green.copy(0.6f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                ) {
                    Row {
                        Text(text = review.user, modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp), textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Row (Modifier.fillMaxWidth()){
                       Column(modifier = Modifier
                           .weight(1f)
                           .padding(end = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                           if (review.user_avatar !== null) {
                               Box(
                                   modifier = Modifier
                                       .size(50.dp)
                                       .clip(CircleShape)
                               ) {
                                   AsyncImage(
                                       model = "https://trayektoriya.ru/build/images/" + review.user_avatar,
                                       contentDescription = "",
                                       contentScale = ContentScale.Crop,
                                   )
                               }

                           } else {
                               Box(modifier = Modifier
                                   .clip(CircleShape)
                                   .border(1.dp, Color.White, CircleShape)
                                   .padding(5.dp))
                               {
                                   Icon(
                                       imageVector = Icons.Default.Person,
                                       contentDescription = "",
                                       tint = Color.White,
                                       modifier = Modifier
                                           .size(40.dp)
                                           .padding(10.dp)

                                   )
                               }
                           }
                           Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                               Text(text = review.rate.toString(), color = Gold)
                               Icon(imageVector = Icons.Default.Star, contentDescription = "", tint = Gold
                               )
                           }
                       }
                       Column(modifier = Modifier.weight(4f)) {
                           if (!review.text.isNullOrEmpty()) {
                               Text(text = review.text!!, fontSize = 12.sp, lineHeight = 14.sp)
                           }
                       }
                   }
                    Row() {
                        Text(text = review.date, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), color = Color.White)
                    }
                }
            }

        }
    }
    Spacer(modifier = Modifier.height(70.dp))

}


@Composable
fun CourseInfo(courseViewModel: CourseViewModel, scrollstate: ScrollState) {
    val course = courseViewModel.courses.value.get(0)

    Column(modifier = Modifier.padding(10.dp)) {
        Text(text = course.name, fontSize = 22.sp)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Text(
                text = "Артикул: " + course.prefix,
                fontSize = 15.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(end = 10.dp)
            )
            Row(
                modifier = Modifier./*border( 1.dp, Primary_Green,RoundedCornerShape(20)).*/padding(
                    end = 10.dp
                ), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.time_svgrepo_com), modifier = Modifier
                        .height(20.dp)
                        .padding(end = 2.dp), contentDescription = "hours", tint = Color.LightGray
                )
                Text(text = course.hours.toString() + " ак.ч.", color = Color.LightGray)
            }
            Row(
                modifier = Modifier.clickable {
                    courseViewModel.tabIndex.value = "Документы"
                }/*.border( 1.dp, Primary_Green,RoundedCornerShape(20))*/,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.diploma_svgrepo_com),
                    modifier = Modifier
                        .height(20.dp)
                        .padding(end = 2.dp),
                    contentDescription = "hours",
                    tint = Color.LightGray
                )
                Text(text = course.doctype, color = Color.LightGray)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = courseViewModel.selectedPrice.value.toString() + " ₽",
                color = Aggressive_red,
                fontSize = 40.sp
            )
            Text(
                text = courseViewModel.selectedPrice.value.times(1.15).toBigDecimal()
                    .setScale(-2, RoundingMode.UP).toInt().toString() + " ₽",
                textDecoration = TextDecoration.LineThrough,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        Text(
            text = "Доступные формы обучения:",
            fontSize = 20.sp,
            modifier = Modifier
        )
        val scope = rememberCoroutineScope()

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            if (course.prices.dist != 0) {
                if (course.prices.dist != 0) {
                    Column(
                        modifier = Modifier
                            .height(45.dp)
                            .padding(end = 7.dp)
                            .clip(
                                RoundedCornerShape(20)
                            )
                            .background(if (courseViewModel.selectedPrice.value == course.prices.dist!!) Primary_Green else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (courseViewModel.selectedPrice.value == course.prices.dist!!) Primary_Green else Color.LightGray,
                                RoundedCornerShape(20)
                            )
                            .clickable {
                                courseViewModel.selectedPrice.value = course.prices.dist!!
                                courseViewModel.selectedType.value = "dist"
                                if (scrollstate.value > 800) {
                                    scope.launch {
                                        scrollstate.animateScrollTo(800)
                                    }
                                }
                            }, verticalArrangement = Arrangement.Center
                    )
                    {
                        Text(
                            text = "Дистанционно",
                            Modifier.padding(10.dp),
                            color = if (courseViewModel.selectedPrice.value == course.prices.dist!!) Color.White else Color.Black
                        )
                    }
                }
            } else {
                if (course.prices.sale15 != 0) {
                    Column(
                        modifier = Modifier
                            .height(45.dp)
                            .padding(end = 7.dp)
                            .clip(
                                RoundedCornerShape(20)
                            )
                            .background(if (courseViewModel.selectedPrice.value == course.prices.sale15!!) Primary_Green else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (courseViewModel.selectedPrice.value == course.prices.sale15!!) Primary_Green else Color.LightGray,
                                RoundedCornerShape(20)
                            )
                            .clickable {
                                courseViewModel.selectedPrice.value = course.prices.sale15!!
                                courseViewModel.selectedType.value = "sale15"

                                if (scrollstate.value > 800) {
                                    scope.launch {
                                        scrollstate.animateScrollTo(800)
                                    }
                                }
                            }, verticalArrangement = Arrangement.Center
                    )
                    {
                        Text(
                            text = "Очно в группе",
                            Modifier.padding(10.dp),
                            color = if (courseViewModel.selectedPrice.value == course.prices.sale15!!) Color.White else Color.Black
                        )
                    }
                }
                if (course.prices.ind != 0) {
                    Column(
                        modifier = Modifier
                            .height(45.dp)
                            .padding(end = 7.dp)
                            .clip(
                                RoundedCornerShape(20)
                            )
                            .background(if (courseViewModel.selectedPrice.value == course.prices.ind!!) Primary_Green else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (courseViewModel.selectedPrice.value == course.prices.ind!!) Primary_Green else Color.LightGray,
                                RoundedCornerShape(20)
                            )
                            .clickable {
                                courseViewModel.selectedPrice.value = course.prices.ind!!
                                courseViewModel.selectedType.value = "ind"

                                if (scrollstate.value > 800) {
                                    scope.launch {
                                        scrollstate.animateScrollTo(800)
                                    }
                                }
                            }, verticalArrangement = Arrangement.Center
                    )
                    {
                        Text(
                            text = "Индивидуально",
                            Modifier.padding(10.dp),
                            color = if (courseViewModel.selectedPrice.value == course.prices.ind!!) Color.White else Color.Black
                        )
                    }
                }
                if (course.prices.weekend != 0) {
                    Column(
                        modifier = Modifier
                            .height(45.dp)
                            .padding(end = 7.dp)
                            .clip(
                                RoundedCornerShape(20)
                            )
                            .background(if (courseViewModel.selectedPrice.value == course.prices.weekend!!) Primary_Green else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (courseViewModel.selectedPrice.value == course.prices.weekend!!) Primary_Green else Color.LightGray,
                                RoundedCornerShape(20)
                            )
                            .clickable {
                                courseViewModel.selectedPrice.value = course.prices.weekend!!
                                courseViewModel.selectedType.value = "weekend"

                                if (scrollstate.value > 800) {
                                    scope.launch {
                                        scrollstate.animateScrollTo(800)
                                    }
                                }
                            }, verticalArrangement = Arrangement.Center
                    )
                    {
                        Text(
                            text = "Учись в выходной",
                            Modifier.padding(10.dp),
                            color = if (courseViewModel.selectedPrice.value == course.prices.weekend!!) Color.White else Color.Black
                        )
                    }
                }
            }
        }


        if (course.description.isNotBlank()) {
            Text(text = "Описание", fontSize = 20.sp, modifier = Modifier.padding(end = 5.dp))
//            WebViewScreen(course.description)
            Html(text = course.description)
        }
        Text(
            text = "Документы для поступления:",
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 10.dp)
        )
        for (i in 0 until course.admissions.size) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp)
                        .size(8.dp)
                        .background(Color.Black, shape = CircleShape),
                )

                Text(course.admissions[i])
            }
        }
        Spacer(modifier = Modifier.height(70.dp))

    }
}

@Composable
fun CourseDocs(course: Course) {
    val list = course.docs
    Column(modifier = Modifier.padding(10.dp)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Выдаваемые документы",
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            list.forEachIndexed { index, doc ->
                AsyncImage(
                    model = "https://trayektoriya.ru/build/images/documents/" + doc.image,
                    contentDescription = "", modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 15.dp)
//                        .clip(
//                            RoundedCornerShape(20.dp)
//                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(70.dp))

    }
}


@Composable
fun CourseGroups(
    course: Course,
    courseViewModel: CourseViewModel,
    navHostController: NavHostController
) {
    val group = course.groups
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formaTime = DateTimeFormatter.ofPattern("HH:mm")
    val courses = remember {
        mutableStateOf(course.groups?.filter { group -> group.month == courseViewModel.selectedMonth.value }
            ?: listOf())
    }

    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        LazyRow(
            Modifier
                .fillMaxWidth()
                .padding(start = 5.dp)
                .padding(vertical = 5.dp)
        ) {

            itemsIndexed(courseViewModel.availible_months.value)
            { index, item ->
                Column(
                    modifier = Modifier
                        .height(36.dp)
                        .padding(horizontal = 5.dp)
                        .clip(
                            RoundedCornerShape(20)
                        )
                        .border(
                            width = 1.dp,
                            color = if (courseViewModel.selectedMonth.value == item) Primary_Green else Color.LightGray,
                            RoundedCornerShape(20)
                        )
                        .clickable {
                            courseViewModel.selectedMonth.value = item
                            courses.value =
                                course.groups?.filter { group -> group.month == item } ?: listOf()
                        }, verticalArrangement = Arrangement.Center
                )
                {
                    Text(
                        text = courseViewModel.monthes[item],
                        modifier = Modifier.padding(horizontal = 5.dp),
                        fontSize = 18.sp,
                        lineHeight = 36.sp,
                        color = Color.Black
                    )
                }

            }

        }
        if (courses.value.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        contentDescription = "No Schedule Icon",
                        Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = "Ближайшие даты уточняйте у менеджера",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        Column {
            courses.value.forEachIndexed { index, group ->
                CourseGroup(course.id, group, navHostController)
            }
        }
        Spacer(modifier = Modifier.height(70.dp))

    }

}


@Composable
fun CourseGroup(
    course_id: Int = 0,
    group: GroupCart = GroupCart(
        1,
        1,
        1,
        "МАС-1-ВХ",
        "LocalDate.now()",
        "LocalDate.now()",
        "LocalTime.now()",
        "uid",
        "https://trayektoriya.ru/build/images/teachers/2.jpg",
        "Михайлов Р.С",
        1
    ),
    navHostController: NavHostController
) {
    val ctx = LocalContext.current
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formaTime = DateTimeFormatter.ofPattern("HH:mm")
    var ok = 0;
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(100.dp)
            .border(1.dp, Primary_Green, RoundedCornerShape(10))
            .background(
                Brush.linearGradient(listOf(Primary_Green, Primary_Green)),
                RoundedCornerShape(10),
                0.5f
            )
            .clip(RoundedCornerShape(10.dp)).clickable {
                CartViewModel.addToCart(ctx, course_id, group.id)
                if (ok == 0) {
                    ok = 1
                    val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    if (vibrator.hasVibrator()) { // Vibrator availability checking
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                200,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        ) // New vibrate method for API Level 26 or higher
                    }

                }
                navHostController.navigate("cart")
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
            IconButton(
                onClick = {
                    CartViewModel.addToCart(ctx, course_id, group.id)
                    if (ok == 0) {
                        ok = 1
                        val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (vibrator.hasVibrator()) { // Vibrator availability checking
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                    200,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )
                            ) // New vibrate method for API Level 26 or higher
                        }

                    }
                    navHostController.navigate("cart")

                },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_shopping_cart_24),
                    contentDescription = "",
                    tint = Aggressive_red,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(50))
                        .padding(5.dp)
                )
            }
        }
        Column(
            Modifier
                .padding(5.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                    {
                        append("Начало: ")
                    }
                    append(group.start.format(format))
                }, color = Color.White
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                    {
                        append("Время начала: ")
                    }
                    append(group.time.format(formaTime))
                }, color = Color.White
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold))
                    {
                        append("Преподаватель: ")
                    }
                    append(group.teacher_name ?: "Не указан")
                }, color = Color.White
            )

        }

        AsyncImage(model = group.teacher, contentDescription = "")
    }
}

@Composable
fun CourseModules(course: Course) {
    Log.d("MyLog", course.modules.size.toString())
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        course.modules.forEach { it ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 5.dp)
                    .background(
                        Brush.linearGradient(listOf(Primary_Green, Primary_Green)),
                        RoundedCornerShape(10),
                        0.5f
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                    Text(text = it.name, color = Color.White)
                }
                Column(verticalArrangement = Arrangement.Center) {
                    if (it.hours != 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.time_svgrepo_com),
                                contentDescription = "",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(text = it.hours.toString(), color = Color.White)
                        }
                    }
                }
            }

        }
    }
}

@Preview(name = "CatalogScreen")
@Composable
private fun PreviewCatalogScreen() {
    CourseScreen(navHostController = rememberNavController(), 1)
}

@Composable
fun Html(text: String) {
    AndroidView(factory = { context ->
        TextView(context).apply {
            setText(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT))
        }
    })
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SliderView(
    state: PagerState,
    viewModel: CourseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val imageUrl =
        remember { mutableStateOf("") }
    HorizontalPager(
        state = state,
        count = viewModel.courses.value.get(0).images.size, modifier = Modifier
            .height(250.dp)
            .fillMaxWidth()
    ) { page ->

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            AsyncImage(
                model = viewModel.courses.value.get(0).images[page],
                contentDescription = "",
                Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

        }

    }
}


@Preview
@Composable
fun DotsIndicator(
    totalDots: Int = 6,
    selectedIndex: Int = 3
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), horizontalArrangement = Arrangement.Center
    ) {

        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = Primary_Green)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = Color.LightGray)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}


@Composable
fun ModalForm(mainViewModel: CourseViewModel) {
    var name = remember { mutableStateOf(TextFieldValue("")) }
    var email = remember { mutableStateOf(TextFieldValue("")) }
    var phone = remember { mutableStateOf("") }
    var comment = remember { mutableStateOf(TextFieldValue("")) }


    var nameError = remember {
        mutableStateOf(false)
    }
    var emailError = remember {
        mutableStateOf(false)
    }
    var phoneError = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    if (mainViewModel.modalForm.value) {
        AlertDialog(
            onDismissRequest = {
                mainViewModel.modalForm.value = false
            },
            text = {
                Column() {
                    Text("Задать вопрос по курсу", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    NameTextField(name = name, nameError)
                    PhoneTextField(phone = phone, phoneError)
                    EmailTextField(email = email, emailError)
                    OutlinedTextField(
                        value = comment.value,
                        placeholder = { Text(text = "Ваш вопрос", Modifier.alpha(0.5f)) },
                        onValueChange = { comment.value = it },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Email Icon"
                            )
                        },
                        label = { Text(text = "Вопрос") },
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .height(100.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary_Green, cursorColor = Color.Black
                        )
                    )


                }

            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (phone.value.length != 10) {
                                    phoneError.value = true
                                    return@Button
                                } else {
                                    phoneError.value = false

                                }
                                if (!isValidEmail(email.value.text)) {
                                    emailError.value = true
                                    return@Button
                                } else {
                                    emailError.value = false
                                }
                                if (name.value.text.isEmpty()) {
                                    nameError.value = true
                                    return@Button
                                } else {
                                    nameError.value = false
                                }


                                SendDataToAmo.sendDataToAmo(
                                    Data2Amo(
                                        "Задать вопрос по курсу " + mainViewModel.courses.value[0].name + " с мобильного приложения\n" + comment.value.text,
                                        email = email.value.text,
                                        "Задать вопрос по курсу " + mainViewModel.courses.value[0].name + " с мобильного приложения",
                                        name.value.text.toString(),
                                        phone = phone.value
                                    ), ctx = context
                                )
                                mainViewModel.modalForm.value = false
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Оставить заявку", color = Color.White)
                        }
                        Privacy()
                    }
                }
            },
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetWithAnchor() {

    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scope = rememberCoroutineScope()
    val sheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
        sheetElevation = 2.dp,
        sheetBackgroundColor = Color.Transparent,
        sheetPeekHeight = 49.dp,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    scope.launch {
                        if (sheetState.isCollapsed) {
                            sheetState.expand()
                        } else if (sheetState.isExpanded) {
                            sheetState.collapse()
                        }
                    }
                }) {
                    val icon = if (sheetState.isExpanded) {
                        Icons.Filled.KeyboardArrowDown
                    } else {
                        Icons.Filled.KeyboardArrowUp
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = "Icon button"
                    )
                }
                BottomSheetContent()
            }
        },
        content = { Text(text = "test") },

        )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalSheetWithAnchor(
    sheetState: ModalBottomSheetState,
    showModalSheet: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "",
            modifier = Modifier.align(
                alignment = Alignment.BottomCenter
            )
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun SheetLayout() {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    val showModalSheet = rememberSaveable {
        mutableStateOf(false)
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheetContent() }
    ) {
        ModalSheetWithAnchor(sheetState, showModalSheet)
    }
}

@Composable
fun BottomSheetContent() {
    Surface(
        modifier = Modifier.height(300.dp),
        color = Color(0xff7353ba)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Modal Bottom Sheet",
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp),
                color = Color.White
            )
            Divider(
                modifier = Modifier.padding(5.dp),
                color = Color.White
            )
            Text(
                text = "test",
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}