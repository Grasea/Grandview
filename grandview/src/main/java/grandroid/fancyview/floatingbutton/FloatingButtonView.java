/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview.floatingbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import grandroid.view.LayoutMaker;
import grandroid.view.ViewDesigner;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Rovers
 */
public class FloatingButtonView extends LinearLayout {

    protected CircleButton mainButton;
    protected ArrayList<CircleButton> subButtons;
    protected ConcurrentHashMap<String, TextView> labels;
    protected FloatingButtonEventListener listener;
    protected LayoutMaker maker;
    protected FrameLayout frame;
    protected AnimatorSet animSet;
    protected int cx;
    protected int cy;
    protected boolean expanded;
    protected boolean lock;
    protected int maskColor;
    protected int buttonSize;
    protected boolean showLabel;
    protected LayoutArranger arranger;
    protected ViewDesigner designer;

    public final static int DEFAULT_PADDING = 20;

    public FloatingButtonView(Activity activity) {
        this(activity, 640, 110, 90);
    }
   public FloatingButtonView(Activity activity, int drawableDesignWidth, int mainButtonSize, int subButtonSize) {
       this(activity,drawableDesignWidth,mainButtonSize,subButtonSize,Gravity.RIGHT | Gravity.BOTTOM);
   }
    public FloatingButtonView(Activity activity, int drawableDesignWidth, int mainButtonSize, int subButtonSize,int gravity) {
        super(activity);
        maskColor = Color.argb(200, 100, 100, 100);
        buttonSize = subButtonSize;
        showLabel = true;
        maker = new LayoutMaker(activity, this, false);
        maker.setDrawableDesignWidth(activity, drawableDesignWidth);
        maker.getLastLayout().setBackgroundColor(Color.TRANSPARENT);
        mainButton = new CircleButton(activity, Color.rgb(233, 233, 255));
        frame = maker.addFrame(maker.layFF());
        maker.setScalablePadding(frame, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
        //frame.setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);

        maker.add(mainButton, maker.layFrameAbsolute(0, 0, mainButtonSize, mainButtonSize, gravity));
        mainButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if (listener != null) {
                    listener.onClick(mainButton, null);
                }
                if (!subButtons.isEmpty()) {
                    if (expanded) {
                        collapse();
                    } else {
                        expand();
                    }
                }
            }
        });
        subButtons = new ArrayList<CircleButton>();
        labels = new ConcurrentHashMap<String, TextView>();
        arranger = new ArcLayoutArranger(100);
        arranger.setParent(this);
    }

    public void setButtonPadding(int left, int top, int right, int bottom) {
        maker.setScalablePadding(frame, left, top, right, bottom);
    }

    public CircleButton getMainButton() {
        return mainButton;
    }

    public CircleButton addSubButton(String key) {
        return addSubButton(key, null);
    }

    public CircleButton addSubButton(final String key, String label) {
        CircleButton btn = new CircleButton(this.getContext(), Color.WHITE);
        btn.setTag(key);
        btn.setOnClickListener(new OnClickListener() {

            public void onClick(View btn) {
                if (listener != null) {
                    listener.onClick((CircleButton) btn, key);
                }
                collapse();
            }
        });
        subButtons.add(btn);
        btn.setVisibility(View.GONE);
        maker.add(btn, maker.getLastLayout().getChildCount() - 1, maker.layFrameAbsolute(0, 0, buttonSize, buttonSize));

        if (label != null) {
            TextView tv = maker.createStyledText(label).size(16).color(Color.WHITE).get();
            tv.setSingleLine();
            if (designer != null) {
                designer.stylise(tv);
            }
            maker.add(tv, maker.getLastLayout().getChildCount() - 1, maker.layFrameAbsolute(0, 0, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            tv.setVisibility(View.INVISIBLE);
            labels.put(key, tv);
        }

        return btn;
    }

    public void setListener(FloatingButtonEventListener listener) {
        this.listener = listener;
    }

    public LayoutArranger getArranger() {
        return arranger;
    }

    public void setArranger(LayoutArranger arranger) {
        arranger.setParent(this);
        this.arranger = arranger;
    }

    public ViewDesigner getDesigner() {
        return designer;
    }

    public void setDesigner(ViewDesigner designer) {
        this.designer = designer;
    }

    public void setSubButtonRelativePosition(CircleButton subButton, int offsetX, int offsetY) {
        FrameLayout.LayoutParams fllp = (FrameLayout.LayoutParams) subButton.getLayoutParams();
        int btnW = fllp.width;
        int btnH = fllp.height;
        fllp.setMargins(cx + offsetX - btnW / 2 - frame.getPaddingLeft(), cy + offsetY - btnH / 2 - frame.getPaddingTop(), 0, 0);
        logViewCenter(subButton);

    }

    public void setSubButtonLabelRelativePosition(TextView tv, CircleButton subButton, int offsetX, int offsetY) {
        FrameLayout.LayoutParams fllp = (FrameLayout.LayoutParams) subButton.getLayoutParams();
        FrameLayout.LayoutParams fllpLabel = (FrameLayout.LayoutParams) tv.getLayoutParams();
        fllpLabel.setMargins(fllp.leftMargin + fllp.width / 2 + offsetX, fllp.topMargin + fllp.height / 2 + offsetY, 0, 0);
    }

    @Deprecated
    public int getRadius() {
        return arranger.getMajorLength();
    }

    @Deprecated
    public void setRadius(int radius) {
        arranger.setMajorLength(radius);
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public void setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
    }

    public int getMaskColor() {
        return maskColor;
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }

    protected void calculatePosition() {
        cx = mainButton.getLeft() + (mainButton.getWidth()) / 2;
        cy = mainButton.getTop() + (mainButton.getHeight()) / 2;
        Log.d("grandroid", "cx=" + cx + ", cy=" + cy);
        logViewCenter(mainButton);
        arranger.calculatePosition(maker, mainButton, subButtons, labels);

    }

    private void logViewCenter(View v) {
        FrameLayout.LayoutParams fllp = (FrameLayout.LayoutParams) v.getLayoutParams();
        int x = fllp.leftMargin + fllp.width / 2;
        int y = fllp.topMargin + fllp.height / 2;
        Log.d("grandroid", "view " + v.toString() + ", cx=" + x + ", cy=" + y);
    }

    protected void prepareAnimations() {
        animSet = new AnimatorSet();
        for (int i = 0; i < subButtons.size(); i++) {
            FrameLayout.LayoutParams fllp = (FrameLayout.LayoutParams) subButtons.get(i).getLayoutParams();
            ObjectAnimator oax = new ObjectAnimator();
            oax.setDuration(300);
            oax.setTarget(subButtons.get(i));
            oax.setProperty(View.TRANSLATION_X);
            ObjectAnimator oay = new ObjectAnimator();
            oay.setDuration(300);
            oay.setTarget(subButtons.get(i));
            oay.setProperty(View.TRANSLATION_Y);
            if (expanded) {
                TextView tv = labels.get(subButtons.get(i).getTag().toString());
                if (tv != null) {
                    tv.setVisibility(View.INVISIBLE);
                }
                //收回
//                oax.setFloatValues(fllp.leftMargin + frame.getPaddingLeft(), cx - fllp.width / 2);
//                oay.setFloatValues(fllp.topMargin + frame.getPaddingTop(), cy - fllp.height / 2);
                Interpolator inter = new DecelerateInterpolator();
                oax.setInterpolator(inter);
                oay.setInterpolator(inter);
                oax.setFloatValues(0, cx - fllp.leftMargin - frame.getPaddingLeft() - fllp.width / 2);
                oay.setFloatValues(0, cy - fllp.topMargin - frame.getPaddingTop() - fllp.height / 2);
                animSet.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!expanded) {
                            for (int i = 0; i < subButtons.size(); i++) {
                                subButtons.get(i).setVisibility(View.GONE);
                            }
                        } else {
                            for (int i = 0; i < subButtons.size(); i++) {
                                subButtons.get(i).setVisibility(View.VISIBLE);
                                if (showLabel) {
                                    TextView tv = labels.get(subButtons.get(i).getTag().toString());
                                    if (tv != null) {
                                        tv.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                });
            } else {
                //展開
                //oax.setFloatValues(cx - fllp.width / 2, fllp.leftMargin + frame.getPaddingLeft());
                //oay.setFloatValues(cy - fllp.height / 2, fllp.topMargin + frame.getPaddingTop());
                Interpolator inter = new OvershootInterpolator(0.5f);
                oax.setInterpolator(inter);
                oay.setInterpolator(inter);
                oax.setFloatValues(cx - fllp.leftMargin - frame.getPaddingLeft() - fllp.width / 2, 0);
                oay.setFloatValues(cy - fllp.topMargin - frame.getPaddingTop() - fllp.height / 2, 0);
                subButtons.get(i).setVisibility(View.VISIBLE);
                animSet.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!expanded) {
                            for (int i = 0; i < subButtons.size(); i++) {
                                subButtons.get(i).setVisibility(View.GONE);
                            }
                        } else {
                            for (int i = 0; i < subButtons.size(); i++) {
                                subButtons.get(i).setVisibility(View.VISIBLE);
                                if (showLabel) {
                                    TextView tv = labels.get(subButtons.get(i).getTag().toString());
                                    if (tv != null) {
                                        tv.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                });
            }
            animSet.play(oax);
            animSet.play(oay);
        }
        animSet.start();
    }

    public void lock() {
        lock = true;
    }

    public void unlock() {
        lock = false;
    }

    public void expand() {
        if (!lock && !expanded) {
            frame.setBackgroundColor(maskColor);
            frame.setClickable(true);
            frame.setOnClickListener(new OnClickListener() {

                public void onClick(View arg0) {
                    collapse();
                }
            });
            calculatePosition();
            prepareAnimations();
            expanded = !expanded;
        }
    }

    public void collapse() {
        if (!lock && expanded) {
            frame.setBackgroundColor(Color.TRANSPARENT);
            frame.setOnClickListener(null);
            frame.setClickable(false);
            calculatePosition();
            prepareAnimations();
            expanded = !expanded;
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

}
