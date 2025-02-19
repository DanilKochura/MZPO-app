package lk.mzpo.ru.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import lk.mzpo.ru.R
import lk.mzpo.ru.models.study.ActiveFile
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.FileViewModel


@SuppressLint("UnrememberedMutableInteractionSource", "UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfGiftScreen(
    navHostController: NavHostController,
    material: ActiveFile,
    gift: Int,
    fileViewModel: FileViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val pdfState = rememberVerticalPdfReaderState(
        resource = ResourceType.Remote("https://trayektoriya.ru/"+material.upload),
        isZoomEnable = true
    )


    val progress = remember {
        mutableStateOf(0)
    }

    val pref = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token_ = pref.getString("token_lk", "")
    val loading = remember {
        mutableStateOf(false)
    }



    Scaffold(
        bottomBar = { },
        content = { padding ->
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

                    //region Header
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navHostController.navigateUp() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "",
                                tint = Color.White
                            )
                        }
                    }
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
                    ) {

//                        Log.d("MyLogЕЕЕЕЕ", pdfState.isLoaded.toString())
//                        Log.d("MyLogЕЕЕЕЕ", pdfState.loadPercent.toString())
//                        Log.d("MyLogЕЕЕЕЕ", pdfState.loadPercent.toFloat().div(100.0f).toString())

                        if (!pdfState.isLoaded) {
                            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(text = "Подождите, идет загрузка документа...")
                                Box(modifier = Modifier.fillMaxWidth(), Alignment.Center)
                                {
                                    Text(text = pdfState.loadPercent.toString()+"%")
                                    CircularProgressIndicator(
                                        progress =  pdfState.loadPercent.toFloat().div(100.0f),
                                        modifier = Modifier.padding(10.dp).size(120.dp),
                                        strokeWidth = 12.dp,
                                        color = Primary_Green,
                                        strokeCap = StrokeCap.Round,
                                        trackColor = Color.White
                                    )
                                }
                            }
                        }
                           VerticalPDFReader(
                               state = pdfState,
                               modifier = Modifier
                                   .fillMaxSize()
                                   .background(color = Color.Gray)
                           )
                    }
                }
            }
        })
}










