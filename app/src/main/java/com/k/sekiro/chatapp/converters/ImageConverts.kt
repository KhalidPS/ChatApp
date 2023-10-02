package com.k.sekiro.chatapp.converters

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream

class ImageConverts {

    companion object{
        fun convertUriToBitmap(uri: Uri,contentResolver:ContentResolver): Bitmap {
            var bitmap: Bitmap? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                val imageDecoder = ImageDecoder.createSource(contentResolver,uri)
                bitmap = ImageDecoder.decodeBitmap(imageDecoder)



            }
            return bitmap as Bitmap
        }


        fun convertBitmapToBase64(uri: Uri,contentResolver: ContentResolver):String{
            val stream = ByteArrayOutputStream()
            val bitmap = convertUriToBitmap(uri,contentResolver)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)

            val byte = stream.toByteArray()
            val base64 =  Base64.encodeToString(byte, Base64.DEFAULT)


            return base64
        }

        fun convertBase64StringToBitmap(img:String): Bitmap {
            val byte = Base64.decode(img, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size)

            return bitmap

        }
    }
}