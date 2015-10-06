package sermat;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mylib.Point2D;
import mylib.Point3D;
import sermat.parser.*;

public class Main {
	public static void main(String[] args) throws Exception {
		//BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		Sermat sermat = new Sermat();
		sermat.register(new Construction<Point2D>("mylib.Point2D"));
		sermat.register(new Construction<Point2D>("mylib.Point3D"));
		
		System.out.println(sermat.serialize(new Date()));
		System.out.println(sermat.serialize(new Point2D(10,33)));
		System.out.println(sermat.materialize("Date(2015,9,4,10,22,50)"));
		System.out.println(sermat.serialize(new Point3D(22,11,true)));
		System.out.println(sermat.materialize("Point3D(2,1,false)"));
		//System.out.println(sermat.materialize("mylib.Point2D(31,24)"));
		//System.out.println(sermat.materialize("Date()"));

		
		/* SAME OBJECT */
		Map<String, Object> obj1 = new HashMap<String, Object>(); 
		obj1.put("a", 7); 
		List<Object> list1 = new ArrayList<Object>();
		list1.add(obj1);
		list1.add(obj1);

		String str = sermat.serialize(list1,sermat.BINDING_MODE);
		System.out.println(" ** MODES **");
		System.out.println(str);
		
		obj1.put("b", obj1);
		
		Object strMat = sermat.materialize("$0=[$1={a:7},$1]");
		System.out.println(sermat.serialize(sermat.materialize("$0=[$1={a:7},$1]"),sermat.BINDING_MODE));
		System.out.println(sermat.serialize(list1, sermat.CIRCULAR_MODE));

	}
}