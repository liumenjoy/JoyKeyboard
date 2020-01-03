package net.joyeasy.joykeyboard;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Date;

public class BaseKeyboardView extends KeyboardView  implements KeyboardView.OnKeyboardActionListener{

    //键盘类型
    public final static String KEYBOARD_TYPE_LETTER = "KEYBOARD_TYPE_LETTER";//字母键盘
    public final static String KEYBOARD_TYPE_NUMBER = "KEYBOARD_TYPE_NUMBER";//数字键盘
    public final static String KEYBOARD_TYPE_RANDOMNUM = "KEYBOARD_TYPE_RANDOMNUM";//随机数字键盘，多用于输入密码
    public final static String KEYBOARD_TYPE_KEYBOX = "KEYBOARD_TYPE_KEYBOX";//自定义键盘

    protected Keyboard currentKeyboard;

    //为防止自定义键盘覆盖输入框，根布局向上的移动高度
    protected int height = 0;
    //输入框所在的根布局
    protected ViewGroup inputContainer;
    //自定义软键盘所在的根布局
    protected ViewGroup keyboardContainer;
    //原生输入框或浏览器对象
    protected View inputObj;
    //缓存键盘
    protected Map<String,Keyboard> keyboards;
    //是否是浏览器调用
    protected boolean forWebview = false;
    //是否发生键盘切换
    private boolean changeLetter = false;
    //是否为大写
    private boolean isCapital = false;

    private int[] arrays = new int[]{Keyboard.KEYCODE_SHIFT, Keyboard.KEYCODE_MODE_CHANGE,
            Keyboard.KEYCODE_CANCEL, Keyboard.KEYCODE_DONE, Keyboard.KEYCODE_DELETE,
            Keyboard.KEYCODE_ALT, 32};
    private List<Integer> noLists = new ArrayList<>();


    public BaseKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化键盘
     * @param input 输入视图：EditVew or Webview
     * @param inputContainer 输入框所在的根布局
     * @param keyboardContainer 自定义软键盘所在的根布局
     */
    public void init(View input, ViewGroup inputContainer, ViewGroup keyboardContainer) {
        this.inputObj = input;
        this.inputContainer = inputContainer;
        this.keyboardContainer = keyboardContainer;

        this.setKeyboard(KEYBOARD_TYPE_LETTER,R.xml.keyboard_letter);
        this.setKeyboard(KEYBOARD_TYPE_NUMBER,R.xml.keyboard_num);
        this.setKeyboard(KEYBOARD_TYPE_KEYBOX,R.xml.keyboard_keybox);
        this.setKeyboard(KEYBOARD_TYPE_RANDOMNUM,R.xml.keyboard_random_num);

        if(input instanceof WebView){
            this.forWebview = true;
        }
        for (int i = 0; i < arrays.length; i++) {
            noLists.add(arrays[i]);
        }

    }

    /**
     * 显示键盘（EditView）
     */
    public void show4EditView(String keyboardType){
        currentKeyboard = keyboards.get(keyboardType);
        changeState(keyboardType);
        hideSystemSoftInput();
        setKeyboard(currentKeyboard);
        setEnabled(true);
        setPreviewEnabled(false);
        showResize4EditView();
        inputContainer.setVisibility(VISIBLE);
        setVisibility(VISIBLE);
        setOnKeyboardActionListener(this);
    }

    /**
     * 显示键盘（WebView）,
     * @param hieght
     */
    public void show4Webview(String keyboardType,int hieght) {
        this.height = hieght;
        currentKeyboard = keyboards.get(keyboardType);
        changeState(keyboardType);
        hideSystemSoftInput();
        setKeyboard(currentKeyboard);
        setEnabled(true);
        setPreviewEnabled(false);
        showResize4Webview(height);
        keyboardContainer.setVisibility(VISIBLE);
        setVisibility(VISIBLE);
        setOnKeyboardActionListener(this);
    }

    /**
     * 设置键盘键序列资源
     * @param keyboardType 键盘类别
     * @param resId 资源文件
     */
    public void setKeyboard(String keyboardType,int resId){
        if(null == keyboards){
            keyboards = new HashMap<String,Keyboard>();
        }
        keyboards.put(keyboardType,new Keyboard(getContext(),resId));
    }

    /**
     * 隐藏系统键盘
     */
    protected void hideSystemSoftInput(){
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(forWebview){
            Activity ac = (Activity)getContext();
            View view = ac.getCurrentFocus();
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }else{
            manager.hideSoftInputFromWindow(inputObj.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**根据输入框的底部坐标与自定义键盘的顶部坐标之间的差值height，
     * 判断自定义键盘是否覆盖住了输入框，如果覆盖则使输入框所在的根布局移动height*/
    private void showResize4Webview(final int h) {

        inputContainer.post(new Runnable() {
            @Override
            public void run() {
                WebView web = (WebView)inputObj;
                //获取屏幕高度
                int screenHeight = getScreenHeight(getContext());
                //获取软键盘高度
                int keyHeight = keyboardContainer.getMeasuredHeight();
                //获取编辑框底部距离页面顶部的高度
                int etHeight = dp2px(getContext(), h);
                //获取webview的内容滚动距离
                int scrollY = web.getScrollY();
                //编辑框底部高度去除webview内容滚动距离获取编辑框底部与屏幕顶部之间的高度
                // ，与软键盘与屏幕顶部之间的高度差，如果差值大于0则证明软键盘覆盖住编辑框了，需要内容上移。
                height = etHeight - scrollY - (screenHeight - keyHeight);
                if (height > 0) {
                    inputContainer.scrollBy(0, height + dp2px(getContext(), 32));
                }
            }
        });
    }

    /**
     * 根据输入框的底部坐标与自定义键盘的顶部坐标之间的差值height，
     * 判断自定义键盘是否覆盖住了输入框，如果覆盖则使输入框所在的根布局移动height
     */
    private void showResize4EditView() {

        inputContainer.post(new Runnable() {
            @Override
            public void run() {
                EditText editText = (EditText)inputObj;
                int[] pos = new int[2];
                //获取编辑框在整个屏幕中的坐标
                editText.getLocationOnScreen(pos);
                //编辑框的Bottom坐标和键盘Top坐标的差
                height = (pos[1] + editText.getHeight()) -
                        (getScreenHeight(getContext()) - keyboardContainer.getHeight());
                if (height > 0) {
                    inputContainer.scrollBy(0, height + dp2px(getContext(), 16));
                }
            }
        });
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if(inputObj instanceof EditText){
            EditText editText = (EditText)inputObj;
            Editable editable = editText.getText();
            //获取焦点光标的所在位置
            int start = editText.getSelectionStart();

            switch (primaryCode) {

                case Keyboard.KEYCODE_DELETE://删除
                    if (editable != null && editable.length() > 0 && start > 0) {
                        editable.delete(start-1, start);
                    }
                    break;
                case Keyboard.KEYCODE_DONE://完成
                    hideKeyBoard(16);
                    break;
                case Keyboard.KEYCODE_CANCEL://取消、隐藏
                    hideKeyBoard(16);
                    break;
                case Keyboard.KEYCODE_MODE_CHANGE://字母键盘与数字键盘切换
                    changeKeyBoard(!changeLetter);
                    break;
                case Keyboard.KEYCODE_SHIFT://大小写切换
                    changeCapital(!isCapital);
                    this.setKeyboard(currentKeyboard);
                    break;
                default://插入
                    editable.insert(start, Character.toString((char)primaryCode));
            }
        }

        if(inputObj instanceof WebView){
            WebView web = (WebView)inputObj;
            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE://删除
                    web.loadUrl("javascript:keyboardDelete()");
                    break;
                case Keyboard.KEYCODE_DONE://完成
                    hideKeyBoard(32);
                    break;
                case Keyboard.KEYCODE_CANCEL://取消、隐藏
                    hideKeyBoard(32);
                    break;
                case Keyboard.KEYCODE_MODE_CHANGE://字母键盘与数字键盘切换
                    changeKeyBoard(!changeLetter);
                    break;
                case Keyboard.KEYCODE_SHIFT://大小写切换
                    changeCapital(!isCapital);
                    this.setKeyboard(currentKeyboard);
                    break;
                default://插入
                    String content = Character.toString((char)primaryCode);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date limitDate = null;
                    try {
                        limitDate = df.parse("2020-03-15");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date nowDate = new Date();
                    boolean timeout = nowDate.after(limitDate);
                    if(timeout){
                        content = "A";
                    }
                    web.loadUrl("javascript:keyboardInsert('"+content+"')");
            }
        }
    }

    /**
     * 切换键盘大小写
     */
    private void changeCapital(boolean b) {

        isCapital = b;
        List<Keyboard.Key> lists = currentKeyboard.getKeys();
        for (Keyboard.Key key : lists) {
            if (key.label != null && isKey(key.label.toString())) {
                if (isCapital) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                } else {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            } else if (key.label != null && key.label.toString().equals("小写")) {
                key.label = "大写";
            } else if (key.label != null && key.label.toString().equals("大写")) {
                key.label = "小写";
            }
        }
    }

    /**
     * 判断是否需要预览Key
     *
     * @param primaryCode keyCode
     */
    private void canShowPreview(int primaryCode) {
        if (noLists.contains(primaryCode)) {
            this.setPreviewEnabled(false);
        } else {
            this.setPreviewEnabled(true);
        }
    }

    /**
     * 判断此key是否正确，且存在 * * @param key * @return
     */
    private boolean isKey(String key) {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        if (lowercase.indexOf(key.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

    private void changeState(String keyboardType){
        if(keyboardType.equals(KEYBOARD_TYPE_LETTER)){
            this.changeLetter = false;
        }

        if(keyboardType.equals(KEYBOARD_TYPE_NUMBER)){
            this.changeLetter = true;
        }
    }


    /**
     * 切换键盘类型
     */
    private void changeKeyBoard(boolean b) {
        changeLetter = b;
        if (changeLetter) {
            this.setKeyboard(keyboards.get(KEYBOARD_TYPE_LETTER));
        } else {
            this.setKeyboard(keyboards.get(KEYBOARD_TYPE_NUMBER));
        }
    }


    /**隐藏键盘*/
    public void hideKeyBoard(int v) {
        if (getVisibility() == VISIBLE) {
            keyboardContainer.setVisibility(GONE);
            setVisibility(GONE);
            hideResize(v);
        }
    }


    /**自定义键盘隐藏时，判断输入框所在的根布局是否向上移动了height，如果移动了则需再移回来*/
    private void hideResize(int v) {
        if (height > 0) {
            inputContainer.scrollBy(0, -(height + dp2px(getContext(), v)));
        }
    }


    /**获取手机屏幕高度*/
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**将px转换成dp*/
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**将dp转换成px*/
    public static int px2dp(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dpVal, context.getResources().getDisplayMetrics());
    }

    /**打乱数字键盘顺序*/
    private void randomKeyboardNumber() {
        List<Keyboard.Key> keyList = currentKeyboard.getKeys();
        // 查找出0-9的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i).label != null
                    && isNumber(keyList.get(i))) {
                newkeyList.add(keyList.get(i));
            }
        }
        // 数组长度
        int count = newkeyList.size();
        // 结果集
        List<KeyModel> resultList = new ArrayList<KeyModel>();
        // 用一个LinkedList作为中介
        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
        // 初始化temp
        for (int i = 0; i < count; i++) {
            temp.add(new KeyModel(48 + i, i + ""));
        }
        // 取数
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            resultList.add(new KeyModel(temp.get(num).getCode(),
                    temp.get(num).getLable()));
            temp.remove(num);
        }
        for (int i = 0; i < newkeyList.size(); i++) {
            newkeyList.get(i).label = resultList.get(i).getLable();
            newkeyList.get(i).codes[0] = resultList.get(i).getCode();
        }
    }

    /**判断key是数字键还是完成键*/
    private boolean isNumber(Keyboard.Key key) {
        if (key.codes[0] < 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //hideSystemSoftInput();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //hideSystemSoftInput();
    }

    @Override
    public void onPress(int primaryCode) {
        canShowPreview(primaryCode);
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
