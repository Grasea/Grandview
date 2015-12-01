/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import grandroid.view.LayoutMaker;

/**
 *
 * @author 柏堯
 */
public class DropDownTextView<T extends BaseAdapter> extends TextView {

    protected PopupWindow popupWindow;
    protected T adapter;
    protected int xOff;
    protected int yOff;
    protected int windowWidth;
    protected int selection;
    protected LayoutMaker maker;
    protected AdapterView.OnItemClickListener onItemClickListener;
    protected boolean isDisplayble;
    protected int drawbleDesignWidth;
    protected int windowColor = -1;

    public DropDownTextView(Context context) {
        this(context, 640);
    }

    public DropDownTextView(Context context, int drawbleDesignWidth) {
        super(context);
        selection = -1;
        isDisplayble = true;
    }

    public void setAdapter(final T adapter) {
        this.adapter = adapter;
        if (adapter.getCount() > 0) {
            selection = 0;
        }
        this.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {
                if (isDisplayble) {
                    if (onShowList()) {
                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        LayoutMaker maker = new LayoutMaker(getContext(), layout, false);
                        maker.setDrawableDesignWidth((Activity) getContext(), drawbleDesignWidth);
                        if (windowColor != -1) {
                            maker.getLastLayout().setBackgroundColor(windowColor);
                        }
                        //maker.addListView(adapter, maker.layFF());
                        ListView lv = maker.addListView(adapter, maker.layFF());
                        onItemClickListener = lv.getOnItemClickListener();
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                                selection = i;
                                if (onClickItem(adapter.getItem(i))) {
                                    Log.i("grandroid", "select:" + getItemLabel(adapter.getItem(i)) + ",index:" + selection);
                                    DropDownTextView.this.setText(getItemLabel(adapter.getItem(i)));
                                    onItemClickListener.onItemClick(av, view, i, l);
                                }
                                popupWindow.dismiss();
                            }
                        });
                        int width = (int) maker.getMatrix().mapRadius(windowWidth);
                        popupWindow = new PopupWindow(layout, width == 0 ? view.getWidth() : width, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if (width > 0) {
                            xOff = (view.getWidth() - width) / 2;
                        }
                        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
                        popupWindow.setFocusable(true);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                            public void onDismiss() {
                                onDismissList();
                            }
                        });
                        popupWindow.showAsDropDown(view, xOff, yOff);
                        //popupWindow.showAtLocation(DropDownTextView.this, Gravity.CENTER, 0, 0);
                    }

                }
            }
        });
    }

    public void unselection() {
        this.selection = -1;
        this.setText("");
    }

    public int getSelection() {
        return selection;
    }
    
    public void setSelection(int position) {
        if (adapter != null) {
            if (position < adapter.getCount()) {
                selection = position;
                this.setText(getItemLabel(adapter.getItem(selection)));
            }
        }
    }

    public Object getSelectedItem() {
        if (adapter != null && selection > -1 && selection < adapter.getCount()) {
            return adapter.getItem(selection);
        }
        return null;
    }

    public <S> S getSelectedItem(Class<S> c) {
        //Log.i("grandroid", "getSelectItem:" + adapter.getItem(selection).toString() + "index : " + selection);
        if (adapter != null && selection > -1 && selection < adapter.getCount()) {
            return (S) adapter.getItem(selection);
        }
        return null;
    }

    public String getItemLabel(Object item) {
        return item.toString();
    }

    public void setXOff(int x) {
        this.xOff = x;
    }

    public void setYOff(int y) {
        this.yOff = y;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setWindowColor(int windowColor) {
        this.windowColor = windowColor;
    }

    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public boolean onShowList() {
        return true;
    }

    public void onDismissList() {

    }

    public boolean onClickItem(Object obj) {
        return true;
    }

    public void setDisplayable(boolean isDisplayble) {
        this.isDisplayble = isDisplayble;
    }

    public boolean isDisplayble() {
        return this.isDisplayble;
    }
}
