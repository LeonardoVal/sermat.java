package sermat;

import java.lang.reflect.Method;
import java.util.*;

public class Construction<T> {
	public final String identifier;
	//private String packageRoute;

	public Construction(String identifier) {
		this.identifier = identifier;
	}

	public Object[] serializer(T obj) throws Exception {
		//FIXME
		List<Object> attributes = new ArrayList<>();
		Class tClass = obj.getClass();
		//String nameClass = tClass.getSimpleName();
		try{					
			Class[] objparams = {obj.getClass()};
			Method method = obj.getClass().getDeclaredMethod("serializer", objparams);
			attributes = (List<Object>) method.invoke(obj, obj);
		}catch(Exception e){
			throw new Exception("Serializer method undefined on class " + obj.getClass().getSimpleName());
		}
		return attributes.toArray(); //FIXME
	}

	public T materializer(T obj, Object[] args) throws Exception {
		try{
			if(identifier.equals("Date")){
				return (T) new Date(Date.UTC(((Number)args[0]).intValue(), ((Number)args[1]).intValue(), ((Number)args[2]).intValue(), ((Number)args[3]).intValue(), ((Number)args[4]).intValue(), ((Number)args[5]).intValue()));
			}
		}catch(Exception e){
			throw new Exception(" Wrong arguments for construction of " + obj.getClass().getSimpleName());
		}
		try{ // Customs construction
			Class[] objparams = {obj.getClass(), args.getClass()};
			Object[] objMaterializeParams = {obj,args};
			
			Method[] methods = obj.getClass().getMethods(); // get publics methods			
			/*for (int i = 0; i < methods.length; i++) {
				System.out.println(methods[i].getName());
			}*/
			Method method = obj.getClass().getDeclaredMethod("materializer", objparams);
			T tt = (T) method.invoke(obj, objMaterializeParams);
			return tt;
		}catch(Exception e){
			throw new Exception("Materializer method undefined on class " + obj.getClass().getSimpleName());
		}
	}

	public T getNewInstance(Object[] args) throws ReflectiveOperationException {
		return null;
		/*FIXME
		Constructor intArgsConstructor;
		if (args == null) {
			intArgsConstructor = integerDefinition.getConstructor();
			return (T) intArgsConstructor.newInstance();
		} else {
			Class[] classes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				// Type of attributes accepted
				if (args[i] instanceof Boolean)
					classes[i] = boolean.class;
				if (args[i] instanceof Number) {
					classes[i] = int.class;
					args[i] = ((Number) args[i]).intValue();
				}
			}
			intArgsConstructor = integerDefinition.getConstructor(classes);
			return (T) intArgsConstructor.newInstance(args);
		}
		*/
	}
	
	public boolean _bool(Object n) {
		return true; //TODO
	}

	public int _int(Object n) {
		return ((Number)n).intValue();
	}

	public double _double(Object n) {
		return ((Number)n).doubleValue();
	}

	public String _str(Object s) {
		return s.toString();
	}
	
	// Default constructions ///////////////////////////////////////////////////
	
	public static final Construction<java.time.ZonedDateTime> DATE = new Construction<java.time.ZonedDateTime>("Date"){
		@Override public Object[] serializer(java.time.ZonedDateTime obj) throws Exception {
			return new Object[]{obj.getYear(), obj.getMonthValue(), obj.getDayOfMonth(),
					obj.getHour(), obj.getMinute(), obj.getSecond() + 1.0e-9 * obj.getNano()};
		}

		@Override public java.time.ZonedDateTime materializer(java.time.ZonedDateTime obj, Object[] args) throws Exception {
			//FIXME Check arguments.
			double secs = _double(args[5]);
			int intSecs = (int)secs;
			int nanos = (intSecs - intSecs) * 1000000000;
			return java.time.ZonedDateTime.of(_int(args[0]), _int(args[1]), _int(args[2]), 
				_int(args[3]), _int(args[4]), intSecs, nanos, java.time.ZoneOffset.UTC);
		}
	};
	
	public static final Map<String, Construction<?>> DEFAULT_CONSTRUCTIONS;	static {
		Map<String, Construction<?>> dcs = new HashMap<String, Construction<?>>();
		dcs.put("Date", DATE);
		DEFAULT_CONSTRUCTIONS = dcs;
	}
}
