package core;

public class EdgeType{
	private int weight; 	
	
	public int getWeight(){
		return weight;
	}
	
	public void setWeight(int weight){
		this.weight=weight;
	}
		
	public EdgeType(int w){
	    this.weight=w;
	}
}