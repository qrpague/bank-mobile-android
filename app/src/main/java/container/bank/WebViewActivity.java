package container.bank;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.net.Uri;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import container.bank.domain.Config;
import container.bank.domain.Contact;
import container.bank.tools.LogApp;
import container.bank.tools.Tools;
import dmax.dialog.SpotsDialog;


/**
 * Created by administrator on 06/10/17.
 */

public class WebViewActivity  extends Activity {
    private WebView webView ;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        Config config = (Config) Tools.getObjectJsonToObject(this , Config.class );
        if ( config != null && config.colorPrimario != null ) {
            Tools.setColorBackgroud(this, config);
        }


        setContentView(R.layout.activity_container_webview);
        List<Contact> listaContact = listaDeContatos();

//        Log.d( "DEBUG" ,  listaContact.toString() );

        initWebView();

    }

    private void initWebView(){
        webView = (WebView)findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);




        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogApp.debug("Progress -> " + newProgress );
                if ( newProgress == 100 ) {
                    if (  progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                }

            }


        });

        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.addJavascriptInterface(new WebAppInterface(this), "ContainerFunctions");

        webView.getSettings().setDomStorageEnabled(true);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String myUrl = request.getUrl().toString();
                Log.d("DEBUG", myUrl );
                view.loadUrl(myUrl);
                String myUrlFitler = myUrl.replace("file://" , "http://");

                Uri uri = Uri.parse(myUrlFitler);

                String queryParams = uri.getQuery();

                if ( queryParams != null ) {
                    HashMap<String , String > mapQueryParams = new HashMap<String, String>();

                    String[] listQueryParans = queryParams.split("&");

                    for ( String params : listQueryParans ) {

                        String paramsName = params.split("=")[0];
                        String paramsValue = params.split("=")[1];

                        mapQueryParams.put( paramsName, paramsValue );
                        LogApp.debug("DEBUG -> " + paramsName + " " + paramsValue);

                    }
                    if ( mapQueryParams.get("acao") != null  && mapQueryParams.get("acao").equals("setColor") ) {
                        setConfigColorApp( mapQueryParams);
                    }


                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                progressDialog.dismiss();

            }
            //            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("WebView", "onPageStarted " + url);
                progressDialog = new SpotsDialog(WebViewActivity.this , R.style.Dialog);

                progressDialog.show();

            }



        });

        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        Config config = (Config) Tools.getObjectJsonToObject(this , Config.class );
        if ( config != null  ) {
            String queryParams = config.getQueryParams();
            String myUrl = "file:///android_asset/index.html?"+queryParams;
            LogApp.debug("URL -> " + myUrl );
            webView.loadUrl(myUrl);
        } else {
//            webView.loadUrl("file:///android_asset/index.html");
            webView.loadUrl("file:///android_asset/index.html");
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        //webView.goBack();
                        webView.clearHistory();
                        Config config = (Config) Tools.getObjectJsonToObject(WebViewActivity.this , Config.class );
                        if ( config != null ) {
                            String queryParams = config.getQueryParams();
                            if ( queryParams != null ) {
                                webView.loadUrl("file:///android_asset/index.html?" + queryParams);
                            }
                        }
                        else {
                            finish();
                        }
                     } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    public List<Contact> listaDeContatos(){
        Cursor cursor = null;
        List<Contact> lista = new ArrayList<>();
        try {

            cursor = getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
            int contactIdIdx = cursor.getColumnIndex(Phone._ID);
            int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
            int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
            int idUrlPhoto = cursor.getColumnIndex(Phone.PHOTO_URI);
            if (cursor.moveToFirst()) {
                do {
                    String phoneNumber = cursor.getString(phoneNumberIdx);
                    if ( phoneNumber.length() > 8 ) {

//                    String idContact = cursor.getString(contactIdIdx);
                        String name = cursor.getString(nameIdx);
//                    String photoUrlContact = cursor.getString(idUrlPhoto);
                        phoneNumber = Tools.formatNumberPhone(phoneNumber);
                        Contact contact = new Contact(name, phoneNumber);
                        //contact.photoContact = openPhoto(getActivity() , Integer.valueOf(idContact));
                        lista.add(contact);
                    }

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lista;

    }


    private void setConfigColorApp( HashMap<String , String > mapa  ) {

        Config config = new Config();

        config.colorPrimario = mapa.get("colorPrimario") ;
        config.colorSecundario  =   mapa.get("colorSecundario") ;
        config.bancoEscolhido = mapa.get("bancoEscolhido");

        Tools.saveObjectLocal(this , Config.class , config.toJson() );
        Tools.setColorBackgroud( this , config );
    }



    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String listaContatos()
        {
            List<Contact> listaContatos = listaDeContatos();
            Gson gson = new Gson();

            String element = gson.toJson(  listaContatos,
                    new TypeToken<ArrayList<Contact>>() {}.getType());
//            try {
//                JSONArray list = new JSONArray(element);

//                String jsonString =  gson.toJson(list);
                return element ;

//            } catch (Exception e ) {e.printStackTrace();}


//            return   "n-a";
        }
        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

}
