package base;
import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class BuildAnAuton extends JFrame implements ActionListener {
	private ArrayList<CommandBlock> commands = new ArrayList<CommandBlock>();
	
	JComponent workArea = new JComponent() {
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.draw(new Line2D.Double(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2));
			for(CommandBlock c:commands) {
				c.paint(g2);
			}
		}
	};
	
	private JScrollPane workAreaPane = new JScrollPane(workArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	private JMenuBar menu = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem save = new JMenuItem("Save");
	private JMenuItem load = new JMenuItem("Load");
	private JMenuItem export = new JMenuItem("Export");
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem help = new JMenu("Help");
	JFileChooser fs = new JFileChooser();

	
	private JPanel buttons = new JPanel();
	private JButton add = new JButton("add");
	
	private int xOffset;
	private int yOffset;
	private int focus = -1;
	
	private int snapGap = 60;
	
	public BuildAnAuton() {
		
		setLayout(new BorderLayout());
		
		
		buttons.add(add);

		workArea.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				for(int i = commands.size() - 1; i >= 0; i--) {
					if(commands.get(i).getEditPortion().contains(e.getPoint())) {
						commands.get(i).edit();
						return;
					}
					if(commands.get(i).getDelPortion().contains(e.getPoint())) {
						if(JOptionPane.OK_OPTION==JOptionPane.showConfirmDialog(null, "Delete Command?", "Delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE))
							commands.remove(i);
						return;
					}
				
				}
				
			}
			public void mouseEntered(MouseEvent e) {
				
			}
			public void mouseExited(MouseEvent e) {
				
			}
			public void mouseReleased(MouseEvent e) {
				if(focus != -1) {
				int temp = focus;
				focus = -1;
				if(Math.abs(commands.get(temp).getHitBox().y + 60 - workArea.getHeight()/2) < snapGap){

					commands.get(temp).setY(workArea.getHeight()/2 - 60);
					commands.get(temp).snap();
				}
				if(commands.get(temp).getHitBox().x < 0){
					commands.get(temp).setX(0);
				}
				place(temp);
				}
			}
			public void mousePressed(MouseEvent e) {
				
				for(int i = commands.size() - 1; i >= 0; i--) {
					Rectangle r = commands.get(i).getDragPortion();
					if(r.contains(e.getPoint())) {
						commands.get(i).unsnap();
						focus = i;
						xOffset = e.getX() - r.x;
						yOffset = e.getY() - r.y;

						break;
					}
				}
			}
			
		}); 

		Thread t = new Thread(new Runnable() {
			public void run() {
				while(true) {
					workArea.repaint();	
					
					if(focus != -1) {
											
					try{
						Thread.sleep(10);
						commands.get(focus).setX(workArea.getMousePosition().x - xOffset);
						commands.get(focus).setY(Math.abs(workArea.getMousePosition().y - yOffset + 60 - workArea.getHeight()/2) < snapGap ? 
							workArea.getHeight()/2 - 60: 
							workArea.getMousePosition().y - yOffset);
					}
					catch(Exception e){}
					
					}
				}
			}
		});
		
		
		
		menu.add(fileMenu);
		fileMenu.add(save);
		fileMenu.add(load);
		fileMenu.add(export);
		menu.add(helpMenu);
		helpMenu.add(help);
		add(menu, BorderLayout.NORTH);
	
		save.addActionListener(this);
		load.addActionListener(this);
		export.addActionListener(this);
		
		add(buttons, BorderLayout.SOUTH);
		add(workArea, BorderLayout.CENTER);
		
		validate();
		t.start();
	}

	public void place(int f) {
		if(f != -1) {
			CommandBlock temp = commands.get(f);
			int xtoswap = temp.getHitBox().x;
			int indexToPlace = 0;
			commands.remove(temp);
			for (int i = 0; i < commands.size(); i++) {
				if(xtoswap > commands.get(i).getHitBox().x) {
					indexToPlace += 1;
				}
			}
			commands.add(indexToPlace, temp);
		}
	}

	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == save) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Auton", "aut");
			fs.setFileFilter(fil);
			if(fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {				
				File f = fs.getSelectedFile();
				save(f);
			}
		}
		if(e.getSource() == load) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Auton", "aut");
			fs.setFileFilter(fil);
			if(fs.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File f = fs.getSelectedFile();
				open(f);
			}
			
		}
		if(e.getSource() == export) {
			FileNameExtensionFilter fil = new FileNameExtensionFilter("Program", "autr");
			fs.setFileFilter(fil);
			if(fs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {				
				File f = fs.getSelectedFile();
				export(f);
			}

		}
	}
	
	public void save(File f) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream (f));
			oos.writeObject(commands);
			oos.close();
		}
		catch(IOException exc) {
			exc.printStackTrace();
		}
	}
	public void open(File f) {
		try {
			System.out.println(f.getPath());
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			commands = (ArrayList<CommandBlock>) ois.readObject();
			ois.close();
		}
		catch(IOException exc){exc.printStackTrace();}
		catch(ClassNotFoundException exc) {System.out.println("Error 2");}
	}
	public void export(File f) {
		ArrayList<Command> program = new ArrayList<Command>();
		for(CommandBlock c: commands) {
			if(c.isSnapped())
				program.add(c.getCommand());
		}
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream (f));
			oos.writeObject(program);
			oos.close();
		}
		catch(IOException exc){exc.printStackTrace();}

	}
	
	public JButton getAddButton() {
		return add;
	}
	
	public void Add(CommandBlock c) {
		commands.add(c);
	}
}
