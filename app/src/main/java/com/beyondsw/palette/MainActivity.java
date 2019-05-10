package com.beyondsw.palette;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.beyondsw.palette.drawinginfo.DrawingInfo;
import com.beyondsw.palette.drawinginfo.PathDrawingInfo;
import com.beyondsw.palette.listener.OnItemClickListener;
import com.beyondsw.palette.popupwindow.SetColorSizePopupWindow;
import com.beyondsw.palette.popupwindow.SetShapePopupWindow;
import com.beyondsw.palette.shape.LineShape;
import com.beyondsw.palette.xmlparser.PullPathDrawingInfoParser;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PaletteView.Callback,Handler.Callback ,OnItemClickListener,SetColorSizePopupWindow.OnSeekBarChangeListener {

    private View mUndoView;
    private View mRedoView;
    private View mPenView;
    private View mEraserView;
    private View mClearView;
    private View mshowDrawSizeSeekBar;

    private PaletteView mPaletteView;
    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    private Handler mHandler;
    private boolean isDrawSizeSeekBarshowed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPaletteView = (PaletteView) findViewById(R.id.palette);
        mPaletteView.setCallback(this);

        mUndoView = findViewById(R.id.undo);
        mRedoView = findViewById(R.id.redo);
        mPenView = findViewById(R.id.pen);
        mPenView.setSelected(true);
        mEraserView = findViewById(R.id.eraser);
        mClearView = findViewById(R.id.clear);
        mshowDrawSizeSeekBar=findViewById(R.id.showDrawSizeSeekBar);

        mUndoView.setOnClickListener(this);
        mRedoView.setOnClickListener(this);
        mPenView.setOnClickListener(this);
        mEraserView.setOnClickListener(this);
        mClearView.setOnClickListener(this);
        mshowDrawSizeSeekBar.setOnClickListener(this);

        mUndoView.setEnabled(false);
        mRedoView.setEnabled(false);

        mHandler = new Handler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    private void initSaveProgressDlg(){
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存,请稍候...");
        mSaveProgressDlg.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_SAVE_FAILED:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this,"保存失败",Toast.LENGTH_SHORT).show();
                break;
            case MSG_SAVE_SUCCESS:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this,"画板已保存",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    private static String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if(mSaveProgressDlg==null){
                    initSaveProgressDlg();
                }
                mSaveProgressDlg.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bm = mPaletteView.buildBitmap();
                        String savedFile = saveImage(bm, 100);
                        if (savedFile != null) {
                            scanFile(MainActivity.this, savedFile);
                            mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                        }else{
                            mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                        }
                    }
                }).start();
                break;
            case R.id.saveasxml:
                /*PullPathDrawingInfoParser mPullPathDrawingInfoParser=new PullPathDrawingInfoParser();
                try {
                    mPullPathDrawingInfoParser.serialize(mPaletteView.getDrawingList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/

                String fileName="handWriting1.xml";
                File file = new File(Environment.getExternalStorageDirectory().getPath(), fileName);
                XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例
                // FileOutputStream outS = new FileOutputStream(file,true);
                //serializer.startDocument("utf-8", true);//文档开头，true表示独立，不需要其他文件依赖
                StringBuilder mStringBuilder=new StringBuilder();
                int no=0;
                for (DrawingInfo mDrawingInfo:mPaletteView.getDrawingList()) {
                    try {
                        FileOutputStream outS = new FileOutputStream(file,no==0?false:true);
                        serializer.setOutput(outS, "utf-8");
                        mDrawingInfo.serialize(serializer);
                        outS.close();
                        no++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(mSaveProgressDlg==null){
                    initSaveProgressDlg();
                }
                break;
            case R.id.openfromxml:
                fileName="handWriting1.xml";
                file = new File(Environment.getExternalStorageDirectory().getPath(), fileName);

                try {
                    FileInputStream in=new FileInputStream(file);
                    List<PathDrawingInfo> mDrawingInfos=new PullPathDrawingInfoParser().parse(in);
                    mPaletteView.setDrawingList(mDrawingInfos);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
        }
        return true;
    }

    @Override
    public void onUndoRedoStatusChanged() {
        mUndoView.setEnabled(mPaletteView.canUndo());
        mRedoView.setEnabled(mPaletteView.canRedo());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showDrawSizeSeekBar:
                showSetupPopupWindow(v);
                break;
            case R.id.undo:
                mPaletteView.undo();
                break;
            case R.id.redo:
                mPaletteView.redo();
                break;
            case R.id.pen:
                showShapePopupWindow(v);
                v.setSelected(true);
                mEraserView.setSelected(false);
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                break;
            case R.id.eraser:
                v.setSelected(true);
                mPenView.setSelected(false);
                mPaletteView.setMode(PaletteView.Mode.ERASER);
                break;
            case R.id.clear:
                mPaletteView.clear();
                break;
        }
    }

    private void showShapePopupWindow(View v) {
        SetShapePopupWindow mPop = new SetShapePopupWindow(this);
        mPop.setOnItemClickListener(this);

        View view = LayoutInflater.from(this).inflate(R.layout.shape_popupwindow, null);
        //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
        int[] location = new int[2];
        // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
        v.getLocationOnScreen(location);
        //  mPop.setAnimationStyle(R.style.mypopwindow_anim_style);  //设置动画
        //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
        mPop.showAtLocation(v, Gravity.TOP, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);

    }

    private void showSetupPopupWindow(View v) {
        SetColorSizePopupWindow mPop = new SetColorSizePopupWindow(this);
        mPop.setDrawSizeSeekBar(mPaletteView.getPenSize());
        mPop.setOnItemClickListener(this);
        mPop.setmOnSeekBarChangeListener(this);

        View view = LayoutInflater.from(this).inflate(R.layout.color_size_popupwindow, null);
        //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
        int[] location = new int[2];
        // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
        v.getLocationOnScreen(location);
        //  mPop.setAnimationStyle(R.style.mypopwindow_anim_style);  //设置动画
        //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
        mPop.showAtLocation(v, Gravity.TOP, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);

    }

    @Override
    public void setOnItemClick(View v) {

        switch (v.getId()) {
            case R.id.colorAccent:
                mPaletteView.setPenColor(Color.YELLOW);
//                MyApp.getApp().showToast("lin");
                break;
            case R.id.colorRed:
                mPaletteView.setPenColor(Color.RED);
//                MyApp.getApp().showToast("lin");
                break;
            case R.id.colorGreen:
                mPaletteView.setPenColor(Color.GREEN);
//                MyApp.getApp().showToast("lin");
                break;
            case R.id.colorBlue:
                mPaletteView.setPenColor(Color.BLUE);
//                MyApp.getApp().showToast("lin");
                break;
            case R.id.colorBlack:
                mPaletteView.setPenColor(Color.BLACK);
//                MyApp.getApp().showToast("lin");
                break;

            case R.id.line:
                mPaletteView.setShapeMode(PaletteView.ShapeMode.LINE);
                break;
            case R.id.circle:
                mPaletteView.setShapeMode(PaletteView.ShapeMode.CIRCLE);
//                MyApp.getApp().showToast("lin");
                break;
            case R.id.rectangle:
                mPaletteView.setShapeMode(PaletteView.ShapeMode.RECTANGLE);
//                MyApp.getApp().showToast("lin");
                break;
            case R.id.pen:
                mPaletteView.setShapeMode(PaletteView.ShapeMode.HANDWRITING);
//                MyApp.getApp().showToast("lin");
                break;
        }
    }

    @Override
    public void setOnChanging(SeekBar seekBar, int i, boolean b) {
        mPaletteView.setPenRawSize(i);
    }

}
