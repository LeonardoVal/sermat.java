package sermat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.StyledEditorKit.BoldAction;

import mylib.Point2D;

public class Construction<T> {
	public String identifier;
	private String packageRoute;

	public Construction(String identifier) {
		if (identifier.equals("java.util.Date")) {
			this.packageRoute = "java.util.Date";
			this.identifier = "Date";
		} else {
			this.packageRoute = identifier;
			this.identifier = identifier.substring(6,identifier.length());
		}
	}

	public List<Object> serializer(T obj) throws Exception, Exception {
		List<Object> attributes = new ArrayList<>();
		Class tClass = obj.getClass();
		String nameClass = tClass.getSimpleName();
		switch (nameClass) {
		case "Date":
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date) obj);

			attributes.add(cal.get(Calendar.YEAR));
			attributes.add(cal.get(Calendar.MONTH));
			attributes.add(cal.get(Calendar.DAY_OF_MONTH));

			attributes.add(cal.get(Calendar.HOUR));
			attributes.add(cal.get(Calendar.MINUTE));
			attributes.add(cal.get(Calendar.SECOND));

			break;
		default:		
			try{					
				Class[] objparams = {obj.getClass()};
				Method method = obj.getClass().getDeclaredMethod("serializer", objparams);
				attributes = (List<Object>) method.invoke(obj, obj);
			}catch(Exception e){
				throw new Exception("Serializer method undefined on class " + obj.getClass().getSimpleName());
			}
			break;
		}
		return attributes;
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
		Class integerDefinition = Class.forName(packageRoute);
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
	}
	
	public boolean _bool(Object n) {
		return true;
	}

	public int _int(Object n) {
		return 0;
	}

	public double _double(Object n) {
		return 0.0;
	}

	public String _str(Object s) {
		return "";
	}
}
