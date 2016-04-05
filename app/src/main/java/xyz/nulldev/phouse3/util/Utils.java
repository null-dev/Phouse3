package xyz.nulldev.phouse3.util;

import android.content.Context;
import android.content.res.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Project: Phouse3
 * Created: 13/12/15
 * Author: hc
 */
public class Utils {
    //Return lazy-initialized GSON
    static Gson gson = null;
    public static Gson getGson() {
        if(gson == null) gson = new GsonBuilder().create();

        return gson;
    }

    public static Byte[] box(byte[] array) {
        Byte[] newArray = new Byte[array.length];
        for(int i = 0; i < array.length; i++){
            newArray[i] = array[i];
        }
        return newArray;
    }
    public static byte[] unbox(Byte[] array) {
        byte[] newArray = new byte[array.length];
        for(int i = 0; i < array.length; i++){
            newArray[i] = array[i];
        }
        return newArray;
    }

    public static double wrapAround(double d, double min, double max) {
        double rMin = Math.min(min, max);
        double rMax = Math.max(min, max);
        double diff = rMax - rMin;
        //Return the min/max immediately
        if(diff == 0) return rMin;
        while(d > max) {
            d -= diff;
        }
        while(d < min) {
            d += diff;
        }
        return d;
    }

    public static double addWithWrapping(double orig, double toAdd, double min, double max) {
        return wrapAround(orig + toAdd, min, max);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
