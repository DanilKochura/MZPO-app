package lk.mzpo.ru.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import lk.mzpo.ru.models.PracticeData
import lk.mzpo.ru.models.PracticeOchno
import lk.mzpo.ru.models.study.Admission
import lk.mzpo.ru.models.study.CourseSchedule
import lk.mzpo.ru.models.study.Exam
import lk.mzpo.ru.models.study.ExamNew
import lk.mzpo.ru.models.study.PassedModules
import lk.mzpo.ru.models.study.StudyModule
import lk.mzpo.ru.models.study.UserDocument
import lk.mzpo.ru.network.retrofit.AuthStatus

class StudyModuleViewModel: ViewModel() {


    val loaded = mutableStateOf(false)
    val error = mutableStateOf(false)

    val module = mutableStateOf<StudyModule?>(null)
    val answered = mutableStateOf(false)

    val auth_tested = mutableStateOf(AuthStatus.TEST)

    val studyModules = mutableStateOf(listOf<StudyModule>())

    val admissions = mutableListOf<Admission>()
    val schedules = mutableListOf<CourseSchedule>()
    val practiceData = mutableListOf<PracticeData>()
    val passedModules = mutableListOf<PassedModules>()

    val docsBitMaps = mutableMapOf<Int, Bitmap>()

    val practiceOcno = mutableListOf<PracticeOchno>()

    val documents = mutableListOf<UserDocument>()

    val selected = mutableStateOf("Расписание")

    val exam = mutableListOf<Exam>()
    val examNew = mutableListOf<ExamNew>()

    val verify_docs = mutableStateOf(false)
    fun getData(context: Context, contract_id: Int, moduleId: Int)
    {
        val url = "https://trayektoriya.ru/mobile/v2/user/contract/${contract_id}/module/${moduleId}"
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    val gson = Gson();

                   try {
                       this.module.value = gson.fromJson(response, StudyModule::class.java)
                       loaded.value = true
                   } catch (e: Exception)
                   {
                       loaded.value = true
                       error.value = true
                       Log.e("MYLOG", e.message.toString())
                       return@Listener;
                   }


                },
                Response.ErrorListener {
                    err: VolleyError? ->
                        loaded.value = true
                        if(err?.networkResponse !== null)
                        {
                            Log.e("AAAAA", err.networkResponse.statusCode.toString())
                            if(err.networkResponse.statusCode == 500)
                            {
                                error.value = true
                            }
                        }
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer "+token?.trim('"')
                    return headers
                }

            }
        queue.add(stringReq)
    }






}