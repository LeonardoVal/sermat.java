package sermat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sermat.parser.Parser;

public class Sermat {
	
	public static final int DEFAULT_MODE = 0;
	public static final int REPEAT_MODE = 1;
	public static final int BINDING_MODE = 2;
	public static final int CIRCULAR_MODE = 3;
	
	public static final Map<String, Construction<?>> CONSTRUCTIONS = new HashMap<String, Construction<?>>();
	
	public Sermat (){
		CONSTRUCTIONS.put("Date", new Construction<>("java.util.Date")); // constructions defined by default
	}

	//hash string(date) construccion
// Serialization //////////////////////////////////////////////////////////
	
	private static class SerializationContext {
		public final List<Object> visited = new ArrayList<Object>();
		public final List<Object> parents = new ArrayList<Object>();
		public final Map<String, Object> bindings = new HashMap<String, Object>();
		
		void addVisited(Object value){
			visited.add(value);
		}
		boolean isVisited(Object value) {
			return visited.indexOf(value) >= 0;
		}
	}
	
	private String serialize(Object value, int mode, SerializationContext context) throws Exception{
		
		if(value instanceof ArrayList<?>){
			return listSerialize(value, mode, context);
		}
		if(value instanceof HashMap<?, ?>){
			return mapSerialize(value, mode, context);
		}		
		if(value instanceof Boolean || value instanceof Number){
			return value.toString();
		}
		if (value instanceof String){			
			//if( ((String)value).charAt(0) == '"')
				return strSerialize((String)value);
			/*else
				return (String)value; //index*/
		}
		if(value == null){
			return "null";
		}
		
		// value is instanceof Class
		Class tClass = value.getClass();
		if(CONSTRUCTIONS.containsKey(tClass.getSimpleName()) || CONSTRUCTIONS.containsKey("mylib."+tClass.getSimpleName())){
			Construction constr = CONSTRUCTIONS.containsKey(tClass.getSimpleName()) ? CONSTRUCTIONS.get(tClass.getSimpleName()):CONSTRUCTIONS.get("mylib."+tClass.getSimpleName());
			List<Object> attributes = constr.serializer(value);
			String data = constr.identifier + "(";
			for(Object o : attributes){
				data+= "" + o + ",";
			}
			data = data.substring(0,data.length()-1);
			
			return data + ")";
		}else{			
			throw new Exception("Class " + tClass.getSimpleName() + " is not defined!");
		}	
		
		//throw new Exception("Serialize Error");
	}
	
	public String serialize(Object value) throws Exception{
		return this.serialize(value, DEFAULT_MODE);
	}
	public String serialize(Object value, int mode) throws Exception{
		return this.serialize(value, mode, new SerializationContext());
	}
	
	private String strSerialize(String str){	
		return "\\\"" + str + "\\\"";
	}
	private String listSerialize(Object value, int mode, SerializationContext context) throws Exception{
		String result = "";	
		boolean isBind = false;
		String key = "";
		switch(mode){
			case DEFAULT_MODE:
				if (context.isVisited(value)) {
					throw new Exception("Repeated list!");				
				}
				context.addVisited(value);
				break;
			case BINDING_MODE:
				if(context.parents.indexOf(value) >=0)
					throw new Exception("Sermat.serialize: Circular reference detected!");
				break;
		}
		if(mode == BINDING_MODE || mode == CIRCULAR_MODE){
			int index = context.visited.indexOf(value);
			key = "$";
			if(!(index >= 0)){
				context.visited.add(value);
				index = context.visited.indexOf(value);
			}
			key+= index;
			if(!context.bindings.containsKey(key)){
				context.bindings.put(key,value);					
				key += "=";
			}else{
				isBind = true;
			}
		}
		context.parents.add(value);
		for(Object o : (List<?>)value) {
			if(isBind)
	        	result += ",";
	        else
	        	result += serialize(o, mode, context) + ",";
		}
		if (result.length() > 1) {
			result = result.substring(0,result.length()-1);
		}
		context.parents.remove(context.parents.size()-1);
		return isBind ? key : key+ "[" + result + "]";
	}
	private String mapSerialize(Object map, int mode, SerializationContext context) throws Exception{
		@SuppressWarnings("unchecked")
		Iterator<?> it = ((Map<String, Construction<?>>) map).entrySet().iterator();
		String result = "";
		int len = 0;
		boolean isBind = false;
		String key = "";
		switch(mode){
			case DEFAULT_MODE:
				if (context.isVisited(map)) {
					throw new Exception("Repeated list!");	
				}
				context.addVisited(map);
				break;
			case BINDING_MODE:				
				if(context.parents.indexOf(map) >=0)
					throw new Exception("Sermat.serialize: Circular reference detected!");
				break;
		}
		
		if(mode == BINDING_MODE || mode == CIRCULAR_MODE){
			int index = context.visited.indexOf(map);
			key = "$";
			if(!(index >= 0)){
				context.visited.add(map);
				index = context.visited.indexOf(map);
			}
			key+= index;
			
			if(!context.bindings.containsKey(key)){
				context.bindings.put(key, map);
				key +="=";
			}else{
				isBind = true;
			}
		}
		context.parents.add(map);
	    while (it.hasNext()) {
	    	len++;
	        Map.Entry pair = (Map.Entry)it.next();
	        if(isBind)
	        	result += ",";
	        else
	       		result += pair.getKey() + ":" + serialize(pair.getValue(), mode, context) + "," ;
	    }
	    context.parents.remove(context.parents.size()-1);
	    if(len > 0)
			result = result.substring(0,result.length()-1);
	    return isBind ? key : key+ "{" + result + "}";
	}
	
// Materialization ////////////////////////////////////////////////////////////
	
	public Object materialize(String code){
		if(code.startsWith("mylib.")){ //could be a Construction
			code = code.substring(6,code.length());
		}
		/*if(code.startsWith("java.util.")){ //could be a Construction
			code = code.substring(10,code.length());
		}*/
		try {
			return Parser.parse(code).value;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
		}
		return null;
	}
	/**
	 * Register a construction. It can be use by serializer or materializer.
	 * If the construction already exist then thrown an exception. 
	 * @param construction
	 * @throws Exception 
	 */
	public void register(Construction<?> construction) throws Exception{
		if(CONSTRUCTIONS.containsKey(construction.identifier)){
			throw new Exception("Construction already exist!");
		}else{
			CONSTRUCTIONS.put(construction.identifier, construction);
		}
	}
	
	public boolean remove(String identifier){
		return true;
	}
	
}
