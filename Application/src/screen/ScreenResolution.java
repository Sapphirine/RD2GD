package screen;

import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenResolution {
	private static int width;
	private static int height;
	static
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		width = dimension.width;
		height = dimension.height;
	}
	public static int getWidth()
	{
		return width;
	}
	public static int getHeight()
	{
		return height;
	}
}
