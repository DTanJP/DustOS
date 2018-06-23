package Dust;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.cardboard.dtanjp.Computer;

/**
 * Display.java
 * 
 * @author David Tan
 **/
public class Display extends JPanel {

	/** Generated serial version UID **/
	private static final long serialVersionUID = -2651608314638340422L;

	/** Constructor **/
	private Display() {
	}
	
	/** Singleton **/
	public static Display GetInstance() {
		if(instance == null)
			instance = new Display();
		return instance;
	}
	
	public void Initialize() {
		components = new ArrayList<>();
		setLayout(null);
		
		menuBar = new JMenuBar();
		//Menus
		states = new JMenu("State");
		about = new JMenu("About");
		
		//Menu Items
		menuItem = new JMenuItem("Home");
		aboutItem = new JMenuItem("About");
		exitItem = new JMenuItem("Exit");
		minimizeItem = new JMenuItem("Minimize");
		
		menuBar.setBackground(Color.BLACK);
		states.setBackground(Color.BLACK);
		about.setBackground(Color.BLACK);
		menuItem.setBackground(Color.BLACK);
		menuItem.setForeground(Color.CYAN);
		aboutItem.setBackground(Color.BLACK);
		aboutItem.setForeground(Color.CYAN);
		exitItem.setBackground(Color.BLACK);
		exitItem.setForeground(Color.CYAN);
		minimizeItem.setBackground(Color.BLACK);
		minimizeItem.setForeground(Color.CYAN);
		menuBar.setForeground(Color.CYAN);
		states.setForeground(Color.CYAN);
		about.setForeground(Color.CYAN);
		menuBar.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		states.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		about.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		aboutItem.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		menuItem.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		exitItem.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		minimizeItem.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		
		menuItem.setFocusable(false);
		minimizeItem.setFocusable(false);
		aboutItem.setFocusable(false);
		exitItem.setFocusable(false);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dust.GetInstance().menu.SetCurrentState();
				System.out.println("[DustOS]: Returning to Home Menu state");
			}
			
		});
		
		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "DustOS v "+Config.VERSION+"\nA prototype OS for CardboardPC\n Created by David Tan");
			}
			
		});
		
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Computer.getInstance().GetOS().Destruct();
				Dust.GetInstance().allowShutDown = true;
				System.out.println("[DustOS]: Shutting down...");
			}
			
		});
		
		minimizeItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setExtendedState(JFrame.ICONIFIED);
			}
			
		});
		
		states.add(menuItem);
		states.add(minimizeItem);
		states.add(exitItem);
		about.add(aboutItem);
		
		menuBar.add(states);
		menuBar.add(about);
		menuBar.setBounds(0, 0, 800, 25);
		
		frame = new JFrame(Config.APP_TITLE);
		frame.setSize(Config.APP_WIDTH, Config.APP_HEIGHT);
		frame.setBounds(0, 0, Config.APP_WIDTH, Config.APP_HEIGHT);
		frame.setResizable(false);
		frame.setUndecorated(Dust.GetInstance().disableTitleBar);
		frame.setLocationRelativeTo(null);
		FrameDragListener frameDragListener = new FrameDragListener(frame);
        frame.addMouseListener(frameDragListener);
        frame.addMouseMotionListener(frameDragListener);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(this);
		frame.add(menuBar);
		
		frame.setIconImage(Dust.loadImage("resources/dust.png"));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;

		super.paintComponent(g2);
		
		setBackground(Color.BLACK);
		
		if(!Dust.IsInitialized()) {
			g.setColor(Color.CYAN);
			g.setFont(new Font(Font.SERIF, Font.BOLD, 20));
			g.drawString("Starting up DustOS...", 360, 390);
		}
		if(Dust.states.CurrentState() != null)
			Dust.states.CurrentState().Render(g2);
	}
	
	public void AddComponent(JComponent component) {
		if(component == null || components == null)
			return;
		
		boolean exists = false;
		for(JComponent jc : components) {
			if(jc != null) {
				if(jc == component) {
					exists = true;
					break;
				}
			}
		}
		 
		if(exists)
			return;

		add(component);
		components.add(component);
	}
	
	public void RemoveComponent(JComponent component) {
		if(component == null || components == null)
			return;
		
		if(!components.contains(component))
			return;
		
		remove(component);
		components.remove(component);
	}
	
	public void Update() {
		revalidate();
		repaint();
	}
	
	/** Variables **/
	private static Display instance = null;
	public JFrame frame = null;
	public List<JComponent> components = null;

	public JMenuBar menuBar = null;
	public JMenu states = null, about = null;
	
	//Takes you back to the menu state
	public JMenuItem menuItem = null, aboutItem = null, exitItem = null, minimizeItem = null;
}
