package lk.mzpo.ru.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import lk.mzpo.ru.models.study.NewMaterials
import lk.mzpo.ru.viewModel.FileViewModel


@SuppressLint("UnrememberedMutableInteractionSource", "UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreenNew(
    navHostController: NavHostController,
    material: NewMaterials,
    contract: Int,
    fileViewModel: FileViewModel = viewModel(),
) {
    throw Exception("AAAA")
    val ctx = LocalContext.current



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
                            val uri = Uri.parse("https://trayektoriya.ru/"+material.file?.upload)


}










