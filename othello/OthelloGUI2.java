
package othello;

import gamePlayer.Action;
import gamePlayer.Decider;
import gamePlayer.InvalidActionException;
import gamePlayer.State.Status;
import gamePlayer.algorithms.MTDDecider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

// This is based on GUI code from http://code.google.com/p/reversi-game/wiki/Reversi 

class GamePanel extends JPanel implements MouseListener {

	static final int Square_L = 33; // length in pixel of a square in the grid
	static final int Width = 8 * Square_L; // Width of the game board
	static final int Height = 8 * Square_L; // Width of the game board
	
	private static final long serialVersionUID = 1L;
	private OthelloState board;
	private Decider computerPlayer;
	private boolean turn;
	private boolean inputEnabled;
	private final boolean humanPlayerOne;

	public GamePanel(Decider computerPlayer, OthelloState board, boolean computerStart) {
		this.board = board;
		this.computerPlayer = computerPlayer;
		this.turn = computerStart;
		this.humanPlayerOne = !computerStart;
		
		addMouseListener(this);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Cursor savedCursor = getCursor();
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (turn) {
					computerMove();
				} else {
					inputEnabled = true;
				}
				setCursor(savedCursor);
			}
		});
		setBackground(Color.green);
	}

	protected void drawPanel(Graphics g) {
		// int currentWidth = getWidth();
		// int currentHeight = getHeight();
		for (int i = 1; i < 8; i++) {
			g.drawLine(i *  Square_L, 0, i *  Square_L,
					 Height);
		}
		g.drawLine(Width, 0, Width, Height);
		for (int i = 1; i < 8; i++) {
			g.drawLine(0, i *  Square_L,  Width, i
					*  Square_L);
		}
		g.drawLine(0,  Height,  Width,  Height);
		//System.out.println("Redrawing board\n" + board);
		for (byte i = 0; i < 8; i++)
			for (byte j = 0; j < 8; j++)
				switch (board.getSpotAsChar(j, i)) {
				case 'X':
					g.setColor(Color.white);
					g.fillOval(1 + i *  Square_L, 1 + j
							*  Square_L,  Square_L - 1,
							 Square_L - 1);
					break;
				case 'O':
					g.setColor(Color.black);
					g.fillOval(1 + i *  Square_L, 1 + j
							*  Square_L,  Square_L - 1,
							 Square_L - 1);
					break;
				}
	}

	@Override
	protected void paintComponent(Graphics arg0) {
		super.paintComponent(arg0);
		drawPanel(arg0);
	}

	public Dimension getPreferredSize() {
		return new Dimension( Width,  Height);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (inputEnabled) {
			byte j = (byte) (e.getX() /  Square_L);
			byte i = (byte) (e.getY() /  Square_L);
			OthelloAction a = new OthelloAction(humanPlayerOne, (byte) i, (byte) j);
			if (a.validOn(board)) {
				try {
					board = a.applyTo(board);
					inputEnabled = false;
				} catch (InvalidActionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				/*
				 * score_black.setText(Integer.toString(board.getCounter(TKind.black
				 * )));
				 * score_white.setText(Integer.toString(board.getCounter(TKind
				 * .white)));
				 */
				repaint();
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Cursor savedCursor = getCursor();
						setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						computerMove();
						setCursor(savedCursor);
					}
				});
			} else
				JOptionPane.showMessageDialog(this, "Illegal move", "Reversi",
						JOptionPane.ERROR_MESSAGE);
		}
	}

	public void computerMove() {
		if (board.getStatus() != Status.Ongoing) {
			showWinner();
			return;
		}

		List<Action> actions;
		boolean isPass;
		do {
			System.out.println("Starting Computer Move");
			OthelloAction action = (OthelloAction) computerPlayer.decide(board);
			try {
				board = action.applyTo(board);
				//System.out.println(board);
			} catch (InvalidActionException e) {
				throw new RuntimeException("Invalid action!");
			}
			repaint();
			actions = board.getActions();
			System.out.println("Finished with computer move");
			isPass = (actions.size() == 1 && ((OthelloAction)actions.get(0)).isPass());
			if (isPass) {
				try {
					board = (OthelloState) actions.get(0).applyTo(board);
					repaint();
				} catch (InvalidActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				repaint();
			}
		} while (isPass && board.getStatus() == Status.Ongoing);
		
		
		// Next person's turn
		this.turn = !this.turn;
		inputEnabled = true;
		/*
		 * Move move = new Move(); if
		 * (board.findMove(TKind.white,gameLevel,move)) {
		 * board.move(move,TKind.white);
		 * score_black.setText(Integer.toString(board.getCounter(TKind.black)));
		 * score_white.setText(Integer.toString(board.getCounter(TKind.white)));
		 * repaint(); if (board.gameEnd()) showWinner(); else if
		 * (!board.userCanMove(TKind.black)) {
		 * JOptionPane.showMessageDialog(this,
		 * "You pass...","Reversi",JOptionPane.INFORMATION_MESSAGE);
		 * javax.swing.SwingUtilities.invokeLater(new Runnable() { public void
		 * run() { computerMove(); } }); } } else if
		 * (board.userCanMove(TKind.black)) JOptionPane.showMessageDialog(this,
		 * "I pass...","Reversi",JOptionPane.INFORMATION_MESSAGE); else
		 * showWinner();
		 */
	}

	private void showWinner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}

public class OthelloGUI2 extends JFrame {

	private JLabel score_black;
	private JLabel score_white;
	private JPanel gamePanel;

	static final int Square_L = 33; // length in pixel of a square in the grid
	static final int Width = 8 * Square_L; // Width of the game board
	static final int Height = 8 * Square_L; // Width of the game board

	public OthelloGUI2(int whichPlayer, int nummilli) {
		score_black = new JLabel("2"); // the game start with 2 black pieces
		score_black.setForeground(Color.blue);
		score_black.setFont(new Font("Dialog", Font.BOLD, 16));
		score_white = new JLabel("2"); // the game start with 2 white pieces
		score_white.setForeground(Color.red);
		score_white.setFont(new Font("Dialog", Font.BOLD, 16));

		OthelloState start = new OthelloState();
		start.setStandardStartState();

		if (whichPlayer == 1)
			gamePanel = new GamePanel(new MTDDecider(true, nummilli, 64), start, true);
		else
			gamePanel = new GamePanel(new MTDDecider(false, nummilli, 64), start, false);
		
		gamePanel.setMinimumSize(new Dimension( Width,
				 Height));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel status = new JPanel();
		status.setLayout(new BorderLayout());
		status.add(score_black, BorderLayout.WEST);
		status.add(score_white, BorderLayout.EAST);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				gamePanel, status);

		splitPane.setOneTouchExpandable(false);
		getContentPane().add(splitPane);

		pack();
		setVisible(true);
		setResizable(false);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		if (args.length != 2) throw new IllegalArgumentException();
		final int whichPlayer = Integer.parseInt(args[0]);
		final int nummilli = Integer.parseInt(args[1]);
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				OthelloGUI2 frame = new OthelloGUI2(whichPlayer, nummilli);
			}
		});

	}

}
