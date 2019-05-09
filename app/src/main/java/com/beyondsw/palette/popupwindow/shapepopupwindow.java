package com.beyondsw.palette.popupwindow;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.PopupWindow;
        import android.widget.SeekBar;

        import com.beyondsw.palette.R;

public class shapepopupwindow extends PopupWindow implements View.OnClickListener ,SeekBar.OnSeekBarChangeListener {
    private View line;
    private View circle;
    private View rectangle;
    private View pen;
    View mPopView;

    private OnItemClickListener mListener;
    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public shapepopupwindow(final Context context) {
        super(context);
        initView(context);
        setPopupWindow();
    }

    private void initView(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        mPopView = inflater.inflate(R.layout.shape_popupwindow, null);

        //comment_item_linear = (LinearLayout) mPopView.findViewById(R.id.comment_item_linear);
        line = (ImageView) mPopView.findViewById(R.id.line);
        circle = (ImageView) mPopView.findViewById(R.id.circle);
        rectangle = (ImageView) mPopView.findViewById(R.id.rectangle);
        pen = (ImageView) mPopView.findViewById(R.id.pen);
        //comment_item_linear.setOnClickListener(this);
        line.setOnClickListener(this);
        circle.setOnClickListener(this);
        rectangle.setOnClickListener(this);
        pen.setOnClickListener(this);
    }

    private void setPopupWindow() {
        this.setContentView(mPopView);// 设置View
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        //this.setAnimationStyle(R.style.mypopwindow_anim_style);// 设置动画
        //this.setBackgroundDrawable(new ColorDrawable(0x00000000));// 设置背景透明
        mPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
//                int height = mPopView.findViewById(R.id.id_pop_layout).getTop();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height) {
//                        dismiss();
//                    }
//                }

                // MyApp.getApp().showToast("ontouch....");
                return true;
            }
        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow

//            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (mListener != null) {
            mListener.setOnItemClick(v);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (mListener != null) {
            mOnSeekBarChangeListener.setOnChanging(seekBar,i,b);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    /**
     * 定义一个接口，公布出去 在Activity中操作按钮的单击事件
     */
    public interface OnItemClickListener {
        void setOnItemClick(View v);
    }
    /**
     * 定义一个接口，公布出去 在Activity中操作按钮的单击事件
     */
    public interface OnSeekBarChangeListener {
        void setOnChanging(SeekBar seekBar, int i, boolean b);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    public void setmOnSeekBarChangeListener(OnSeekBarChangeListener listener){
        this.mOnSeekBarChangeListener=listener;
    }
}
