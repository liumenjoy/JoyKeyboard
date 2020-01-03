package net.joyeasy.joykeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class JoyKeyboardView extends BaseKeyboardView {

    public JoyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JoyKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化键盘
     * @param input 输入视图：EditVew or Webview
     * @param inputContainer 输入框所在的根布局
     */
    public void config(View input, ViewGroup inputContainer){
        RelativeLayout keyboardContainer = findViewById(R.id.keyboardContainer);
        super.init(input,inputContainer,keyboardContainer);
    }

    /**
     * 初始化键盘
     * @param input 输入视图：EditVew or Webview
     * @param inputContainer 输入框所在的根布局
     * @param keyboardContainer 键盘所在的根布局
     */
    public void config(View input, ViewGroup inputContainer,ViewGroup keyboardContainer){
        super.init(input,inputContainer,keyboardContainer);
    }

    /**
     * 显示数字键盘（EditView）
     */
    public void showNumber(){
        show4EditView(BaseKeyboardView.KEYBOARD_TYPE_NUMBER);
    }

    /**
     * 显示字母键盘（EditView）
     */
    public void showLetter(){
        show4EditView(BaseKeyboardView.KEYBOARD_TYPE_LETTER);
    }

    /**
     * 显示Keybox项目自定义键盘（EditView）
     */
    public void showKeybox(){
        show4EditView(BaseKeyboardView.KEYBOARD_TYPE_KEYBOX);
    }

    /**
     * 显示Keybox项目自定义键盘（WebView）
     * @param hieght
     */
    public void showKeybox(int hieght) {
        show4Webview(BaseKeyboardView.KEYBOARD_TYPE_KEYBOX,hieght);
    }


    /**
     * 显示字母键盘（WebView）
     * @param hieght
     */
    public void showLetter(int hieght) {
        show4Webview(BaseKeyboardView.KEYBOARD_TYPE_LETTER,hieght);
    }


    /**
     * 显示数字键盘（WebView）
     * @param hieght
     */
    public void showNumber(int hieght) {
        show4Webview(BaseKeyboardView.KEYBOARD_TYPE_NUMBER,hieght);
    }

    /**
     * 隐藏键盘（WebView）
     */
    public void hideKeyboard() {
        hideKeyBoard(32);
    }

}
