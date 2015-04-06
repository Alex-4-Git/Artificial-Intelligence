package post_Correspondence_Problem;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Solver {
	public Hashtable<String, ArrayList<String>> topIsLonger = new Hashtable<String, ArrayList<String>>();
	public Hashtable<String, ArrayList<String>> bottomIsLonger = new Hashtable<String, ArrayList<String>>();

	
	public int maxQueueSize=5;
	public int maxNumOfStates = 50;
	public int stateSize = 0;
	
	public ArrayList<String>  solution = null;
	//no solution exists
	public boolean noSolution = false;
	
	//if no solution was found within the limits of search
	
	public boolean reachMaxNumOfStates = false;
	
	private Queue<State> state_queue = new LinkedList<State>();
	private ArrayList<String> states_sequence = new ArrayList<String>();
	
	
	//change the value of state s and return it. So pay attention to the change of state s
	public State concatenateState (Domino d, State s){
		String bottomString = d.bottom;
		String topString = d.top;
		
		if(s.isTop) topString = topString + s.state;
		else bottomString = bottomString + s.state;
		
		if(topString.length()==bottomString.length()){
			if(topString.equals(bottomString)) {
				s.isSolution = true;
				return s;
			}
			else{
				s.isValide = false;
				return s;
			}
		}
		else if(topString.length()>bottomString.length()){
			s.isTop = true;
			int index = topString.lastIndexOf(bottomString);
			boolean valid = topString.endsWith(bottomString);
			if(!valid){
				s.isValide = false;
				return s;
			}
			else{
				s.state = topString.substring(0,index);
				return s;
			}
		}
		else{
			s.isTop = false;
			int index = bottomString.lastIndexOf(topString);
			boolean valid = bottomString.endsWith(topString);
			if(!valid){
				s.isValide = false;
				return s;
			}
			else{
				s.state = bottomString.substring(0,index);
				return s;
			}
			
		}
		
		
	}

	

	
	public void firstStage(Domino[] dominos) throws CloneNotSupportedException{
		State initial_state = new State();
		state_queue.add(initial_state);
		
		while(!state_queue.isEmpty()){
			//make sure all validate children can be add into state_queue, otherwise add current node back
			Queue<State> tmpQueue = new LinkedList<State>();
			
			State current = state_queue.remove();
			for(int i=0; i<dominos.length; ++i){
				//create child and put it in tmpQueue tmperaly 
				State newState = createChild(current, dominos[i]);
				if(newState!=null){
					if(solution!=null) return;
					if(reachMaxNumOfStates) return;
					tmpQueue.add(newState);
				}
			}
			
			if(tmpQueue.size()+state_queue.size()==maxQueueSize){
				state_queue.addAll(tmpQueue);
				return;
			}
			else if(tmpQueue.size()+state_queue.size()<maxQueueSize){
				state_queue.addAll(tmpQueue);
			}
			else{
				state_queue.add(current);
				//delete states from hash tables
				deleteStatesFromQueue(tmpQueue);
				return;
			}
		}
	}
	
	
	//create a new state by an old state and a Domino
	//add state into HashTable
	//pay attention to the old state, we should use the clone of the old state
	public State createChild(State current, Domino d) throws CloneNotSupportedException
	{
		State target = current.clone();
		String keyState = target.state;
		State newState = concatenateState(d, target);
		ArrayList<String> doms;
		ArrayList<String> original_doms;
		
		//target has been changed, so we can only use current 
		original_doms = get_dominos(current);
		
		if(newState.isValide == false) return null;
		
		if(newState.isTop){
			if(original_doms == null) {
				doms = new ArrayList<String>();
			}
			else{
				doms = (ArrayList<String>) original_doms.clone();
			}
			doms.add(0,d.name);
			if(newState.isSolution) {
				solution = doms;
				return newState;
			}
			else{
				if(topIsLonger.contains(newState.state))
					return null;
				else{
					addState(newState, doms, topIsLonger);
					if(stateSize==maxNumOfStates) {
						reachMaxNumOfStates = true;
					}
					return newState;
				}
			}
		}
		
		// newState's bottom is longer
		else{
			
			if(original_doms == null) {
				doms = new ArrayList<String>();
			}
			else{
				doms = (ArrayList<String>) original_doms.clone();
			}
			doms.add(0,d.name);
			if(newState.isSolution) {
				solution = doms;
				return newState;
			}
			else{
				if(bottomIsLonger.contains(newState.state))
					return null;
				else{
					addState(newState, doms, bottomIsLonger);
					if(stateSize==maxNumOfStates) {
						reachMaxNumOfStates = true;
					}
					return newState;
				}
			}
		}
	}
	

	public void secondStage(Domino[] dominos) throws CloneNotSupportedException{
		int max_depth = maxNumOfStates - topIsLonger.size() - bottomIsLonger.size();
		for(int i = 1;i<=max_depth;i++){
			Iterator<State> it = state_queue.iterator();
			while(it.hasNext()){
				State newRoot = it.next();
				iterativeDeepening(dominos, newRoot, i);
				if(solution!=null) return;
			}
		}
		noSolution = true;
	}
	
	//starting from root do a DFS to depth 
	public void iterativeDeepening(Domino[] dominos, State root, int depth) throws CloneNotSupportedException{
		if(depth<=0) return;
		for(int i=0;i<dominos.length;i++){
			State child = createChild(root, dominos[i]);
			if(solution!=null) return;
			if(child!=null){
				iterativeDeepening(dominos, child, depth-1);
				deleteFromTable(child);
			}
			
		}
	}
	
	
	public ArrayList<String> get_dominos(State s){
		if(s.isTop)
			return topIsLonger.get(s.state);
		else
			return bottomIsLonger.get(s.state);
	}
	
	private void addState(State newState, ArrayList<String> doms, Hashtable<String, ArrayList<String>> table){
		table.put(newState.state, doms);
		stateSize++;
	}
	
	private void deleteStatesFromQueue(Queue<State> q){
		Iterator<State> it = q.iterator();
		while(it.hasNext()){
			State child = it.next();
			deleteFromTable(child);
		}
	}
	
	private void deleteFromTable(State s){
		if(s.isTop) topIsLonger.remove(s.state);
		else bottomIsLonger.remove(s.state);
		stateSize--;
	}
	
	public void printSolution(){
		System.out.println("We find solution!");
		for(String s: solution){
			System.out.print(s+" ");
		}
		System.out.println();
	}
	
	public void print_states_sequence(){
		System.out.print("The states squence is: ");
		for(String s: states_sequence){
			System.out.print(s+" ");
		}
		System.out.println();
	}
	
	public void generate_states_sequence(Domino[] dominos) throws CloneNotSupportedException{
//		ArrayList<String> states_sequence = new ArrayList<String>();
		State currentState = new State();
		for(int i=solution.size()-1;i>0;i--){
			String s = solution.get(i);
			Domino d = getDomino(dominos, s);
			State newState = concatenateState(d, currentState);
			String str = newState.clone().state;
			states_sequence.add(str);
			currentState = newState;
		}
	}
	
	private Domino getDomino(Domino[] dominos, String name){
		String num = name.substring(1);
		int index = Integer.parseInt(num);
		if(index<=dominos.length) return dominos[index-1];
		return null;
	}
}
