package dataRepresentation;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;




public class AuctionContext {
	private int round;
	private int duration_Sec;
	private ArrayList<AuctionItem> itemList;
	private float minIncreament;
	private boolean finalRound;
	
	public AuctionContext() {
		this.round = 1;
		this.duration_Sec = 60;
		this.itemList = new ArrayList<AuctionItem>();
		this.minIncreament = 0;
		this.finalRound = false;
	}
	
	public AuctionContext(int time, ArrayList<AuctionItem> list) {
		this.round = 1;
		this.duration_Sec = time;
		this.itemList = list;
		this.minIncreament = 0;
		this.finalRound = false;
	}
	
	public String generateXml() {  
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
	    	return null;
	    }   
	    child = doc.createElement("round");
	    child.setAttribute("value", Integer.toString(this.round));
	    child.setAttribute("final", this.finalRound?"yes":"no");
	    root.appendChild(child);
	    child = doc.createElement("duration");
	    child.setAttribute("value", Integer.toString(this.duration_Sec));
	    root.appendChild(child);
	    child = doc.createElement("minimum_increament");
	    child.setAttribute("value", Float.toString(this.minIncreament));
	    root.appendChild(child);
	    
	    int len = itemList.size();
	    for (int i=0; i<len; i++) {  
	    	AuctionItem item = itemList.get(i);  
	    	child = doc.createElement("item");  
	    	child.setAttribute("name", item.getName());  
	    	child.setAttribute("price", Float.toString(item.getPrice())); 
	    	child.setAttribute("owner", item.getOwner());  
	    	root.appendChild(child);  
	    }  
	    /* 
	     * Convert Document into String
	     */
	    StringWriter sw = new StringWriter();
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer;
		try {
			transformer = tf.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");  
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.transform(new DOMSource(doc), new StreamResult(sw));
		
			return sw.toString();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }  
	public static void outputXml (Document doc, String fileName){  
	      TransformerFactory tf = TransformerFactory.newInstance();  
	      Transformer transformer;
		try {
			transformer = tf.newTransformer();
			DOMSource source = new DOMSource(doc);  
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");  
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");//设置文档的换行与缩进  
			PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));  
			StreamResult result = new StreamResult(pw);  
			transformer.transform(source, result);  
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	     System.out.println("生成XML文件成功!");  
	}
	public static void main (String args []) { 
    	String outputPath = "./testAuctionContext.xml";
    	try {    
    		//Document doc = generateXml(list);//生成XML文件  
    		//outputXml(doc, outputPath);//将文件输出到指定的路径  
    	} catch (Exception e) {  
    		System.err.println("出现异常");  
    	}  
    }  
}
