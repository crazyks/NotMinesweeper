package com.crazyks.mt;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;

public class Tmp {
    private static Egg latestEgg = null;
    private static Egg tmpEgg = null;
    
    static Egg getLatestEgg() {
        return latestEgg;
    }
    
    static Egg getTmpEgg() {
        return tmpEgg;
    }
    
    static synchronized void init(Egg egg) {
        latestEgg = new Egg(egg.getValues());
        tmpEgg = new Egg(egg.getValues());
    }
    
    static synchronized void sync(Context context) throws Exception{
        ArrayList<String> whatsNew = latestEgg.whatIsNew(context, tmpEgg);
        latestEgg.initValues(tmpEgg.getValues());
        Egg.sync(latestEgg);
        if (whatsNew.size() > 0) {
            StringBuffer msgBuf = new StringBuffer();
            msgBuf.append(context.getString(whatsNew.size() > 1 ? R.string.congratulation_msgs : R.string.congratulation_msg));
            for (String achv : whatsNew) {
                msgBuf.append('\n');
                msgBuf.append(achv);
            }
            AlertHelper.createAlertDialog(context, context.getString(R.string.congratulation), msgBuf.toString(), new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }
    }
    
}
