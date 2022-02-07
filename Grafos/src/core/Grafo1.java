package core;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class Grafo1{
	int tempo;

	public DirectedSparseMultigraph<String, EdgeType> getWg(){
		return wg;
	}

	public void setWg(DirectedSparseMultigraph<String, EdgeType> wg){
		this.wg=wg;
	}

	private DirectedSparseMultigraph<String, EdgeType> wg;
	/**
	 * 
	 * @param graphFile
	 * carrega um grafo do arquivo de entrada
	 */
	public void load(String graphFile){
		this.wg=new DirectedSparseMultigraph<String, EdgeType> ();
		BufferedReader buffread;
	 	String linha;
	  	List<String> vertices, result;
	  	int wei, i;
	  	try{
		  	//obtem os vértices primeiro
			buffread=new BufferedReader(new FileReader(graphFile));
			i=0;
			linha=buffread.readLine();
			vertices=Arrays.asList(linha.split("\\s* \\s*"));
			for(String v : vertices){
				this.wg.addVertex(v);
			}
			i=0;
			while((linha=buffread.readLine())!=null){
				result=Arrays.asList(linha.split("\\s* \\s*"));
				for(String v : result){
					wei=Integer.valueOf(v);
					if(wei!=0){
   					 	EdgeType e=new EdgeType(wei);
					 	this.wg.addEdge(e, vertices.get(i), vertices.get(result.indexOf(v)));
					}
				}
			   	i++;
			   	System.out.print("reg" + i + ": ");
			   	System.out.println(result);
			}
			buffread.close();        
       	}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void mostraGrafo1(DirectedSparseMultigraph<String, EdgeType> g){
		Layout<String, EdgeType> layout=new CircleLayout<String, EdgeType>(g);
		layout.setSize(new Dimension(300,300));
		BasicVisualizationServer<String, EdgeType> vv=new BasicVisualizationServer<String, EdgeType>(layout);
		vv.setPreferredSize(new Dimension(350,350));
		//Setup up a new vertex to paint transformer...
		Transformer<String, Paint> vertexPaint=new Transformer<String, Paint>(){public Paint transform(String i){return Color.YELLOW;}};  
		Transformer<EdgeType,String> edgePaint=new Transformer<EdgeType,String>(){public String transform(EdgeType i){return String.valueOf(i.getWeight());}};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
		vv.getRenderContext().setEdgeLabelTransformer(edgePaint);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		JFrame frame=new JFrame("Visualização de um Grafo Ponderado");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	//Esta visualização ignora os pesos do grafo
	public void mostraGrafo2(DirectedGraph<String, EdgeType> g){
		Layout<String, EdgeType> layout=new CircleLayout<String, EdgeType>(g);
		layout.setSize(new Dimension(300,300));
		BasicVisualizationServer<String, EdgeType> vv=new BasicVisualizationServer<String, EdgeType>(layout);
		vv.setPreferredSize(new Dimension(350,350));       
		// Setup up a new vertex to paint transformer...
		Transformer<String,Paint> vertexPaint=new Transformer<String,Paint>(){public Paint transform(String i){return Color.YELLOW;}};  
		Transformer<EdgeType,String> edgePaint=new Transformer<EdgeType,String>(){public String transform(EdgeType i){return "";}};       	     
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
		vv.getRenderContext().setEdgeLabelTransformer(edgePaint);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);        
		JFrame frame=new JFrame("Grafo de Dependência do Caminho");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	class VData{
		int cor, //flag usado no algoritmo para cada vertice
		td, //tempo de descoberta do vertice
		tt; //tempo de termino do vertice
		String pred; //predecessor antecessor do vertice na busca tanto no DFS quanto no BFS
		int dist;
	}
	//HashMap<String,VData> dVertices=new HashMap<String,VData>();
	public void printVData(VData vd){
		System.out.println("cor pred td tt");
		System.out.println(vd.cor + "    "+ vd.pred + "   "+ vd.td + "   "+ vd.tt);
	}
	


    public void DFS(DirectedGraph<String, EdgeType> g) {
		HashMap<String,VData> dVertices = new HashMap<String,VData>();
		/* inicializando os dados dos vértices */
		tempo= 0;
		for(String u: g.getVertices()) {
			VData vd = new VData();
			vd.cor=0; // 0 é branco
			vd.pred=null;
			dVertices.put(u, vd);
		}
		for(String u: g.getVertices()) {
			if(dVertices.get(u).cor==0) {// verifico se a cor é branca
			   visita(g,dVertices,u);
			}
		}
 
		/* a partir daqui o código cria um grafo a partir do resultado do DFS */
		/*
		 * Adiciono os vértices
		 */
		DirectedGraph<String, EdgeType> gDFS=new DirectedSparseMultigraph<String, EdgeType> ();
		for(String u: g.getVertices()) {
			printVData(dVertices.get(u));
			gDFS.addVertex(u);
 
		}
 /*
  * adiciono as arestas 	    
  */
		for(String u: g.getVertices()) {
			String v =dVertices.get(u).pred;
			if(v!=null) {
				EdgeType e = new EdgeType(1);
				gDFS.addEdge(e,v, u);
				
			}
		}  
		
	   /*
		* chama o mostra grafo 2 para exibir o grafo gerado pelo conjunto de antecessores gerado pel DFS 
		*/
		this.mostraGrafo2(gDFS);
	}
	 
	 /* método visita do DFS
	  *  
	  */
	 
	 private void visita(DirectedGraph<String, EdgeType> g, HashMap<String, VData> dVertices, String u) {
		 VData ud = dVertices.get(u);
		 ud.cor=1; //cinza
		 tempo++;
		 ud.td=tempo;
		 for(String v: g.getNeighbors(u)) { // pega lista de adjacentes de u
			 VData vd = dVertices.get(v);
			 if (vd.cor==0) {// se a cor do adjacente é branco
				 vd.pred=u;
				 visita(g,dVertices,v);
			 }
		 }
		 ud.cor=2; // pinta a cor do vértice visitado de preto quando termino seus adjacentes
		 tempo++; // incremento mais uma vez o tempo
		 ud.tt=tempo; // atribuo o tempo de termino para u
	 }
	
	public static void main(String[] args){
		Grafo1 g=new Grafo1();
		g.load("g1.txt");
		g.mostraGrafo1(g.wg);
		g.DFS(g.wg);
		//Grafo1 g2=new Grafo1();
		//g2.load("g1.txt");
		//g2.mostraGrafo1(g2.wg);
		//g2.BFS(g2.wg);
	}
}