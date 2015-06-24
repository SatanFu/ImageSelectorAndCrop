package com.satan.imageselectorandcrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ClipView extends View
{
	public ClipView(Context context)
	{
		super(context);
	}

	public ClipView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}


	public ClipView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		/*这里就是绘制矩形区域*/
		int width = this.getWidth();
		int height = this.getHeight();
		
		Paint paint = new Paint();
		paint.setColor(0xaa000000);

		//top
		canvas.drawRect(0, 0, width, height/3, paint);
		//left
		canvas.drawRect(0, height/3, (width - height/3)/2, height*2/3, paint);
		//right
		canvas.drawRect((width + height/3)/2, height/3, width ,  height*2/3, paint);
		//bottom
		canvas.drawRect(0, height*2/3, width, height, paint);
	}
	
	
	
}
