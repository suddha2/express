package lk.busbooking.mobile;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private RelativeLayout noNet, loading;
    private BroadcastReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("https://" + Settings.DOMAIN);
        //get settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //set custom user agent string
        webSettings.setUserAgentString("Express-BusBooking/0.2");
        //set view client to handle external links
        webView.setWebViewClient(new ExternalWebViewClient());

        //register network status receiver
        noNet = (RelativeLayout) findViewById(R.id.networkNA);
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setNetworkStat();
                if(isNetworkAvailable()){
                    webView.reload();
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);

        loading = (RelativeLayout) findViewById(R.id.loading);

    }

    @Override
    protected void onStop()
    {
        try {
            unregisterReceiver(networkStateReceiver);
        } catch (Exception e) {

        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        setNetworkStat();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(networkStateReceiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setNetworkStat() {
        //hide if network not available
        if(!isNetworkAvailable()){
            noNet.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }else{
            noNet.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private class ExternalWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(Settings.DOMAIN)) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // Hide placeholder image
            webView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }

        public void onReceivedHttpError (WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            try {
                view.stopLoading();
            } catch (Exception e) {
            }
            Log.d("On error", errorResponse.toString());
            if (view.canGoBack()) {
                view.goBack();
            }
            view.loadUrl("about:blank");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
            alertDialog.setTitle("Error");
            alertDialog.setMessage("No internet connection was found!");
            alertDialog.setPositiveButton("Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });

            alertDialog.show();
            super.onReceivedHttpError(view, request, errorResponse);
        }
    }
}
