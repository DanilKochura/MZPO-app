package lk.mzpo.ru.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import lk.mzpo.ru.InDev
import lk.mzpo.ru.models.BottomItem
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navHostController: NavHostController,
    categoriesViewModel: CategoriesViewModel = viewModel()
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
            val ctx = LocalContext.current
            categoriesViewModel.getStories(ctx)
            Column(modifier = Modifier.padding(padding)) {
                LazyColumn()
                {
                    itemsIndexed(categoriesViewModel.stories.value)
                    {
                        index, item ->
                        Text(text = item.name)
                        if(item.child !== null && item.child.isNotEmpty())
                        {
                            val size = item.child.size
                            LazyRow()
                            {
                                itemsIndexed(item.child)
                                {
                                        index1, item1 ->
                                    Box(modifier = Modifier.width(100.dp).height(100.dp).border(1.dp, Primary_Green).padding(10.dp).clickable {
                                        navHostController.navigate(BottomItem.Cats.route + "?name=" + item1.url)
                                    }) {
                                        Text(text = item1.name.toString())

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
