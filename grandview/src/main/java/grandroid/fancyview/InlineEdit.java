/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * @author Rovers
 */
public abstract class InlineEdit extends LinearLayout {

    private TextView et;
    private View btn;
    private String hint;
    private int textColor;
    private View.OnClickListener listener = new OnClickListener() {

        public void onClick(View arg0) {
            changeEditMode();
        }
    };

    public InlineEdit(Context context, String text) {
        this(context, text, Color.BLACK);
    }

    public InlineEdit(Context context, String text, int textColor) {
        this(context, text, textColor, createButton(context), null);
    }

    public InlineEdit(Context context, String text, int textColor, String hint) {
        this(context, text, textColor, createButton(context), hint);
    }

    public InlineEdit(Context context, String text, int textColor, View btn, String hint) {
        super(context);
        //setBackgroundResource(R.drawable.block_pic);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(8, 0, 8, 0);
        this.textColor = textColor;
        this.et = new TextView(context);
        et.setTextColor(textColor);
        et.setTextSize(14);
        if (hint != null && (text == null || text.equals(""))) {
            et.setText("點擊輸入" + hint);
        } else {
            et.setText(text);
        }
        this.hint = hint;
        this.btn = btn;
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                onChange(InlineEdit.this.et.getText().toString());
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et.getWindowToken(), 0);
                changeTextMode();
            }
        });
        this.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        this.addView(btn, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
        et.setOnClickListener(listener);
        btn.setVisibility(View.GONE);
    }

    protected static Button createButton(Context context) {
        Button btnOk = new Button(context);
        btnOk.setText("確定");
        btnOk.setTextColor(Color.WHITE);
        //btnOk.setBackgroundResource(R.drawable.btn_dialog);
        btnOk.setTextSize(14);
        btnOk.setPadding(6, 6, 6, 6);
        return btnOk;
    }

    public View getButton() {
        return btn;
    }

    public TextView getCurrentTextView() {
        return et;
    }

    public void changeEditMode() {
        if (!(this.getChildAt(0) instanceof EditText)) {
            String text = ((TextView) this.getChildAt(0)).getText().toString();
            this.removeViewAt(0);
            this.et = new EditText(getContext());
            et.setText(text.equals("點擊輸入" + hint) ? "" : text);
            et.setTextSize(14);
            et.setHint(hint);
            et.setTextColor(textColor);
            et.setBackgroundDrawable(null);
            et.setPadding(0, 0, 0, 0);
            this.addView(et, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            et.requestFocus();
        }
        //this.setEnabled(true);
        btn.setVisibility(View.VISIBLE);

        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(et, InputMethodManager.SHOW_FORCED);
    }

    public void changeTextMode() {
        if (this.getChildAt(0) instanceof EditText) {
            String text = ((EditText) this.getChildAt(0)).getText().toString();
            this.removeViewAt(0);
            this.et = new TextView(getContext());
            et.setTextColor(textColor);
            et.setTextSize(14);
            et.setText(text.length() == 0 ? "點擊輸入" + hint : text);
            this.addView(et, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            et.setOnClickListener(listener);
        }
        btn.setVisibility(View.GONE);
        //this.setEnabled(false);
    }

    public abstract void onChange(String text);
}
