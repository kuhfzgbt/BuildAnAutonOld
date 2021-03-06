import java.awt.*;
import java.awt.geom.*;
import java.io.Serializable;

import javax.swing.JComponent;

/**
 * CommandBlock is the class representing the "blocks" in the 
 * Auton builder. It contains the necessary information to display the position
 * of the Command.
 * 
 * @author Daniel Qian
 * @author Dominic Rubino
 * @version 1.0
 * 
 */
public class CommandBlock implements Serializable{
	private static final long serialVersionUID = 874754578L;

	private Rectangle hitbox; //Rectangle the contains all portions of the command block
	private Color primCol; //Background Color
	private Color secCol; //Text and Outline Color
	protected Command command; //What it will do.
	
	private boolean snapped = false;

	
	public final int WIDTH = 120; //Width of the total shape
	public  final int HEIGHT = 120; //Height of the total shape
	

	/**
	 * Creates a <code>CommandBlock</code> object at 0, 0
	 * 
	 * @param parent		Parent component for the CommandBlock to be displayed in
	 * @param primary		Primary color of the commandBlock, or background color
	 * @param secondary		Secondary color, or text and outline color
	 */
	public CommandBlock(Command c, Color primary, Color secondary) {
		command = c;
		primCol = primary;
		secCol = secondary;
		hitbox = new Rectangle(0, 0, WIDTH, HEIGHT);
	}
	
	/**
	 * Creates a <code>CommandBlock</code> object at a specified x and y;
	 * 
	 * @param parent		Parent component for the CommandBlock to be displayed in
	 * @param c				Command
	 * @param x
	 * @param y
	 * @param primary		Primary color of the commandBlock, or background color
	 * @param secondary		Secondary color, or text and outline color
	 */
	public CommandBlock(Command c, int x, int y, Color primary, Color secondary) {
		command = c;
		hitbox = new Rectangle(x, y, WIDTH, HEIGHT);
		primCol = primary;
		secCol = secondary;
	}
	
	/*
	 * Methods that run corresponding methods in the Command object. Used 
	 * in order to save space when writing to a file. Also ensures
	 * no NullPointerException is thrown
	 */
	public void edit() {
		if(command != null)
			command.edit();
	}
	public void execute() {
		if(command != null)
			command.execute();
	}

	
	
	//Getters
	public Rectangle getHitBox() {
		return hitbox;	
	}
	public Color getPrimary() {
		return primCol;
	}
	public Color getSecondary() {
		return secCol;
	}
	public Command getCommand() {
		return command;
	}
	
	//Setters
	public void setX(int x) {
		hitbox.x = x;
	}
	public void setY(int y) {
		hitbox.y = y;
	}

	//Returns the portions of the Rectangles that should implement unique actions when clicked on.
	public Rectangle getDragPortion() {
		return new Rectangle(hitbox.x, hitbox.y, WIDTH, HEIGHT/6);
	}
	public Rectangle getDelPortion() {
		return new Rectangle(hitbox.x, hitbox.y + HEIGHT/6, WIDTH/2, HEIGHT * 5/6);
	}
	public Rectangle getEditPortion() {
		return new Rectangle(hitbox.x + WIDTH/2, hitbox.y + HEIGHT/6, WIDTH/2, HEIGHT * 5/6);
	}
	
	public void snap() {
		snapped = true;
	}
	public void unsnap() {
		snapped = false;
	}
	public boolean isSnapped() {
		return snapped;
	}
	
	
	/**
	 * Draws the command block on the passed graphics
	 * @param g
	 */
	public void paint(Graphics2D g) {
		g.setColor(primCol);
		g.fill(new RoundRectangle2D.Double(hitbox.x, hitbox.y, WIDTH, HEIGHT/6, 8, 8));
		g.fill(new RoundRectangle2D.Double(hitbox.x, hitbox.y + HEIGHT/6, WIDTH/2, HEIGHT * 5/6, 8, 8)); 
		g.fill(new RoundRectangle2D.Double(hitbox.x + WIDTH/2, hitbox.y + HEIGHT/6, WIDTH/2, HEIGHT * 5/6, 8, 8));
		
		g.setColor(secCol);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		g.drawString(this.getClass().getName(), hitbox.x + 2, hitbox.y + 18);
		g.draw(new RoundRectangle2D.Double(hitbox.x, hitbox.y, WIDTH, HEIGHT/6, 8, 8));
		g.draw(new RoundRectangle2D.Double(hitbox.x, hitbox.y + HEIGHT/6, WIDTH/2, HEIGHT * 5/6, 8, 8)); 
		g.draw(new RoundRectangle2D.Double(hitbox.x + WIDTH/2, hitbox.y + HEIGHT/6, WIDTH/2, HEIGHT * 5/6, 8, 8));
		
	}
	
}
