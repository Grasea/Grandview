/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview.combobox;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;
import grandroid.fancyview.AutoCompleteSuit;
import grandroid.json.JSONConverter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rovers
 */
public class ComboEdit extends AutoCompleteTextView {

    protected ArrayList<Item> options;

    public ComboEdit(Context context) {
        super(context);
        setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        setSingleLine();
        //setInputType(InputType.TYPE_NULL);
//        setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
//                showDropDown();
//            }
//        });
        addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
                showDropDown();
            }
        });
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ((AutoCompleteSuit) getAdapter()).onClickItem(arg2, arg1, options.get(arg2));
            }
        });

        //actv.setFocusable(false);
        //setFreezesText(true);
        setThreshold(1);
        options = new ArrayList<Item>();

        setAdapter(new AutoCompleteSuit<Item>(getContext(), options) {
            @Override
            public void onClickItem(int i, View view, Item t) {
                onSelectedItem(t);
            }

            @Override
            public String getLabel(Item item) {
                return item.getName();
            }

            @Override
            public void stylise(TextView tv) {
                ComboEdit.this.stylise(tv);
            }

            public boolean isMatch(CharSequence prefix, Item item) {
                if (prefix == null || prefix.length() == 0) {
                    return false;
                }
                if (item.getName().toLowerCase().startsWith(prefix.toString().toLowerCase())) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        stylise(this);
        //setText(options.get(0).getName());
    }

    public Item addItem(String text) {
        Item i = new Item(text);
        options.add(i);
        ((BaseAdapter) getAdapter()).notifyDataSetChanged();
//        if (options.size() == 1) {
//            setText(options.get(0).getName());
//            onSelectedItem(options.get(0));
//        }
        return i;
    }

    public void onSelectedItem(Item item) {
        Toast.makeText(getContext(), "you choose " + item.getName() + "\nyou should override onSelectedItem method.", Toast.LENGTH_SHORT).show();
    }

    public void clear() {
        options.clear();
        ((BaseAdapter) getAdapter()).notifyDataSetChanged();
    }

    public void stylise(TextView tv) {
    }
}
