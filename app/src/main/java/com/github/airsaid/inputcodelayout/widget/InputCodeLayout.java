package com.github.airsaid.inputcodelayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.airsaid.inputcodelayout.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.R.attr.gravity;
import static android.R.attr.width;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * @author airsaid
 * @github https://github.com/airsaid
 * @date 2017/7/21
 * @desc 自定义输入验证码布局
 */
public class InputCodeLayout extends RelativeLayout implements TextWatcher, View.OnKeyListener {

    @IntDef({NORMAL, PASSWORD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShowMode {}

    public static final int NORMAL = 0;
    public static final int PASSWORD = 1;

    private final Context mContext;

    /** 输入框数量 */
    private int mNumber;
    /** 输入框宽度 */
    private int mWidth;
    /** 输入框高度 */
    private int mHeight;
    /** 输入框之间的分割线宽度 */
    private int mDivideWidth;
    /** 输入文字颜色 */
    private int mTextColor;
    /** 输入文字大小 */
    private int mTextSize;
    /** 有焦点时输入框背景 */
    private int mFocusBackground;
    /** 无焦点时输入框背景 */
    private int mUnFocusBackground;
    /** 显示模式 */
    private int mShowMode;

    private LinearLayout mContainer;
    private EditText mEdtCode;
    private TextView[] mTextViews;

    public InputCodeLayout(Context context) {
        this(context, null);
    }

    public InputCodeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputCodeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initViews();
        initAttrs(attrs);
        initListener();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.InputCodeLayout);
        mNumber = a.getInt(R.styleable.InputCodeLayout_icl_number, -1);
        mWidth = a.getDimensionPixelSize(R.styleable.InputCodeLayout_icl_width, -1);
        mHeight = a.getDimensionPixelSize(R.styleable.InputCodeLayout_icl_height, -1);
        int divideWidth = a.getDimensionPixelSize(R.styleable.InputCodeLayout_icl_divideWidth, -1);
        if(divideWidth != -1) setDivideWidth(divideWidth);
        mTextColor = a.getColor(R.styleable.InputCodeLayout_icl_textColor, -1);
        mTextSize = a.getDimensionPixelSize(R.styleable.InputCodeLayout_icl_textSize, 14);
        mFocusBackground = a.getResourceId(R.styleable.InputCodeLayout_icl_focusBackground, -1);
        mUnFocusBackground = a.getResourceId(R.styleable.InputCodeLayout_icl_unFocusBackground, -1);
        mShowMode = a.getInt(R.styleable.InputCodeLayout_icl_showMode, NORMAL);
        int gravity = a.getInt(R.styleable.InputCodeLayout_android_gravity, -1);
        if(gravity != -1) setGravity(gravity);
        a.recycle();
    }

    private void initViews() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mContainer = new LinearLayout(mContext);
        mContainer.setLayoutParams(params);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        mContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        addView(mContainer);

        mEdtCode = new EditText(mContext);
        mEdtCode.setLayoutParams(params);
        mEdtCode.setCursorVisible(false);
        mEdtCode.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        mEdtCode.setBackgroundResource(android.R.color.transparent);
        addView(mEdtCode);
    }

    private void initListener() {
        mEdtCode.addTextChangedListener(this);
        mEdtCode.setOnKeyListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        setCode(s.toString());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            deleteCode();
            return true;
        }
        return false;
    }

    /**
     * 设置验证码
     *
     * @param code 验证码
     */
    private void setCode(String code) {
        if (TextUtils.isEmpty(code)) return;

        for (int i = 0; i < mTextViews.length; i++) {
            TextView textView = mTextViews[i];
            // 如果没设置文本则设置
            if (TextUtils.isEmpty(textView.getText().toString())) {
                textView.setText(code);
                // 取消焦点
                textView.setBackgroundResource(mUnFocusBackground);
                // 有下一个输入框则设置下一个输入框焦点
                if (i < mTextViews.length - 1)
                    mTextViews[i + 1].setBackgroundResource(mFocusBackground);
                // 设置输入完成回调
                if (i == mTextViews.length - 1 && mOnInputCompleteCallback != null)
                    mOnInputCompleteCallback.onInputCompleteListener(getCode());
                // 跳出
                break;
            }
        }
        mEdtCode.setText("");
    }

    /**
     * 删除验证码
     */
    private void deleteCode() {
        for (int i = mTextViews.length - 1; i >= 0; i--) {
            TextView textView = mTextViews[i];
            // 如果有设置文本则删除
            if (!TextUtils.isEmpty(textView.getText().toString())) {
                textView.setText("");
                // 设置焦点
                textView.setBackgroundResource(mFocusBackground);
                // 有下一个输入框则取消下一个输入框的焦点
                if (i < mTextViews.length - 1)
                    mTextViews[i + 1].setBackgroundResource(mUnFocusBackground);
                // 跳出
                break;
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContainer.post(new Runnable() {
            @Override
            public void run() {
                initTextView();
            }
        });
    }

    private void initTextView() {
        if(mNumber <= 0) return;

        int measuredWidth = mContainer.getMeasuredWidth();
        // 均分情况下，根据控件的宽度计算输入框的高度
        int height = (measuredWidth - (mDivideWidth * (mNumber - 1))) / mNumber;

        mTextViews = new TextView[mNumber];
        mContainer.removeAllViews();
        for (int i = 0; i < mNumber; i++) {
            final TextView textView = new TextView(mContext);

            // 判断如果没有设置宽高，则根据控件宽度均分
            if (mWidth != -1 && mHeight != -1) {
                textView.setWidth(mWidth);
                textView.setHeight(mHeight);
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT + mDivideWidth, height, 1);
                textView.setLayoutParams(lp);
            }

            if (mTextSize != -1)
                textView.getPaint().setTextSize(mTextSize);
            if (mTextColor != -1)
                textView.setTextColor(mTextColor);
            if (mFocusBackground != -1 && mUnFocusBackground != -1)
                textView.setBackgroundResource(i != 0 ? mUnFocusBackground : mFocusBackground);

            textView.setGravity(Gravity.CENTER);
            textView.setFocusable(false);
            setShowMode(textView);
            mTextViews[i] = textView;
            mContainer.addView(textView);
        }

        mContainer.post(new Runnable() {
            @Override
            public void run() {
                mEdtCode.setHeight(mContainer.getMeasuredHeight());
            }
        });
    }

    /**
     * 设置输入框数量.
     * @param number 输入框数量
     */
    public void setNumber(int number){
        if(mNumber != number){
            mNumber = number;
            mEdtCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mNumber)});
            onFinishInflate();
        }
    }

    /**
     * 设置分割线宽度.
     * @param width 分割线宽度
     */
    public void setDivideWidth(int width){
        if(width != mDivideWidth){
            mDivideWidth = width;
            mContainer.setDividerDrawable(createDivideShape(mDivideWidth));
        }
    }

    private Drawable createDivideShape(int width) {
        GradientDrawable shape = new GradientDrawable();
        shape.setSize(width, 0);
        return shape;
    }

    /**
     * 设置输入框宽度.（如果宽度 == -1, 则输入框大小按照控件的宽度来均分）
     * @param width 输入框宽度
     */
    public void setWidth(int width){
        if(mWidth != width){
            mWidth = width;
            onFinishInflate();
        }
    }

    /**
     * 设置输入框高度.（如果高度 == -1, 则输入框大小按照控件的宽度来均分）
     * @param height 输入框高度
     */
    public void setHeight(int height){
        if(mHeight != height){
            mHeight = height;
            onFinishInflate();
        }
    }

    /**
     * 设置显示模式.
     *
     * @param showMode 通过 {@link #NORMAL} 或者 {@link #PASSWORD} 设置
     *                 默认是 {@link #NORMAL}
     */
    public void setShowMode(@ShowMode int showMode) {
        if (mShowMode != showMode) {
            mShowMode = showMode;
            for (TextView textView : mTextViews) {
                setShowMode(textView);
            }
        }
    }

    private void setShowMode(TextView textView){
        if (mShowMode == NORMAL)
            textView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else
            textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    /**
     * 设置输入框的摆放位置.
     * @param gravity 请参阅 {@link android.view.Gravity}
     */
    public void setGravity(int gravity) {
        if(mContainer != null)
            mContainer.setGravity(gravity);
    }

    /**
     * 获取已经输入的验证码
     *
     * @return 验证码
     */
    public String getCode() {
        StringBuilder sb = new StringBuilder();
        for (TextView textView : mTextViews) {
            sb.append(textView.getText().toString());
        }
        return sb.toString();
    }

    /**
     * 清空输入框
     */
    public void clear() {
        for (int i = 0; i < mTextViews.length; i++) {
            TextView textView = mTextViews[i];
            textView.setText("");
            textView.setBackgroundResource(i != 0 ? mUnFocusBackground : mFocusBackground);
        }
    }

    private OnInputCompleteCallback mOnInputCompleteCallback;

    public interface OnInputCompleteCallback {
        void onInputCompleteListener(String code);
    }

    /**
     * 设置输入完成监听
     *
     * @param callback 回调
     */
    public void setOnInputCompleteListener(OnInputCompleteCallback callback) {
        this.mOnInputCompleteCallback = callback;
    }
}
