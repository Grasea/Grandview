/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview.floatingbutton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.View;
import grandroid.image.ImageUtil;

/**
 *
 * @author Rovers
 */
public class CircleButton extends View {

    protected int w;
    protected int h;
    protected int color;
    protected Bitmap bmp;

    public CircleButton(Context context) {
        super(context);
    }

    public CircleButton(Context context, int color) {
        super(context);
        this.color = color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setResource(int res) {
        setBitmap(BitmapFactory.decodeResource(this.getResources(), res));
    }

    public void setBitmap(Bitmap bitmap) {
//        bmp = Bitmap.createBitmap(bitmap.getWidth(),
//                bitmap.getHeight(), Config.ARGB_8888);
        //Canvas canvas = new Canvas(bmp);

        //final int color = 0xff424242;
        this.bmp = ImageUtil.circle(bitmap);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh); //To change body of generated methods, choose Tools | Templates.
        this.w = w;
        this.h = h;

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (bmp == null) {
            Paint _paintSimple = new Paint();
            _paintSimple.setAntiAlias(true);
            _paintSimple.setDither(true);
            _paintSimple.setColor(color);
            _paintSimple.setStrokeWidth(3f);
            _paintSimple.setStyle(Paint.Style.STROKE);
            _paintSimple.setStrokeJoin(Paint.Join.ROUND);
            _paintSimple.setStrokeCap(Paint.Cap.ROUND);
            _paintSimple.setStyle(Paint.Style.FILL);

            Paint _paintBlur = new Paint();
            _paintBlur.set(_paintSimple);
            _paintBlur.setColor(Color.argb(235, 150, 150, 150));
            _paintBlur.setStrokeWidth(30);
            _paintBlur.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.OUTER));
            canvas.drawCircle(w / 2, h / 2, Math.min(w / 2 - 30, h / 2 - 30), _paintBlur);
            canvas.drawCircle(w / 2, h / 2 - 10, Math.min(w / 2 - 30, h / 2 - 30) + 10, _paintSimple);
        } else {
            color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bmp.getWidth(),
                    bmp.getHeight());
            Rect dest = new Rect(0, 0, w, h);

            paint.setAntiAlias(true);
//            canvas.drawARGB(0, 0, 0, 0);
//            //paint.setColor(color);
////            canvas.drawCircle(bitmap.getWidth() / 2,
////                    bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
//            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bmp, rect, dest, paint);

        }
    }
}
