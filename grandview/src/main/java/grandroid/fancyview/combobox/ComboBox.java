/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview.combobox;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;
import grandroid.fancyview.AutoCompleteSuit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rovers
 */
public class ComboBox extends AutoCompleteTextView {

    protected ArrayList<Item> options;

    public ComboBox(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        setSingleLine();
        setInputType(InputType.TYPE_NULL);
        setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                showDropDown();
            }
        });
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ((AutoCompleteSuit) getAdapter()).onClickItem(arg2, arg1, options.get(arg2));
            }
        });

        //actv.setFocusable(false);
        setFreezesText(true);
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
                ComboBox.this.stylise(tv);
            }

            @Override
            public Filter getFilter() {
                return new Filter() {
                    List<Item> arr;

                    @Override
                    protected Filter.FilterResults performFiltering(CharSequence prefix) {

                        Filter.FilterResults r = new Filter.FilterResults();
                        //ArrayList<String> list = new ArrayList<String>();
                        arr = getFilterSource();
                        ArrayList<String> newLabels = new ArrayList<String>();
                        for (Item obj : arr) {
                            newLabels.add(getLabel(obj));
                        }
                        r.count = newLabels.size();
                        r.values = newLabels;
                        return r;
                    }

                    @Override
                    protected void publishResults(CharSequence arg0, Filter.FilterResults result) {
                        data = arr;
                        notifyDataSetChanged();
                    }
                };
            }
        });
        stylise(this);
        //setText(options.get(0).getName());
    }

    public Item addItem(String text) {
        Item i = new Item(text);
        options.add(i);
        ((BaseAdapter) getAdapter()).notifyDataSetChanged();
        if (options.size() == 1) {
            setText(options.get(0).getName());
            onSelectedItem(options.get(0));
        }
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
