package post_Correspondence_Problem;

public class Domino {
	private static int index =1;
	public String name;
	public String bottom;
	public String top;
	public Domino(String t, String b){
		name = "D"+index;
		bottom = b;
		top = t;
		index++;
	}
	
}
