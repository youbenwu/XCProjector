package com.outmao.xcprojector.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.outmao.xcprojector.MainActivity;

import androidx.core.content.ContextCompat;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent==null)return;
        if(TextUtils.equals(intent.getAction(),"android.intent.action.BOOT_COMPLETED")){
            Intent newIntent=new Intent(context, MainActivity.class);
            newIntent.setAction("android.intent.action.MAIN");
            newIntent.addCategory("android.intent.category.LAUNCHER");
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }
    }
}


//public class Receiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent)
//    {
//
//        Intent mBootIntent = new Intent(context, MainActivity.class);
//        // 必须设置FLAG_ACTIVITY_NEW_TASK
//         mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//         context.startActivity(mBootIntent);}}
//    }
