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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.airsaid.inputcodelayout.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
        initAttrs(attrs);
        initViews();
        initListener();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.InputCodeLayout);
        mNumber = a.getInt(R.styleable.InputCodeLayout_icl_number, 4);
        mWidth = a.getDimensionPixelSize(R.styleable.InputCodeLayout_icl_width, -1);
        mHeight = a.getDimensionPixelSize(R.styleable.InputCodeLayout_icl_height, -1);
        mDivideWidth = a.getDimensionPixelSize(R.styleable.InputCodeLayout_icl_divideWidth, -1);
        mTextColor = a.getColor(R.styleable.InputCodeLayout_icl_textColor, -1);
        mTextSize = a.getDimensionPixelSize(R.styleable.InputCodeLayout_icl_textSize, 16);
        mFocusBackground = a.getResourceId(R.styleable.InputCodeLayout_icl_focusBackground, -1);
        mUnFocusBackground = a.getResourceId(R.styleable.InputCodeLayout_icl_unFocusBackground, -1);
        mShowMode = a.getInt(R.styleable.InputCodeLayout_icl_showMode, NORMAL);
        a.recycle();
    }

    private void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_input_code, this, true);
        mContainer = (LinearLayout) findViewById(R.id.container);
        mEdtCode = (EditText) findViewById(R.id.edt_code);
        // 隐藏光标
        mEdtCode.setCursorVisible(false);
        // 设置输入最大长度
        mEdtCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mNumber)});
        // 设置间距
        mContainer.setDividerDrawable(createDivideShape());
    }

    private Drawable createDivideShape() {
        GradientDrawable shape = new GradientDrawable();
        shape.setSize(mDivideWidth, 0);
        return shape;
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
        int measuredWidth = mContainer.getMeasuredWidth();
        Log.e("test", "measuredWidth: " + measuredWidth);

        // 均分情况下，根据控件的宽度计算输入框的高度
        int height = (measuredWidth - (mDivideWidth * (mNumber - 1))) / mNumber;
        Log.e("test", "height: " + height);


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
            if (mShowMode == NORMAL)
                textView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
            textView.setGravity(Gravity.CENTER);
            textView.setFocusable(false);
            mTextViews[i] = textView;
            mContainer.addView(textView);

            textView.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("test", "textview width: " + textView.getMeasuredWidth());
                }
            });
        }

        mContainer.post(new Runnable() {
            @Override
            public void run() {
                mEdtCode.setHeight(mContainer.getMeasuredHeight());
            }
        });

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
            invalidate();
        }
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
