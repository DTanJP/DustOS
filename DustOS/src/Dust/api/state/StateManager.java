package Dust.api.state;

import java.util.HashMap;
import java.util.Map;

/**
 * StateManager.java
 * 
 * @author David Tan
 */
public class StateManager {

	/** Constructor **/
	private StateManager() {
		states = new HashMap<>();
	}

	/** Singleton **/
	public static StateManager getInstance() {
		if(instance == null)
			instance = new StateManager();
		return instance;
	}
	
	/** Adds a state **/
	public int AddState(State state) {
		if(state == null || states.containsValue(state))
			return -1;
		
		int num = statesNum;
		statesNum++;
		if(states.get(num) == null) {
			states.put(num, state);
			return num;
		}
		return -1;
	}
	
	/** Removes a state **/
	public boolean RemoveState(State state) {
		if(state == null)
			return false;
		if(states.containsKey(state.ID)) {
			states.remove(state.ID);
			return true;
		}
		return false;
	}
	
	/** Removes a state by ID **/
	public boolean RemoveState(int id) {
		if(id < 0)
			return false;
		if(states.containsKey(id)) {
			states.remove(id);
			return true;
		}
		return false;
	}
	
	/** Returns current state **/
	public State CurrentState() {
		return currentState;
	}
	
	/** Set the current state **/
	public void SetCurrent(State state) {
		//Don't reenter the same state
		if(currentState == state)
			return;
		
		//Disable all components
		if(currentState != null) {
			currentState.Exit();
			if(currentState.components != null) {
				currentState.components.values().stream()
				.filter(c -> c != null)
				.filter(c -> c.isVisible() || c.isEnabled())
				.forEach(c -> {
					c.setVisible(false);
					c.setEnabled(false);
				});
			}
		}
		
		if(!states.containsValue(state))
			return;
		
		currentState = state;
		
		//Reenable all of their components
		if(currentState != null) {
			currentState.Enter();
			if(currentState.components != null) {
				currentState.components.values().stream()
				.filter(c -> c != null)
				.filter(c -> !c.isVisible() || !c.isEnabled())
				.forEach(c -> {
					c.setVisible(true);
					c.setEnabled(true);
				});
			}
		}
	}
	
	/** Returns a state **/
	public State GetState(int id) {
		if(id < 0)
			return null;
		if(states.containsKey(id))
			return states.get(id);
		return null;
	}
	
	/** Returns the number of states **/
	public int size() {
		return states.size();
	}
	
	/** Variables **/
	private static StateManager instance = null;
	private final Map<Integer, State> states;
	private State currentState = null;
	private static int statesNum = 0;
}
