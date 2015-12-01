/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Rovers
 */
public class ImagePlayer {

    protected ImageView iv;
    protected int interval = 1000;//unit=ms
    protected int index;
    protected String resourceFormat;
    protected Timer timer;
    protected boolean changeBackground;

    public ImagePlayer(ImageView iv, String resourceFormat, int interval) {
        this(iv, resourceFormat, interval, false);
    }

    public ImagePlayer(ImageView iv, String resourceFormat, int interval, boolean changeBackground) {
        this.iv = iv;
        this.resourceFormat = resourceFormat;
        this.interval = interval;
        index = 1;
        this.changeBackground = changeBackground;
    }

    public void play() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(createTask(), 0, interval);
        }
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            //iv.setImageResource(0);
            //iv = null;
        }
    }

    public String getResourceFormat() {
        return resourceFormat;
    }

    public void setResourceFormat(String resourceFormat) {
        this.resourceFormat = resourceFormat;
    }
    
    public TimerTask createTask() {
        return new TimerTask() {

            @Override
            public void run() {
                ((Activity) iv.getContext()).runOnUiThread(new Runnable() {

                    public void run() {
                        if (iv != null) {
                            String uri = "drawable/" + String.format(resourceFormat, index++);
//                            Log.d("Starbucks", "play " + uri);
                            int imageResource = 0;
                            try {
                                imageResource = iv.getContext().getResources().getIdentifier(uri, null, iv.getContext().getPackageName());
                                if (imageResource == 0) {
                                    index = 1;
                                    uri = "drawable/" + String.format(resourceFormat, index++);
                                    imageResource = iv.getContext().getResources().getIdentifier(uri, null, iv.getContext().getPackageName());
                                }
                            } catch (Exception ex) {
                                Log.e("Starbucks", "can't found resource " + imageResource, ex);
                            }
                            if (changeBackground) {
                                iv.setBackgroundResource(imageResource);
                            } else {
                                iv.setImageResource(imageResource);
                            }
                        }
                    }
                });
            }
        };
    }
}
