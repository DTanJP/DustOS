package Dust.state;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import Dust.Display;
import Dust.Dust;
import Dust.api.state.State;
import Dust.gui.DustButton;
import Plugin.Plugin;

/**
 * AppState.java
 * 
 * @author David Tan
 **/
public class AppState extends State {

	/** Constants **/
	public static final String MAIN_JPANEL = "main-jpanel";
	
	
	/** Constructor **/
	public AppState() {
		appHome = new DustButton("Back");
		appHome.setBounds(10, 30, 50, 30);
		AddComponent("appStateButton", appHome);
		Display.GetInstance().setBackground(Color.BLACK);
	}
	

	@Override
	public void Enter() {
		if(Dust.GetInstance().currentApplication != null) 
			Dust.GetInstance().currentApplication.OnEnable();
	}

	@Override
	public void Update() {
		if(appHome.isPressed() || Dust.GetInstance().currentApplication == null) {
			if(Dust.GetInstance().currentApplication == null)
				System.out.println("[DustOS]: Error! Application is invalid.");
			Dust.GetInstance().appMenu.SetCurrentState();
			return;
		}
		
		Dust.GetInstance().currentApplication.Update();
	}

	@Override
	public void Render(Graphics2D g) {
	}

	@Override
	public void Exit() {
		appComponents.values().stream().forEach(c -> display.remove(c));
		appComponents.clear();
		Dust.GetInstance().currentApplication.Destruct();
		Dust.GetInstance().currentApplication = null;
	}
	
	public void HandleRequest(Plugin app, String cmd, Object... params) {
		switch(cmd.toLowerCase()) {
		case "add-component": //Attaches a component to this state
			handleCreate(params);
			break;
			
		case "remove-component"://Removes a component from this state
			if(params[0] instanceof String)
				handleRemove(params[0].toString());
			else if(params[0] instanceof JComponent)
				handleRemove((JComponent)params[0]);
			break;
			
		case "quit":
		case "exit":
			Dust.GetInstance().appState.SetCurrentState();
			break;
		}
	}
	
	private void handleCreate(Object[] params) {
		if(params == null || params.length == 0)
			return;
		if(params[0] instanceof String) {
			if(params[1] instanceof JComponent) {
				//Must exist
				if(params[1] == null || params[0] == null || params[0].toString().isEmpty())
					return;
				//Must be unique
				if(components.containsValue(params[1]) || components.containsKey(params[0]))
					return;
				appComponents.put(params[0].toString(), (JComponent)params[1]);
				display.add((JComponent)params[1]);
			}
		}
	}
	
	private void handleRemove(Object component) {
		if(component == null) return;
		if(component instanceof JComponent) {
			if(appComponents.containsValue(component)) {
				((JComponent) component).setVisible(false);
				((JComponent) component).setEnabled(false);
				display.remove((JComponent)component);
				for(String s : appComponents.keySet()) {
					if(appComponents.get(s) == ((JComponent) component))
						appComponents.remove(s);
				}
			}
		} else if(component instanceof String) {
			if(component.toString().isEmpty()) return;
			if(appComponents.containsKey(component.toString())) {
				appComponents.get(component.toString()).setVisible(false);
				appComponents.get(component.toString()).setEnabled(false);
				display.remove(appComponents.get(component.toString()));
				appComponents.remove(component.toString());
			}
		}
	}
	
	/** Variables **/
	public DustButton appHome = null;
	private Map<String, JComponent> appComponents = new HashMap<>();
}
