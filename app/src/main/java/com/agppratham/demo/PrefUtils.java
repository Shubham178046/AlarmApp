package com.agppratham.demo;

import android.content.Context;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefUtils {
    public static String URI = "uri";

    public static void setURI(Context ctx, String value) {
        Prefs.with(ctx).save(URI, value);
    }

    public static String getURI(Context ctx) {
        return Prefs.with(ctx).getString(URI, "");
    }

}