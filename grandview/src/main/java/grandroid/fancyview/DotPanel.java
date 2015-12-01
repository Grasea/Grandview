/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 *
 * @author Rovers
 */
public class DotPanel extends LinearLayout {

    protected int selectImage;
    protected int unselectImage;
    protected int lastIndex = -1;

    public DotPanel(Context context, int selectImage, int unselectImage) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
        this.selectImage = selectImage;
        this.unselectImage = unselectImage;
    }

    public DotPanel bind(Gallery gallery) {

        removeAllViews();
        setPadding(4, 4, 4, 4);
        ImageView[] ivDot = new ImageView[gallery.getCount()];
        for (int i = 0; i < gallery.getCount(); i++) {
            ivDot[i] = new ImageView(getContext());
            if (i == gallery.getSelectedItemPosition()) {
                ivDot[i].setImageResource(selectImage);
            } else {
                ivDot[i].setImageResource(unselectImage);
            }
            ivDot[i].setPadding(4, 4, 4, 4);
            addView(ivDot[i], new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
        }
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int i, long arg3) {
                if (getChildCount() > i) {
                    ((ImageView) getChildAt(i)).setImageResource(selectImage);
                }
                if (lastIndex >= 0) {
                    ((ImageView) getChildAt(lastIndex)).setImageResource(unselectImage);
                }
                lastIndex = i;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                ((ImageView) getChildAt(lastIndex)).setImageResource(unselectImage);
            }
        });
        if (gallery.getSelectedItemPosition() < 0) {
            gallery.setSelection(0);
        }
        return this;
    }
}
