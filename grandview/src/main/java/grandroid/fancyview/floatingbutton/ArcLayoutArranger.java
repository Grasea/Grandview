/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview.floatingbutton;

import android.graphics.Matrix;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 *
 * @author Rovers
 */
public class ArcLayoutArranger extends LayoutArranger {

    protected int startAngle = 270;
    protected int totalAngle = -90;

    public ArcLayoutArranger(int majorLength) {
        super(majorLength);
    }

    public void calculatePosition(int btnIndex, int btnCount, CircleButton subButton, TextView label, Matrix m) {
        int angle = startAngle + (btnCount == 0 ? 0 : btnIndex * totalAngle / (btnCount - 1));
        //Log.d("grandroid", i + "'s angel=" + angle);
        double r = m.mapRadius(majorLength);
        parent.setSubButtonRelativePosition(subButton, (int) (r * Math.cos(angle / 180d * Math.PI)), (int) (r * Math.sin(angle / 180d * Math.PI)));
        if (label != null) {
            FrameLayout.LayoutParams fllp = (FrameLayout.LayoutParams) subButton.getLayoutParams();
            //Log.d("grandroid", "label " + subButton.getTag().toString() + "'s w=" + label.getWidth() + ", h=" + fllp.height);
            int offsetX = (int) ((fllp.width / 2) * Math.cos(angle / 180d * Math.PI)) - label.getWidth();
            int offsetY = (int) ((fllp.height / 2) * Math.sin(angle / 180d * Math.PI))- label.getHeight();
            parent.setSubButtonLabelRelativePosition(label, subButton, offsetX, offsetY);
        }

    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getTotalAngle() {
        return totalAngle;
    }

    public void setTotalAngle(int totalAngle) {
        this.totalAngle = totalAngle;
    }

}
