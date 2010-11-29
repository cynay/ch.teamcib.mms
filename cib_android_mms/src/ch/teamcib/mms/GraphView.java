package ch.teamcib.mms;

/**
 *  This class was copied from http://android.arnodenhond.com/components/graphview
 *  
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;
import android.widget.Button;

/**
 * GraphView creates a scaled line or bar graph with x and y axis labels. 
 * @author Arno den Hond
 *
 */
public class GraphView extends View {

	public static boolean BAR = true;
	public static boolean LINE = false;

	private Paint paint;
	private float[] values;
	private String[] horlabels;
	private String[] verlabels;
	private String title;
	private boolean type;

	public GraphView(Context context, float[] values, String title, String[] horlabels, int maxGB, boolean type) {
		super(context);
		if (values == null)
			values = new float[0];
		else
			this.values = values;
		if (title == null)
			title = "";
		else
			this.title = title;
		if (horlabels == null)
			this.horlabels = new String[0];
		else
			this.horlabels = horlabels;
		if (maxGB == 0)
			this.verlabels = setVerlabels(4);
		else
			this.verlabels = setVerlabels(maxGB);
		this.type = type;
		paint = new Paint();
	}
	
	private String[] setVerlabels(int maxGB)
	{
		int GB =  maxGB;
		String[] verlabels = new String[ maxGB +1 ];
		for(int i = 0; i <= maxGB ;i++)
		{
			verlabels[i] = GB+" GB";
			GB--;	
		}
			
		return verlabels;
	}

	
	@Override
	protected void onDraw(Canvas canvas) {
		float border = 20;
		float horstart = border * 2;
		float height = getHeight();
		float width = getWidth() - 1;
//		float max = getMax();
//		float min = getMin();
		
		float max = 4;
		float min = 0;
		
		float diff = max - min;
		float graphheight = height - (2 * border);
		float graphwidth = width - (2 * border);

		paint.setTextAlign(Align.LEFT);
		int vers = verlabels.length - 1;
		for (int i = 0; i < verlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			float y = ((graphheight / vers) * i) + border;
			canvas.drawLine(horstart, y, width, y, paint);
			paint.setColor(Color.argb(255, 69, 139, 116));
			canvas.drawText(verlabels[i], 0, y, paint);
		}
		int hors = horlabels.length - 1;
		for (int i = 0; i < horlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			float x = ((graphwidth / hors) * i) + horstart;
			canvas.drawLine(x, height - border, x, border, paint);
			paint.setTextAlign(Align.CENTER);
			if (i==horlabels.length-1)
				paint.setTextAlign(Align.RIGHT);
			if (i==0)
				paint.setTextAlign(Align.LEFT);
			paint.setColor(Color.argb(255, 69, 139, 116));
			canvas.drawText(horlabels[i], x, height - 4, paint);
		}

		paint.setTextAlign(Align.CENTER);
		canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

		if (max != min) {
			paint.setColor(Color.GREEN);
			if (type == BAR) {
				float datalength = values.length;
				float colwidth = (width - (2 * border)) / datalength;
				for (int i = 0; i < values.length; i++) {
					float val = values[i] - min;
					float rat = val / diff;
					float h = graphheight * rat;
					canvas.drawRect((i * colwidth) + horstart, (border - h) + graphheight, ((i * colwidth) + horstart) + (colwidth - 1), height - (border - 1), paint);
				}
			} else {
				float datalength = values.length;
				float colwidth = (width - (2 * border)) / datalength;
				float halfcol = colwidth / 2;
				float lasth = 0;
				for (int i = 0; i < values.length; i++) {
					float val = values[i] - min;
					float rat = val / diff;
					float h = graphheight * rat;
					if (i > 0)
						canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
					lasth = h;
				}
			}
		}
	}

	private float getMax() {
		float largest = Integer.MIN_VALUE;
		for (int i = 0; i < values.length; i++)
			if (values[i] > largest)
				largest = values[i];
		return largest;
	}
	
//	/**
//	 * Set the Maximum
//	 * @param largest
//	 * @return
//	 */
//	private float setMax(float largest) {
//		float setlargest = largest;
//		
//		return setlargest;
//	}

	private float getMin() {
		float smallest = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; i++)
			if (values[i] < smallest)
				smallest = values[i];
		return smallest;
	}
	
//	
//	/**
//	 * Set minimum for the canvas
//	 * @return
//	 */
//	private float setMin() {
//		float setsmallest = 0;
//		return setsmallest;
//	}
	
	

}
