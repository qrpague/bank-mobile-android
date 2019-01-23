package container.bank.tools;


        import android.app.Activity;
        import android.app.ActivityManager;
        import android.app.Dialog;
        import android.content.ActivityNotFoundException;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Matrix;
        import android.graphics.drawable.BitmapDrawable;
        import android.graphics.drawable.Drawable;
        import android.location.Location;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Environment;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AlertDialog;
        import android.telephony.TelephonyManager;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.RadioButton;
        import android.widget.TextView;
        import android.widget.Toast;


        import com.google.gson.Gson;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.sql.Timestamp;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Map;

        import container.bank.ConvertJsonForObject;
        import container.bank.domain.Config;

/**
 * Created by leonardo on 12-10-2015.
 */
public class Tools {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    public static String formatNumberPhone(String phone ) {

         String contatoFiltrado = phone.replace("+55" , "");

            contatoFiltrado = contatoFiltrado.replace("-" , "");
            contatoFiltrado = contatoFiltrado.replace(" " , "");
            contatoFiltrado = contatoFiltrado.replace("(", "");
            contatoFiltrado = contatoFiltrado.replace(")", "");

         if ( contatoFiltrado.subSequence(0,1).equals("0")) {
             return contatoFiltrado.substring(1,contatoFiltrado.length());
         }
          return contatoFiltrado ;
    }

    public static void setColorBackgroud(Activity activity , Config config ){

        int colorInt = Color.parseColor("#" + config.colorPrimario) ;

        activity.getWindow().setNavigationBarColor(colorInt );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colorInt);
        }
    }

    public static String convertMapToStringJson(Map<String, String> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static String convertListtoStringJson(List<Object> list) {
        String json = new Gson().toJson(list);
        return json;

    }
    public static HashMap<String, String> jsonToMap(String t) throws JSONException {

        HashMap<String, String> map = new HashMap<String, String>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = jObject.getString(key);
            map.put(key, value);

        }
        return map ;
    }



    public static final String PREFS_NAME = "MyPrefsFile";


    public static boolean saveObjectLocal(Context c , Class classe  ,  String jsonString ) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(classe.getCanonicalName(), jsonString);
        return editor.commit();
    }

    public static String getObjectLocal(Context c , Class classe ) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        String jsonString = settings.getString(classe.getCanonicalName(), null);
        return jsonString ;
    }

    public static <E> Object getObjectJsonToObject(Context c, Class classe) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        String jsonString = settings.getString(classe.getCanonicalName(), null);
        return (E) ConvertJsonForObject.getInstance().convert(jsonString, classe);
    }


    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static boolean isServiceRunning(Context context, String nomeServico) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (nomeServico.equals(service.service.getClassName())) {
                return true;
            }

        }
        return false;
    }

    public static Timestamp getTimeStamp() {

        java.util.Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp;

    }


    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }


    public static String getCarrier(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        return carrierName;
    }

    public static String getImei(Context ctx) {
        TelephonyManager telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();

    }

    public static String getModelDevice() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (str == null) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }


    public static void popUpMessageToast(final Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
    public static void popUpMessage(final Context ctx, String msg) {
        Snackbar.make(((Activity) ctx).findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .show();
    }

 
    public static void popUpMessage(final Context ctx, String title, CharSequence[] cs, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setItems(cs, onClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void popUpMessage(final Context ctx, String title, ArrayAdapter arrayAdapter, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title).setAdapter(arrayAdapter, onClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static EditText getEditText(Activity ctx, int idEditText) {
        EditText editText = (EditText) ctx.findViewById(idEditText);
        editText.clearFocus();
        return editText;

    }

    public static EditText getEditText(View view, int idEditText) {
        EditText editText = (EditText) view.findViewById(idEditText);
        return editText;

    }

    public static EditText getEditText(Dialog dialog, int idEditText) {
        EditText editText = (EditText) dialog.findViewById(idEditText);
        return editText;

    }

    public static RadioButton getRadioButton(Activity ctx, int idEditText) {
        RadioButton checkBox = (RadioButton) ctx.findViewById(idEditText);
        return checkBox;

    }

    public static Button getButton(Activity ctx, int idEditText) {
        Button button = (Button) ctx.findViewById(idEditText);
        return button;

    }

    public static Button getButton(View view, int idEditText) {
        Button button = (Button) view.findViewById(idEditText);
        return button;

    }

    public static String getValueEditText(Activity ctx, int idEditText) {
        EditText editText = (EditText) ctx.findViewById(idEditText);
        return editText.getText().toString();

    }

    public static TextView getTextView(Dialog dialog, int idEditText) {
        TextView textview = (TextView) dialog.findViewById(idEditText);
        return textview;

    }

    public static TextView getTextView(Activity ctx, int idEditText) {
        TextView textview = (TextView) ctx.findViewById(idEditText);
        return textview;

    }



    public static TextView getTextView(View view, int idEditText) {

        TextView textview = (TextView) view.findViewById(idEditText);
        return textview;

    }
    public static TextView getTextView(Context c, int idEditText) {

        TextView textview = (TextView) ((Activity)c).findViewById(idEditText);
        return textview;

    }


    public static Object validationObjectNullReturn(Context ctx, Object object, String msg) throws Exception {
        if (object == null) {
            popUpMessage(ctx, msg);
            throw new Exception("no valid object");
        }
        return object;
    }

    public static String formatDoublePreco(Double value) {
        String df = String.format("%.2f", value);
        return df;
    }



    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }


        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 96; // Replaced the 1 by a 96
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 96; // Replaced the 1 by a 96

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getImageLocalUrl(String url, int horizontal, int vertical) {
        Bitmap bitmap = BitmapFactory.decodeFile(url);

        Bitmap toyImageScaled = Bitmap.createScaledBitmap(bitmap, horizontal, vertical * bitmap.getHeight() / bitmap.getWidth(), false);

        Matrix matrix = new Matrix();
        //matrix.postRotate(90);
        Bitmap rotatedScaledToyImage = Bitmap.createBitmap(toyImageScaled, 0, 0, toyImageScaled.getWidth(), toyImageScaled.getHeight(), matrix, true);
        return rotatedScaledToyImage;

    }

    public static long getValidateDateBirth(long valueEditText) {
        //year-mouth-day

        return valueEditText;


    }

    public static void openFIlePdf(Context ctx, String filename) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateUserName(String nome) {

        if ( nome != null && nome.length() > 3  && !nome.contains(".")) {
            return true;
        }

        return false;
    }

    public static String validadeString(String value) {

        if (value == null && value.length() < 1) {
            return "--";
        } else {
            return value;
        }
    }
    public static boolean validateEmail(String email) {

        if ( email != null && email.length() > 3 & email.contains("@") && email.contains(".")) {
            return true;
        }

        return false;
    }

    public static boolean validatePass(String pass) {
        if ( pass != null && pass.length() >= 4) {
            return true ;

        }
        return false;

    }

    public static String getFormatDateTime(long dataCriacao) {
        return "11/05/2016";
    }



    public static void setStatusBarTranslucent(Activity activity, boolean makeTranslucent) {
        if (makeTranslucent) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }





}

