# InputCodeLayout
这是一个 Android 上输入验证码的自定义布局.

## 预览
![](https://github.com/Airsaid/InputCodeLayout/blob/master/preview.gif)

## 使用
- 布局中：
```
 <com.github.airsaid.inputcodelayout.widget.InputCodeLayout
        android:id="@+id/inputCodeLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        app:icl_divideWidth="10dp"
        app:icl_focusBackground="@drawable/bg_edit_focus"
        app:icl_height="40dp"
        app:icl_number="4"
        app:icl_showMode="password"
        app:icl_textColor="@color/colorPrimaryDark"
        app:icl_textSize="16sp"
        app:icl_unFocusBackground="@drawable/bg_edit_un_focus"
        app:icl_width="40dp"/>
```

- 代码中：
```
mInputCodeLayout = (InputCodeLayout) findViewById(R.id.inputCodeLayout);
mInputCodeLayout.setOnInputCompleteListener(new InputCodeLayout.OnInputCompleteCallback() {
    @Override
    public void onInputCompleteListener(String code) {
        Log.e(TAG, "输入的验证码为：" + code);
        Toast.makeText(MainActivity.this, "输入的验证码为：" + code, Toast.LENGTH_SHORT).show();
    }
});
```

## 自定义属性
```
<declare-styleable name="InputCodeLayout">
    <!--输入框数量-->
    <attr name="icl_number" format="integer"/>
    <!--输入框宽度-->
    <attr name="icl_width" format="dimension|reference"/>
    <!--输入框高度-->
    <attr name="icl_height" format="dimension|reference"/>
    <!--输入框之间的分割线宽度-->
    <attr name="icl_divideWidth" format="dimension|reference"/>
    <!--输入文字颜色-->
    <attr name="icl_textColor" format="color|reference"/>
    <!--输入文字大小-->
    <attr name="icl_textSize" format="dimension|reference"/>
    <!--有焦点时输入框背景-->
    <attr name="icl_focusBackground" format="reference"/>
    <!--无焦点时输入框背景-->
    <attr name="icl_unFocusBackground" format="reference"/>
    <!--显示模式-->
    <attr name="icl_showMode">
        <enum name="normal" value="0"/><!--正常-->
        <enum name="password" value="1"/><!--密码-->
    </attr>
    <!--输入框显示位置-->
    <attr name="android:gravity"/>
</declare-styleable>
```

## 联系我
- **QQ 群：** 5707887
- **Blog：**[http://blog.csdn.net/airsaid](http://blog.csdn.net/airsaid)
- **Email：** airsaid1024@gmail.com
