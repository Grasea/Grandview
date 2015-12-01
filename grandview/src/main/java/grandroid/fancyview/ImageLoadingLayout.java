/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import grandroid.action.ThreadAction;
import grandroid.image.ImageUtil;

/**
 *
 * @author Rovers
 */
public class ImageLoadingLayout extends LinearLayout {

    protected Bitmap bmp;

    public ImageLoadingLayout(Context context, final String imageURL) {
        this(context, imageURL, 0);
    }

    public ImageLoadingLayout(Context context, final String imageURL, final int maxLength) {
        super(context);
        this.setGravity(Gravity.CENTER);
        this.addView(new ProgressBar(context));
        new ThreadAction(context, 0) {

            @Override
            public boolean execute(Context cntxt) {
                bmp = null;
                try {
                    bmp = ImageUtil.loadBitmap(imageURL);
                    if (maxLength > 0) {
                        if (bmp.getWidth() > bmp.getHeight()) {
                            bmp = ImageUtil.resizeBitmap(bmp, maxLength / (float) bmp.getWidth());
                        } else {
                            bmp = ImageUtil.resizeBitmap(bmp, maxLength / (float) bmp.getHeight());
                        }
                    }

                    ((Activity) context).runOnUiThread(new Runnable() {

                        public void run() {
                            ImageLoadingLayout.this.removeAllViews();
                            if (bmp != null) {
                                ImageView iv = new ImageView(getContext());
                                iv.setImageBitmap(bmp);
                                bmp = null;
                                ImageLoadingLayout.this.addView(iv);
                            }
                        }
                    });
                } catch (Exception ex) {
                    Log.e("grandroid", null, ex);
                }
                return true;
            }
        };
    }
}
