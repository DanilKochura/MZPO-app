package lk.mzpo.ru.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import lk.mzpo.ru.InDev
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CatalogScreen(
    navHostController: NavHostController,
    string: String,
    catalogViewModel: CatalogViewModel = viewModel()
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
        content = {padding ->
            val ctxx = LocalContext.current

            catalogViewModel.getCourses(ctxx, string)

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
                        SearchViewPreview();
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
                                ).background(color = if (catalogViewModel.selected_cat.value == item.id) Primary_Green else Color.White, RoundedCornerShape(20))
                                .border(
                                    width = 1.dp,
                                    color = if (catalogViewModel.selected_cat.value == item.id) Color.White else Color.LightGray,
                                    RoundedCornerShape(20)
                                )
                                .clickable {
                                    catalogViewModel.selected_cat.value = item.id
                                    catalogViewModel.courses_selected.value = catalogViewModel.courses.value.filter { course ->  course.category == item.id}
                                    Log.d("MyLog", catalogViewModel.courses_selected.value.size.toString())
                                    Log.d("MyLog", item.id.toString())
                                }, verticalArrangement = Arrangement.Center)
                            {
                                androidx.compose.material3.Text(text = catalogViewModel.categories.value[index].name, Modifier.padding(5.dp), color = if (catalogViewModel.selected_cat.value == item.id) Color.White else Color.Black)
                            }

                        }

                    }

                    Column(
                        Modifier
                            .fillMaxSize()
                         /*   .background(
                                color = Color.White,
                                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                            )*/
                            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                    ) {

                            LazyColumn(Modifier
                                .fillMaxSize())
                            {
                                itemsIndexed(catalogViewModel.courses_selected.value)
                                { index, item ->
                                    CourseCard(item, modifier = Modifier.fillMaxWidth().padding(10.dp).clickable {
                                        navHostController.navigate("course/"+item.id)
                                    })
                                }
                            }
                    }
                }
            }
        }
    )
}

@Preview(name = "CatalogScreen")
@Composable
private fun PreviewCatalogScreen() {
    CatalogScreen(navHostController = rememberNavController(), "")
}