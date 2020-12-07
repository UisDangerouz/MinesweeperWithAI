package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gui extends JPanel implements KeyListener, MouseListener{

	//JFRAME
	JFrame guiFrame;
	
	//MOUSE
	public int lastMouseClickLocX = 0;
	public int lastMouseClickLocY = 0;
	public boolean leftClicked = false;
	public boolean rightClicked = false;
	
	public boolean[] keyPressed = new boolean[1000];
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//NEW GRAPICHS 2D OBJECT 
		Graphics2D g2d = (Graphics2D) g;

	    //SETS THE GRAPHICS OBJECT TO HAVE ANTI ALIASING
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
	    //ALSO FOR TEXT
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 	
		
		//GRAPICHS CODE HERE:::
		g2d = Game.draw(g2d);
	}
	
	public Gui(String title, int width, int height) {	
		//FRAME
		guiFrame = new JFrame(title);
		
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setSize(width, height);
		guiFrame.setBackground(Color.WHITE);
		guiFrame.setResizable(false);
		
		//ADSS THE PAINT COMPONENT
		guiFrame.add(this);	
		guiFrame.setVisible(true);
		guiFrame.repaint();
		
		//KEYLISTENER
		guiFrame.addKeyListener(this);
		//MOUSELISTENER
		guiFrame.addMouseListener(this);
		
		guiFrame.setVisible(true);
	}
	
	//KEY EVENTS
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		keyPressed[e.getKeyCode()] = true;
	}

	//MOUSE EVENTS
	public void mouseClicked(MouseEvent e) {
		
	}
	public void mousePressed(MouseEvent e) {
		lastMouseClickLocX = e.getX() - guiFrame.getInsets().left;
		lastMouseClickLocY = e.getY() - guiFrame.getInsets().top;
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			leftClicked = true;
		} else if(e.getButton() == MouseEvent.BUTTON3) {
			rightClicked = true;
		}
	}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}	
	
}
