package com.colinlvbin.extreme.anywhere;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Colin on 2016/6/9.
 */
public class RoutineOps {

    public static void MakeToast(Context context,String toastInformation){
        Toast.makeText(context,toastInformation,Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog ShowProgressDialog(Context context,String progressInformation){
        final ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle(progressInformation);
        progressDialog.setMessage("请耐心等待");
        progressDialog.onStart();
        progressDialog.show();
        return progressDialog;
    }

}
