package Dust.api.state;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import Dust.Display;
/**
 * State.java
 * 
 * @author David Tan
 **/
public abstract class State {
	
	public abstract void Enter();
	public abstract void Update();
	public abstract void Render(Graphics2D g);
	public abstract void Exit();
	
	/** Leaving permanently: Free up the resources **/
	protected void Terminate() {
		StateManager.getInstance().RemoveState(this);
		components.clear();
	}
	
	/** Sets the current state to this **/
	public void SetCurrentState() {
		StateManager.getInstance().SetCurrent(this);
	}
	
	/** Register a JComponent **/
	public void AddComponent(String name, JComponent component) {
		//Must exist
		if(component == null || name == null || name.isEmpty())
			return;
		//Must be unique
		if(components.containsValue(component) || components.containsKey(name))
			return;
		if(StateManager.getInstance().CurrentState() != this) {
			component.setVisible(false);
			component.setEnabled(false);
		}
		components.put(name, component);
		display.add(component);
	}
	
	public void RemoveComponent(String name) {
		if(name == null || name.isEmpty())
			return;
		if(components.containsKey(name))
			components.remove(name);
	}
	
	
	public void RemoveComponent(JComponent component) {
		if(component == null || !components.containsValue(component))
			return;
		
		for(String name : components.keySet()) {
			if(components.get(name) == component) {
				components.remove(name);
				break;
			}
		}
	}
	
	public void EnableAllComponents() {
		if(StateManager.getInstance().CurrentState() == this) {
			for(String s : components.keySet()) {
				components.get(s).setEnabled(true);
				components.get(s).setVisible(true);
			}
		}
	}
	
	public void DisableAllComponents() {
		for(String s : components.keySet()) {
			components.get(s).setEnabled(false);
			components.get(s).setVisible(false);
		}
	}
	
	/** Variables **/
	public boolean initialized = false;
	public final int ID = StateManager.getInstance().AddState(this);
	protected Map<String, JComponent> components = new HashMap<>();
	public final static Display display = Display.GetInstance();
}
