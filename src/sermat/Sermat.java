package sermat;

import java.text.ParseException;
import java.util.ArrayList;
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
	
	private static final Map<String, Construction> CONSTRUCTIONS = new HashMap<String, Construction>();
	//Sermat.java
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
		throw new Exception("Serialize Error");
	}
	
	public String serialize(Object value) throws Exception{
		return this.serialize(value, DEFAULT_MODE);
	}
	public String serialize(Object value, int mode) throws Exception{
		return this.serialize(value, mode, new SerializationContext());
	}
	
	private String strSerialize(String str){
		//str = str.substring(1, str.length()-1);		
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
		for(Object o : (List)value) {
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
		Iterator it = ((Map<String, Construction>) map).entrySet().iterator();
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
	
// Materialization ////////////////////////////////////////////////////////////7
	
	public Object materialize(String code){
		try {
			return Parser.parse(code).value;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
		}
		return null;
	}
	
	public void register(Construction construction){
		
	}
	
	public boolean remove(String identifier){
		return true;
	}
	
}
