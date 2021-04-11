import java.awt.*;
import java.util.Random;
public class GameData
{
	public final static Color backgroundColor = new Color(102,51,0);
	public final static Color borderColor = Color.BLACK;

	public static Color colors[] = 
	{new Color(118,119,123), // lol
	new Color(236, 132, 41),
	new Color(218, 121, 111),
	new Color(236, 41, 106),
	new Color(115, 113, 252),
	new Color(147, 255, 150),
	new Color(197, 93, 246),
	new Color(252, 214, 87),
	new Color(123, 223, 242)
	};
	public final static Color validHighlight = Color.YELLOW;
	public final static Color invalidHighlight = Color.RED;
	
	public final static int preferredSize = 600;
	public static int tileSize;
	
	public static void shuffleColors()
	{
		Random rand = new Random();
		// improved to Fisher-Yates
		
		for(int i = colors.length - 1; i >= 1; i--)
		{
			int u = rand.nextInt(i)+1;

			Color temp = colors[u];
			colors[u] = colors[i];
			colors[i] = temp;
		}
	}
	/*
		d = 0, 2 -> North, South
		d = 1, 3 -> East, West
	*/
	
}