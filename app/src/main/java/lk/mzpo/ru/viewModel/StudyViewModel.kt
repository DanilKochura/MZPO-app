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
import lk.mzpo.ru.models.Contract
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
import org.json.JSONObject

class StudyViewModel: ViewModel() {

    lateinit var contract: Contract
    val loaded = mutableStateOf(false)
    val error = mutableStateOf(false)

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
    fun getData(context: Context)
    {
        Log.d("MyLog", "Here")
        val url = "https://lk.mzpo-s.ru/mobile/user/contract/${contract.id}"
        val test = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val token = test.getString("token_lk", "")
        val queue = Volley.newRequestQueue(context)
        val stringReq: StringRequest =
            object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    val gson = Gson();


                   try {
                       val main = JSONObject(response)
                       val json = main.getJSONArray("studyModules")
                       val passedJson = main.getJSONArray("passed")
                       val admJson = main.getJSONArray("adm")
                       val docsJson = main.getJSONArray("documents")
                       val schedJson = main.getJSONArray("sched")
                       val examJson = main.getJSONArray("exam")
                       val examsJson = main.getJSONArray("exams")
                       if(examJson.length() > 0)
                       {
                           exam.add(gson.fromJson(examJson[0].toString(), Exam::class.java))
                       }
                       if(examsJson.length() > 0)
                       {
                           examNew.add(gson.fromJson(examsJson[0].toString(), ExamNew::class.java))
                       }
//                    Log.d("MyLog", main.getJSONObject("practiceData").toString())
                       verify_docs.value = main.getBoolean("verify_docs")
                       try {
                           val pr = main.getJSONArray("practiceData");
                           for (i in 0 until pr.length())
                           {

                               practiceData.add(gson.fromJson(pr[i].toString(), PracticeData::class.java))
                           }
                       } catch (e: Exception)
                       {
                           Log.d("MyLog", e.toString())
                       }
                       try {
                           val pr = main.getJSONObject("practiceOchno");
                           practiceOcno.add(gson.fromJson(main.getJSONObject("practiceOchno").toString(), PracticeOchno::class.java))
                       } catch (e: Exception)
                       {
                           Log.d("MyLog", e.toString())

                       }
                       val array = arrayListOf<StudyModule>()
                       for (i in 0 until  json.length())
                       {
                           val string = json[i].toString()
                           array.add(gson.fromJson(string, StudyModule::class.java))
                       }
                       admissions.clear()
                       for (i in 0 until  admJson.length())
                       {
                           val string = admJson[i].toString()
                           admissions.add(gson.fromJson(string, Admission::class.java))
                       }
                       schedules.clear()
                       for (i in 0 until  schedJson.length())
                       {
                           val string = schedJson[i].toString()
                           schedules.add(gson.fromJson(string, CourseSchedule::class.java))
                       }
                       passedModules.clear()
                       for (i in 0 until  passedJson.length())
                       {
                           val string = passedJson[i].toString()
                           passedModules.add(gson.fromJson(string, PassedModules::class.java))
                       }
                       for (i in 0 until  docsJson.length())
                       {
                           val string = docsJson[i].toString()
                           documents.add(gson.fromJson(string, UserDocument::class.java))
                       }
                       this.studyModules.value = array
                       loaded.value = true
                   } catch (_: Exception)
                   {
                       loaded.value = true
                       error.value = true
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