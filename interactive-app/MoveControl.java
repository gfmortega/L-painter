import java.util.Stack;
public class MoveControl
{
	public Stack<Move> undoStack;
	public Stack<Move> redoStack;
	public MoveControl()
	{
		undoStack = new Stack<Move>();
		redoStack = new Stack<Move>();
	}
	public void clearMoves()
	{
		undoStack.clear();
		redoStack.clear();
	}
	public void performMove(Move m)
	{
		undoStack.push(m);
		redoStack.clear();
	}
	public Move undoMove()
	{
		Move m = undoStack.pop();
		redoStack.push(m);
		return m;
	}
	public Move redoMove()
	{
		Move m = redoStack.pop();
		undoStack.push(m);
		return m;
	}
}