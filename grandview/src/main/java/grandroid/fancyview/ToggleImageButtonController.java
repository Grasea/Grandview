/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.widget.ImageView;
import grandroid.data.DataAgent;

/**
 *
 * @author Rovers
 */
public class ToggleImageButtonController extends ToggleButtonController<ImageView, Integer> {

    public ToggleImageButtonController() {
    }

    public ToggleImageButtonController(DataAgent da, String key) {
        super(da, key);
    }

    @Override
    public void setButtonDown(ImageView view, Integer valueDown) {
        view.setImageResource(valueDown);
    }

    @Override
    public void setButtonUp(ImageView view, Integer valueUp) {
        view.setImageResource(valueUp);
    }
}
