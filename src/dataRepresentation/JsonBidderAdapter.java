package dataRepresentation;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonBidderAdapter implements JsonSerializer<Bidder>, JsonDeserializer<ArrayList<Bidder>>{
 
	@Override
	public ArrayList<Bidder> deserialize(JsonElement arg0, Type arg1,
		JsonDeserializationContext arg2) throws JsonParseException {
		//diserialize will not be used in this project, so needn't implement
		return null;
	}

	@Override
	public JsonElement serialize(Bidder bidder, Type type, JsonSerializationContext context) {
		JsonObject json_bidder = new JsonObject();
		json_bidder.addProperty("name", bidder.getName());
		json_bidder.addProperty("ipAddress", bidder.getIP());
		json_bidder.addProperty("warningMessage", bidder.getWarnMsg());
		
	    return json_bidder;
	}
}
