import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
public class Lattice
{
	private int width, height;
	private int stick_thick;

	private int level;
	private int max_level;

	private ArrayList<Rectangle2D.Double> sticks;
	Color[] colors = {
		new Color(249, 65, 68),
		new Color(248, 150, 30),
		new Color(144, 190, 109),
		new Color(87, 117, 144)
	};

	private boolean visible;
	
	public void level_up()
	{
		if(level==max_level)
		{
			sticks.clear();
			level = 0;
		}
		
		level += 1;

		// each level introduces 1<<level sticks
		for(int t = 1; t < (1<<level); t += 2)
		{
			int v = (width*t)/(1<<level) - stick_thick/2;
			sticks.add(new Rectangle2D.Double(v,0,stick_thick,height));
			sticks.add(new Rectangle2D.Double(0,v,width,stick_thick));
		}
	}
	public Lattice(int width, int height, int max_level)
	{
		this.width = width;
		this.height = height;
		this.max_level = max_level;

		sticks = new ArrayList<Rectangle2D.Double>();

		this.stick_thick = 6;
		this.level = 0;

		level_up();

		this.visible = false;
	}
	public void draw(Graphics2D g2d)
	{
		if(!visible)
			return;

		int i = sticks.size()-1;
		for(int t = level; t >= 1; t--)
		{
			g2d.setColor(colors[t-1]);
			for(int k = 0; k < (1<<t); k++, i--)
			{
				// System.out.println(sticks.get(i));
				g2d.fill(sticks.get(i));
			}
		}
	}
	public void toggleVisibility()
	{
		visible = !visible;
	}
}