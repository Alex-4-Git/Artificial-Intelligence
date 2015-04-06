package post_Correspondence_Problem;



public class State implements Cloneable{

	
	public boolean isSolution=false;
	public boolean isValide=true;
	public boolean isTop=true;
	public String state = "";
	
	public State(){
		isSolution=false;
		isValide=true;
		isTop=true;
		state = "";
	}
	
	
//	public ArrayList<String> list = new ArrayList<String>();
	
	@Override	public State clone() throws CloneNotSupportedException{
		return (State)super.clone();
	}

}
