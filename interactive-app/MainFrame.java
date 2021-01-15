import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
public class MainFrame extends JFrame
{
	public int width;
	private int height;
	private int grid_size;
	
	private Tile grid_information[][];
	private MyComponent GridPainter;
	private MouseControl mouseController;
	private MoveControl moveController;
	
	private int currX;
	private int currY;
	private int currD;
	
	private int successfulClicks;
	
	private JButton undoButton;
	private JButton redoButton;
	private JButton clearButton;
	private JButton newGameButton;
	
	public MainFrame()
	{
		JOptionPane.showMessageDialog(null,"Welcome to L-painter!  The goal of this game is to tile the given board entirely out of Ls.\nLeft-click to paint the highlighted cells.\nRight-click or Ctrl+click to rotate the direction of your L.\nHave fun!");
		GridPainter = new MyComponent();
		mouseController = new MouseControl(this);
		moveController = new MoveControl();
		
		if(!newGame())
			return;
			
		this.add(GridPainter);
		setUpButtons();
		
		this.getContentPane().setPreferredSize(new Dimension(width,height));
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	private boolean gameIsWon()
	{
		return successfulClicks==(grid_size*grid_size-1)/3;
	}
	private void setUpButtons()
	{
		undoButton = new JButton("Undo");
		redoButton = new JButton("Redo");
		clearButton = new JButton("Clear");
		newGameButton = new JButton("New Game");
		
		ActionListener undoListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(moveController.undoStack.empty()||gameIsWon())
					return;
				undoMove(moveController.undoMove());
			}
		};
		undoButton.addActionListener(undoListener);
		
		ActionListener redoListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(moveController.redoStack.empty()||gameIsWon())
					return;
				redoMove(moveController.redoMove());
			}
		};
		redoButton.addActionListener(redoListener);
		
		ActionListener clearListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(gameIsWon())
					return;
				while(!moveController.undoStack.empty())
					undoMove(moveController.undoMove());
				moveController.clearMoves();
			}
		};
		clearButton.addActionListener(clearListener);
		
		ActionListener newGameListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				if(JOptionPane.showConfirmDialog(null,"Are you sure you want to start a new game?")==JOptionPane.YES_OPTION)
					newGame();
				repaint();
			}
		};
		newGameButton.addActionListener(newGameListener);
		
		Container buttonPane = new Container();
		buttonPane.setLayout(new GridLayout(2,2));
		buttonPane.add(undoButton);
		buttonPane.add(redoButton);
		buttonPane.add(clearButton);
		buttonPane.add(newGameButton);
		
		this.getContentPane().add(buttonPane,BorderLayout.SOUTH);
	}
	private void undoMove(Move m)
	{
		grid_information[m.x][m.y].unfill();
		grid_information[m.x+rX(m.d)][m.y+rY(m.d)].unfill();
		grid_information[m.x+rX((m.d+1)%4)][m.y+rY((m.d+1)%4)].unfill();
		successfulClicks--;
		repaint();
	}
	private void redoMove(Move m)
	{
		int temp = currD;
		currD = m.d;
		clickTile(m.x,m.y,true);
		currD = temp;
		repaint();
	}
	private boolean newGame()
	{
		boolean invalid_N = true;
		int n = 1;
		while(invalid_N)
		{
			try
			{
				String s = JOptionPane.showInputDialog(null,"Create a 2^N x 2^N board.  Input N from 2 to 4:");
				if(s==null)
					return false;
				n = Integer.parseInt(s);
				invalid_N = !(2 <= n && n <= 4);
			}
			catch(Exception e)
			{
			}
		}
		GameData.shuffleColors();
		
		grid_size = (1<<n);
		GameData.tileSize = GameData.preferredSize/grid_size + (GameData.preferredSize%grid_size==0 ? 0 : 1);
		width = grid_size*GameData.tileSize;
		height = grid_size*GameData.tileSize + 50;
				
		grid_information = new Tile[grid_size+4][grid_size+4];
		for(int i = 0; i <= grid_size+3; i++)
			for(int j = 0; j <= grid_size+3; j++)
				grid_information[i][j] = new Tile(i,j);
		for(int i = 1; i <= grid_size+1; i++)
		{
			grid_information[i][1].taint();
			grid_information[i][grid_size+2].taint();
		}
		for(int j = 1; j <= grid_size+1; j++)
		{
			grid_information[1][j].taint();
			grid_information[grid_size+2][j].taint();
		}
		Random rand = new Random();
		int seed_x = rand.nextInt(grid_size)+2;
		int seed_y = rand.nextInt(grid_size)+2;
		grid_information[seed_x][seed_y].taint();
		clearHover();
		
		moveController.clearMoves();
		
		successfulClicks = 0;
		this.setTitle("The L Painter");
		
		return true;
	}
	public void clearHover()
	{
		/*
		switch(currD)
		{
			case 0:
				currX = 0;
				currY = grid_size+1;
				break;
			case 1:
				currX = 0;
				currY = 0;
				break;
			case 2:
				currX = grid_size+1;
				currY = 0;
				break;
			case 3:
				currX = grid_size+1;
				currY = grid_size+1;
				break;
		}
		*/
		currX = 1;
		currY = 1;
	}
	public int getX(int x)
	{
		return x/GameData.tileSize+1+1;
	}
	public int getY(int y)
	{
		return y/GameData.tileSize+1+1;
	}
	public int rX(int d)
	{
		if(d==0||d==2)
			return 0;
		if(d==1)
			return 1;
		if(d==3)
			return -1;
		return 0;
	}
	public int rY(int d)
	{
		if(d==1||d==3)
			return 0;
		if(d==0)
			return -1;
		if(d==2)
			return 1;
		return 0;
	}
	public void rotate()
	{
		toggleHighlights();
		currD = (currD+1)%4;
		toggleHighlights();
		repaint();
	}
	public void clickTile(int i, int j, boolean virtual)
	{
		if(gameIsWon())
			return;
		//System.out.println(i+" "+j);
		if(grid_information[i][j].isFilled || grid_information[i+rX(currD)][j+rY(currD)].isFilled || grid_information[i+rX((currD+1)%4)][j+rY((currD+1)%4)].isFilled)
			return;
			
		int colorID = 1;
		for(int a = 0; a < 3; a++)
		{
			int ii = 0, jj = 0;
			if(a==0){ ii = i; jj = j; }
			else if(a==1){ ii = i + rX(currD); jj = j + rY(currD); }
			else if(a==2){ ii = i + rX((currD+1)%4); jj = j + rY((currD+1)%4); }
			
			for(int d = 0; d <= 3; d++)
			{
				if((a==1 && (d+2)%4==currD) || (a==2 && (d+1)%4==currD))
					continue;
				if(grid_information[ii+rX(d)][jj+rY(d)].getID()==colorID)
				{
					//System.out.println(i+rX(d)+ " " + (j+rY(d)));
					colorID++;
					a = -1;
					break;
				}
			}
		}
		grid_information[i][j].fillIn(colorID);
		grid_information[i+rX(currD)][j+rY(currD)].fillIn(colorID);
		grid_information[i+rX((currD+1)%4)][j+rY((currD+1)%4)].fillIn(colorID);
		
		successfulClicks++;
		if(!virtual)
			moveController.performMove(new Move(i,j,currD));
		repaint();
		
		if(gameIsWon())
		{
			this.setTitle("Successfully L-painted");
			toggleHighlights();
			clearHover();
			JOptionPane.showMessageDialog(null,"Congratulations, you have L-painted this board!");
		}
	}
	public void toggleHighlights()
	{
		grid_information[currX][currY].toggleHighlight();
		grid_information[currX+rX(currD)][currY+rY(currD)].toggleHighlight();
		grid_information[currX+rX((currD+1)%4)][currY+rY((currD+1)%4)].toggleHighlight();
	}
	public void updateCurrent(int x, int y)
	{
		if(gameIsWon())
			return;
		toggleHighlights();
		int i = getX(x), j = getY(y);
		currX = i;
		currY = j;
		toggleHighlights();
		repaint();
	}
	private class MyComponent extends JComponent
	{
		@Override
		protected void paintComponent(Graphics g)
		{
			Graphics2D g2d = (Graphics2D)g;
			for(int i = 1; i <= grid_size; i++)
				for(int j = 1; j <= grid_size; j++)
					grid_information[i+1][j+1].draw(g2d);
		}
	}
}