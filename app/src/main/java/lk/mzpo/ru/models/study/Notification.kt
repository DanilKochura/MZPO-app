package lk.mzpo.ru.models.study

import com.google.gson.annotations.SerializedName


data class UserNotification (

    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("title"      ) var title     : String? = null,
    @SerializedName("text"       ) var text      : String? = null,
    @SerializedName("segment_id" ) var segmentId : String? = null,
    @SerializedName("status"     ) var status    : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null

)


data class Notification (

    @SerializedName("id"                ) var id               : Int?              = null,
    @SerializedName("user_id"           ) var userId           : Int?              = null,
    @SerializedName("notification_id"   ) var notificationId   : Int?              = null,
    @SerializedName("status"            ) var status           : String?           = null,
    @SerializedName("created_at"        ) var createdAt        : String?           = null,
    @SerializedName("updated_at"        ) var updatedAt        : String?           = null,
    @SerializedName("user_notification" ) var userNotification : UserNotification? = UserNotification()

)