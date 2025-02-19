package lk.mzpo.ru.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.ui.theme.Primary_Green_BG
import lk.mzpo.ru.viewModel.TestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    navHostController: NavHostController,
    test: Int,
    contract: Int,
    testViewModel: TestViewModel = viewModel(),
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
            Log.d("StudyLog", contract.toString() + test.toString())
            val context = LocalContext.current
            testViewModel.getData(context, contract, test)
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
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LoadableScreen(loaded = testViewModel.loaded)
                        {
                            if (testViewModel.material.value !== null) {

                                if (!testViewModel.finished.value) {

                                    if (!testViewModel.testStarted.value) {
                                        Column(
                                            Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(painter = painterResource(id = R.drawable.baseline_quiz_24), contentDescription = "", tint = Primary_Green, modifier = Modifier.size(200.dp))
                                            Text(text = "Тест по теме:")
                                            Text(text = testViewModel.material.value!!.name.toString(), Modifier.fillMaxWidth(0.7f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Text(text = "Количество вопросов: " + testViewModel.questionCount.value.toString())
//                                            Text(text = "Осталось попыток: " + testViewModel.attemptsLeft.value.toString())
                                            Button(
                                                onClick = {
                                                    testViewModel.testStarted.value = true
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    backgroundColor = Primary_Green_BG,
                                                    contentColor = Color.White
                                                ),
                                                enabled = (testViewModel.question.value !== null)
                                            ) {
                                                if (testViewModel.hasNotFinishedAttempt.value) {
                                                    Text(
                                                        text = "Продолжить тест",
                                                        color = Color.White
                                                    )
                                                } else {
                                                    Text(text = "Начать тест", color = Color.White)
                                                }
                                            }
                                            LazyRow {
                                                itemsIndexed(testViewModel.attempts.value)
                                                {
                                                    it, value ->
                                                    if (value.result != null)
                                                    {
                                                        var color = Aggressive_red
                                                        if(value.result!! >= 70.0f)
                                                        {
                                                            color = Primary_Green
                                                        }
                                                        Column (modifier = Modifier
                                                            .padding(10.dp)
                                                            .background(
                                                                color.copy(0.1f),
                                                                RoundedCornerShape(10.dp)
                                                            )
                                                            .padding(5.dp)){
                                                            Text(text = "Попытка: "+value.attempt, color = color)
                                                            Text(text = "Результат: "+value.result+"%", color = color)
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    } else {

//                                       if(testViewModel.question.value!!.multiple == "no")
//                                       {
                                        val selected = remember {
                                            mutableStateOf(-1)
                                        }
                                        val selectedMultiple = remember {
                                            mutableStateListOf<Int>()
                                        }
                                        Column(
                                            Modifier
                                                .fillMaxSize()
                                                .padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Column(Modifier.fillMaxWidth()) {
                                                Text(
                                                    testViewModel.question.value!!.question.toString(),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                                if (testViewModel.question.value!!.file !== null)
                                                {
                                                    AsyncImage(model = "https://trayektoriya.ru/uploads/files/"+testViewModel.question.value!!.file, contentDescription = "", modifier = Modifier.fillMaxWidth())
                                                }
//                                                   Text(testViewModel.question.value!!.label.toString(), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top=5.dp))
                                            }
                                            Divider()
                                            if (testViewModel.question.value!!.multiple == "no")
                                            {
                                                Text(text = "Выбор одного ответа", color = Color.Gray, fontSize = 12.sp)
                                            } else
                                            {
                                                Text(text = "Выбор нескольких вариантов ответа", color = Color.Gray, fontSize = 12.sp)
                                            }
                                            val shape =
                                                if (testViewModel.question.value!!.multiple == "no") RoundedCornerShape(
                                                    50
                                                ) else RoundedCornerShape(5.dp)

                                            Log.d(
                                                "MyTestLog",
                                                testViewModel.question.value!!.activeAnswers.size.toString()
                                            )
                                            Log.d(
                                                "MyTestLog",
                                                testViewModel.question.value!!.activeAnswers[0].toString()
                                            )
                                            if (testViewModel.question.value!!.multiple == "no")
                                            {
                                                LazyColumn(content = {
                                                    itemsIndexed(testViewModel.question.value!!.activeAnswers)
                                                    { index, item ->
                                                        Log.d("MyTestLog", item.toString())
                                                        Box(modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 10.dp,
                                                                vertical = 5.dp
                                                            )
                                                            .clickable {
                                                                selected.value = item.id!!
                                                            })
                                                        Row(
                                                            Modifier
                                                                .clip(RoundedCornerShape(10.dp))
                                                                .background(
                                                                    Primary_Green.copy(0.3f),
                                                                    RoundedCornerShape(10.dp)
                                                                )
                                                                .padding(7.dp)
                                                                .fillMaxWidth(),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            if (item.image !== null)
                                                            {
                                                                AsyncImage(model = "https://trayektoriya.ru/uploads/files/"+item.image, contentDescription = "", modifier = Modifier
                                                                    .fillMaxWidth(0.6f)
                                                                    .height(150.dp))
                                                            } else
                                                            {
                                                                Text(
                                                                    text = item.answer.toString(),
                                                                    modifier = Modifier.fillMaxWidth(0.6f)
                                                                )
                                                            }
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(size = 28.dp)
                                                                        .clip(shape = shape) // to remove the ripple effect on the corners
                                                                        .clickable {
                                                                            selected.value =
                                                                                item.id!!

                                                                        }
                                                                        .background(
                                                                            color = if (selected.value == item.id!!) Primary_Green_BG else Color.White,
                                                                            shape = shape
                                                                        )
                                                                        .border(
                                                                            width = 2.dp,
                                                                            color = if (selected.value == item.id!!) Color.DarkGray else Color.Gray,
                                                                            shape = shape
                                                                        ),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    if (selected.value == item.id!!) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Check,
                                                                            contentDescription = null,
                                                                            tint = Color.White
                                                                        )
                                                                    }
                                                                }


                                                            }
                                                        }
                                                    }
                                                    item {
                                                        Row(
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .padding(top = 10.dp),
                                                            horizontalArrangement = Arrangement.Center,
                                                        ) {
                                                            Button(
                                                                onClick = {
                                                                    if (selected.value == -1) {
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Выберите вариант ответа!",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        return@Button
                                                                    }
                                                                    testViewModel.sendSingle(
                                                                        context,
                                                                        contract,
                                                                        test,
                                                                        selected.value,
                                                                        testViewModel.question.value!!.id!!
                                                                    )
                                                                    selected.value = -1
                                                                },
                                                                colors = ButtonDefaults.buttonColors(
                                                                    backgroundColor = Color.Red,
                                                                    contentColor = Color.White
                                                                )
                                                            ) {
                                                                Text(
                                                                    text = "Подтвердить",
                                                                    color = Color.White
                                                                )
                                                            }
                                                        }
                                                    }
                                                })
                                            } else
                                            {
                                                LazyColumn(content = {
                                                    itemsIndexed(testViewModel.question.value!!.activeAnswers)
                                                    { index, item ->
                                                        Log.d("MyTestLog", item.toString())
                                                        Box(modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 10.dp,
                                                                vertical = 5.dp
                                                            )
                                                            .clickable {
                                                                selected.value = item.id!!
                                                            })
                                                        Row(
                                                            Modifier
                                                                .clip(RoundedCornerShape(10.dp))
                                                                .background(
                                                                    Primary_Green.copy(0.3f),
                                                                    RoundedCornerShape(10.dp)
                                                                )
                                                                .padding(7.dp)
                                                                .fillMaxWidth(),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            if (item.image !== null)
                                                            {
                                                                AsyncImage(model = "https://trayektoriya.ru/uploads/files/"+item.image, contentDescription = "", modifier = Modifier
                                                                    .fillMaxWidth(0.6f)
                                                                    .height(150.dp))
                                                            } else
                                                            {
                                                                Text(
                                                                    text = item.answer.toString(),
                                                                    modifier = Modifier.fillMaxWidth(0.6f)
                                                                )
                                                            }
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(size = 28.dp)
                                                                        .clip(shape = shape) // to remove the ripple effect on the corners
                                                                        .clickable {
                                                                            if (selectedMultiple.contains(
                                                                                    index + 1
                                                                                )
                                                                            ) {
                                                                                selectedMultiple.remove(
                                                                                    index + 1
                                                                                )
                                                                            } else {
                                                                                selectedMultiple.add(
                                                                                    index + 1
                                                                                )
                                                                            }
                                                                            Log.d(
                                                                                "MyLog",
                                                                                selectedMultiple.joinToString(
                                                                                    " "
                                                                                )
                                                                            )
                                                                            Log.d(
                                                                                "MyLog",
                                                                                selectedMultiple
                                                                                    .contains(index + 1)
                                                                                    .toString()
                                                                            )
                                                                            Log.d(
                                                                                "MyLog",
                                                                                selectedMultiple
                                                                                    .indexOf(index + 1)
                                                                                    .toString()
                                                                            )
                                                                        }
                                                                        .background(
                                                                            color = if (selectedMultiple.indexOf(
                                                                                    index + 1
                                                                                ) != -1
                                                                            ) Primary_Green_BG else Color.White,
                                                                            shape = shape
                                                                        )
                                                                        .border(
                                                                            width = 2.dp,
                                                                            color = if (selectedMultiple.indexOf(
                                                                                    index + 1
                                                                                ) != -1
                                                                            ) Color.DarkGray else Color.Gray,
                                                                            shape = shape
                                                                        ),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    if (index+1  in selectedMultiple) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Check,
                                                                            contentDescription = null,
                                                                            tint = Color.White
                                                                        )
                                                                    }
                                                                }


                                                            }
                                                        }
                                                    }
                                                    item {
                                                        Row(
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .padding(top = 10.dp),
                                                            horizontalArrangement = Arrangement.Center,
                                                        ) {
                                                            Button(
                                                                onClick = {
                                                                    if (selectedMultiple.size == 0) {
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Выберите все правильные варианты ответа!",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        return@Button
                                                                    }
                                                                    testViewModel.sendMultiple(
                                                                        context,
                                                                        contract,
                                                                        test,
                                                                        selectedMultiple.toMutableList(),
                                                                        testViewModel.question.value!!.id!!
                                                                    )
                                                                    selectedMultiple.clear()
                                                                },
                                                                colors = ButtonDefaults.buttonColors(
                                                                    backgroundColor = Color.Red,
                                                                    contentColor = Color.White
                                                                )
                                                            ) {
                                                                Text(
                                                                    text = "Подтвердить",
                                                                    color = Color.White
                                                                )
                                                            }
                                                        }
                                                    }
                                                })

                                            }

                                        }
                                    }

                                } else {
                                    Column(
                                        Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (testViewModel.result.value!!.result >= 70.0f) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = "", tint = Primary_Green, modifier = Modifier
                                                .size(100.dp)
                                                .padding(10.dp))
                                            Text(text = "Результат: " + testViewModel.result.value?.result.toString() + "%")
                                            Text(
                                                text = "Тест пройден!",
                                                fontSize = 18.sp,
                                                color = Primary_Green
                                            )
                                        } else {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Aggressive_red, modifier = Modifier
                                                .size(100.dp)
                                                .padding(10.dp))
                                            Text(text = "Результат: " + testViewModel.result.value?.result.toString() + "%")
                                            Text(
                                                text = "Тест не пройден!",
                                                fontSize = 18.sp,
                                                color = Aggressive_red
                                            )

                                        }
                                        OutlinedButton(
                                            onClick = { navHostController.navigateUp() },
                                            border = BorderStroke(2.dp, Primary_Green),
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        ) {
                                            Text(
                                                text = "Назад",
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
//                }
            }
        }
    )
}


@Preview
@Composable
fun Answer() {
    val checked = remember {
        mutableStateOf(false)
    }
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Вариант ответа", modifier = Modifier.padding(horizontal = 20.dp))
    }
}

