/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview.floatingbutton;

import android.graphics.Color;
import android.graphics.Matrix;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 *
 * @author Rovers
 */
public class LinearLayoutArranger extends LayoutArranger {

    public LinearLayoutArranger(int majorLength) {
        super(majorLength);
    }

    public void calculatePosition(int btnIndex, int btnCount, CircleButton subButton, TextView label, Matrix m) {
        //Log.d("grandroid", i + "'s angel=" + angle);
        double r = m.mapRadius(majorLength);
        parent.setSubButtonRelativePosition(subButton, 0, -(int) r * (btnIndex + 1));
        if (label != null) {
            FrameLayout.LayoutParams fllp = (FrameLayout.LayoutParams) subButton.getLayoutParams();
            //Log.d("grandroid", "label " + subButton.getTag().toString() + "'s w=" + label.getWidth() + ", h=" + fllp.height);
            int offsetX = fllp.width / 2 + fllp.width / 4;
            int offsetY = -label.getHeight() / 2;
            parent.setSubButtonLabelRelativePosition(label, subButton, offsetX, offsetY);
        }

    }
}
