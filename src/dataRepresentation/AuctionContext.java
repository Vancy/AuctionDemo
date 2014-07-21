package dataRepresentation;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class AuctionContext {
	private int round;
	private int duration_Sec;
	private ArrayList<AuctionItem> itemList;
	private boolean finalRound;
	
	public AuctionContext() {
		this.round = 1;
		this.duration_Sec = 60;
		this.itemList = new ArrayList<AuctionItem>();
		finalRound = false;
	}
	
	public AuctionContext(int time, ArrayList<AuctionItem> list) {
		this.round = 1;
		this.duration_Sec = time;
		this.itemList = list;
		finalRound = false;
	}
	
	public Document generateXml() {  
	    Document doc = null;  
	    Element root = null;  
	    Element child = null; 
	    try {  
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();  
	    	doc = builder.newDocument();  
	    	root = doc.createElement("auction_context");  
	    	doc.appendChild(root);  
	    } catch (Exception e) {  
	    	e.printStackTrace();  
	    	return null;//如果出现异常，则不再往下执行  
	    }   
	    child = doc.createElement("round");
	    child.setAttribute("value", Integer.toString(this.round));
	    child.setAttribute("final", this.finalRound?"yes":"no");
	    root.appendChild(child);
	    child = doc.createElement("duration");
	    child.setAttribute("value", Integer.toString(this.duration_Sec));
	    root.appendChild(child);
	    
	    int len = itemList.size();
	    for (int i=0; i<len; i++) {  
	    	AuctionItem item = itemList.get(i);  
	    	child = doc.createElement("item"+i);  
	    	child.setAttribute("name", item.getName());  
	    	child.setAttribute("price", Float.toString(item.getPrice())); 
	    	child.setAttribute("owner", item.getOwner());  
	    	root.appendChild(child);  
	    }  
	   return doc; 
    }  
	private static void outputXml (Document doc, String fileName) throws Exception{  
	      TransformerFactory tf = TransformerFactory.newInstance();  
	      Transformer transformer = tf.newTransformer();  
	      DOMSource source = new DOMSource(doc);  
	      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");  
	      transformer.setOutputProperty(OutputKeys.INDENT, "yes");//设置文档的换行与缩进  
	      PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));  
	      StreamResult result = new StreamResult(pw);  
	      transformer.transform(source, result);  
	      System.out.println("生成XML文件成功!");  
	}
	public static void main (String args []) { 
    	String outputPath = "./HAHAHA.xml";
    	try {    
    		//Document doc = generateXml(list);//生成XML文件  
    		//outputXml(doc, outputPath);//将文件输出到指定的路径  
    	} catch (Exception e) {  
    		System.err.println("出现异常");  
    	}  
    }  
}
