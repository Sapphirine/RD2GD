package geo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import datamodel.ItemToItemMap;
import datamodel.MappableItem;

public class Arrow<T extends MappableItem> //extends JComponent
{

	//private static final long serialVersionUID = -6643662712117938784L;

	public enum HighlightStatus 
	{
		IS_SELECTED (new Color(204, 255, 204, 192)),
		BOTH_SELECTED (new Color(255, 255, 0, 192)),
		SOURCE_SELECTED (new Color(255, 128, 0, 192)),
		TARGET_SELECTED (new Color(0, 0, 255, 192)),
		NONE_SELECTED (new Color(128, 128, 128, 192)),
		IS_COMPLETED (new Color(128, 128, 128, 50));
		
		private final Color color;
		
		HighlightStatus(Color color) {
			this.color = color;
		}
	}
	
	public static final float			THICKNESS		= 5;
	public static final int				HEAD_THICKNESS	= 15;
	private static Color				color	    	= HighlightStatus.NONE_SELECTED.color;
	private static BasicStroke			dashed			= new BasicStroke(2.0f, BasicStroke.CAP_BUTT, 
																BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.f }, 0.0f);
	private int							x1;
	private int							y1;
	private int							x2;
	private int							y2;
	private LabeledRectangle<T>			source;
	private LabeledRectangle<T>			target;
	private ItemToItemMap<T>			itemToItemMap;

	private int							width;
	private int							height;

	private Polygon						polygon;

	private boolean						isSelected		= false;
	private boolean						isVisible		= true;
	
	public Arrow(LabeledRectangle<T> source) 
	{
		this.source = source;
	}
	
	public Arrow(int x1, int y1, int x2, int y2) 
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.width = Math.abs(x1 - x2);
		this.height = Math.abs(y1 - y2);
	}

	public Arrow(LabeledRectangle<T> source, LabeledRectangle<T> target) {
		this.source = source;
		this.target = target;
		this.itemToItemMap = new ItemToItemMap<T>(source.getItem(), target.getItem());
	}
	public Arrow(LabeledRectangle<T> source, LabeledRectangle<T> target, ItemToItemMap<T> itemToItemMap) {
		this.source = source;
		this.target = target;	
		this.itemToItemMap = itemToItemMap;
	}

	public ItemToItemMap<T> getItemToItemMap() {
		return itemToItemMap;
	}
	
	public void setItemToItemMap(ItemToItemMap<T> itemToItemMap) {
		this.itemToItemMap = itemToItemMap;
	}
	
	public void setItemToItemMap(T relationalItem, T graphItem)
	{
		this.itemToItemMap = new ItemToItemMap<>(relationalItem, graphItem);
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isVisible(){
		return isVisible;
	}
	public LabeledRectangle<T> getSource() {
		return source;
	}

	public void setTargetPoint(Point point) {
		if (point == null) {
			x2 = source.getX() + source.getWidth() + Arrow.HEAD_THICKNESS;
			y2 = source.getY() + source.getHeight() / 2;
		} else {
			x2 = point.x;
			y2 = point.y;
		}
	}
	//@Override
	public void paint(Graphics g) {
		if (!isVisible)
			return;
		
		if( source != null && target != null){
			if(!source.isVisible() || !target.isVisible()){
				return;
			}
		}
		Graphics2D g2d = (Graphics2D) g;

		if (source != null) {
			x1 = source.getX() + source.getWidth();
			y1 = source.getY() + source.getHeight() / 2;
			width = Math.abs(x1 - x2);
			height = Math.abs(y1 - y2);
		}
		if (target != null) {
			x2 = target.getX();
			y2 = target.getY() + target.getHeight() / 2;
			width = Math.abs(x1 - x2);
			height = Math.abs(y1 - y2);
		}
		int nPoints = 25;
		int[] xPoints = new int[nPoints * 2 + 3];
		int[] yPoints = new int[nPoints * 2 + 3];
		float widthMinHead = getWidth() - HEAD_THICKNESS;
		float stepSize = widthMinHead / (float) (nPoints - 1);

		for (int i = 0; i < nPoints; i++) {
			float x = x1 + stepSize * i;
			float y = (float) (y1 + (Math.cos(Math.PI * i / (float) nPoints) / 2d - 0.5) * (y1 - y2));
			xPoints[i] = Math.round(x);
			yPoints[i] = Math.round(y - THICKNESS);
			xPoints[nPoints * 2 + 3 - i - 1] = Math.round(x);
			yPoints[nPoints * 2 + 3 - i - 1] = Math.round(y + THICKNESS);
		}
		xPoints[nPoints] = x2 - HEAD_THICKNESS;
		yPoints[nPoints] = y2 - HEAD_THICKNESS;
		xPoints[nPoints + 1] = x2;
		yPoints[nPoints + 1] = y2;
		xPoints[nPoints + 2] = x2 - HEAD_THICKNESS;
		yPoints[nPoints + 2] = y2 + HEAD_THICKNESS;
		polygon = new Polygon(xPoints, yPoints, nPoints * 2 + 3);

		g2d.setColor(fillColor());
		g2d.fillPolygon(polygon);

		if (isSelected) {
			g2d.setColor(Color.BLACK);
			g2d.setStroke(dashed);
			g2d.drawPolygon(polygon);
		}
	}
	public Color fillColor() {
		return getHighlightStatus().color;
	}

	private boolean isTargetSelected() {
		return target != null && target.isSelected();
	}

	private boolean isSourceSelected() {
		return source != null && source.isSelected();
	}
	
	public void setTarget(LabeledRectangle<T> target) {
		this.target = target;
	}

	public LabeledRectangle<T> getTarget() {
		return target;
	}

	public HighlightStatus getHighlightStatus() {
		if (isSelected()) {
			return HighlightStatus.IS_SELECTED;
		} else if (isSourceSelected() && isTargetSelected()) {
			return HighlightStatus.BOTH_SELECTED;
		} else if (isSourceSelected()) {
			return HighlightStatus.SOURCE_SELECTED;
		} else if (isTargetSelected()) {
			return HighlightStatus.TARGET_SELECTED;
		} else if (isCompleted()) {
			return HighlightStatus.IS_COMPLETED;
		} else {
			return HighlightStatus.NONE_SELECTED;
		}
	}

	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isCompleted() {
		if (getItemToItemMap() != null) {
			return getItemToItemMap().isCompleted();
		}
		else {
			return false;
		}
	}
	
	public boolean contains(Point point) {
		return polygon.contains(point);
	}

	public void setVisible(boolean value) {
		isVisible = value;
	}
	
	public boolean isSourceAndTargetVisible(){
		return source.isVisible() && target.isVisible();
	}
	
	public boolean isConnected(){
		return source != null && target != null;
	}
	
	public static void drawArrowHead(Graphics2D g2d, int x, int y) {
		int nPoints = 3;
		int[] xPoints = new int[nPoints];
		int[] yPoints = new int[nPoints];
		xPoints[0] = x - HEAD_THICKNESS;
		yPoints[0] = y - HEAD_THICKNESS;
		xPoints[1] = x;
		yPoints[1] = y;
		xPoints[2] = x - HEAD_THICKNESS;
		yPoints[2] = y + HEAD_THICKNESS;
		g2d.setColor(color);
		g2d.fillPolygon(xPoints, yPoints, nPoints);
	}

}
