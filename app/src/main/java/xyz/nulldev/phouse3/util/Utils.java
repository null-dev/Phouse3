package xyz.nulldev.phouse3.util;

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

    public static double getLargest(double a1, double a2) {
        if(a1 > a2) {
            return a1;
        } else {
            return a2;
        }
    }

    public static double getSmallest(double a1, double a2) {
        if(a1 < a2) {
            return a1;
        } else {
            return a2;
        }
    }

    public static float getLargest(float a1, float a2) {
        if(a1 > a2) {
            return a1;
        } else {
            return a2;
        }
    }

    public static float getSmallest(float a1, float a2) {
        if(a1 < a2) {
            return a1;
        } else {
            return a2;
        }
    }

    public static double wrapAround(double d, double min, double max) {
        double rMin = getSmallest(min, max);
        double rMax = getLargest(min, max);
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
}
