package lk.mzpo.ru.models

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("organization_id"   ) var organizationId   : Int?    = null,
    @SerializedName("phone"             ) var phone            : String? = null,
    @SerializedName("address"           ) var address          : String? = null,
    @SerializedName("birthday"          ) var birthday         : String? = null,
    @SerializedName("sex"               ) var sex              : String? = null,
    @SerializedName("snils"             ) var snils            : String? = null,
    @SerializedName("pass_series"       ) var passSeries       : String? = null,
    @SerializedName("pass_number"       ) var passNumber       : String? = null,
    @SerializedName("pass_doi"          ) var passDoi          : String? = null,
    @SerializedName("pass_poi"          ) var passPoi          : String? = null,
    @SerializedName("pass_dpt"          ) var passDpt          : String? = null,
    @SerializedName("pass_registration" ) var passRegistration : String? = null,
    @SerializedName("avatar"            ) var avatar           : String? = null,
    @SerializedName("passport"          ) var passport         : String? = null,
    @SerializedName("snils_doc"         ) var snilsDoc         : String? = null,
    @SerializedName("edu_doc"           ) var eduDoc           : String? = null,
    @SerializedName("ordinatura"        ) var ordinatura       : String? = null,
    @SerializedName("name_change"       ) var nameChange       : String? = null,
    @SerializedName("certificate"       ) var certificate      : String? = null,
    @SerializedName("training"          ) var training         : String? = null,
    @SerializedName("work_book"         ) var workBook         : String? = null,
    @SerializedName("accreditation"     ) var accreditation    : String? = null,
    @SerializedName("nmo"               ) var nmo              : String? = null,
    @SerializedName("registration_card" ) var registrationCard : String? = null,
    @SerializedName("id_1c"             ) var id1c             : String? = null,
    @SerializedName("amo_id"            ) var amoId            : String? = null,
    @SerializedName("job_access"        ) var jobAccess        : Int? = null
)
