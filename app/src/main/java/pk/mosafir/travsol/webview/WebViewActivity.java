package pk.mosafir.travsol.webview;

import static pk.mosafir.travsol.utils.ExtensionsKt.setLoggedIn;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import pk.mosafir.travsol.R;
import pk.mosafir.travsol.ui.MainActivity;
import pk.mosafir.travsol.utils.ExtensionsKt;
import timber.log.Timber;

public class WebViewActivity extends AppCompatActivity {
    private WebView webview;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> uploadMessage;
    private static final int REQUEST_SELECT_FILE = 100;
    private final static int FILE_CHOOSER_RESULT_CODE = 2;
    private static final String TAG = "Main";
    private Dialog dialog;

    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt", "SourceLockedOrientationActivity"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dialog = new Dialog(this, R.style.full_screen_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.cutom_dialog_loading);
        dialog.show();
        Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }
        });
        String myUrl = getIntent().getStringExtra("url");
        this.webview = findViewById(R.id.mosafirbrowser);
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().setAcceptCookie(true);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webview.addJavascriptInterface(
                    new AnalyticsWebInterface(this), AnalyticsWebInterface.TAG);
            webview.addJavascriptInterface(
                    new DataFromJS(this), DataFromJS.TAG);
        }
        webview.setWebViewClient(new WebViewClient() {

            @SuppressLint("LogNotTimber")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.equals("https://mosafir.pk/mobile/Admin/dashboard")) {
                    setLoggedIn(true);
                    finish();
                } else if (url.equals("https://mosafir.pk/mobile/users/b2c_logout") || url.equals("https://www.mosafir.pk/mobile/users/b2c_logout")) {
                    setLoggedIn(false);
                    ExtensionsKt.loggedOutUser();
                    finish();
                } else if (url.equals("https://mosafir.pk/mobile/Hotels/search_form") || url.equals("https://mosafir.pk/mobile/Tours/search_form") || url.equals("https://mosafir.pk/mobile/Flights/search_form")) {
                    finish();
                } else if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    view.reload();
                    return true;
                } else if (url.equals("https://mosafir.pk/mobile/") || url.equals("mosafir.pk/mobile/") || url.equals("http://mosafir.pk/mobile/") || url.equals("https://www.mosafir.pk/mobile/") || url.equals("http://www.mosafir.pk/mobile/")) {
                    MainActivity.Companion.setFragment("home");
                    finish();
                } else if (url.contains("redirect=Admin/all_bookings")) {
                    MainActivity.Companion.setFragment("booking");
                    finish();
                } else if (url.contains("user_login")) {
                    MainActivity.Companion.setFragment("login");
                    finish();
                } else if (url.contains("redirect=Admin/paid_bookings")) {
                    MainActivity.Companion.setFragment("payment");
                    finish();
                } else if (url.startsWith("share:")) {
                    String webUrl = view.getUrl();
                    Intent sendIntent = new Intent();
                    Timber.d("url: %s", webUrl);
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, webUrl);
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);

                    startActivity(shareIntent);
                    return true;
                } else if (url.startsWith("whatsapp:")) {
                    try {
                        String trimToNumber = Uri.parse(url).toString(); //10 digit number
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://wa.me/" + trimToNumber + "/?text=" + ""));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                Log.e("url", url);
                view.loadUrl(url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            public void onPageFinished(WebView view, String url) {
                Uri data = getIntent().getData();
                if (data != null) {
                    String strData = data.toString();
                    int a = strData.indexOf("mosafir.pk/mobile/");
                    int b = strData.indexOf("mosafir.pk/");
                    String[] separated = new String[4];

                    if (a > 0)
                        separated = strData.split("mosafir.pk/mobile/");
                    else if (b > 0)
                        separated = strData.split("mosafir.pk/");

                    if (url.contains("android_asset/index")) {
                        view.loadUrl("https://www.mosafir.pk/mobile/" + separated[1]);
                    } else if (url.contains("https://mosafir.pk/mobile")) {

                        setTheme(R.style.AppTheme2);

                    }
                } else {
                    if (url.contains("android_asset/index")) {
                        view.loadUrl("https://mosafir.pk/mobile");
                    }
                    if (url.contains("https://mosafir.pk/mobile")) {
                        setTheme(R.style.AppTheme2);
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/nonet.html");
                dialog.cancel();
            }
        });
        webview.loadUrl(myUrl);
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    dialog.cancel();
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
    }

    @SuppressLint("LogNotTimber")
    public void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Failed to get the token.");
                return;
            }
            String token = task.getResult();
            Log.d(TAG, "Token : " + token);
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to get the token : " + e.getLocalizedMessage()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != WebViewActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getBaseContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            finish();
        }
    }
}