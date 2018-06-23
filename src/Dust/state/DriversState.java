package Dust.state;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.cardboard.dtanjp.Computer;

import Dust.Display;
import Dust.Dust;
import Dust.api.state.State;
import Dust.gui.DustButton;

public class DriversState extends State {

	/** Constructor **/
	public DriversState() {
		driversPanel = new JPanel();
		driversPanel.setBackground(Color.BLACK);
		driversPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		driversPanel.setPreferredSize(new Dimension(320, 10 + (buttons.length * 120)));
		driversPanel.setLayout(null);
		
		scrollpane = new JScrollPane(driversPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBackground(Color.BLACK);
		scrollpane.setForeground(Color.CYAN);
		scrollpane.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		scrollpane.setBounds(10, 70, 340, 680);
		scrollpane.setWheelScrollingEnabled(true);
		
		buttons[0] = new DustButton("None");
		buttons[0].setBounds(10, 10, 300, 100);
		buttons[0].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
						Dust.GetInstance().selectedDriver = null;
				}
				
			});
		driversPanel.add(buttons[0]);
		String names[] = new String[buttons.length-1];
		Computer.getInstance().GetDrivers().keySet().toArray(names);
		for(int i=1; i<buttons.length;i++) {
			final String name = names[i-1];
			buttons[i] = new JButton(name);
			buttons[i].setBackground(Color.BLACK);
			
			//Enable/Disable status
			if(Dust.GetInstance().EnabledDrivers.get(name)) {
				buttons[i].setForeground(Color.CYAN);
				buttons[i].setBorder(BorderFactory.createLineBorder(Color.CYAN));
			} else {
				buttons[i].setForeground(Color.DARK_GRAY);
				buttons[i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
			}
			
			//Set position
			buttons[i].setBounds(10, 10 + (i * 120), 300, 100);
			buttons[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if(Dust.GetInstance().EnabledDrivers.get(name))
						Dust.GetInstance().selectedDriver = Computer.getInstance().GetDrivers().get(name).GetDriver();
						Dust.GetInstance().selectedDriver_name = name.replace(".jar", "");
				}
				
			});
			driversPanel.add(buttons[i]);
		}
		if(buttons.length > 5)
			scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
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
		if(Dust.GetInstance().selectedDriver != null) {
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
			g.setColor(Color.CYAN);
			g.drawString("Currently selected driver: "+Dust.GetInstance().selectedDriver_name, 70, 50);
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		}
	}

	@Override
	public void Exit() {
		DisableAllComponents();
	}
	
	/** Variables **/
	public DustButton menuHome = null;
	public JPanel driversPanel = null;
	private JScrollPane scrollpane = null;
	public JButton[] buttons = new JButton[Computer.getInstance().GetDrivers().size()+1];
}
