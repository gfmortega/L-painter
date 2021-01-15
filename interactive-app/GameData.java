import java.awt.*;
import java.util.Random;
public class GameData
{
	public final static Color backgroundColor = new Color(102,51,0);
	public final static Color borderColor = Color.BLACK;
	public static Color colors[] = 
	{Color.BLACK,
	Color.BLUE,
	Color.MAGENTA,
	Color.CYAN,
	Color.GREEN,
	new Color(170,0,255), //purple
	new Color(226,162,162),//light pink
	new Color(145,255,0), //lime green
	Color.ORANGE
	};
	public final static Color validHighlight = Color.YELLOW;
	public final static Color invalidHighlight = Color.RED;
	
	public final static int preferredSize = 650;
	public static int tileSize;
	
	public static void shuffleColors()
	{
		Random rand = new Random();
		int swapCount = rand.nextInt(50)+50;
		for(int i = 0; i < swapCount; i++)
		{
			int u = rand.nextInt(5)+1;
			int v = rand.nextInt(5)+1;
			Color temp = colors[u];
			colors[u] = colors[v];
			colors[v] = temp;
		}
	}
	/*
		d = 0, 2 -> North, South
		d = 1, 3 -> East, West
	*/
	
}