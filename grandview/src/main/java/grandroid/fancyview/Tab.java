/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.widget.Button;
import grandroid.view.LayoutMaker;

/**
 *
 * @author Rovers
 */
public abstract class Tab {

    int tabUpImage;
    int tabDownImage;
    int position;
    String label;
    Button tabButton;

    public Tab(int tabUpImage, int tabDownImage) {
        this.tabUpImage = tabUpImage;
        this.tabDownImage = tabDownImage;
    }

    public Tab(int tabUpImage, int tabDownImage, String label) {
        this.tabUpImage = tabUpImage;
        this.tabDownImage = tabDownImage;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Button getTabButton() {
        return tabButton;
    }

    public void setTabButton(Button tabButton) {
        this.tabButton = tabButton;
    }

    public int getTabDownImage() {
        return tabDownImage;
    }

    public int getTabUpImage() {
        return tabUpImage;
    }

//    public void styliseTabDown(Button tabButton) {
//        tabButton.setBackgroundResource(getTabDownImage());
//    }
//
//    public void styliseTabUp(Button tabButton) {
//        tabButton.setBackgroundResource(getTabUpImage());
//    }
    public abstract void createContent(LayoutMaker maker);

    public void styliseButton(Button btn) {
    }
}
