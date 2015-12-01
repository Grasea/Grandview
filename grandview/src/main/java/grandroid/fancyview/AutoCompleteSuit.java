/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import grandroid.adapter.ItemClickable;
import grandroid.adapter.ObjectAdapter;
import grandroid.database.Identifiable;
import grandroid.json.JSONConverter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rovers
 */
public class AutoCompleteSuit<T extends Identifiable> extends BaseAdapter implements ItemClickable<T>, Filterable {

    protected ObjectAdapter<T> adapter;
    protected List<T> data;
    protected List<String> labels;

    public AutoCompleteSuit(Context context, List<T> list) {
        this.adapter = new ObjectAdapter<T>(context, list) {
            @Override
            public View getView(int index, View view, ViewGroup parent) {
                if (view == null) {
                    view = createRowView(index, data.get(index));
                }
                fillRowView(index, view, data.get(index));
                return view;
            }

            @Override
            public View createRowView(int index, T item) {
                TextView tv = new TextView(context);
                tv.setTextSize(16);
                tv.setPadding(10, 10, 10, 10);
                stylise(tv);
                return tv;
            }

            @Override
            public void fillRowView(int index, View cellRenderer, T item) {
                ((TextView) cellRenderer).setText(getLabel(item));
            }
        };
        data = new ArrayList<T>();
        data.addAll(adapter.getList());
        labels = new ArrayList<String>();
        for (int i = 0; i < adapter.getList().size(); i++) {
            labels.add(getLabel(adapter.getList().get(i)));
        }
    }

    public void stylise(TextView tv) {
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int index) {
        return data.get(index);
    }

    public long getItemId(int index) {
        return data.get(index).get_id();
    }

    public View getView(int index, View view, ViewGroup vg) {
        return adapter.getView(index, view, vg);
    }

    public void onClickItem(int i, View view, T t) {
        adapter.onClickItem(i, view, t);
    }

    public void onLongPressItem(int i, View view, T t) {
        adapter.onLongPressItem(i, view, t);
    }

    public boolean isMatch(CharSequence prefix, T item) {
        if (prefix == null || prefix.length() == 0) {
            return false;
        }
        if (JSONConverter.fromObject(item).toString().contains(prefix)) {
            Log.d("grandroid", "JSONConverter.fromObject(item)=" + JSONConverter.fromObject(item).toString() + ", prefix=" + prefix);
            return true;
        } else {
            return false;
        }
    }

    public List<T> getFilterSource() {
        return adapter.getList();
    }

    public String getLabel(T item) {
        return item.toString();
    }

    public Filter getFilter() {
        return new Filter() {
            List<T> arr;

            @Override
            protected Filter.FilterResults performFiltering(CharSequence prefix) {

                Filter.FilterResults r = new Filter.FilterResults();
                //ArrayList<String> list = new ArrayList<String>();
                List<T> source = getFilterSource();
                arr = new ArrayList<T>();
                ArrayList<String> newLabels = new ArrayList<String>();
                if (prefix != null) {
                    //for (int i = 0; i < source.size(); i++) {
                    for (T obj : source) {
                        try {
                            if (isMatch(prefix, obj)) {
                                arr.add(obj);
                                newLabels.add(getLabel(obj));
                            }
                        } catch (Exception ex) {
                            Log.e("grandroid", null, ex);
                        }
                    }
                }
                r.count = newLabels.size();
                r.values = newLabels;
                return r;
            }

            @Override
            protected void publishResults(CharSequence arg0, Filter.FilterResults result) {
                data = arr;
                //(List<T>) result.values;
                Log.d("Tripwon", "data=" + data);
                notifyDataSetChanged();
            }
        };
    }
}
