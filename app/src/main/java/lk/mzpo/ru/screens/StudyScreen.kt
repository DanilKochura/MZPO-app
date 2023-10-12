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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.material.tooltip.TooltipDrawable
import com.google.gson.Gson
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.study.StudyModule
import lk.mzpo.ru.network.retrofit.AuthData
import lk.mzpo.ru.network.retrofit.AuthService
import lk.mzpo.ru.ui.components.EmailTextField
import lk.mzpo.ru.ui.components.PasswordTextField
import lk.mzpo.ru.ui.components.PieChart
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Orange
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.ContractsViewModel
import lk.mzpo.ru.viewModel.StudyViewModel
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    contract: Contract,
    navHostController: NavHostController,
    studyViewModel: StudyViewModel = viewModel()
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

            studyViewModel.contract = contract
            studyViewModel.getData(context)
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


                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp)) {
                            Text(text = contract.course!!.name, modifier = Modifier.weight(2f), fontSize = 20.sp, maxLines = 3, fontWeight = FontWeight.Bold)
                            AsyncImage(model = contract.course!!.image, contentDescription = "", modifier = Modifier
                                .weight(1f)
                                .clip(
                                    RoundedCornerShape(10.dp)
                                ))
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
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
                        LinearProgressIndicator(
                            progress = contract.progress!!.total!!.toFloat().div(100f),
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .height(10.dp)
                                .clip(
                                    RoundedCornerShape(50)
                                ),
                            color = Primary_Green,
                            trackColor = Color.LightGray
                        ) //70% progress
                        Divider()
                        LazyColumn(content = {
                            itemsIndexed(studyViewModel.studyModules.value)
                            {
                                i, item ->

                                module(studyModule = item, int = i, navHostController, onClick = {
                                    val gson = Gson()
                                    val contractJson = gson.toJson(
                                        contract,
                                        Contract::class.java
                                    )
                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                        "Contract",
                                        contractJson
                                    )
                                    val smJson = gson.toJson(
                                        item,
                                        StudyModule::class.java
                                    )
                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                        "StudyModule",
                                        smJson
                                    )

                                    navHostController.navigate("study/module")
                                })
                            }
                        }, modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }
            }
        }
    )
}


@Composable fun module(studyModule: StudyModule, int: Int, navHostController: NavHostController, onClick: () -> Unit = {})
{

    var sum = 0;
    var checked = 0;
    for(i in studyModule.activeMaterials)
    {
        if (i.activeFile!!.type == "video")
        {
            sum+=i.activeFile!!.size ?: 0
        } else if(i.activeFile!!.type == "file")
        {
            sum+=(i.activeFile!!.size ?: 0)*120

        } else if(i.activeFile!!.type == "test")
        {
            sum+= 1200

        }

        if(i.activeFile!!.userProgress !== null)
        {
            if(checked != 1)
            {
                if (i.activeFile!!.userProgress?.viewed == "1")
                {
                    checked = 1;
                }
                if (i.activeFile!!.userProgress?.viewed == "2")
                {
                    checked = 2;
                }
            }
        } else
        {
            if(checked != 0)
            {
                checked = 1
            }
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                onClick.invoke()
            }, horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(
            Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 20.dp)
        ) {
            Text(text = "${int+1}.  ${studyModule.name} ", fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
            Text(text = "~ ${sum.div(60).toInt()} мин.")

        }
        Box (modifier = Modifier.fillMaxSize()){
            if(checked == 2)
            {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "", tint = Primary_Green, modifier = Modifier.align(
                    Alignment.Center))
            } else if (checked == 1)
            {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "", tint = Orange, modifier = Modifier.align(
                    Alignment.Center))

            }
        }
    }
    Divider()


}
