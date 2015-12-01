/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview.floatingbutton;

import android.graphics.Matrix;
import android.widget.TextView;
import grandroid.view.LayoutMaker;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Rovers
 */
public abstract class LayoutArranger {

    protected int majorLength;
    protected FloatingButtonView parent;

    public LayoutArranger(int majorLength) {
        this.majorLength = majorLength;
    }

    public FloatingButtonView getParent() {
        return parent;
    }

    public void setParent(FloatingButtonView parent) {
        this.parent = parent;
    }

    public int getMajorLength() {
        return majorLength;
    }

    public void setMajorLength(int majorLength) {
        this.majorLength = majorLength;
    }

    protected boolean isLengthScaled() {
        return true;
    }

    public final void calculatePosition(LayoutMaker maker, CircleButton mainButton, ArrayList<CircleButton> subButtons, ConcurrentHashMap<String, TextView> labels) {
        for (int i = 0; i < subButtons.size(); i++) {
            calculatePosition(i,subButtons.size(),subButtons.get(i),labels.get(subButtons.get(i).getTag().toString()),maker.getMatrix());
        }
    }
    
    public abstract void calculatePosition(int btnIndex, int btnCount, CircleButton subButton, TextView label,Matrix m);
}
