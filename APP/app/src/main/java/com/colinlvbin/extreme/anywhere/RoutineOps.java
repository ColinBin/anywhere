package com.colinlvbin.extreme.anywhere;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.FileNotFoundException;

/**
 * Created by Colin on 2016/6/9.
 */
public class RoutineOps {

    public static void MakeToast(Context context,String toastInformation){
        Toast.makeText(context,toastInformation,Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog ShowProgressDialog(Context context,String progressInformation) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(progressInformation);
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
        progressDialog.onStart();
        progressDialog.show();
        return progressDialog;
    }

    public static Bitmap GetBitmapFromUri(Context context,Uri uri){
        Bitmap bitmap=null;
        try{
            bitmap= BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
