package org.dalol.oldcirculardialview;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filippo on 8/7/2016.
 */
public class OldCircularDialView extends RelativeLayout implements View.OnTouchListener {

    private static final String TAG = OldCircularDialView.class.getSimpleName();

    private int dialSize;
    private Paint mPaint;
    private List<WheelView> wheelViews = new ArrayList<>();
    private List<Button> dialButtons = new ArrayList<>();
    private boolean created;
    private Paint defaultPaint;
    private View rotatoryView;
    private float intialRotationPos;

    private OnDialTouchListener listener;
    private PointF centerPoint;
    private int quarterSize;

    public OldCircularDialView(Context context) {
        super(context);
        initialise(context, null);
    }

    public OldCircularDialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context, attrs);
    }

    public OldCircularDialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OldCircularDialView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialise(context, attrs);
    }

    private void initialise(Context context, AttributeSet attrs) {
        verifyEditMode();
        setWillNotDraw(false);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);

        defaultPaint = new Paint();
        defaultPaint.setStyle(Paint.Style.FILL);
        defaultPaint.setAntiAlias(true);

        setBackgroundColor(Color.DKGRAY);
        setClipToPadding(false);
        setClipChildren(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(created) return;
        created = true;
        createBackground(getWidth(), getHeight());
        cover();
    }

    private void createBackground(int width, int height) {
        if(width > height) {
            dialSize = height;
        } else {
            dialSize = width;
        }

        int halfWidth =  Math.round(dialSize / 2);
        quarterSize = Math.round(dialSize / 4);

        int center = halfWidth;

        float originX = halfWidth;
        float originY = halfWidth;

        centerPoint = new PointF(originX, originY);

        float divDegree = 360/12;

        removeAllViews();

        int count = 0;




        for(int i = 4; i < 14; i++) {

            double degree = divDegree * i;

            degree -= 10;

            Log.d(TAG, "Degree -> " + degree);

            float radius = dialSize/12;

            float oldX1 = (float) (originX + (center - radius*(1.5)) * Math.sin(Math.toRadians(degree)));
            float oldY1 = (float) (originY + (center - radius*(1.5)) * Math.cos(Math.toRadians(degree)));




            WheelView wheelView = new WheelView(oldX1, oldY1, 40);
           // wheelViews.add(wheelView);



            int diameter = (int) (radius * 2);
            float top = wheelView.getY() - radius;
            float left = wheelView.getX() - radius;

            Button button = new Button(getContext());

            LayoutParams params = new LayoutParams(diameter, diameter);
            params.topMargin = (int) top;
            params.leftMargin = (int) left;
           /// button.setLayoutParams(params);
            button.setText(Integer.toString(count));
            button.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            button.setOnTouchListener(this);
            button.setFocusable(true);
            button.setFocusableInTouchMode(true);
            button.setClickable(true);

            button.setBackgroundResource(R.drawable.round_shape);

            dialButtons.add(button);

            //addView(button);
            addViewInLayout(button, count, params);

            count++;
        }


        FrameLayout frameLayout = new FrameLayout(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                verifyEditMode();

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                //paint.setStrokeCap(Paint.Cap.ROUND);
                int strokeWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                paint.setStrokeWidth(strokeWidth);
                paint.setColor(Color.YELLOW);
                paint.setStyle(Paint.Style.STROKE);

                canvas.drawCircle(quarterSize, quarterSize, quarterSize - (strokeWidth/2), paint);
            }
        };

        LayoutParams params = new LayoutParams(halfWidth, halfWidth);
        params.topMargin = quarterSize;
        params.leftMargin = quarterSize;
        frameLayout.setLayoutParams(params);

        frameLayout.setBackgroundResource(R.drawable.round_shape);
        //frameLayout.setBackgroundColor(Color.RED);

        ImageView imageView = new ImageView(getContext());

        Bitmap ddd = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        imageView.setImageBitmap(ddd);

        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.gravity = Gravity.CENTER;
        frameLayout.addView(imageView, params1);

        addView(frameLayout);
    }

    private void cover() {
        rotatoryView = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                verifyEditMode();
                float radius = dialSize/12;
                Bitmap bitmap = getClippedHolder(radius);
                canvas.drawBitmap(bitmap, 0,0, defaultPaint);
            }


            private Bitmap getClippedHolder(float radius) {
                Bitmap output = Bitmap.createBitmap(dialSize, dialSize, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

                paint.setAntiAlias(true);
                paint.setColor(Color.BLACK);

                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.blue_bg);

                canvas.drawBitmap(ImageHelper.getCircleBitmap(bm, dialSize), 0, 0, paint);

               // canvas.drawCircle(dialSize/2, dialSize/2, dialSize/2, paint);

                //canvas.drawRect(0, 0, dialSize, dialSize, paint);
                //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

                paint.setAlpha(0);
               // paint.setColor(Color.TRANSPARENT);
                paint.setStyle(Paint.Style.FILL);
                //paint.setStrokeWidth(60);
               // paint.setMaskFilter(null);


                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                for(int i = 0; i < dialButtons.size(); i++) {
                    Button button = dialButtons.get(i);
                    //wheelView.draw(canvas, mPaint);
                   // float radius = wheelView.getRadius();
                    float diameter = radius * 2;
                    //float top = wheelView.getY() - radius;
                   // float left = wheelView.getX() - radius;
                    canvas.drawCircle(button.getX()+ radius, button.getY() + radius, radius, paint);
//                    paint.setXfermode(null);
//                    paint.setStyle(Paint.Style.STROKE);
//                    paint.setStrokeWidth(10);
//                    paint.setStrokeCap(Paint.Cap.ROUND);
//                    canvas.drawCircle(button.getX()+ radius, button.getY() + radius, radius, paint);
                    //canvas.drawRect(button.getX()+ radius, top, left + diameter, top + diameter, mPaint);
                }

                canvas.drawCircle(dialSize/2, dialSize/2, quarterSize, paint);
                return output;
            }
        };
        ViewCompat.setElevation(rotatoryView, 15f);
        ViewCompat.setTranslationZ(rotatoryView, 15f);
        addViewInLayout(rotatoryView, -1, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //rotatoryView.bringToFront();
        intialRotationPos = rotatoryView.getRotation();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        verifyEditMode();


//        for(int i = 0; i < wheelViews.size(); i++) {
//            WheelView wheelView = wheelViews.get(i);
//            //wheelView.draw(canvas, mPaint);
//            float radius = wheelView.getRadius();
//            float diameter = radius * 2;
//            float top = wheelView.getY() - radius;
//            float left = wheelView.getX() - radius;
//            canvas.drawRect(left, top, left + diameter, top + diameter, mPaint);
//        }

//        if(getWidth() > getHeight()) {
//            dialSize = getHeight();
//        } else {
//            dialSize = getWidth();
//        }
        //canvas.drawRect(0, 0, dialSize, dialSize, mPaint);

       // canvas.drawCircle(dialSize/2, dialSize/2, dialSize/2, mPaint);
    }

    private void verifyEditMode() {
        if(isInEditMode()) {
            return;
        }
    }

    public void setListener(OnDialTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Button button = (Button) v;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (listener != null) {
                    listener.onTouch(button.getText().toString());
                }
                Log.d(TAG, "Button name -> " + button.getText());
                return true;
            case MotionEvent.ACTION_MOVE:
                rotatoryView.setPivotX(dialSize/2);
                rotatoryView.setPivotY(dialSize/2);
                //rotatoryView.setRotation(event.getRawX());

                updateValue(event.getRawX(), event.getRawY());

                Log.d(TAG, "Is inside circle -> " + isInsideCircle(event));
                Log.d(TAG, "Is inside circle -> " + isInsideCircle(event));

                return true;
            case MotionEvent.ACTION_UP:

                animateBack();


                return true;
            case MotionEvent.ACTION_HOVER_ENTER:
                Log.d(TAG, "ACTION -> Button ACTION_HOVER_ENTER -> " + button.getText());
                return true;
            case MotionEvent.ACTION_HOVER_EXIT:
                Log.d(TAG, "ACTION -> Button ACTION_HOVER_EXIT -> " + button.getText());
                return true;
            case MotionEvent.ACTION_HOVER_MOVE:
                Log.d(TAG, "ACTION -> Button ACTION_HOVER_MOVE -> " + button.getText());
                return true;
        }
        return true;
    }

    private void updateValue(float x, float y) {
        rotatoryView.setRotation(x);

        float angle = getAngleForPoint(x, y);
    }

    private float getAngleForPoint(float x, float y) {
        PointF c = getCenterPoint();

        double tx = x - c.x, ty = y - c.y;
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (x > c.x)
            angle = 360f - angle;

        angle = angle + 180;

        // neutralize overflow
        if (angle > 360f)
            angle = angle - 360f;
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent -> ACTION_DOWN " + event.getRawX());
                return true;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent -> ACTION_MOVE " + event.getRawX());
                return true;
        }

        return super.onTouchEvent(event);
    }

    private boolean isInsideCircle(MotionEvent event) {

        int center_x = dialSize / 2;
        int center_y = dialSize / 2;

        double sqrt = Math.sqrt(Math.pow(event.getRawX() - center_x, 2) + Math.pow(event.getRawY() - center_y, 2));
        return sqrt <= center_x;
    }

    private void animateBack() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rotatoryView, "rotation", rotatoryView.getRotation(), intialRotationPos);
        animator.setDuration(1000L);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    public PointF getCenterPoint() {
        return centerPoint;
    }


    public interface OnDialTouchListener {

        void onTouch(String number);
    }
}
