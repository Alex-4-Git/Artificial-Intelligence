package cardProbability;

public class BlakJack {
	public static void main(String[] args) throws Exception{
		if(args.length!=3){
			throw new Exception("Please input 3 paramter:NCards,LTarget,UTarget");
		}
		int nCards = Integer.parseInt(args[0]);
		int lTarget = Integer.parseInt(args[1]);
		int uTarget = Integer.parseInt(args[2]);
		
		Calculator cal = new Calculator(nCards,lTarget,uTarget);
		cal.go();
		cal.printPlayTable();
		cal.printProbTable();
	}

}
