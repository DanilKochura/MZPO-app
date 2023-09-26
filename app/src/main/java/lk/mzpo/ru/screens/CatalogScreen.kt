package lk.mzpo.ru.screens

import CoursePreview
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Course
import lk.mzpo.ru.models.CourseFilterModel
import lk.mzpo.ru.models.Prices
import lk.mzpo.ru.ui.components.DashedDivider
import lk.mzpo.ru.ui.components.PriceTextField
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.Blue_BG
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable

fun CatalogScreen(
    navHostController: NavHostController,
    string: String,
    catalogViewModel: CatalogViewModel = viewModel()
) {
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val contextForToast = LocalContext.current.applicationContext


        val coroutineScope = rememberCoroutineScope()
        Scaffold(
        floatingActionButton = {
           if(bottomSheetState.targetValue != ModalBottomSheetValue.Expanded)
           {
               Button(onClick = {
                   coroutineScope.launch {
                       bottomSheetState.show()
                   }
               }, colors = ButtonDefaults.buttonColors(backgroundColor = Primary_Green, contentColor = Color.White), modifier = Modifier.size(60.dp), shape = RoundedCornerShape(10.dp)) {
                   Icon(painter = painterResource(id = R.drawable.filter_svgrepo_com), contentDescription = "", tint = Color.White,  )
               }
           }
        },
            floatingActionButtonPosition = FabPosition.End,
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
            val ctxx = LocalContext.current
            if(catalogViewModel.courses_selected.value.isEmpty())
            {
                if(string.take(1) == "_")
                {
                    catalogViewModel.searchCourses(ctxx, string)

                } else
                {

                    catalogViewModel.getCourses(ctxx, string)
                }
            }

            Box(
                Modifier
                    .background(color = Primary_Green)
                    .fillMaxSize()) {
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
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                    ) {
                        SearchViewPreview(navHostController);
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "bell",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    LazyRow (
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp)
                    ){

                        itemsIndexed(catalogViewModel.categories.value)
                        {
                                index, item ->
                            Column(modifier = Modifier
                                .height(30.dp)
                                .padding(horizontal = 5.dp)
                                .clip(
                                    RoundedCornerShape(20)
                                )
                                .background(
                                    color = if (catalogViewModel.selected_cat.value == item.id) Primary_Green else Color.White,
                                    RoundedCornerShape(20)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (catalogViewModel.selected_cat.value == item.id) Color.White else Color.LightGray,
                                    RoundedCornerShape(20)
                                )
                                .clickable {
                                    catalogViewModel.selected_cat.value = item.id
                                    catalogViewModel.courses_selected.value =
                                        catalogViewModel.courses.value.filter { course -> course.category == item.id }
//                                    Log.d(
//                                        "MyLog",
//                                        catalogViewModel.courses_selected.value.size.toString()
//                                    )
//                                    Log.d("MyLog", item.id.toString())
                                }, verticalArrangement = Arrangement.Center)
                            {
                                androidx.compose.material3.Text(text = catalogViewModel.categories.value[index].name, Modifier.padding(5.dp), color = if (catalogViewModel.selected_cat.value == item.id) Color.White else Color.Black)
                            }

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
                    ) {
                            if (catalogViewModel.courses.value.isEmpty())
                            {
                                    Text(text = "Ничего не найдено", fontSize = 22.sp, modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxSize(), textAlign = TextAlign.Center)
                            }
                            if(catalogViewModel.courses_selected.value.isEmpty())
                            {
                                Text(text = "Ничего не найдено(((")
                            }
                        Text(text = catalogViewModel.h1.value, fontSize = 20.sp, modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(), textAlign = TextAlign.Center)
                        Column (modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()) ) {
                            for (i in 0 until catalogViewModel.courses_selected.value.size step 2)
                            {
                                Row (
                                    Modifier
                                        .fillMaxSize()
                                        .padding(5.dp)){
                                    CourseCardDual(catalogViewModel.courses_selected.value[i], modifier = Modifier
                                        .fillMaxSize(0.5f)
                                        .weight(1f)
                                        .padding(horizontal = 5.dp)
                                        .clickable {
                                            navHostController.navigate("course/" + catalogViewModel.courses_selected.value[i].id)
                                        })
                                    if (i+1 < catalogViewModel.courses_selected.value.size)
                                    {
                                        CourseCardDual(catalogViewModel.courses_selected.value[i+1], modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 5.dp)
                                            .clickable {
                                                navHostController.navigate("course/" + catalogViewModel.courses_selected.value[i + 1].id)
                                            })
                                    }
                                }
                            }
                        }
//                        LazyColumn(Modifier
//                                .fillMaxSize())
//                            {
//                                itemsIndexed(catalogViewModel.courses_selected.value)
//                                { index, item ->
//                                    CourseCard(item, modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(10.dp)
//                                        .clickable {
//                                            navHostController.navigate("course/" + item.id)
//                                        })
//                                }
//                            }

                    }
                }
            }

            ModalBottomSheetLayout(
                sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                sheetContent = {
                    val from = remember {
                        mutableStateOf("")
                    }
                    val to = remember {
                        mutableStateOf("")
                    }
                    val sale = remember {
                        mutableStateOf(true)
                    }
                    val dist = remember {
                        mutableStateOf(true)
                    }
                    Column (modifier = Modifier.padding(10.dp)){
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                            Icon(painter = painterResource(id = R.drawable.filter_svgrepo_com), contentDescription = "",
                                Modifier
                                    .size(30.dp)
                                    .padding(end = 7.dp))
                            Text(text = "Отфильтровать курсы", fontSize = 20.sp)
                        }
                        Divider(modifier = Modifier.padding(10.dp), thickness = 2.dp)

                        Text(text = "Цена", fontSize = 18.sp)
                        Row (horizontalArrangement = Arrangement.SpaceBetween) {
                            PriceTextField(price = from, placeholder = "2000", label = "Цена от", modifier = Modifier
                                .padding(end = 5.dp)
                                .weight(1f))
                            PriceTextField(price = to, placeholder = "2000", label = "Цена до", modifier = Modifier
                                .padding(start = 5.dp)
                                .weight(1f))

                        }
                        Divider(modifier = Modifier.padding(5.dp))
                        Text(text = "Форма обучения")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = sale.value,
                                onCheckedChange = { checked_ ->
                                    sale.value = checked_
                                } ,colors = CheckboxDefaults.colors(checkedColor = Primary_Green),
                                modifier = Modifier.scale(1.3f)
                            )
                            Text(
                                modifier = Modifier.padding(start = 2.dp),
                                text = "Очно",
                                fontSize = 22.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = dist.value,
                                onCheckedChange = { checked_ ->
                                    dist.value = checked_
                                },
                                 colors = CheckboxDefaults.colors(checkedColor = Primary_Green),
                                modifier = Modifier.scale(1.3f)

                            )

                            Text(
                                modifier = Modifier.padding(start = 2.dp),
                                text = "Дистанционно",
                                fontSize = 22.sp
                            )
                        }
                        Button(onClick = {
                            val fromCost = if (from.value == "") 0 else from.value.toInt()
                            val toCost = if (to.value == "") 100000 else to.value.toInt()
                            catalogViewModel.courses_selected.value = CourseFilterModel(fromCost, toCost, dist.value, sale.value).filter(catalogViewModel.courses.value);
                            coroutineScope.launch { bottomSheetState.hide() }

                                         }, colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red, contentColor = Color.White), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                            Text(text = "Показать")
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

@Preview(name = "CatalogScreen")
@Composable
private fun PreviewCatalogScreen() {
    CatalogScreen(navHostController = rememberNavController(), "")
}

@Composable
fun CourseCardDual(course: CoursePreview, modifier: Modifier = Modifier)
{
    Card(modifier = modifier
        .shadow(2.dp, RoundedCornerShape(10.dp))
        , colors = CardDefaults.cardColors(
            containerColor = Color.White
        ))
    {
        AsyncImage(model = course.image, contentDescription = course.id.toString(), modifier = Modifier
            .height(100.dp)
            .fillMaxWidth(), contentScale = ContentScale.Crop)
        Column(modifier = Modifier
            .padding(7.dp)
            .fillMaxWidth()) {
            Text(
                course.name,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                color = Primary_Green,
                modifier = Modifier.height((14*4/3*3).dp),
                fontSize = 14.sp


            )

//            Row() {
//
//                androidx.compose.material3.Text(
//                    text = "Длительность: ",
//                    fontWeight = FontWeight.Bold
//                )
//                androidx.compose.material3.Text(text = "1,5 - 2 мес.")
//            }

//            Button(onClick = { /*TODO*/ }, modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 5.dp), shape = RoundedCornerShape(30), colors = ButtonDefaults.buttonColors(backgroundColor = Aggressive_red, contentColor = Color.White)) {
//                androidx.compose.material3.Text("Купить со скидкой", color = Color.White)
//            }
        }
        Column (Modifier.padding(7.dp)) {
            DashedDivider(thickness = 1.dp, modifier = Modifier.fillMaxWidth(), color = Color.LightGray, phase = 2f)

            Row(verticalAlignment = Alignment.CenterVertically, modifier= Modifier.padding(end = 10.dp, top = 5.dp)) {
                Icon(painter = painterResource(id = R.drawable.time_svgrepo_com), modifier = Modifier
                    .height(14.dp)
                    .padding(end = 2.dp),contentDescription = "hours", tint = Color.Gray)
                androidx.compose.material3.Text(
                    text = course.hours.toString() + " ак.ч.",
                    color = Color.Gray, fontSize = 14.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, ) {
                Icon(painter = painterResource(id = R.drawable.diploma_svgrepo_com), modifier = Modifier
                    .height(14.dp)
                    .padding(end = 2.dp),contentDescription = "doc", tint = Color.Gray)
                androidx.compose.material3.Text(text = course.doctype, color = Color.Gray, fontSize = 14.sp)
            }
        }
            if (course.prices.dist != 0 && course.prices.dist != null)
            {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF586f81))) {
                    Box(modifier = Modifier.padding(7.dp))
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text(
                                text = "Дистанционно : ",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                            androidx.compose.material3.Text(
                                text = course.prices.dist.toString() + " руб.",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }

                }
            } else
            {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Primary_Green)) {
                    Box(modifier = Modifier.padding(7.dp))
                    {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            androidx.compose.material3.Text(text = "Очно: ", fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp)
                            androidx.compose.material3.Text(
                                text = "от " + course.prices.sale15.toString() + " руб.",
                                color = Color.White, fontSize = 16.sp
                            )

                        }
                    }


            }
        }

    }
}