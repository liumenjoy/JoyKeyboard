package net.joyeasy.joykeyboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private WebView web;
    private JoyKeyboardView joyKeyboardView;
    private RelativeLayout inputContainer;
    private RelativeLayout keyboardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputContainer = findViewById(R.id.inputContainer);
        joyKeyboardView = findViewById(R.id.joyKeyboardView);
        keyboardContainer = findViewById(R.id.keyboardContainer);

        web = findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        JsApi jsApi = new JsApi(MainActivity.this, web,joyKeyboardView);
        web.addJavascriptInterface(jsApi,"app");
        web.loadUrl("file:///android_asset/index.html");
    }

    public class DemoJavaScriptInterface {

        @JavascriptInterface
        public void showInput(final int height) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (joyKeyboardView.getVisibility() != View.VISIBLE) {
                        joyKeyboardView.showKeybox(height);//web, height, inputContainer, keyboardContainer);
                    }
                }
            });
        }
    }
}
