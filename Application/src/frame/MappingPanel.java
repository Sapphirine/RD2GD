package frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import datamodel.ETL;
import datamodel.Field;
//import datamodel.ETL;
import datamodel.MappableItem;
import datamodel.Mapping;
import geo.Arrow;
import geo.Arrow.HighlightStatus;
import geo.LabeledRectangle;
import datamodel.ResizeListener;
import datamodel.Table;

public class MappingPanel<T extends MappableItem> extends JPanel
{

	private static final long 			serialVersionUID 			= 981897645400910679L;
	private static final int			ITEM_HEIGHT 				= 50;
	private static final int			ITEM_WIDTH 					= 250;
	private static final int			MARGIN						= 10;
	private static final int			HEADER_HEIGHT				= 25;
	private static final int 			HEADER_TOP_MARGIN			= 0;
	private static final int 			MIN_SPACE_BETWEEN_COLUMNS 	= 200;
	private static final int 			ARROW_START_WIDTH			= 50;
	private static final int			BORDER_HEIGHT				= 25;
	
	private static int 					RELATIONAL_X				= 10;
	private static int					GRAPH_X						= 500;
	
	private List<LabeledRectangle<T>> 	relationalComponents 		= new ArrayList<>();
	private List<LabeledRectangle<T>> 	graphComponents 			= new ArrayList<>();
	
	private List<Arrow<T>>				arrows						= new ArrayList<>();
	private LabeledRectangle<T>			dragRectangle				= null;
	private LabeledRectangle<T>			lastSelectedRectangle		= null;
	private Arrow<T>					dragArrow					= null;
	private Arrow<T>					zoomArrow					= null;
	private Arrow<T>					selectedArrow				= null;
	private LabeledRectangle<T>			dragArrowPreviousTarget		= null;
	private int							dragOffsetY;
	private int							maxHeight					= Integer.MAX_VALUE;
	private boolean						minimized					= false;
	private boolean						showOnlyConnectedItems		= false;
	
	private Mapping<T> 					mapping;
	private MappingPanel<Field> 		slaveMappingPanel;
	

	private int							shortcutMask				= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	private String						lastSourceFilter			= "";
	private String						lastTargetFilter			= "";

	private boolean						showingArrowStarts			= false;

	private DetailsListener				detailsListener;
	private List<ResizeListener>		resizeListeners				= new ArrayList<ResizeListener>();
	
	JFrame parentFrame;

	@SuppressWarnings("serial")
	public MappingPanel(JFrame parentFrame)
	{
		super();
		this.parentFrame = parentFrame;
		this.setFocusable(true);
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseMotionHandler());
		//renderModel();

		// Add key bindings to delete arrows
		this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), "del pressed");
		this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false), "del pressed");
		this.getActionMap().put("del pressed", 
				new AbstractAction() 
					{
						@Override
						public void actionPerformed(ActionEvent e) 
						{
							if (selectedArrow != null) 
							{
								removeArrow(selectedArrow);
								if(isMappingComplete())
								{
									parentFrame.getJMenuBar().getMenu(4).getItem(0).setEnabled(true);
								};
							}
						}
					}
		);

	}
	/*public MappingPanel(Mapping<T> mapping, JFrame parentFrame) 
	{
		this(parentFrame);
		this.mapping = mapping;
		maximize();
		renderModel();
	}*/
	
	public String getLastSourceFilter() 
	{
		return lastSourceFilter;
	}

	public String getLastTargetFilter() 
	{
		return lastTargetFilter;
	}

	public boolean isMinimized() 
	{
		return minimized;
	}

	public void setMapping(Mapping<T> mapping) 
	{
		maximize();
		this.mapping = mapping;
		renderModel();
	}
	
	public List<LabeledRectangle<T>> getVisibleRectangles(List<LabeledRectangle<T>> components) 
	{
		List<LabeledRectangle<T>> visible = new ArrayList<>();

		for (LabeledRectangle<T> component : components) 
		{

			if (component.isVisible())
				visible.add(component);
		}

		return visible;
	}
	
	public List<LabeledRectangle<T>> getVisibleSourceComponents() 
	{
		return getVisibleRectangles(relationalComponents);
	}
	
	public List<LabeledRectangle<T>> getVisibleTargetComponents() 
	{
		return getVisibleRectangles(graphComponents);
	}
	
	public void setSlaveMappingPanel(MappingPanel<Field> mappingPanel) 
	{
		this.slaveMappingPanel = mappingPanel;
	}
	
	public MappingPanel<Field> getSlaveMappingPanel() 
	{
		return this.slaveMappingPanel;
	}
	
	public void setShowOnlyConnectedItems(boolean value) 
	{
		showOnlyConnectedItems = value;
		renderModel();
	}
	
	public void renderModel() 
	{
		relationalComponents.clear();
		graphComponents.clear();
		arrows.clear();
		for (T item : mapping.getRelationalItems()) 
		{
			relationalComponents.add(new LabeledRectangle<T>(0, 400, ITEM_WIDTH, ITEM_HEIGHT, item, new Color(255, 128, 0)));
		}
		for (T item : mapping.getGraphItems())
		{
			graphComponents.add(new LabeledRectangle<T>(0, 400, ITEM_WIDTH, ITEM_HEIGHT, item, new Color(128, 128, 255)));
		}
		for (T relationalItem : mapping.getMapRelationalItems()) {
			for(T graphItem : mapping.getGraphItems(relationalItem))
			{
				Arrow<T> component = new Arrow<>(getComponentWithItem(relationalItem, relationalComponents), 
						getComponentWithItem(graphItem, graphComponents));
				arrows.add(component);
			}
		}
		layoutItems();
		repaint();
	}
	
	private LabeledRectangle<T> getComponentWithItem(T item, List<LabeledRectangle<T>> components) 
	{
		for (LabeledRectangle<T> component : components)
			if (component.getItem().equals(item))
				return component;
		return null;
	}
	
	private void setLabeledRectanglesLocation(List<LabeledRectangle<T>> components, int xpos) 
	{
		int avoidY = Integer.MAX_VALUE;
		if (dragRectangle != null && dragRectangle.getX() == xpos)
			avoidY = dragRectangle.getY();
		int y = HEADER_HEIGHT + HEADER_TOP_MARGIN;
		for (int i = 0; i < components.size(); i++) {
			LabeledRectangle<T> item = components.get(i);
			if (y > avoidY - ITEM_HEIGHT && y <= avoidY + MARGIN)
				y += MARGIN + ITEM_HEIGHT;

			if (dragRectangle == null || item != dragRectangle) {
				item.setLocation(xpos, y);
				y += MARGIN + ITEM_HEIGHT;
			}

		}
	}
	public void setDetailsListener(DetailsListener detailsListener) {
		this.detailsListener = detailsListener;
	}
	
	private void layoutItems() 
	{
		if (minimized) { // Only update x coordinate
			for (LabeledRectangle<T> targetComponent : getVisibleTargetComponents()) {
				targetComponent.setLocation(GRAPH_X, targetComponent.getY());
			}
		} else {
			setLabeledRectanglesLocation(getVisibleSourceComponents(), RELATIONAL_X);
			setLabeledRectanglesLocation(getVisibleTargetComponents(), GRAPH_X);
		}
	}
	private void addLabel(Graphics2D g2d, String string, int x, int y) {
		g2d.setFont(new Font("default", Font.PLAIN, 20));
		FontMetrics fm = g2d.getFontMetrics();
		Rectangle2D r = fm.getStringBounds(string, g2d);
		g2d.drawString(string, x - Math.round(r.getWidth() / 2), y - Math.round(r.getHeight() / 2) + fm.getAscent());
	}
	
	public boolean isMappingComplete()
	{
		boolean allComplete = false;
		
		for(Arrow<T> arrow : arrows)
		{
			if(!arrow.isCompleted())
			{
				break;
			}
			allComplete = true;
		}
		
		return allComplete;
	}
	
	@Override
	public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		Image offscreen = createVolatileImage(getWidth(), getHeight());
		
		Graphics2D g2d;

		if (offscreen == null) 
		{
			g2d = (Graphics2D) g;
		} else {
			g2d = (Graphics2D) offscreen.getGraphics();
		}

		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, getWidth(), getHeight());

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);

		g2d.setColor(Color.BLACK);
		addLabel(g2d, "Relational", RELATIONAL_X + ITEM_WIDTH / 2, HEADER_TOP_MARGIN + HEADER_HEIGHT / 2);
		addLabel(g2d, "Graph", GRAPH_X + ITEM_WIDTH / 2, HEADER_TOP_MARGIN + HEADER_HEIGHT / 2);

		if (showingArrowStarts && dragRectangle == null) {
			for (LabeledRectangle<T> item : getVisibleSourceComponents())
				Arrow.drawArrowHead(g2d, Math.round(item.getX() + item.getWidth() + Arrow.HEAD_THICKNESS), 
						item.getY() + item.getHeight() / 2);
		}

		for (LabeledRectangle<T> component : getVisibleSourceComponents())
			if (component != dragRectangle)
				component.paint(g2d);

		for (LabeledRectangle<T> component : getVisibleTargetComponents())
			if (component != dragRectangle)
				component.paint(g2d);

		for (int i = HighlightStatus.values().length - 1; i >= 0; i--) {
			HighlightStatus status = HighlightStatus.values()[i];
			for (Arrow<T> arrow : arrowsByStatus(status)) {
				if (arrow != dragArrow) {
					arrow.paint(g2d);
				}
			}
		}

		if (dragRectangle != null)
			dragRectangle.paint(g2d);

		if (dragArrow != null)
			dragArrow.paint(g2d);

		if (offscreen != null)
			g.drawImage(offscreen, 0, 0, this);
	}
	
	private List<Arrow<T>> arrowsByStatus(HighlightStatus status) {
		List<Arrow<T>> matchingArrows = new ArrayList<Arrow<T>>();
		for (Arrow<T> arrow : arrows) {
			if (arrow.getHighlightStatus() == status) {
				matchingArrows.add(arrow);
			}
		}
		return matchingArrows;
	}
	private void removeArrow(Arrow<T> a) {
		arrows.remove(a);
		mapping.removeSourceToTargetMap(a.getSource().getItem(), a.getTarget().getItem());
		repaint();
	}
	private List<LabeledRectangle<T>> getSelectedRectangles(List<LabeledRectangle<T>> components) {

		List<LabeledRectangle<T>> selected = new ArrayList<LabeledRectangle<T>>();

		for (LabeledRectangle<T> c : components) {
			if (c.isSelected()) {
				selected.add(c);
			}
		}

		return selected;
	}
	public void removeMapSelectedSourceAndTarget() {

		for (LabeledRectangle<T> source : getSelectedRectangles(relationalComponents)) {
			for (LabeledRectangle<T> target : getSelectedRectangles(graphComponents)) {
				removeMapSourceToTarget(source, target);
			}
		}
	}
	private void removeMapSourceToTarget(LabeledRectangle<T> source, LabeledRectangle<T> target) {

		for (Iterator<Arrow<T>> iterator = arrows.iterator(); iterator.hasNext();) {
			Arrow<T> arrow = iterator.next();
			if (source == arrow.getSource() && target == arrow.getTarget()) {
				iterator.remove();
			}
		}

		mapping.removeSourceToTargetMap(source.getItem(), target.getItem());
		repaint();
	}
	
	public void filterComponents(String searchTerm, boolean filterTarget) 
	{
		List<LabeledRectangle<T>> components;

		if (filterTarget == true) {
			components = graphComponents;
			lastTargetFilter = searchTerm;
		} else {
			components = relationalComponents;
			lastSourceFilter = searchTerm;
		}

		for (LabeledRectangle<T> c : components) {
			c.filter(searchTerm);
		}

		layoutItems();
		repaint();
	}
	
	private void maximize() 
	{
		maxHeight = Integer.MAX_VALUE;
		minimized = false;
		for (ResizeListener resizeListener : resizeListeners)
			resizeListener.notifyResized(maxHeight, false, true);

		filterComponents(lastSourceFilter, false);

		filterComponents(lastTargetFilter, true);

		for (Arrow<T> component : arrows)
			component.setVisible(true);

		this.requestFocusInWindow();
	}
	
	public boolean isBeingFiltered() 
	{
		return lastSourceFilter != "" || lastTargetFilter != "";
	}
	
	private boolean isSorted(List<LabeledRectangle<T>> sourceComponents2, Comparator<LabeledRectangle<T>> comparator) 
	{
		for (int i = 0; i < sourceComponents2.size() - 1; i++)
			if (comparator.compare(sourceComponents2.get(i), sourceComponents2.get(i + 1)) < 0)
				return false;
		return true;
	}
	
	private Set<T> getItemsList(List<LabeledRectangle<T>> components) {
		Set<T> items = new TreeSet<>();
		for (LabeledRectangle<T> component : components)
			items.add(component.getItem());
		return items;
	}
	
	private void makeMapSourceToTarget(LabeledRectangle<T> source, LabeledRectangle<T> target) 
	{
		boolean isNew = true;

		for (Arrow<T> other : arrows) 
		{
			if (source == other.getSource() && target == other.getTarget()) 
			{
				isNew = false;
			}
		}

		if (isNew) 
		{
			Arrow<T> arrow = new Arrow<>(source);
			arrow.setTarget(target);
			ETL.addSourceToTargetMap(source.getItem(), target.getItem());
			arrow.setItemToItemMap(mapping.getSourceToTargetMap(source.getItem(), target.getItem()));
			arrows.add(arrow);
		}
		repaint();
	}
	
	public void addResizeListener(ResizeListener resizeListener) 
	{
		resizeListeners.add(resizeListener);
	}
	
	private void LabeledRectangleClicked(MouseEvent event, List<LabeledRectangle<T>> components) 
	{
		int startIndex = 0;
		int endIndex = 0;

		for (LabeledRectangle<T> component : components) 
		{

			if (component.contains(event.getPoint())) 
			{

				if ((event.getModifiers() & shortcutMask) == shortcutMask) 
				{ // Add one at a time
					component.toggleSelected();
				} else if (event.isShiftDown()) 
				{ // Add in consecutive order

					startIndex = Math.min(components.indexOf(lastSelectedRectangle), components.indexOf(component));
					endIndex = Math.max(components.indexOf(lastSelectedRectangle), components.indexOf(component));

					if (startIndex >= 0 && endIndex >= 0) 
					{
						for (int i = startIndex; i <= endIndex; i++) 
						{
							components.get(i).setSelected(true);
						}
					} 
					else 
					{
						component.toggleSelected();
					}

				} 
				else 
				{
					component.setSelected(true);
				}

				if (component.isSelected()) 
				{
					lastSelectedRectangle = component;
				} 
				else 
				{
					lastSelectedRectangle = null;
				}

				detailsListener.showDetails(component.getItem());
				repaint();
				break;
			}
		}
	}
	
	public Dimension getMinimumSize() 
	{
		Dimension dimension = new Dimension();
		dimension.width = 2 * (ITEM_WIDTH + MARGIN) + MIN_SPACE_BETWEEN_COLUMNS;
		dimension.height = Math.min(HEADER_HEIGHT + HEADER_TOP_MARGIN + Math.max(relationalComponents.size(), graphComponents.size()) * (ITEM_HEIGHT + MARGIN),
				maxHeight);

		return dimension;
	}

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public void setSize(Dimension dimension) {
		setSize(dimension.width, dimension.height);
	}

	public void setSize(int width, int height) {
		RELATIONAL_X = MARGIN;
		GRAPH_X = width - MARGIN - ITEM_WIDTH;

		layoutItems();
		super.setSize(width, height);
	}
	
	public void markCompleted() 
	{
		for (Arrow<T> arrow : arrows) 
		{
			if (arrow.isSelected() || arrow.getHighlightStatus() == HighlightStatus.BOTH_SELECTED) 
			{
				arrow.getItemToItemMap().setCompleted(true);
			}
		}
		repaint();
	}

	public void unmarkCompleted() 
	{
		for (Arrow<T> arrow : arrows) 
		{
			if (arrow.isSelected() || arrow.getHighlightStatus() == HighlightStatus.BOTH_SELECTED) 
			{
				arrow.getItemToItemMap().setCompleted(false);
			}
		}
		repaint();
	}

	private class MouseHandler extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent event) 
		{
			// Save away which arrows are currently highlighted vs normal before we
			// de-select all the tables and arrows
			Hashtable<HighlightStatus, List<Arrow<T>>> currentArrowStatus = new Hashtable<>();
			for (HighlightStatus status : HighlightStatus.values()) 
			{
				currentArrowStatus.put(status, arrowsByStatus(status));
			}

			if (selectedArrow != null) 
			{
				selectedArrow.setSelected(false);
				detailsListener.showDetails(null);
				selectedArrow = null;
			}

			if (!event.isShiftDown() && !((event.getModifiers() & shortcutMask) == shortcutMask)) 
			{
				for (LabeledRectangle<T> component : graphComponents) 
				{
					component.setSelected(false);
				}

				for (LabeledRectangle<T> component : relationalComponents) 
				{
					component.setSelected(false);
				}
			}
			if (event.getX() > RELATIONAL_X && event.getX() < RELATIONAL_X + ITEM_WIDTH) 
			{ // Source component
				LabeledRectangleClicked(event, getVisibleSourceComponents());
			} 
			else if (event.getX() > GRAPH_X && event.getX() < GRAPH_X + ITEM_WIDTH) 
			{ // target component
				LabeledRectangleClicked(event, getVisibleTargetComponents());
			} 
			else if (event.getX() > RELATIONAL_X + ITEM_WIDTH && event.getX() < GRAPH_X) 
			{ // Arrows
				lastSelectedRectangle = null;
				Arrow<T> clickedArrow = null;
				for (HighlightStatus status : HighlightStatus.values()) 
				{
					for (Arrow<T> arrow : currentArrowStatus.get(status)) 
					{
						if (arrow.contains(event.getPoint())) 
						{
							clickedArrow = arrow;
							break;
						}
					}
					if (clickedArrow != null) 
					{
						break;
					}
				}

				if (clickedArrow != null) 
				{
					if (event.getClickCount() == 2) 
					{ // double click
						zoomArrow = clickedArrow;
						Table relationalItem = (Table)zoomArrow.getSource().getItem();
						Table graphItem = (Table)zoomArrow.getTarget().getItem();
						
						if (slaveMappingPanel != null) 
						{
							slaveMappingPanel.setMapping(ETL.getMappingFields(relationalItem,graphItem));
							new AnimateThread(true).start();

							slaveMappingPanel.filterComponents("", false);
							slaveMappingPanel.filterComponents("", true);
							
							if(graphItem.getName().equals("node"))
							{
								parentFrame.getJMenuBar().getMenu(1).getItem(3).setEnabled(true);
							}
							else
							{
								parentFrame.getJMenuBar().getMenu(1).getItem(4).setEnabled(true);
							}
							
						}

					} 
					else 
					{ // single click
						if (!clickedArrow.isSelected()) 
						{
							clickedArrow.setSelected(true);
							if(!clickedArrow.isCompleted())
							{
								parentFrame.getJMenuBar().getMenu(3).getItem(0).setEnabled(true);
								parentFrame.getJMenuBar().getMenu(3).getItem(1).setEnabled(false);
							}
							else
							{
								parentFrame.getJMenuBar().getMenu(3).getItem(0).setEnabled(false);
								parentFrame.getJMenuBar().getMenu(3).getItem(1).setEnabled(true);
							}
							selectedArrow = clickedArrow;
							detailsListener.showDetails(mapping.getSourceToTargetMap(selectedArrow.getSource().getItem(), 
									selectedArrow.getTarget().getItem()));
						}
						repaint();
					}
				} 
				else 
				{
					detailsListener.showDetails(null);
					parentFrame.getJMenuBar().getMenu(3).getItem(0).setEnabled(false);
					parentFrame.getJMenuBar().getMenu(3).getItem(1).setEnabled(false);
				}
			} 
			else 
			{
				lastSelectedRectangle = null;
				detailsListener.showDetails(null);
			}

		}
		@Override
		public void mouseReleased(MouseEvent event) {

			if (dragRectangle != null) { // Dragging rectangles to reorder
				if (!isSorted(relationalComponents, new YComparator())) {
					Collections.sort(relationalComponents, new YComparator());
					mapping.setRelationalItems(getItemsList(relationalComponents));
				}
				if (!isSorted(graphComponents, new YComparator())) {
					Collections.sort(graphComponents, new YComparator());
					mapping.setGraphItems(getItemsList(graphComponents));
				}
				dragRectangle = null;
				layoutItems();
			} 
			else if (dragArrow != null) 
			{ // dragging arrow to set source and target
				if (event.getX() > GRAPH_X - ARROW_START_WIDTH && event.getX() < GRAPH_X + ITEM_WIDTH)

					for (LabeledRectangle<T> component : getVisibleRectangles(graphComponents)) 
					{
						if (component.contains(event.getPoint(), ARROW_START_WIDTH, 0)) 
						{
							dragArrow.setTarget(component);
							if (dragArrow.getTarget() == dragArrowPreviousTarget) 
							{
								arrows.add(dragArrow);
								break;
							}
							makeMapSourceToTarget(dragArrow.getSource(), dragArrow.getTarget());
							parentFrame.getJMenuBar().getMenu(4).getItem(0).setEnabled(false);
							break;
						}
					}
				if (dragArrowPreviousTarget != null && dragArrow.getTarget() != dragArrowPreviousTarget) 
				{ // Retargeted an existing arrow, remove old map from model
					mapping.removeSourceToTargetMap(dragArrow.getSource().getItem(), dragArrowPreviousTarget.getItem());
				}
				dragArrowPreviousTarget = null;
				dragArrow = null;
			}
			repaint();
		}
		@Override
		public void mouseMoved(MouseEvent event) 
		{
			if (event.getX() > RELATIONAL_X + ITEM_WIDTH && event.getX() < RELATIONAL_X + ITEM_WIDTH + ARROW_START_WIDTH 
					&& dragArrow == null) 
			{
				if (!showingArrowStarts) 
				{
					showingArrowStarts = true;
					repaint();
				}
			} else 
			{
				if (showingArrowStarts) 
				{
					showingArrowStarts = false;
					repaint();
				}
			}
		}
		@Override
		public void mouseExited(MouseEvent event) 
		{
			if (showingArrowStarts) 
			{
				showingArrowStarts = false;
				repaint();
			}
		}
		@Override
		public void mousePressed(MouseEvent event) 
		{
			if (minimized) {
				maximize();
				return;
			}

			if (event.getX() > RELATIONAL_X + ITEM_WIDTH && event.getX() < RELATIONAL_X + ITEM_WIDTH + ARROW_START_WIDTH) 
			{ // Arrow starts
				for (LabeledRectangle<T> item : getVisibleSourceComponents()) 
				{
					if (event.getY() >= item.getY() && event.getY() <= item.getY() + item.getHeight()) {
						dragArrow = new Arrow<>(item);
						dragArrow.setTargetPoint(new Point(item.getX() + item.getWidth() + Arrow.HEAD_THICKNESS, 
								item.getY() + item.getHeight() / 2));
						showingArrowStarts = false;
						repaint();
						break;
					}
				}
			} 
			else if (event.getX() > GRAPH_X - ARROW_START_WIDTH && event.getX() < GRAPH_X && dragArrow == null) 
			{ // Existing arrowheads
				for (Arrow<T> arrow : arrows) 
				{
					if (event.getY() >= arrow.getTarget().getY() && event.getY() <= arrow.getTarget().getY() 
							+ arrow.getTarget().getHeight()
							&& arrow.isSourceAndTargetVisible()) 
					{
						dragArrow = arrow;
						dragArrowPreviousTarget = dragArrow.getTarget();
						dragArrow.setTarget(null);
						break;
					}
				}
				if (dragArrow != null) 
				{
					arrows.remove(dragArrow);
				}
				repaint();
			} 
			else 
			{
				for (LabeledRectangle<T> item : getVisibleSourceComponents()) 
				{
					if (item.contains(event.getPoint()) && !isBeingFiltered()) 
					{
						dragRectangle = item;
						dragOffsetY = event.getY() - item.getY();
						break;
					}
				}

				for (LabeledRectangle<T> item : getVisibleTargetComponents()) {
					if (item.contains(event.getPoint()) && !isBeingFiltered()) {
						dragRectangle = item;
						dragOffsetY = event.getY() - item.getY();
						break;
					}
				}
			}
		}
	}
	
	private class MouseMotionHandler implements MouseMotionListener
	{
		@Override
		public void mouseMoved(MouseEvent event) 
		{
			if (event.getX() > RELATIONAL_X + ITEM_WIDTH && event.getX() < RELATIONAL_X + ITEM_WIDTH + ARROW_START_WIDTH 
					&& dragArrow == null) 
			{
				if (!showingArrowStarts) 
				{
					showingArrowStarts = true;
					repaint();
				}
			} 
			else 
			{
				if (showingArrowStarts) 
				{
					showingArrowStarts = false;
					repaint();
				}
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent event) 
		{
			if (dragRectangle != null) {
				dragRectangle.setLocation(dragRectangle.getX(), event.getY() - dragOffsetY);
				layoutItems();
				repaint();
			}
			if (dragArrow != null) {
				if (event.getX() < RELATIONAL_X + ITEM_WIDTH + Arrow.HEAD_THICKNESS)
					dragArrow.setTargetPoint(null);
				else
					dragArrow.setTargetPoint(event.getPoint());
				repaint();
			}
			scrollRectToVisible(new Rectangle(event.getX() - 40, event.getY() - 40, 80, 80));
		}
	}
	private class YComparator implements Comparator<LabeledRectangle<T>> 
	{

		@Override
		public int compare(LabeledRectangle<T> o1, LabeledRectangle<T> o2) {
			return o1.compareTo(o2);
		}
	}
	private class AnimateThread extends Thread 
	{
		public int		nSteps	= 10;
		private boolean	minimizing;

		public AnimateThread(boolean minimizing) 
		{
			this.minimizing = minimizing;
		}

		public void run() 
		{
			if (minimizing) 
			{
				parentFrame.getJMenuBar().getMenu(1).getItem(2).setEnabled(minimizing);
				
				LabeledRectangle<T> sourceComponent = zoomArrow.getSource();
				LabeledRectangle<T> targetComponent = zoomArrow.getTarget();

				for (LabeledRectangle<T> component : relationalComponents)
					if (component != sourceComponent)
						component.setVisible(false);

				for (LabeledRectangle<T> component : graphComponents)
					if (component != targetComponent)
						component.setVisible(false);

				for (Arrow<T> component : arrows)
					if (component != zoomArrow)
						component.setVisible(false);
				minimized = true;
				Path heightPath = new Path(getHeight(), HEADER_TOP_MARGIN + HEADER_HEIGHT + MARGIN + ITEM_HEIGHT + BORDER_HEIGHT);
				Path sourcePath = new Path(sourceComponent.getY(), HEADER_TOP_MARGIN + HEADER_HEIGHT);
				Path targetPath = new Path(targetComponent.getY(), HEADER_TOP_MARGIN + HEADER_HEIGHT);
				for (int i = 0; i < nSteps; i++) 
				{
					maxHeight = heightPath.getValue(i);
					sourceComponent.setLocation(RELATIONAL_X, sourcePath.getValue(i));
					targetComponent.setLocation(GRAPH_X, targetPath.getValue(i));
					for (ResizeListener resizeListener : resizeListeners)
						resizeListener.notifyResized(maxHeight, false, false);
					try 
					{
						sleep(20);
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
				maxHeight = heightPath.getEnd();
				sourceComponent.setLocation(RELATIONAL_X, sourcePath.getEnd());
				targetComponent.setLocation(GRAPH_X, targetPath.getEnd());
				for (ResizeListener resizeListener : resizeListeners)
					resizeListener.notifyResized(maxHeight, true, false);
			} 
			else 
			{ // maximizing
				parentFrame.getJMenuBar().getMenu(1).getItem(2).setEnabled(minimizing);
			}
		}

		private class Path 
		{
			private int	start;
			private int	end;
			private int	stepSize;

			public Path(int start, int end) 
			{
				this.start = start;
				this.end = end;
				this.stepSize = (end - start) / nSteps;
			}

			public int getValue(int step) 
			{
				return start + stepSize * step;
			}

			public int getEnd() 
			{
				return end;
			}
		}
	}
}