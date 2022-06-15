package pk.mosafir.travsol.webview;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class DataFromJS {
    public static final String TAG = "DataFromJS";
    Context mContext;

    public DataFromJS(Context c) {
        mContext = c;
        Log.d(TAG, "Tests");
    }

    @JavascriptInterface
    public void TestData(String name) {
        Log.d(TAG, "Test");
        Log.d(TAG, name);
    }
}
