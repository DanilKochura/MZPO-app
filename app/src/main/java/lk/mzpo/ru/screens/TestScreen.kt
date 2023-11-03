package lk.mzpo.ru.screens

import android.content.Context
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.navigation.NavHostController
import androidx.navigation.ui.navigateUp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.study.ActiveMaterials
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Orange
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.ui.theme.Primary_Green_BG
import lk.mzpo.ru.viewModel.TestViewModel
import lk.mzpo.ru.viewModel.VideoViewModel
import retrofit2.http.Url
import java.io.InputStream
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    navHostController: NavHostController, test: Int, contract: Int, testViewModel: TestViewModel = viewModel()
, cart_sum: MutableState<Int> = mutableStateOf(0)) {
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
            Log.d("StudyLog", contract.toString()+test.toString())
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
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded)), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                           LoadableScreen(loaded = testViewModel.loaded)
                           {
                               if(testViewModel.material.value !== null)
                               {

                                   if(!testViewModel.testStarted.value)
                                   {
                                       Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                           Text(text = "Тест по теме:")
                                           Text(text = testViewModel.material.value!!.name.toString())
                                           Spacer(modifier = Modifier.height(20.dp))
                                           Text(text = "Количество вопросов: "+testViewModel.questionCount.value.toString())
                                           Text(text = "Осталось попыток: "+testViewModel.attemptsLeft.value.toString())
                                           Button(onClick = { testViewModel.testStarted.value = true }, colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green_BG, contentColor = Color.White), enabled = (testViewModel.question.value !== null)) {
                                               if(testViewModel.hasNotFinishedAttempt.value)
                                               {
                                                   Text(text = "Продолжить тест", color = Color.White)
                                               } else
                                               {
                                                   Text(text = "Начать тест", color = Color.White)
                                               }
                                           }
                                       }
                                   } else
                                   {

                                       if(testViewModel.question.value!!.multiple == "no")
                                       {
                                           val selected = remember {
                                               mutableStateOf(-1)
                                           }
                                           Column(
                                               Modifier
                                                   .fillMaxSize()
                                                   .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                               Column(Modifier.fillMaxWidth()) {
                                                   Text(testViewModel.question.value!!.question.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
                                                   Text(testViewModel.question.value!!.label.toString(), fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top=5.dp))
                                               }
                                               Divider()
                                               val shape = if(testViewModel.question.value!!.multiple == "no") RoundedCornerShape(50) else RoundedCornerShape(5.dp)
                                               LazyColumn(content = {
                                                   itemsIndexed(testViewModel.question.value!!.activeAnswers)
                                                   {
                                                           index, item ->
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
                                                           Modifier.clip(RoundedCornerShape(10.dp))
                                                               .background(Primary_Green.copy(0.3f), RoundedCornerShape(10.dp))
                                                               .padding(7.dp)
                                                               .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                                           Text(text = item.answer.toString(), modifier = Modifier.fillMaxWidth(0.6f))
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
                                                       Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.Center,) {
                                                           Button(onClick = {
                                                               testViewModel.sendSingle(context, contract, test, selected.value, testViewModel.question.value!!.id!!)
                                                           }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red, contentColor = Color.White)) {
                                                               Text(text = "Подтвердить", color = Color.White)
                                                           }
                                                       }
                                                   }
                                               })

                                           }
                                       }

                                   }
                               }
                           }
                    }
                }
            }
        }
    )
}


@Preview
@Composable
fun Answer()
{
    val checked = remember {
        mutableStateOf(false)
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Вариант ответа", modifier = Modifier.padding(horizontal = 20.dp))
    }
}

