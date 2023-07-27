package lk.mzpo.ru.screens

import CoursePreview
import Prices
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.network.retrofit.Data2Amo
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.components.stories.Story
import lk.mzpo.ru.ui.components.stories.data.StoryIndicator
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.MainViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Main(navHostController: NavHostController = rememberNavController(), padding: PaddingValues = PaddingValues(25.dp), mainViewModel: MainViewModel = viewModel())
{
    mainViewModel.getStories(LocalContext.current)
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
        content = {padding ->
            Box(
                Modifier
                    .background(color = Primary_Green)
                    .fillMaxSize()) {
                //region Top
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.lebed),
                        contentDescription = "lebed_back",
                        modifier = Modifier.padding(1.dp))
                }
                //endregion

                Column (
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Row (horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp)){
                        SearchViewPreview();
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "bell", tint = Color.White, modifier = Modifier.size(40.dp))
                        }
                    }
                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                            )
                            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                            .verticalScroll(rememberScrollState())) {

                        //region Strories
                        LazyRow(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {

                            itemsIndexed(mainViewModel.stories.value)
                            {
                                    index, item ->
                                Column( verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
                                {
                                    Image(painter = painterResource(id = mainViewModel.Story_lables[index][1] as Int), contentDescription = "",
                                        contentScale = ContentScale.Inside, modifier = Modifier.clip(RoundedCornerShape(50)).clickable {

                                           if(mainViewModel.stories.value[index].isNotEmpty())
                                           {
                                               mainViewModel._storyState.targetState = true
                                               mainViewModel.story_position.value = index
                                           }
                                        }
                                        )
                                    Text(
                                        text = mainViewModel.Story_lables[index][0] as String,
                                        style = TextStyle(
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight(400),
                                            color = Color(0xFF505050),
                                            textAlign = TextAlign.Center,
                                        )
                                    )

                                }

                            }


                        }
                        //endregion

                        Text(
                            text = "ПОПУЛЯРНЫЕ КУРСЫ",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF1D2B4B),
                                textAlign = TextAlign.Center,
                            ),
                            modifier = Modifier.padding(10.dp)
                        )

                        LazyRow(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 10.dp)
                        ) {
                            itemsIndexed(mainViewModel.courses)
                            {
                                _, index ->
                                CourseCard(index, Modifier.width(300.dp))
                            }
                        }
                        MainPromoBanner()
                        Text(
                            text = "НАПРАВЛЕНИЯ ОБУЧЕНИЯ",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF1D2B4B),
                                textAlign = TextAlign.Center,
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                        MainBottomCats(navHostController)
                    }
                }

            }
        }, modifier = Modifier.fillMaxSize()
    )
    AnimatedVisibility(visibleState = mainViewModel._storyState, modifier = Modifier.fillMaxSize(), exit = fadeOut()) {

        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {

                detectDragGestures { change, dragAmount ->
                    val (x, y) = dragAmount
                    when {
                        x > 150 && abs(y) < 5 -> {
                            if (mainViewModel.story_position.value > mainViewModel.stories.value.size - 1) {
                                Log.d(
                                    "MyLog2",
                                    mainViewModel.story_position.value.toString() + " forward"
                                )
                                mainViewModel.story_position.value++

                            } else {
                                mainViewModel._storyState.targetState = false
                            }
                        }

                        x < 150 && abs(y) < 5 -> {
                            Log.d("MyLog2", mainViewModel.story_position.value.toString())
                            Log.d(
                                "MyLog2",
                                mainViewModel.story_position.value.toString() + " forward"
                            )
                            Log.d(
                                "MyLog2",
                                mainViewModel.stories.value.size.toString() + " forward"
                            )
                            if (mainViewModel.story_position.value < mainViewModel.stories.value.size - 1) {
                                mainViewModel.story_position.value++


                            } else {
                                mainViewModel._storyState.targetState = false
                            }
                        }
                    }
                    when {
                        y > 10 && abs(x) < 5 -> {
                            Log.d("MyLog2", mainViewModel.story_position.value.toString())

                            Log.d("MyLog", "down")
                            mainViewModel._storyState.targetState = false
                        }

                        y < 0 -> {
                            Log.d("MyLog", "up")
                        }
                    }
                    Log.d("MyLog1", "$x $y")

                }
//                detectDragGestures { change, dragAmount ->
//                    Log.d("MyLog1", change.scrollDelta.x.toString())
//                }

            })
        {
//                    Log.d("MyLog", mainViewModel.stories.value.size.toString())
//                    Log.d("MyLog", mainViewModel.stories.value[0].size.toString())

            val  stories_array = remember {
                  mutableStateOf(mainViewModel.stories.value[mainViewModel.story_position.value])
            }
            val ctx = LocalContext.current
            Story(
                urlList = stories_array.value,
                indicator = StoryIndicator.multiIndicator(),
                onAllStoriesShown =  {
                    if(mainViewModel.story_position.value <= mainViewModel.stories.value.size - 2)
                    {
                        Toast.makeText(ctx, "All Showed", Toast.LENGTH_SHORT).show()
                        mainViewModel._storyState.targetState = false



                    } else
                    {
                        mainViewModel._storyState.targetState = false
                    }
                },
                pause = mainViewModel.paused

            )

        }
    }
    var name  = remember { mutableStateOf(TextFieldValue("")) }
    var email  = remember { mutableStateOf(TextFieldValue("")) }
    var phone  = remember { mutableStateOf(TextFieldValue("+7")) }
    val context = LocalContext.current

    AnimatedVisibility(visibleState = mainViewModel.openDialog, exit = slideOutVertically()) {
        AlertDialog(
            onDismissRequest = {
                mainViewModel.paused.value = false
                mainViewModel.openDialog.targetState = false
            },
            title = {
                Text(mainViewModel.form_title.value, fontSize = 20.sp, modifier = Modifier.padding(vertical = 5.dp))

            },
            text = {
                Column() {
                    TextField(
                        value = name.value,
                        placeholder = { Text(text = "ФИО", Modifier.alpha(0.5f))},
                        onValueChange = { name.value = it },
                        )
                    TextField(
                        value = phone.value,
                        placeholder = { Text(text = "Телефон", Modifier.alpha(0.5f))},
                        onValueChange = { phone.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),

                    )
                    TextField(
                        value = email.value,
                        placeholder = { Text(text = "Email", Modifier.alpha(0.5f))},
                        onValueChange = { email.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

                        )
                }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            mainViewModel.sendDataToAmo(
                                Data2Amo(
                                    "Записаться на "+mainViewModel.form_title.value+" с мобильного приложения",
                                    email = email.value.text,
                                    "Записаться на "+mainViewModel.form_title.value+" с мобильного приложения",
                                    name.value.text.toString(),
                                    phone = phone.value.text
                                ), ctx = context)
                            mainViewModel.paused.value = false
                            mainViewModel.openDialog.targetState = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red)
                    ) {
                        Text("Оставить заявку", color = Color.White)
                    }
                }
            }
        )
    }

}



@Preview
@Composable
fun CourseCard(course: CoursePreview = CoursePreview(1, "https://lk.mzpo-s.ru/build/images/courses/2022-03-10/1.jpg", "Классический массаж тела", "МАС-1", 60, Prices(1,2,3)), modifier: Modifier = Modifier)
{
    Card(modifier = modifier
        .padding(horizontal = 5.dp)
        .shadow(2.dp, RoundedCornerShape(10.dp))
        .width(300.dp), colors = CardDefaults.cardColors(
        containerColor = Color.White
    )) {
//        Image(painter = painterResource(id = R.drawable.masage), contentDescription = "", modifier = Modifier
//            .height(150.dp)
//            .fillMaxWidth(), contentScale = ContentScale.Crop)
        AsyncImage(model = course.image, contentDescription = course.id.toString(), modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(), contentScale = ContentScale.Crop)
        Column(modifier = Modifier
            .padding(7.dp)
            .fillMaxWidth()) {
            Text(course.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
            Row() {
                Icon(painter = painterResource(id = R.drawable.time_svgrepo_com), modifier = Modifier.height(20.dp),contentDescription = "hours")
                Text(text = course.hours.toString()+" ак.ч.")
            }
            Row() {

                Text(text = "Срок обучения: ", fontWeight = FontWeight.Bold)
                Text(text = "1,5 - 2 мес.")
            }
            Row{
                Text(text = "Дистанционно: ", fontWeight = FontWeight.Bold)
                Text(text = "9000 руб.", color = Aggressive_red)
            }
            Row{
                Text(text = "Очно: ", fontWeight = FontWeight.Bold)
                Text(text = course.prices.sale15.toString()+" руб.", color = Aggressive_red)
            }
            Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red, contentColor = Color.White)) {
                Text("Купить со скидкой", color = Color.White)
            }
        }
    }
}

@Composable
fun MainPromoBanner()
{
    AsyncImage(model = "https://www.mzpo-s.ru/images/events/2023/01/01/56_6352aae82c764.jpg", placeholder = painterResource(
        id = R.drawable.masage
    ), modifier = Modifier
        .padding(vertical = 10.dp, horizontal = 0.dp)
        .fillMaxWidth(), contentDescription = "", contentScale = ContentScale.FillBounds)
}
@Preview(showBackground = true)
@Composable
fun MainBottomCats(navHostController: NavHostController = rememberNavController())
{
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Surface (modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 5.dp)
            .shadow(1.dp, RoundedCornerShape(10.dp))
            .clickable {
                navHostController.navigate("catalog?name=massazh-i-reabilitaciya")
            }) {
            Image(
                painter = painterResource(id = R.drawable.kos), contentDescription = "",
                Modifier
                    .offset((-70).dp)
                    .alpha(0.3f), contentScale = ContentScale.FillHeight
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(start = 50.dp)
            ) {
                Text(
                    text = "Массаж и ",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight(600))
                )
                Text(
                    text = "реабилитация",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight(600))
                )

                Row {
                    Text(text = "более ", style = TextStyle(fontSize = 12.sp))
                    Text(text = "250 ", color = Aggressive_red, style = TextStyle(fontSize = 12.sp))
                    Text(text = "курсов ", style = TextStyle(fontSize = 12.sp))

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Подробнее", fontSize = 14.sp, color = Aggressive_red)
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "next",
                        tint = Aggressive_red
                    )

                }
            }
        }
        Surface (modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 5.dp)
            .shadow(1.dp, RoundedCornerShape(10.dp))
            .clickable {
                navHostController.navigate("catalog?name=medicinskaya-podgotovka")
            }) {
            Image(
                painter = painterResource(id = R.drawable.med), contentDescription = "",
                Modifier
                    .offset((-70).dp)
                    .alpha(0.3f), contentScale = ContentScale.FillHeight
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(start = 50.dp)
            ) {
                Text(
                    text = "Медицинская ",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight(600))
                )
                Text(
                    text = "подготовка",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight(600))
                )

                Row {
                    Text(text = "более ", style = TextStyle(fontSize = 12.sp))
                    Text(text = "600 ", color = Aggressive_red, style = TextStyle(fontSize = 12.sp))
                    Text(text = "курсов ", style = TextStyle(fontSize = 12.sp))

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Подробнее", fontSize = 14.sp, color = Aggressive_red)
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "next",
                        tint = Aggressive_red
                    )

                }
            }
        }


    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        Surface (modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(0.5f)
            .padding(horizontal = 5.dp)
            .shadow(1.dp, RoundedCornerShape(10.dp))
            .clickable {
                navHostController.navigate("catalog?name=kosmetologiya")
            }) {
            Image(
                painter = painterResource(id = R.drawable.mas1), contentDescription = "",
                Modifier
                    .offset((-70).dp)
                    .alpha(0.3f), contentScale = ContentScale.FillHeight
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(start = 50.dp)
            ) {
                Text(
                    text = "Косметология",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight(600))
                )
//                Text(text = "")

                Row {
                    Text(text = "более ", style = TextStyle(fontSize = 12.sp))
                    Text(text = "159 ", color = Aggressive_red, style = TextStyle(fontSize = 12.sp))
                    Text(text = "курсов ", style = TextStyle(fontSize = 12.sp))

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Подробнее", fontSize = 14.sp, color = Aggressive_red)
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "next",
                        tint = Aggressive_red
                    )

                }
            }
        }



    }
}