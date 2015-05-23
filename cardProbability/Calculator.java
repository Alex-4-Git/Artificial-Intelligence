package cardProbability;

import java.util.Arrays;

public class Calculator {
	private int Ncards;
	private int LTarget;
	private int UTarget;
	private int[][] play;
	private double[][][] prob;
	
	public Calculator(int Ncards, int LTarget, int UTarget){
		if(Ncards<=0||LTarget<=0||UTarget<=0||LTarget>UTarget){
			throw new IllegalArgumentException();
		}
		this.Ncards = Ncards;
		this.LTarget = LTarget;
		this.UTarget = UTarget;
		play = new int[LTarget][LTarget];
		prob = new double[LTarget][LTarget][2];
		
		for(int i=0;i<play.length;i++){
			for(int j=0;j<play[0].length;j++){
				play[i][j]=-1;
			}
		}
		
		for(int i=0;i<prob.length;i++){
			for(int j=0;j<prob[0].length;j++){
				for(int k=0;k<2;k++){
					prob[i][j][k]=-1.0;
				}
			}
		}
	}
	
	public void go(){
		for(int sum=2*LTarget-2;sum>=0;sum--){
			if(sum>=LTarget){
				for(int x=LTarget-1;x>=sum+1-LTarget;x--){
					int y=sum-x;
					play(x,y);
				}
			}
			if(sum<LTarget){
				for(int x=sum;x>=0;x--){
					int y=sum-x;
					play(x,y);
				}
			}
		}
	}
	
	private double proOfDraw(int x, int y){
		if(prob[x][y][1]>0) return prob[x][y][1];
		double probWinning=0.0;
		for(int i=1;i<Ncards+1;i++){
			double probYWins=0.0;
			if(x+i>UTarget){
				probYWins=1.0;
			}else if(x+i>=LTarget){
				probYWins=0.0;
			}else{
				probYWins=prob[y][x+i][play(y,x+i)];
			}
			probWinning+=(1-probYWins)/Ncards;
		}
		prob[x][y][1]=probWinning;
		return probWinning;
	}
	
	private double proOfPass(int x,int y){
		if(prob[x][y][0]>0) {
			return prob[x][y][0];
		}
		if(x<=y){
			prob[x][y][0]=0.0;
		}else{
			prob[x][y][0]=1-proOfDraw(y,x);
		}
		return prob[x][y][0];
	}

	private int play(int x, int y) {
		if(play[x][y]>=0){
			return play[x][y];
		}
		if(proOfDraw(x, y)>proOfPass(x, y)){
			play[x][y]=1;
		}else{
			play[x][y]=0;
		}
		return play[x][y];
	}
	
	public void printPlayTable(){
		for(int i=0;i<play.length;i++){
			for(int j=0;j<play[0].length;j++){
				System.out.println(String.format("play[%d,%d] = %d",i,j,play[i][j]));
			}
		}
	}
	
	public void printProbTable(){
		for(int i=0;i<prob.length;i++){
			for(int j=0;j<prob[0].length;j++){
				for(int k=0;k<2;k++){
					System.out.println(String.format("prob[%d,%d,%d] = %f",i,j,k,prob[i][j][k]));
				}
			}
		}
	}
}
