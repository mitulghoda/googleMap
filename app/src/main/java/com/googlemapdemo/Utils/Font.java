package com.googlemapdemo.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Admin on 4/30/2016.
 */
public class Font {


    public static final String Montserrat_Light = "MyriadPro_Bold.otf";




    public static void setupFont(Context context, TextView textView, String fontName) {
        if (fontName == null) {
            return;
        }
        Typeface tf = FontCache.get(fontName, context);
        if (tf != null) {
            textView.setTypeface(tf);
        }
    }

    public static void setupFont(Context context, Button button, String fontName) {
        if (fontName == null) {
            return;
        }
        Typeface tf = FontCache.get(fontName, context);
        if (tf != null) {
            button.setTypeface(tf);
        }
    }


    public static void setupFont(Context context, EditText editText, String fontName) {
        if (fontName == null) {
            return;
        }
        Typeface tf = FontCache.get(fontName, context);
        if (tf != null) {
            editText.setTypeface(tf);
        }

//        Typeface font = Typeface.createFromAsset(context
//                .getAssets(), fontName);
//
//        editText.setTypeface(font);
    }
}
