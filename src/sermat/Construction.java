package sermat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Construction<T> {
	public String identifier = "";

	public Construction(String identifier){
		this.identifier = identifier;
	}
	public List<Object> serializer(T obj) throws Exception, Exception { // Date
		List<Object> attributes = new ArrayList<>();
		Class tClass = obj.getClass();
		String nameClass = tClass.getSimpleName();
		switch(nameClass){
			case "Date":
				Calendar cal = Calendar.getInstance();
			    cal.setTime((Date)obj);
			    
				attributes.add(cal.get(Calendar.YEAR));
				attributes.add(cal.get(Calendar.MONTH));
				attributes.add(cal.get(Calendar.DAY_OF_MONTH));
				
				attributes.add(cal.get(Calendar.HOUR));
				attributes.add(cal.get(Calendar.MINUTE));
				attributes.add(cal.get(Calendar.SECOND));
				
				break;
			default:
				List<String> methodsName = new ArrayList();
				Method[] methods = tClass.getMethods(); //get publics methods
				for (int i = 0; i < methods.length; i++) {
					if (methods[i].getName().substring(0,3).equals("get")){
						if(!methods[i].getName().equals("getClass"))
							methodsName.add(methods[i].getName());
						
					}
				}
				Method method;
				Class noparams[] = {};
				for (String s : methodsName) {
					method = obj.getClass().getDeclaredMethod(s, noparams);
					//System.out.println("invoke " + s + " " + method.invoke(obj, null));
					attributes.add(method.invoke(obj, null));
				}
				//System.out.println("length: " + methodsName.size());
				break;
		}

		
		
		Method[] ff = obj.getClass().getDeclaredMethods();
		
		
		//String dd = new String("pop");
		
		//System.out.println("" + obj.getClass().getDeclaredMethods());// .getDeclaredFields().length
																	// + "\n");
		
		
		
		
		/*for (Method f : ff) {
			if (f.getName().substring(0, 3).equals("get")){
				getMethods.add(f.getName());
				//System.out.println(""+f.toGenericString());
				//System.out.println(""+f.);
			}
			
			/*System.out.println("" + " " + f.getName()); // get all gets
			System.out.println("" + " " + f.getDeclaringClass());
			int length = f.getParameterAnnotations().length;
			System.out.println(length);
		}*/
		for(int i = 0; i< attributes.size(); i++){
			System.out.println(""+attributes.get(i));
		}
		
		System.out.println("##########################");
		return attributes;
	}

	public T materializer(T obj, Object[] args) {
		return null;
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
