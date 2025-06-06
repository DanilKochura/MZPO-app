package lk.mzpo.ru.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import lk.mzpo.ru.R
import lk.mzpo.ru.models.BottomItem
import lk.mzpo.ru.models.BottomNavigationMenu
import lk.mzpo.ru.models.Category
import lk.mzpo.ru.ui.components.SearchViewPreview
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.ui.theme.Primary_Green_BG
import lk.mzpo.ru.ui.theme.Second_Green
import lk.mzpo.ru.ui.theme.Third_green
import lk.mzpo.ru.viewModel.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    navHostController: NavHostController,
    categoriesViewModel: CategoriesViewModel = viewModel(),
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
            val ctx = LocalContext.current
            categoriesViewModel.getCategories(ctx)
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
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 15.dp)
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
                    //endregion

                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded)
                            )
                            .clip(RoundedCornerShape(topStart = MainRounded, topEnd = MainRounded))
                            ) {

                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp))
                        {
                            itemsIndexed(categoriesViewModel.categories.value)
                            { index, item ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = item.name, fontSize = 22.sp, color = Primary_Green, modifier = Modifier.padding(5.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                                        navHostController.navigate(BottomItem.Cats.route + "?name=" + item.url)
                                    }) {
                                        androidx.compose.material3.Text(text = "все    ", fontSize = 12.sp, color = Aggressive_red)
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "next",
                                            tint = Aggressive_red
                                        )

                                    }
                                }
                                if (item.child !== null && item.child.isNotEmpty()) {
                                    val size = item.child.size

                                    for (i in 0 until size step 2) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        )
                                        {
                                            CatCardAlternate(
                                                category = item.child[i],
                                                navHostController = navHostController,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if(item.child.size > i+1)
                                            {
                                                CatCardAlternate(
                                                    category = item.child[i + 1],
                                                    navHostController = navHostController,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
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





@Composable
fun CatCard(category: Category, navHostController: NavHostController, modifier: Modifier = Modifier)
{

    var painter_res = R.drawable.mas1
    var color_bg = Second_Green
    if(category.parent == 7)
    {
        color_bg = Primary_Green_BG
        painter_res = R.drawable.kos
    } else if(category.parent == 11)
    {
        color_bg = Third_green
        painter_res = R.drawable.med
    }
    Surface (modifier = modifier
        .height(100.dp)
        .padding(horizontal = 5.dp)
        .shadow(1.dp, RoundedCornerShape(10.dp))
        .clickable {
            navHostController.navigate("catalog?name=" + category.url)
        }) {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = color_bg, shape = RoundedCornerShape(10.dp)))
        {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxSize()
            ) {
                androidx.compose.material3.Text(
                    text = category.name,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight(600)),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,color = Color.White
                )
//                Text(text = "")

                Row (/*horizontalArrangement = Arrangement.Center, */modifier = Modifier.fillMaxWidth()){
                    androidx.compose.material3.Text(text = category.amount.toString(), color = Color.White, style = TextStyle(fontSize = 12.sp))
                    androidx.compose.material3.Text(text = " курсов ", style = TextStyle(fontSize = 12.sp),color = Color.White)

                }

            }
            Icon(
                painter = painterResource(id = painter_res), contentDescription = "",
                Modifier
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
                    .alpha(0.8f)
                    .padding(2.dp), tint = Color.White
            )
        }
    }
}

@Composable
fun CatCardAlternate(category: Category, navHostController: NavHostController, modifier: Modifier = Modifier)
{
    var painter_res = R.drawable.mas1
    var color_bg = Second_Green
    if(category.parent == 7)
    {
        color_bg = Primary_Green_BG
        painter_res = R.drawable.kos
    } else if(category.parent == 11)
    {
        color_bg = Third_green
        painter_res = R.drawable.med
    }
    Surface (modifier = modifier
        .padding(horizontal = 5.dp)
        .shadow(5.dp, RoundedCornerShape(10.dp))
        .clickable {
            navHostController.navigate("catalog?name=" + category.url)
        }) {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = color_bg, shape = RoundedCornerShape(10.dp)).height(150.dp))
        {
            AsyncImage(model = category.image, contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Column(
//                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
                    .fillMaxSize()
            ) {
                androidx.compose.material3.Text(
                    text = category.name,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight(600)),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,color = Color.White, modifier = Modifier.fillMaxSize(0.6f)
                )
//                Text(text = "")

//                Row (/*horizontalArrangement = Arrangement.Center, */modifier = Modifier.fillMaxWidth().padding(top = 20.dp)){
//                    androidx.compose.material3.Text(text = category.amount.toString(), color = Color.White, style = TextStyle(fontSize = 12.sp))
//                    androidx.compose.material3.Text(text = " курсов ", style = TextStyle(fontSize = 12.sp),color = Color.White)
//
//                }
            }

        }
    }
}

@Composable
fun CatCardMainScreen(category: Category, navHostController: NavHostController, modifier: Modifier = Modifier)
{
    var painter_res = R.drawable.mas1
    var color_bg = Second_Green
    if(category.parent == 7)
    {
        color_bg = Primary_Green_BG
        painter_res = R.drawable.kos
    } else if(category.parent == 11)
    {
        color_bg = Third_green
        painter_res = R.drawable.med
    }
    Surface (modifier = modifier
        .shadow(1.dp, RoundedCornerShape(10.dp))
        .border(1.dp, Primary_Green, RoundedCornerShape(10.dp))
        .clickable {
            navHostController.navigate("catalog?name=" + category.url)
        }) {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = color_bg, shape = RoundedCornerShape(10.dp)).height(150.dp))
        {
            AsyncImage(model = category.image, contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Column(
//                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
                    .fillMaxSize()
            ) {
                androidx.compose.material3.Text(
                    text = category.name,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight(600)),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,color = Color.Black, modifier = Modifier.fillMaxSize(0.6f)
                )
//                Text(text = "")

//                Row (/*horizontalArrangement = Arrangement.Center, */modifier = Modifier.fillMaxWidth().padding(top = 20.dp)){
//                    androidx.compose.material3.Text(text = category.amount.toString(), color = Color.White, style = TextStyle(fontSize = 12.sp))
//                    androidx.compose.material3.Text(text = " курсов ", style = TextStyle(fontSize = 12.sp),color = Color.White)
//
//                }
            }

        }
    }
}

@Preview
@Composable
fun CardPreview()
{
    CatCard(category = Category(1, "Масса", "",6), navHostController = rememberNavController())
}