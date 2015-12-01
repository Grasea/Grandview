/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.view.View;
import grandroid.data.DataAgent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Rovers
 */
public abstract class ToggleButtonController<T extends View, V> {

    protected ArrayList<T> buttons;
    protected String key;
    protected DataAgent da;
    protected String currentValue;
    protected String unselectValue;

    /**
     * 決定是否允許所有按鈕都不被按下，此變數在增加第2顆按鈕時，會自動設為false (只有一顆按鈕時，效果就像CheckBox)
     */
    protected boolean allowUnselect = true;
    protected int defaultIndex = -1;
    protected boolean lock;
    protected HashMap<T, V> imagesUp;
    protected HashMap<T, V> imagesDown;

    public ToggleButtonController() {
        this(null, null);
    }

    public ToggleButtonController(DataAgent da, String key) {
        this.key = key;
        this.da = da;
        currentValue = "";
        unselectValue = "";
        buttons = new ArrayList<T>();
        imagesUp = new HashMap<T, V>();
        imagesDown = new HashMap<T, V>();
    }

    public ToggleButtonController addButton(T btn, String value, V dataUp, V dataDown) {
        return addButton(btn, value, dataUp, dataDown, false);
    }

    public ToggleButtonController addButton(T btn, String value, V dataUp, V dataDown, boolean asDefault) {
        return addButton(btn, btn, value, dataUp, dataDown, asDefault);
    }

    public ToggleButtonController addButton(View clickedView, final T btn, String value, V dataUp, V dataDown, boolean asDefault) {
        btn.setTag(value);
        buttons.add(btn);
        imagesUp.put(btn, dataUp);
        imagesDown.put(btn, dataDown);
        if (buttons.size() > 1) {
            allowUnselect = false;
        }
        if (asDefault) {
            defaultIndex = buttons.size() - 1;
        }
        if (da == null || !da.getPreferences().contains(key)) {
            if (asDefault) {
                if (da != null) {
                    da.putPreference(key, value);
                }
                setButtonDown(btn, dataDown);
                currentValue = value;
            } else {
                setButtonUp(btn, dataUp);
            }
        } else {
            if (da.getPreference(key).equals(value)) {
                setButtonDown(btn, dataDown);
                currentValue = value;
            } else {
                setButtonUp(btn, dataUp);
            }
        }
        clickedView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!lock) {
                    if (allowUnselect) {
                        T oldButton = null, newButton = null;

                        for (int i = 0; i < buttons.size(); i++) {
                            if (!buttons.get(i).equals(btn)) {
                                if (buttons.get(i).getTag().toString().equals(currentValue)) {
                                    oldButton = buttons.get(i);
                                }
                                setButtonUp(buttons.get(i), imagesUp.get(buttons.get(i)));
                            } else {
                                if (currentValue.equals(btn.getTag().toString())) {
                                    oldButton = buttons.get(i);
                                    setButtonUp(buttons.get(i), imagesUp.get(buttons.get(i)));
                                    currentValue = unselectValue;
                                } else {
                                    newButton = buttons.get(i);
                                    setButtonDown(buttons.get(i), imagesDown.get(buttons.get(i)));
                                    currentValue = btn.getTag().toString();
                                }
                            }
                        }
                        if (da != null) {
                            da.putPreference(key, currentValue);
                        }
                        if (oldButton != null) {
                            onUnselectButton(oldButton, oldButton.getTag().toString());
                        }
                        if (newButton != null) {
                            onSelectButton(newButton, newButton.getTag().toString());
                        }
                    } else {
                        if (!currentValue.equals(btn.getTag().toString())) {
                            currentValue = btn.getTag().toString();
                            for (int i = 0; i < buttons.size(); i++) {
                                if (!buttons.get(i).equals(btn)) {
                                    setButtonUp(buttons.get(i), imagesUp.get(buttons.get(i)));
                                } else {
                                    setButtonDown(buttons.get(i), imagesDown.get(buttons.get(i)));
                                }
                            }
                            if (da != null) {
                                da.putPreference(key, currentValue);
                            }
                            onSelectButton((T) btn, btn.getTag().toString());
                        }
                    }
                }
            }
        });
        return this;
    }

    public String getUnselectValue() {
        return unselectValue;
    }

    public void setUnselectValue(String unselectValue) {
        if (buttons.isEmpty() || (currentValue != null && currentValue.equals(this.unselectValue))) {
            currentValue = unselectValue;
        }
        this.unselectValue = unselectValue;
    }

    public String getValue() {
        return currentValue;
    }

    /**
     * 尋找按鈕群組裡 值=newValue的按鈕，設置其為按下狀態
     * performEvent會傳false，所以不會執行onSelectButton、onUnselectButton事件
     *
     * @param newValue
     * @return
     */
    public boolean setValue(String newValue) {
        return setValue(newValue, false);
    }

    public boolean setValue(String newValue, boolean performEvent) {
        int originSelected = -1;
        boolean setValueSuccess = false;
        if (newValue != null) {
            String oldValue = this.currentValue;
            for (int i = 0; i < buttons.size(); i++) {
                if (buttons.get(i).getTag().equals(newValue)) {
                    setButtonDown(buttons.get(i), imagesDown.get(buttons.get(i)));
                    this.currentValue = newValue;
                    setValueSuccess = true;
                    if (da != null) {
                        da.putPreference(key, newValue);
                    }
                    if (performEvent) {
                        onSelectButton(buttons.get(i), currentValue);
                    }
                } else if (buttons.get(i).getTag().equals(oldValue)) {
                    originSelected = i;
                }
                //Log.d("grandroid", "setValueSuccess=" + setValueSuccess + ", originSelected=" + originSelected);
            }
            if (setValueSuccess && originSelected > -1) {
                setButtonUp(buttons.get(originSelected), imagesUp.get(buttons.get(originSelected)));
                if (performEvent) {
                    onUnselectButton(buttons.get(originSelected), oldValue);
                }
            }
        }
        return setValueSuccess;
    }

    public boolean isSelected() {
        return !unselectValue.equals(currentValue);
    }

    public void reset() {
        T defaultButton = defaultIndex > -1 ? buttons.get(defaultIndex) : null;
        for (int i = 0; i < buttons.size(); i++) {
            if (i != defaultIndex) {
                setButtonUp(buttons.get(i), imagesUp.get(buttons.get(i)));
            } else {
                setButtonDown(buttons.get(i), imagesDown.get(buttons.get(i)));
            }
        }
        if (defaultButton != null) {
            currentValue = defaultButton.getTag().toString();
        } else {
            currentValue = unselectValue;
        }
        if (da != null) {
            da.putPreference(key, currentValue);
        }
    }

    public boolean setLock(boolean lock) {
        return this.lock = lock;
    }

    public boolean isLock() {
        return lock;
    }

    public void onSelectButton(T button, String value) {
    }

    public void onUnselectButton(T button, String value) {
    }

    public abstract void setButtonDown(T view, V valueDown);

    public abstract void setButtonUp(T view, V valueUp);
}
