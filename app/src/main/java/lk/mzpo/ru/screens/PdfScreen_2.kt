package lk.mzpo.ru.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import lk.mzpo.ru.R
import lk.mzpo.ru.models.study.NewMaterials
import lk.mzpo.ru.network.retrofit.SaveLastPageService
import lk.mzpo.ru.ui.theme.Aggressive_red
import lk.mzpo.ru.ui.theme.MainRounded
import lk.mzpo.ru.ui.theme.Orange
import lk.mzpo.ru.ui.theme.Primary_Green
import lk.mzpo.ru.viewModel.FileViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import kotlin.math.roundToInt


@SuppressLint("UnrememberedMutableInteractionSource", "UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreen_2(
    navHostController: NavHostController,
    material: NewMaterials,
    contract: Int,
    fileViewModel: FileViewModel = viewModel(),
) {
    val ctx = LocalContext.current
    val pdfState = rememberVerticalPdfReaderState(
        resource = ResourceType.Remote("https://trayektoriya.ru/"+material.file?.upload),
        isZoomEnable = true,
    )


    val progress = remember {
        mutableStateOf(0)
    }
    if (material.watched != 0 && material.percent !== null) {
        progress.value = material.watched
    }
    val pref = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)
    val token_ = pref.getString("token_lk", "")
    val loading = remember {
        mutableStateOf(false)
    }

    Log.d("MyLogProgress", material.watched.toString())

    LaunchedEffect(key1 = pdfState.currentPage) {

        if ( pdfState.pdfPageCount - pdfState.currentPage < 5)
        {
            progress.value = pdfState.pdfPageCount
        }
        if ((pdfState.currentPage > progress.value && pdfState.currentPage - progress.value < 3) || progress.value == pdfState.pdfPageCount)
        {

            SaveLastPageService().send(
                "Bearer " + token_?.trim('"'),
                SaveLastPageService.PostBody(
                    progress = material.id,
                    page =  progress.value
                )

            ).enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("API Request", "I got an error and i don't know why :(")
                    Log.e("API Request", t.message.toString())
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("API Request", response.body().toString())
                    Log.d("API Request", response.message())
                    Log.d("API Request", response.errorBody().toString())
                    Log.d("API Request", response.raw().body.toString())
                    if (response.isSuccessful) {
                        progress.value = pdfState.currentPage
                    }
                }
            })
            if (progress.value == pdfState.pdfPageCount && pdfState.pdfPageCount - pdfState.currentPage < 5 && pdfState.isLoaded)
            {
                Toast.makeText(ctx, "Вы просмотрели документ полностью!", Toast.LENGTH_SHORT).show()
            }
        }

    }
    Scaffold(
        floatingActionButton = {

            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, end = 5.dp), contentAlignment = Alignment.TopEnd) {
                if (pdfState.isLoaded) {
                    var progress_reading = pdfState.currentPage.toFloat().div(pdfState.pdfPageCount).times(100).roundToInt()
                    var filling = pdfState.currentPage.toFloat().div(pdfState.pdfPageCount)
                    if (pdfState.pdfPageCount - pdfState.currentPage < 5)
                    {
                        progress_reading = 100
                        filling= 1f
                    }
                    Row(
                        modifier = Modifier
                            .width(150.dp)
                            .height(20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        LinearProgressIndicator(
                            progress = filling,
                            Modifier
                                .fillMaxSize()
                                .clip(
                                    RoundedCornerShape(50)
                                ), color = if(progress_reading < 30) Aggressive_red else if (progress_reading < 75) Orange else Primary_Green, backgroundColor = Color.White
                        ) //70% progress

                    }
                    Text(
                        text = "$progress_reading%",
                        color = if(progress_reading > 90) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 7.dp),
                        textAlign = TextAlign.Left
                    )
                }
            }
        },
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










