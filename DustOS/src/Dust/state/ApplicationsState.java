package Dust.state;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.cardboard.dtanjp.Computer;

import Dust.Config;
import Dust.Display;
import Dust.Dust;
import Dust.api.state.State;
import Dust.gui.DustButton;

/**
 * ApplicationsState.java
 * Allows the user to select which application to run
 * 
 * @author David Tan
 **/
public class ApplicationsState extends State {

	/** Constructor **/
	public ApplicationsState() {
		appsPanel = new JPanel();
		appsPanel.setBackground(Color.BLACK);
		appsPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		appsPanel.setPreferredSize(new Dimension(Config.APP_WIDTH-60, (Config.APP_HEIGHT - 100) + ((buttons.length-1) * 120)));
		appsPanel.setLayout(null);
		
		scrollpane = new JScrollPane(appsPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBackground(Color.BLACK);
		scrollpane.setForeground(Color.CYAN);
		scrollpane.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		scrollpane.setBounds(30, 80, Config.APP_WIDTH-60, Config.APP_HEIGHT-100);
		scrollpane.setWheelScrollingEnabled(true);
		scrollpane.setVisible(false);
		
		String names[] = new String[buttons.length];
		Computer.getInstance().GetApplications().keySet().toArray(names);
		int count = 0;
		int dy = 0;
		for(int i=0; i<buttons.length-1;i++) {
			final String name = names[i];
			buttons[i] = new DustButton(name);
			
			//Set position
			buttons[i].setBounds(10 + (count * 120), 10 + (dy * 120), 100, 100);
			buttons[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Computer.getInstance().GetApplications().get(name).InitiateApplicationClass();
						Dust.GetInstance().currentApplication = Computer.getInstance().GetApplications().get(name).GetApplication();
						Dust.GetInstance().appState.SetCurrentState();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				
			});
			appsPanel.add(buttons[i]);
			count++;
			if(count == 6) {
				count = 0;
				dy++;
			}
		}
		
		menuHome = new DustButton("Menu");
		menuHome.setBounds(10, 30, 50, 30);
		AddComponent("menuButton", menuHome);
		AddComponent("scrollPanel", scrollpane);
	}
	
	@Override
	public void Enter() {
		EnableAllComponents();
	}

	@Override
	public void Update() {
		if(menuHome.isPressed())
			Dust.GetInstance().menu.SetCurrentState();
	}

	@Override
	public void Render(Graphics2D g) {
		Display.GetInstance().setBackground(Color.BLACK);
	}

	@Override
	public void Exit() {
		DisableAllComponents();
	}
	
	/** Variables **/
	public DustButton menuHome = null;
	public JPanel appsPanel = null;
	private JScrollPane scrollpane = null;
	public JButton[] buttons = new JButton[Computer.getInstance().GetApplications().size()+1];
}
