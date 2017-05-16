package geo;

import java.awt.BasicStroke;
import java.awt.Color;
//import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;

import datamodel.MappableItem;

public class LabeledRectangle<T extends MappableItem> implements Comparable<LabeledRectangle<T>>
{
	
	//private static final int 	FONT_SIZE 	= 14;
		
	private static Stroke 		stroke 		= new BasicStroke(2);
	private static BasicStroke	dashed		= new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,10.0f,
			new float[]{10.0f},0.0f);
	
	private List<ChangeListener> changeListener = new ArrayList<>();
		
	private int x;
	private int y;
	private int width;
	private int height;
	private T item;
		
	private Color baseColor;
	private Color transparentColor;
	private boolean isVisible = true;
	private boolean isSelected = false;
		
	public LabeledRectangle(int x, int y, int width, int height,T item ,Color baseColor)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.item = item;
		this.baseColor = baseColor;
		this.transparentColor = new Color(baseColor.getRed(),baseColor.getGreen(),baseColor.getBlue(),128);
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListener.add(listener);
	}
	
	public void removeChangeListener(ChangeListener listener)
	{
		changeListener.remove(listener);
	}
	
	public boolean isVisible()
	{
		return isVisible == true;
	}
	public void setVisible(boolean isVisible)
	{
		this.isVisible = isVisible;
	}
	public boolean isSelected()
	{
		return isSelected;
	}
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public T getItem()
	{
		return item;
	}
	
	public void filter(String term)
	{
		if(this.getItem().getName().matches("\\*(" + term + ").\\*") || term.equals(""))
		{
			this.setVisible(true);
		}
		else
		{
			this.setVisible(false);
			this.setSelected(false);
		}
	}
	
	//@Override
	public void paint(Graphics g)
	{
		if(!isVisible) return;
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(transparentColor);
		g2d.fillRect(x, y, width, height);
		if(isSelected)
		{
			g2d.setColor(Color.BLACK);
			g2d.setStroke(dashed);
		}
		else
		{
			g2d.setColor(baseColor);
			g2d.setStroke(stroke);
		}
		g2d.drawRect(x, y, width, height);
		g2d.setColor(Color.BLACK);
		
		//g2d.setFont(new Font("default",Font.PLAIN,FONT_SIZE));
		FontMetrics fm = g2d.getFontMetrics();
		
		Rectangle2D r = fm.getStringBounds(item.getName(), g2d);
		
		if(r.getWidth() >= width)
		{
			int breakPoint = 0;
			int index = nextBreakPoint(item.getName(),0);
			double midPoint = item.getName().length() / 2d;
			while(index != -1)
			{
				if(Math.abs(index - midPoint) < Math.abs(breakPoint - midPoint))
				{
					breakPoint = index;
				}
				index = nextBreakPoint(item.getName(), index + 1);
			}
			if(breakPoint == 0)
			{
				int textX = (this.getWidth() - (int) r.getWidth()) / 2;
				int textY = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
				g2d.drawString(item.getName(),x + textX,y + textY);
			}
			breakPoint++;
			String line1 = item.getName().substring(0, breakPoint);
			String line2 = item.getName().substring(breakPoint);
			r = fm.getStringBounds(line1, g2d);
			int textX = (this.getWidth() - (int) r.getWidth()) / 2;
			int textY = (this.getHeight() / 2 - (int) r.getHeight()) / 2 + fm.getAscent();
			g2d.drawString(line1, x + textX, y + textY);
			r = fm.getStringBounds(line2, g2d);
			textX = (this.getWidth() - (int) r.getWidth()) / 2;
			textY = (int) Math.round(this.getHeight() * 1.5 - (int) r.getHeight()) / 2 + fm.getAscent();
			g2d.drawString(line2, x + textX,y + textY);
		}
		else
		{
			int textX = (this.getWidth() - (int) r.getWidth()) / 2;
			int textY = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
			g2d.drawString(item.getName(),x + textX,y + textY);
		}
	}
	
	private int nextBreakPoint(String string, int start)
	{
		int index1 = string.indexOf(' ',start);
		int index2 = string.indexOf('_', start);
		if(index1 == -1)
		{
			return index2;
		}
		else if(index2 == -1)
		{
			return index1;
		}
		else
		{
			return Math.min(index1, index2);
		}
	}
	
	public boolean contains(Point point)
	{
		return (point.x >= x && point.x <= x + width && point.y >= y && point.y <= y + height);
	}
	
	public boolean contains(Point point, int xOffset, int yOffset )
	{
		Point p = new Point(point.x + xOffset, point.y + yOffset);
		return contains(p);
	}
	
	public boolean toggleSelected()
	{
		this.isSelected = !this.isSelected;
		return isSelected;
	}
	
	public void setLocation(int x, int y) 
	{
		this.x = x;
		this.y = y;
	}
	@Override
	public int compareTo(LabeledRectangle<T> o)
	{
		return this.getY() - o.getY();
	}
}