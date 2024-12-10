package lk.mzpo.ru.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Contract
import lk.mzpo.ru.models.study.NewMaterials
import lk.mzpo.ru.models.study.StudyModule
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Orange
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun StudyModuleScreen(
    module: StudyModule,
    contract: Contract,
    navHostController: NavHostController,
    studyViewModel: StudyViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0)

) {
    val gson = Gson()

    Scaffold(

        bottomBar = { BottomNavigationMenu(navController = navHostController, cart = cart_sum) },
        content = { padding ->
            Log.d("StudyLog", "entered")
            val context = LocalContext.current
            val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
            val token = test.getString("token_lk", "")
            val uriHandler = LocalUriHandler.current

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

                        Text(
                            text = module.module!!.name!!, textAlign = TextAlign.Center, modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(), fontSize = 18.sp
                        )
                        LazyColumn(content = {
                            itemsIndexed(module.materials)
                            { _, i ->
                                if (i.file !== null) {

                                    Card(
                                        modifier = Modifier
                                            .padding(vertical = 10.dp, horizontal = 20.dp)
                                            .shadow(2.dp, RoundedCornerShape(10.dp))
                                            .clickable(onClick = {
                                                if (i.file!!.image !== null) {
                                                    val video = gson.toJson(
                                                        i,
                                                        NewMaterials::class.java
                                                    )
                                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "video",
                                                        video
                                                    )
                                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "contract",
                                                        contract.id.toString()
                                                    )

                                                    navHostController.navigate("video")
                                                } else if (i.file!!.type == "file") {
                                                    val material = gson.toJson(
                                                        i,
                                                        NewMaterials::class.java
                                                    )
                                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "MATERIAL",
                                                        material
                                                    )

                                                    navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                        "CONTRACT",
                                                        contract.id
                                                    )
                                                    navHostController.navigate("pdf")
                                                } else if (i.file!!.type == "test" || i.file!!.type == "final_test") {
                                                    navHostController.navigate("test/${contract.id}/${i.id}")
                                                }
                                            }),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {

                                        Box(modifier = Modifier.fillMaxWidth())
                                        {
                                            if (i.file!!.image !== null) {
                                                AsyncImage(
                                                    model = "https://lk.mzpo-s.ru/build/images/videos/${i.file!!.image}",
                                                    contentDescription = "",
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    contentScale = ContentScale.FillWidth
                                                )
                                                IconButton(
                                                    onClick = {
                                                        val video = gson.toJson(
                                                            i,
                                                            NewMaterials::class.java
                                                        )
                                                        navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                            "video",
                                                            video
                                                        )
                                                        navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                            "contract",
                                                            contract.id.toString()
                                                        )

                                                        navHostController.navigate("video")
                                                    }, modifier = Modifier.align(
                                                        Alignment.Center
                                                    )
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = "",
                                                        modifier = Modifier
                                                            .background(Primary_Green)
                                                            .padding(10.dp)
                                                            .clip(
                                                                RoundedCornerShape(10.dp)
                                                            ),
                                                        tint = Color.White
                                                    )
                                                }

                                            } else {
                                                if (i.file!!.type == "file" || i.file!!.type == "downloadable") {
                                                    IconButton(
                                                        onClick = {
//                                                               Log.d("MyLog", "https://lk.mzpo-s.ru/mobile/study/${contract.id}/${i.id}/${token?.trim('\"')}")
//                                                               uriHandler.openUri("https://lk.mzpo-s.ru/mobile/study/${contract.id}/${i.id}/${token?.trim('\"')}")
//                                                               navHostController.navigate("material/${contract.id}/${i.id}/${token?.trim('\"')}" )
                                                            val material = gson.toJson(
                                                                i,
                                                                NewMaterials::class.java
                                                            )
                                                            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                                "MATERIAL",
                                                                material
                                                            )

                                                            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                                "CONTRACT",
                                                                contract.id
                                                            )
                                                            navHostController.navigate("pdf")
                                                        }, modifier = Modifier
                                                            .align(
                                                                Alignment.Center
                                                            )
                                                            .padding(10.dp)
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.doc),
                                                            contentDescription = "",
                                                            modifier = Modifier.fillMaxSize(),
                                                            tint = Primary_Green
                                                        )
                                                    }
                                                } else if (i.file!!.type == "test"  || i.file!!.type == "final_test") {
                                                    IconButton(
                                                        onClick = {
                                                            navHostController.navigate("test/${contract.id}/${i.id}")
                                                        }, modifier = Modifier
                                                            .align(
                                                                Alignment.Center
                                                            )
                                                            .padding(10.dp)
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.baseline_quiz_24),
                                                            contentDescription = "",
                                                            modifier = Modifier.fillMaxSize(),
                                                            tint = Primary_Green
                                                        )
                                                    }
                                                }
                                            }
                                            if (i.watched != 0 && i.percent !== null) {
                                                if (i.watched == i.file!!.size) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "",
                                                        tint = Primary_Green,
                                                        modifier = Modifier.padding(5.dp)
                                                    )

                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "",
                                                        tint = Orange,
                                                        modifier = Modifier.padding(5.dp)
                                                    )

                                                }
                                            }

                                        }
                                        Text(
                                            text = i.studyMaterial!!.name!!,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        )


                                    }


                                }
                            }
                            itemsIndexed(module.tests)
                            { _, i ->
                                if (i.activeFile !== null) {

                                    Card(
                                        modifier = Modifier
                                            .padding(vertical = 10.dp, horizontal = 20.dp)
                                            .shadow(2.dp, RoundedCornerShape(10.dp))
                                            .clickable(onClick = {
                                               if (i.activeFile!!.type == "test" || i.activeFile!!.type == "final_test") {
                                                    navHostController.navigate("test/${contract.id}/${i.id}")
                                                }
                                            }),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {

                                        Box(modifier = Modifier.fillMaxWidth())
                                        {
                                           if (i.activeFile!!.type == "test"  || i.activeFile!!.type == "final_test") {
                                                    IconButton(
                                                        onClick = {
                                                            navHostController.navigate("test/${contract.id}/${i.id}")
                                                        }, modifier = Modifier
                                                            .align(
                                                                Alignment.Center
                                                            )
                                                            .padding(10.dp)
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.baseline_quiz_24),
                                                            contentDescription = "",
                                                            modifier = Modifier.fillMaxSize(),
                                                            tint = Primary_Green
                                                        )
                                                    }
                                                }
                                        }
                                        Text(
                                            text = i.activeFile?.name!!,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        )


                                    }


                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(50.dp))
                            }
                        }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }


        }
    )
}

//Column (Modifier.fillMaxWidth()){
//
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .padding(10.dp))
//    {
//
//
//        if(i.activeFile!!.userProgress !== null)
//        {
//            if(i.activeFile!!.userProgress!!.progress == i.activeFile!!.size)
//            {
//                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "", tint = Color.Green)
//
//            } else
//            {
//                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "", tint = Color.Yellow)
//
//            }
//        }
//    }
//    Text(text = i.name!!)
//}