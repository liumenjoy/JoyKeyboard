package net.joyeasy.joykeyboard;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * 用于HTML页面调用键盘
 */
public class JsApi {
    private MainActivity context;
    private WebView webview;
    private JoyKeyboardView joyKeyBoardView;

    public JsApi(MainActivity ctx, WebView webview, JoyKeyboardView joyKeyboardView) {
        this.context = ctx;
        this.webview = webview;
        this.joyKeyBoardView = joyKeyboardView;
    }

    /**
     * 显示自定义键盘
     * @param height
     */
    @JavascriptInterface
    public void showKeyboxKeyboard(final int height) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (joyKeyBoardView.getVisibility() != View.VISIBLE) {
                    joyKeyBoardView.showKeybox(height);
                }
            }
        });
    }

    /**
     * 显示通用键盘
     * @param height
     */
    @JavascriptInterface
    public void showCommonKeyboard(final int height) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (joyKeyBoardView.getVisibility() != View.VISIBLE) {
                    joyKeyBoardView.showLetter(height);
                }
            }
        });
    }

    /**
     * 隐藏键盘
     */
    @JavascriptInterface
    public void hideKeyboard() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (joyKeyBoardView.getVisibility() == View.VISIBLE) {
                    joyKeyBoardView.hideKeyBoard(32);
                }
            }
        });
    }
}
