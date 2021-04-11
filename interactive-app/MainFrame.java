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
	private JButton showHideLatticeButton;
	private JButton enhanceLatticeButton;

	Lattice lattice;
	
	public MainFrame()
	{
		JOptionPane.showMessageDialog(null,"Welcome to L-Triomino Tiling!  The goal of this game is to tile the given board, using only Ls.\nLeft-click to place an L on the highlighted cells.\nRight-click or Ctrl+click to rotate the direction of your L.\nHave fun!");
		GridPainter = new MyComponent();
		mouseController = new MouseControl(this);
		moveController = new MoveControl();
		
		this.add(GridPainter);
		setUpButtons();

		if(!newGame())
			return;
		
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
		showHideLatticeButton = new JButton("Lattice");
		enhanceLatticeButton = new JButton("Enhance");
		
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
				if(JOptionPane.showConfirmDialog(null,"Are you sure you want to clear the board?")==JOptionPane.YES_OPTION)
				{
					while(!moveController.undoStack.empty())
						undoMove(moveController.undoMove());
					moveController.clearMoves();
				}
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

		ActionListener showHideLatticeListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				toggleLattice();
				repaint();
			}
		};
		showHideLatticeButton.addActionListener(showHideLatticeListener);

		ActionListener enhanceLatticeListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent ae)
			{
				enhanceLattice();
				repaint();
			}
		};
		enhanceLatticeButton.addActionListener(enhanceLatticeListener);
		
		Container buttonPane = new Container();
		buttonPane.setLayout(new GridLayout(3,2));
		buttonPane.add(undoButton);
		buttonPane.add(redoButton);
		buttonPane.add(clearButton);
		buttonPane.add(newGameButton);
		buttonPane.add(showHideLatticeButton);
		buttonPane.add(enhanceLatticeButton);
		
		this.getContentPane().add(buttonPane,BorderLayout.SOUTH);
	}
	private void toggleLattice()
	{
		lattice.toggleVisibility();
	}
	private void enhanceLattice()
	{
		lattice.level_up();
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
	void setFont(int n)
	{
		Font f = new Font("Default", Font.BOLD, n);
		// undoButton.setFont(f);
		// redoButton.setFont(f);
		// clearButton.setFont(f);
		// newGameButton.setFont(f);
		// showHideLatticeButton.setFont(f);
		// enhanceLatticeButton.setFont(f);
	}
	private boolean newGame()
	{
		Object[] options = {"2 x 2",
                    "4 x 4",
                    "8 x 8",
                	"16 x 16"};

        JComboBox optionList = new JComboBox(options);
                optionList.setSelectedIndex(0);

		String s = null;
		while(s == null)
			s = (String)JOptionPane.showInputDialog(
			this,
		    "What sized board would you like to tile?",
		    "Select Screen",
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    options,
		    -1);

		int n = 1;
		if(s.equals(options[0]))
		{
			n = 1;
			GameData.tileSize = 100;
			showHideLatticeButton.setVisible(false);
			enhanceLatticeButton.setVisible(false);
		}
		else if(s.equals(options[1]))
		{
			n = 2;
			GameData.tileSize = 50;
			showHideLatticeButton.setVisible(true);
			enhanceLatticeButton.setVisible(false);
		}
		else if(s.equals(options[2]))
		{
			n = 3;
			GameData.tileSize = 50;
			showHideLatticeButton.setVisible(true);
			enhanceLatticeButton.setVisible(true);
		}
		else if(s.equals(options[3]))
		{
			n = 4;
			GameData.tileSize = 30;
			showHideLatticeButton.setVisible(true);
			enhanceLatticeButton.setVisible(true);
		}


		GameData.shuffleColors();
		
		grid_size = (1<<n);
		
		width = grid_size*GameData.tileSize;
		height = grid_size*GameData.tileSize + 75;
		this.getContentPane().setPreferredSize(new Dimension(width,height));
		this.pack();		

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
		this.setTitle("L-Triomino Tiling");

		lattice = new Lattice(width, height, n-1);
		
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
			this.setTitle("Successfully L-tiled");
			toggleHighlights();
			clearHover();
			JOptionPane.showMessageDialog(null,"Congratulations, you have L-tiled this board!");
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
			lattice.draw(g2d);
		}
	}
}