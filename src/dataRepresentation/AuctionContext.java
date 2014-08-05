package dataRepresentation;


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
	public static enum AuctionType {SAA, CCA, ULA};
	private AuctionType type;
	private int round;
	private int duration_Sec;
	private ArrayList<AuctionItem> itemList;
	private double minIncreament;
	private boolean finalRound;
	
	
	/* bidsProcessingFinished flag, to decide current round data processing is finished
	 * GUI check this flag, if true, GUI update auction_context to 
	 * table. Then set back to false
	 */
	public boolean bidsProcessingFinished = false;
	
	public AuctionContext() {
		this.type = AuctionType.SAA;
		this.round = 1;
		this.duration_Sec = 60;
		this.itemList = new ArrayList<AuctionItem>();
		this.minIncreament = 0;
		this.finalRound = false;
	}
	
	//Copy Constructor
	public AuctionContext(AuctionContext ac) {
		this.type = ac.type;
		this.round = ac.round;
		this.duration_Sec = ac.duration_Sec;
		this.minIncreament = ac.minIncreament;
		this.finalRound = ac.finalRound;
		
		this.itemList = new ArrayList<AuctionItem>();
		
		for (AuctionItem item: ac.itemList) {
			this.itemList.add(new AuctionItem(item));
		}
	}
	
	public AuctionContext(AuctionType type, int time, double min, ArrayList<AuctionItem> list) {
		this.type = type;
		this.round = 1;
		this.duration_Sec = time;
		this.itemList = list;
		this.minIncreament = min;
		this.finalRound = false;
	}
	
	public void setData(int time, double min, ArrayList<AuctionItem> list) {
		this.round = 1;
		this.duration_Sec = time;
		this.itemList = list;
		this.minIncreament = min;
		this.finalRound = false;
	}
	
	public void setType(String typeName) {
		 if (typeName.equals("SAA")) {
			 this.type = AuctionType.SAA;
			 return;
		 }
		 if (typeName.equals("CCA")){
			 this.type = AuctionType.CCA;
			 return;
		 }
		 if (typeName.equals("ULA")) {
			 this.type = AuctionType.ULA;
			 return;
		 }  
	}
	
	
	public double getMinIncrement() {
		return this.minIncreament;
	}
	
	public void setMinIncrement(double min) {
		this.minIncreament = min;
	}
	public void setFinalRound() {
		this.finalRound = true;
	}
	
	public ArrayList<AuctionItem> getItemList() {
		return this.itemList;
	}
	
	public void setItemList(ArrayList<AuctionItem> itemList) {
		this.itemList = new ArrayList<AuctionItem>(itemList);
	}
	
	public int getRound() {
		return this.round;
	}
	
	public void incrementRound() {
		round++;
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
	   
	    String typeName = "";

	    switch (this.type){
	    case SAA:
	    	typeName = "SAA";
	    	break;
	    case CCA:
	    	typeName = "CCA";
	    	break;
	    case ULA:
	    	typeName = "ULA";
	    	break;
		default:
			break;
	    }
	    
	    child = doc.createElement("type");
	    child.setAttribute("value", typeName);
	    root.appendChild(child);
	    child = doc.createElement("round");
	    child.setAttribute("value", Integer.toString(this.round));
	    child.setAttribute("final", this.finalRound?"yes":"no");
	    root.appendChild(child);
	    child = doc.createElement("duration");
	    child.setAttribute("value", Integer.toString(this.duration_Sec));
	    root.appendChild(child);
	    child = doc.createElement("minimum_increament");
	    child.setAttribute("value", Double.toString(this.minIncreament));
	    root.appendChild(child);
	    
	    int len = itemList.size();
	    for (int i=0; i<len; i++) {  
	    	AuctionItem item = itemList.get(i);  
	    	child = doc.createElement("item");  
	    	child.setAttribute("id", Integer.toString(item.getID()));
	    	child.setAttribute("name", item.getName());  
	    	child.setAttribute("price", Double.toString(item.getPrice())); 
	    	String ownerName = "";
	    	// if current Item has Owner and Owner has name, then assgin owner name to Item
	    	if ( (null != item.getOwner()) && (null != item.getOwner().getName()) ) {
	    		ownerName = item.getOwner().getName();
	    	}
	    	child.setAttribute("owner", ownerName);  
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
}
