package com.k.sekiro.chatapp.notificationApi


import com.k.sekiro.chatapp.data.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.FileInputStream
import java.io.IOException
import java.util.Arrays


interface NotificationApi {

    companion object{
      private const val endpointNotification = "chat-58209/messages:send"


     // private const val authorization = "419176296476-tr1mv9ctcb5ncgp66ve6di6h9n032e3e.apps.googleusercontent.com"
     private const val authorization = "ya29.c.b0Aaekm1JOlui8X8TPuPYs94iA7CTsm1BFdH1dqAOANZ6VLyYnpW2wJn9QQdCbGSEHUEqG2GVL3weiiQw7oSnV7Zkg5BgptEaSydo-FZrIakBT60kmESQZmzJVTM1wnBLEOyhLvxE7TjkvRYAtVi_L_F5MU2o91ZJd8hBhcE9xaxh8XVnLIWwXH7h8-Cp1ZklQOwPaI38cbMx5Fuejty2w1pCPlUd85q4xgQQAlGhhD0u7XjbK54xC_3_LwR6ik_Svd42J3-5LaLDkKfBkMZrzerOLkKqKY_CaERfNa2BF1yLm6aZzZZDdPS_qR-kywE22a7DOvEj-DyBQL341Pj4Sr7h9dgXVwiMW9U3-is1_RrmX3OqxmWuoJgdRgFx6uRgXgXBsBv5Rm63jna3VptQallj66myV4I4Ml4h9vbJheSRmwq4maoj7MMoubVf82ekIR8MdyiuXzbavJj7fQOgWgmRV--WhOJZwi0aacnzw5r_8m5_g6vOWQ8ra8zbw9BROgItR4g5Qswepf3c-ecQ_2M2oxU8mvk715OyF7wzFelaz5ul2xqQ9exZiS4tr0xrpr1Sb4bS3IyUYyuYWsn6cernjmB8jM6csmlv9bSUO_-R0yfgyv47hvW-4Umui10e1R6B-gJX-kXj-v0ZWwg-rs7kyRRI59zp7xfR9YIMqV4bwI2d5Wl9sck7SV1vZlai2Rya4csWSehjBmdv_tl1YW9o--JMz1FsszmQuQJe93qpy_R37nZezJ2u42In47diFu3Wq5nzgiuYFkM3Jrekhk0YJMi6J3_ZgROiOtxz4layOavSWV6nwg_kvo4xlmwQZ0sgm5OpwXYkuv0ylFMBbUFSSI-i-WmgktkUI6ywdeBIilr2t6oJxjyWvRmr_OmUX83MhSynxdQ_m9oU0mF4ndzljnuyhJMkdFIhUi5n1Uf0vZbQ6OpRS9iuiUFZu58WR6tZS82F6wZ0BaZxlJwjepiI6X2lcMlR7XaQ4ra6Vi4Wo8tk8zm3l5Q3"
      private const val contentType = "application/json"
    }
        @Headers("Authorization: Bearer $authorization","Content-Type: $contentType")
        @POST(endpointNotification)
        suspend fun postNotification(@Body pushNotification: PushNotification):Response<ResponseBody>




}


