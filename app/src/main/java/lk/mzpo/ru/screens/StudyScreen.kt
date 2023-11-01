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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun StudyScreen(
    contract: Contract,
    navHostController: NavHostController,
    studyViewModel: StudyViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0)

) {
    val bottomSheetState =
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
            if(bottomSheetState.targetValue != ModalBottomSheetValue.Expanded)
            {
                Button(onClick = {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }, colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green, contentColor = Color.White), modifier = Modifier.size(60.dp), shape = RoundedCornerShape(10.dp)) {
                    Icon(painter = painterResource(id = R.drawable.baseline_document_scanner_24), contentDescription = "", tint = Color.White,  )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = { BottomNavigationMenu(navController = navHostController, cart = cart_sum) },
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
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(25.dp))
                        {
                            val progress = if(contract.progress!!.total!!.toFloat().div(100f) > 0.1f)  contract.progress!!.total!!.toFloat().div(100f) else 0.1f
                            LinearProgressIndicator(
                                progress = progress,
                                Modifier
                                    .fillMaxSize()
                                    .clip(
                                        RoundedCornerShape(50)
                                    ),
                                color = Primary_Green,
                                trackColor = Color.LightGray
                            ) //70% progress
                            Text(text = contract.progress!!.total!!.toInt().toString()+"%", color = Color.White, modifier = Modifier.padding( horizontal = 7.dp))
                        }
                        if(contract.course!!.prices.dist !== null && contract.course!!.prices.dist !== 0)
                        {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp, bottom = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = "warning", modifier = Modifier.padding(end = 5.dp), tint = Aggressive_red)
                                Text(text = "Для прохождения курса необходимо просмотреть более 70% материалов", fontSize = 12.sp, lineHeight = 12.sp)
                            }
                        }
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
                            item { 
                                Spacer(modifier = Modifier.padding(50.dp))
                            }
                        }, modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }
            }

            ModalBottomSheetLayout(
                sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                sheetContent = {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp)) {
                        Text(text = "Для выписки документа о прохождении обучения, загрузите Ваши документы", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Divider()
                        for (admission in studyViewModel.admissions)
                        {
                            Log.i("ID", admission.name.toString())
                           Column {
                               Text(text = admission.name.toString(), textAlign = TextAlign.Center)
                               var loaded: String? = null
                               for(j in studyViewModel.documents)
                               {
                                   if(admission.id == j.admissionId)
                                   {
                                        loaded = j.file
                                   }

                               }
                               PickImageFromGallery(contract.id, admission.id!!, loaded)

                           }
                            Divider()
                        }
                    }

                },
                sheetState = bottomSheetState,
                modifier = Modifier.padding(padding)
            ) {

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
