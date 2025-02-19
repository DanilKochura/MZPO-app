package lk.mzpo.ru.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Gift
import lk.mzpo.ru.models.study.ActiveFile
import lk.mzpo.ru.ui.components.LoadableScreen
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.GiftViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun GiftScreen(
    gift: Gift,
    navHostController: NavHostController,
    giftViewModel: GiftViewModel = viewModel(),
    cart_sum: MutableState<Int> = mutableStateOf(0)

) {
    val context = LocalContext.current
    val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token = test.getString("token_lk", "")

    giftViewModel.gift = gift
    giftViewModel.getData(context)

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

        bottomBar = { BottomNavigationMenu(navController = navHostController, cart = cart_sum) },
        content = { padding ->
            val gson = Gson()
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
                                text = gift.name!!,
                                modifier = Modifier.weight(2f),
                                fontSize = 20.sp,
                                maxLines = 3,
                                fontWeight = FontWeight.Bold
                            )
                            AsyncImage(
                                model = gift.image,
                                contentDescription = "",
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(
                                        RoundedCornerShape(10.dp)
                                    )
                            )
                        }

                        LoadableScreen(loaded = giftViewModel.loaded, error = giftViewModel.error)
                        {
                            LazyColumn(content = {
                                itemsIndexed(giftViewModel.materials.value)
                                { _, i ->

                                        Card(
                                            modifier = Modifier
                                                .padding(vertical = 10.dp, horizontal = 20.dp)
                                                .shadow(2.dp, RoundedCornerShape(10.dp))
                                                .clickable(onClick = {
                                                    if (i.image !== null) {
                                                        val video = gson.toJson(
                                                            i,
                                                            ActiveFile::class.java
                                                        )
                                                        navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                            "video",
                                                            video
                                                        )
                                                        navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                            "gift",
                                                            gift.id.toString()
                                                        )

                                                        navHostController.navigate("gift/video")
                                                    } else if (i.type == "file") {
                                                        val material = gson.toJson(
                                                            i,
                                                            ActiveFile::class.java
                                                        )
                                                        navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                            "MATERIAL",
                                                            material
                                                        )

                                                        navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                            "gift",
                                                            gift.id
                                                        )
                                                        navHostController.navigate("gift/pdf")
                                                    }
                                                }),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.White
                                            )
                                        ) {

                                            Box(modifier = Modifier.fillMaxWidth())
                                            {
                                                if (i.image !== null) {
                                                    AsyncImage(
                                                        model = "https://trayektoriya.ru/build/images/videos/${i.image}",
                                                        contentDescription = "",
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
                                                        contentScale = ContentScale.FillWidth
                                                    )
                                                    IconButton(
                                                        onClick = {
                                                            val video = gson.toJson(
                                                                i,
                                                                ActiveFile::class.java
                                                            )
                                                            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                                "video",
                                                                video
                                                            )
                                                            navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                                "gift",
                                                                gift.id.toString()
                                                            )

                                                            navHostController.navigate("gift/video")
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
                                                    if (i.type == "file") {
                                                        IconButton(
                                                            onClick = {
//                                                               Log.d("MyLog", "https://trayektoriya.ru/mobile/study/${contract.id}/${i.id}/${token?.trim('\"')}")
//                                                               uriHandler.openUri("https://trayektoriya.ru/mobile/study/${contract.id}/${i.id}/${token?.trim('\"')}")
//                                                               navHostController.navigate("material/${contract.id}/${i.id}/${token?.trim('\"')}" )
                                                                val material = gson.toJson(
                                                                    i,
                                                                    ActiveFile::class.java
                                                                )
                                                                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                                    "MATERIAL",
                                                                    material
                                                                )

                                                                navHostController.currentBackStackEntry?.savedStateHandle?.set(
                                                                    "gift",
                                                                    gift.id
                                                                )
                                                                navHostController.navigate("gift/pdf")
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
                                                    }
                                                }
                                            }
                                            Text(
                                                text = i.name!!,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp)
                                            )


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


        })
}


