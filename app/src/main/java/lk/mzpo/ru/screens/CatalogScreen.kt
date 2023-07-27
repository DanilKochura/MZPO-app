package lk.mzpo.ru.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import lk.mzpo.ru.InDev
import lk.mzpo.ru.models.BottomNavigationMenu
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
            Column(modifier = Modifier.padding(padding)) {
                LazyColumn()
                {
                    itemsIndexed(catalogViewModel.courses.value)
                    {
                        index, item ->
                        CourseCard(item)
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