import java.awt.*;
import java.awt.event.*;
public class MouseControl
{
	MainFrame master;
	public MouseControl(MainFrame master)
	{
		this.master = master;
		
		MouseListener m1 = new MouseListener()
		{
			@Override
			public void mousePressed(MouseEvent me)
			{
				int x = me.getX();
				int y = me.getY();
				if( !(0 <= x && x <= master.width) || !(0 <= y && y <= master.width) )
					return;
				if(me.getButton()==MouseEvent.BUTTON3 || me.isControlDown())
				{
					master.rotate();
					return;
				}
				//System.out.println(x+ " " + y);
				master.clickTile(master.getX(x),master.getY(y),false);
			}
			@Override
			public void mouseClicked(MouseEvent me){}
			@Override
			public void mouseReleased(MouseEvent me){}
			@Override
			public void mouseEntered(MouseEvent me){}
			@Override
			public void mouseExited(MouseEvent me)
			{
				master.toggleHighlights();
				master.clearHover();
				master.repaint();
			}
		};
		MouseMotionListener m2 = new MouseMotionListener()
		{
			@Override
			public void mouseMoved(MouseEvent me)
			{
				int x = me.getX(), y = me.getY();
				if( !(0 <= x && x <= master.width) || !(0 <= y && y <= master.width) )
				{
					master.toggleHighlights();
					master.clearHover();
					master.repaint();
					return;
				}
				master.updateCurrent(x,y);
			}
			@Override
			public void mouseDragged(MouseEvent me){}
		};
		master.getContentPane().addMouseListener(m1);
		master.getContentPane().addMouseMotionListener(m2);
	}
}