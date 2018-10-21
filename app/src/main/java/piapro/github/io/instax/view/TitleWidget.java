package piapro.github.io.instax.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import piapro.github.io.instax.R;


public class TitleWidget extends RelativeLayout implements View.OnClickListener {

    public static final String Tag = TitleWidget.class.getSimpleName();

    private RelativeLayout rlyt_left_back;
    private ImageView mivBack;
    private RelativeLayout rlyt_left_tv_back;
    private TextView tv_back;;
    public TextView tv_title;
    private RelativeLayout rlyt_miv_right1;
    private ImageView mivRight1;
    private RelativeLayout rlyt_miv_right2;
    private ImageView mivRight2;
    private RelativeLayout rlyt_tv_right3;
    private TextView tvExplain;
    private RelativeLayout rlyt_tv_right4;
    private TextView tvExplain2;
    private LinearLayout rl_title;

    private Context m_Context;

    private onReturnListener m_OnReturnListener;
    private onSubmitListener m_OnSubmitListener;
    private onLeftTitleListener m_onLeftTitleListener;
    private onRightMiv1Listener onRightMiv1Listener;
    private onRightMiv2Listener onRightMiv2Listener;



    private int title_miv_back;
    private String title_tv_back;
    private String title_text;
    private int title_miv_right1;
    private int title_miv_right2;
    private String title_tv_right3;
    private String title_tv_right4;
    private int title_bg_color;
    private int title_stu_color;

    public TitleWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        inintAttribut(context,attrs);
        initData(context);
    }

    private void initData(Context context) {
        this.m_Context = context;
        LayoutInflater.from(m_Context).inflate(R.layout.common_widget_title,this, true);
        this.rlyt_left_back = (RelativeLayout) findViewById(R.id.rlyt_left_back);
        this.mivBack= (ImageView) findViewById(R.id.iv_back);
        this.rlyt_left_tv_back = (RelativeLayout) findViewById(R.id.rlyt_left_tv_back);
        this.tv_back = (TextView) findViewById(R.id.tv_back);
        this.tv_title = (TextView) findViewById(R.id.tv_title);
        this.rlyt_miv_right1 = (RelativeLayout) findViewById(R.id.rlyt_miv_right1);
        this.mivRight1= (ImageView) findViewById(R.id.iv_right1);
        this.rlyt_miv_right2 = (RelativeLayout) findViewById(R.id.rlyt_miv_right2);
        this.mivRight2= (ImageView) findViewById(R.id.iv_right2);
        this.rlyt_tv_right3 = (RelativeLayout) findViewById(R.id.rlyt_tv_right3);
        //this.tvExplain = (TextView) findViewById(R.id.tv_explain);
        this.rlyt_tv_right4 = (RelativeLayout) findViewById(R.id.rlyt_tv_right4);
        this.tvExplain2 = (TextView) findViewById(R.id.tv_explain2);
        this.rlyt_left_back.setOnClickListener(this);
        this.rl_title=(LinearLayout) findViewById(R.id. rl_title);


        setTitle(title_text);
        setBackMiv(title_miv_back);
        setBackText(title_tv_back);
        setRightMiv1(title_miv_right1);
        setRightMiv2(title_miv_right2);
        setRighttv3(title_tv_right3);
        setRighttv4(title_tv_right4);
    }

    public TitleWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inintAttribut(context,attrs);
        // TODO Auto-generated constructor stub
        initData(context);
    }

    private void inintAttribut(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.title_widget_layout);
        title_miv_back=a.getResourceId(R.styleable.title_widget_layout_title_miv_back,-1);
        title_tv_back=a.getString(R.styleable.title_widget_layout_title_tv_back);
        title_text=a.getString(R.styleable.title_widget_layout_title_text);
        title_miv_right1=a.getResourceId(R.styleable.title_widget_layout_title_miv_right1,-1);
        title_miv_right2=a.getResourceId(R.styleable.title_widget_layout_title_miv_right2,-1);
        title_tv_right3=a.getString(R.styleable.title_widget_layout_title_tv_right3);
        title_tv_right4=a.getString(R.styleable.title_widget_layout_title_tv_right4);
        title_bg_color= a.getResourceId(R.styleable.title_widget_layout_title_bg_color,-1);
        title_stu_color= a.getResourceId(R.styleable.title_widget_layout_title_stu_color,-1);
        a.recycle();
    }

    public void setBackMiv(int str) {
        if (str!=-1) {
            this.mivBack.setBackgroundResource(str);
        }

    }

    public void setBackText(String str) {
        if (!TextUtils.isEmpty(str)){
            this.rlyt_left_tv_back.setVisibility(View.VISIBLE);
            this.rlyt_left_tv_back.setOnClickListener(this);
            this.tv_back.setText(str);
        }else{
            this.rlyt_left_tv_back.setVisibility(View.GONE);
        }
    }

    public void setTitle(String str) {
        // TODO Auto-generated method stub
        this.tv_title.setText(str);
    }

    public void setRightMiv1(int str) {
        if (str!=-1){
            this.rlyt_miv_right1.setVisibility(View.VISIBLE);
            this.rlyt_miv_right1.setOnClickListener(this);
            this.mivRight1.setBackgroundResource(str);
        }else{
            this.rlyt_miv_right1.setVisibility(View.GONE);
        }
    }

    public void setRightMiv2(int str) {
        if (str!=-1){
            this.rlyt_miv_right2.setVisibility(View.VISIBLE);
            this.rlyt_miv_right2.setOnClickListener(this);
            this.mivRight1.setBackgroundResource(str);
        }else{
            this.rlyt_miv_right2.setVisibility(View.GONE);
        }
    }

    public void setRighttv3(String str) {
        if (!TextUtils.isEmpty(str)){
            this.rlyt_tv_right3.setVisibility(View.VISIBLE);
            this.rlyt_tv_right3.setOnClickListener(this);
            this.tvExplain.setText(str);
        }else{
            this.rlyt_tv_right3.setVisibility(View.GONE);
        }
    }

    public void setRighttv4(String str) {
        if (!TextUtils.isEmpty(str)){
            this.rlyt_tv_right4.setVisibility(View.VISIBLE);
            this.rlyt_tv_right4.setOnClickListener(this);
            this.tvExplain2.setText(str);
        }else{
            this.rlyt_tv_right4.setVisibility(View.GONE);
        }
    }

    public void setBackVisibility(int visibility) {
        rlyt_left_back.setVisibility(visibility);
    }

    public void setTitleBackground(int color) {
        // TODO Auto-generated method stub
        this.rl_title.setBackgroundColor(color);
    }

    public void setReturnListener(onReturnListener paramonReturnListener) {
        this.m_OnReturnListener = paramonReturnListener;
    }

    public void setSubmitListener(onSubmitListener paramonSubmitListener) {
        this.m_OnSubmitListener = paramonSubmitListener;
    }

    public void setLeftTextListener(onLeftTitleListener paramonReturnListener) {
        this.m_onLeftTitleListener = paramonReturnListener;
    }

    public void setRightMiv1Listener(onRightMiv1Listener paramonSubmitListener) {
        this.onRightMiv1Listener = paramonSubmitListener;
    }
    public void setRightMiv2Listener(onRightMiv2Listener Listener) {
        this.onRightMiv2Listener = Listener;
    }

    public abstract interface onReturnListener {
        public abstract void onReturn(View paramView);
    }

    public abstract interface onLeftTitleListener {
        public abstract void LeftTitle(View paramView);
    }

    public abstract interface onRightMiv1Listener {
        public abstract void onRightMiv1(View paramView);
    }

    public abstract interface onRightMiv2Listener {
        public abstract void onRightMiv2(View paramView);
    }

    public abstract interface onSubmitListener {
        public abstract void onSubmit(View paramView);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.rlyt_left_back:
                if (this.m_OnReturnListener != null) {
                    this.m_OnReturnListener.onReturn(v);
                    return;
                }
                ((Activity) this.m_Context).finish();
                return;
            case R.id.rlyt_left_tv_back:
                if (this.m_onLeftTitleListener != null) {
                    this.m_onLeftTitleListener.LeftTitle(v);
                }
                return;
            case R.id.rlyt_miv_right1:
                if (this.onRightMiv1Listener != null) {
                    this.onRightMiv1Listener.onRightMiv1(v);
                }
                return;
            case R.id.rlyt_miv_right2:
                if (this.onRightMiv2Listener != null) {
                    this.onRightMiv2Listener.onRightMiv2(v);
                }
                return;
            case R.id.rlyt_tv_right3:
                if (this.m_OnSubmitListener != null) {
                    this.m_OnSubmitListener.onSubmit(v);
                }
            default:
                break;
        }
    }
}
