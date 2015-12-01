/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import grandroid.view.LayoutMaker;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rovers
 */
public class TabView extends LinearLayout {

    protected LinearLayout tabPanel;
    protected LinearLayout contentPanel;
    protected List<Tab> tabs;
    protected int index;
    protected Tab selectedTab;
    protected Matrix matrix;
    protected OnTabChangeListener tabChangeListener;
    protected boolean tabBarScrollable = true;

    public TabView(Context context) {
        this(context, LinearLayout.VERTICAL);
    }

    public TabView(Context context, int orientation) {
        super(context);

        this.setOrientation(orientation);
        this.setBackgroundColor(Color.WHITE);
        tabPanel = new LinearLayout(context);
        matrix = new Matrix();
        if (orientation == LinearLayout.VERTICAL) {
            tabPanel.setOrientation(LinearLayout.HORIZONTAL);
            tabPanel.setGravity(Gravity.LEFT | Gravity.BOTTOM);
            if (tabBarScrollable) {
                HorizontalScrollView sv = new HorizontalScrollView(context);
                sv.setScrollContainer(true);
                sv.setFocusable(true);
                sv.addView(tabPanel, new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));
                this.addView(sv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            } else {
                this.addView(tabPanel, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        } else {
            tabPanel.setOrientation(LinearLayout.VERTICAL);
            tabPanel.setGravity(Gravity.CENTER | Gravity.TOP);
            if (tabBarScrollable) {
                ScrollView sv = new ScrollView(context);
                sv.setScrollContainer(true);
                sv.setFocusable(true);
                sv.addView(tabPanel, new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT, ScrollView.LayoutParams.FILL_PARENT));
                this.addView(sv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));
            } else {
                this.addView(tabPanel, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));
            }
        }
        contentPanel = new LinearLayout(context);
        contentPanel.setOrientation(LinearLayout.VERTICAL);
        this.addView(contentPanel, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        index = -1;
        tabs = new ArrayList<Tab>();
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Button addTab(final Tab tab) {
        return addTab(tab, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public Button addTab(final Tab tab, int tabWidth, int tabHeight) {
        tab.setPosition(tabs.size());
        tabs.add(tab);
        Button btn = new Button(this.getContext());
        tabPanel.addView(btn, new LinearLayout.LayoutParams(tabWidth, tabHeight));
//        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//                tab.getTabButton().setChecked(changeTab(tab.position));
//            }
//        });
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (changeTab(tab.position)) {
                    tab.getTabButton().setBackgroundDrawable(new BitmapDrawable(loadResourceImage(tab.getTabDownImage())));
                }
            }
        });

        btn.setText(tab.label);
        tab.setTabButton(btn);
        tab.styliseButton(btn);
        if (tabs.size() == 1) {
            selectedTab = tab;
            index = 0;
            LayoutMaker lm = new LayoutMaker(this.getContext(), contentPanel, false);
            tab.createContent(lm);
            btn.setBackgroundDrawable(new BitmapDrawable(loadResourceImage(tab.getTabDownImage())));
        } else {
            btn.setBackgroundDrawable(new BitmapDrawable(loadResourceImage(tab.getTabUpImage())));
        }
        return btn;
    }

    public <T extends View> T addDecoration(T v) {
        return addDecoration(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
    }

    public <T extends View> T addDecoration(T v, LinearLayout.LayoutParams lp) {
        tabPanel.addView(v, lp);
        return v;
    }

    public boolean changeTab(int newIndex) {
        if (newIndex < tabs.size() && newIndex != index) {
            if (tabChangeListener != null) {
                if (tabChangeListener.onTabChange(selectedTab, tabs.get(newIndex))) {
                    selectedTab.getTabButton().setBackgroundDrawable(new BitmapDrawable(loadResourceImage(selectedTab.getTabUpImage())));
                    Tab tab = tabs.get(newIndex);
                    contentPanel.removeAllViews();
                    tab.createContent(new LayoutMaker(this.getContext(), contentPanel));
                    selectedTab = tab;
                    index = newIndex;
                    return true;
                } else {
                    return false;
                }
            } else {
                selectedTab.getTabButton().setBackgroundDrawable(new BitmapDrawable(loadResourceImage(selectedTab.getTabUpImage())));
                Tab tab = tabs.get(newIndex);
                contentPanel.removeAllViews();
                tab.createContent(new LayoutMaker(this.getContext(), contentPanel, false));
                selectedTab = tab;
                index = newIndex;
            }
            return true;
        } else {
            return false;
        }
    }

    public void refresh() {
        contentPanel.removeAllViews();
        selectedTab.createContent(new LayoutMaker(this.getContext(), contentPanel));
    }

    public int getTabCount() {
        return tabs.size();
    }

    public Tab getTab(int index) {
        return tabs.get(index);
    }

    public LinearLayout getContentPanel() {
        return contentPanel;
    }

    public LinearLayout getTabPanel() {
        return tabPanel;
    }

    public void setTabPanelBackgroundColor(int color) {
        if (tabBarScrollable) {
            ((ViewGroup) tabPanel.getParent()).setBackgroundColor(color);
        } else {
            tabPanel.setBackgroundColor(color);
        }
    }

    public void setTabPanelBackgroundResource(int res) {
        if (tabBarScrollable) {
            ((ViewGroup) tabPanel.getParent()).setBackgroundResource(res);
        } else {
            tabPanel.setBackgroundResource(res);
        }
    }

    public OnTabChangeListener getOnTabChangeListener() {
        return tabChangeListener;
    }

    public void setOnTabChangeListener(OnTabChangeListener tabChangeListener) {
        this.tabChangeListener = tabChangeListener;
    }

    public Bitmap loadResourceImage(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //獲取資源圖片 
        InputStream is = getContext().getResources().openRawResource(resId);
        Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);
        Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//        bmp.recycle();
        return bmp2;
    }

    public interface OnTabChangeListener {

        public boolean onTabChange(Tab oldTab, Tab newTab);
    }
}
