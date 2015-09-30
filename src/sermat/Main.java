package sermat;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sermat.parser.*;
import while_ut3.interpreter.State;

public class Main {
	public static void main(String[] args) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		Sermat sermat = new Sermat();
		
		Object c00 = null;
		System.out.println(sermat.serialize(c00));
		
		/* SAME OBJECT */
		Map<String, Object> obj1 = new HashMap<String, Object>(); 
		obj1.put("a", 7); 
		List<Object> list1 = new ArrayList<Object>();
		list1.add(obj1);
		list1.add(obj1);
		
		//assertEquals(str, strMat);
		String str = "\"hello world\"";

			//System.out.println(sermat.serialize(list1,sermat.BINDING_MODE));
		str = sermat.serialize(list1,sermat.BINDING_MODE);
		System.out.println(str);
		
		obj1.put("b", obj1);
		//str = sermat.serialize(list1,sermat.BINDING_MODE);
		
		Object strMat = sermat.materialize("$0=[$1={a:7},$1]");
		System.out.println(sermat.serialize(sermat.materialize("$0=[$1={a:7},$1]"),sermat.BINDING_MODE));
		System.out.println(sermat.serialize(list1, sermat.CIRCULAR_MODE));
		for (String line; (line = in.readLine()) != null ;) {
			line = line.trim();
			try {
				if (line.length() > 0) {
					    
					//System.out.println(line);
					//Object code = sermat.materialize(line);
					
					System.out.println(sermat.serialize(line, sermat.DEFAULT_MODE));
					
					/*
					 PRUEBAS:
					 []
					 [{}]
					 [Infinity,NaN,null]
					 ["apple",1 ,3.4, true]
					 [{"a": 7}, 77, {"a": 7}]
					 
					 {}
					 {"Data": [],"Object": {}}
					 {"a":10 /*put me as a comment* }
					 {"Name":"user", "Age":10, "Gender": "F"}
					 [{"Name":["firstname", "lastname"], "Age": Infinity, "Gender": "F"}]
					 
					 {"$menu": { "id": "file", "value": "File", "popup": { "menuitem": [ {"value": "New", "onclick": "CreateNewDoc()"}, {"value": "Open", "onclick": "OpenDoc()"},   {"value": "Close", "onclick": "CloseDoc()"}     ]   } }}
					 { "firstName": "John",  "lastName": "Smith",  "isAlive": true,  "age": 25,  "address": { "streetAddress": "21 2nd Street", "city": "New York", "state": "NY",   "postalCode": 100213100  },  "phoneNumbers": [ {  "type": "home",  "number": "212 555-1234" },  { "type": "office", "number": "646 555-4567" }],  "children": [], "spouse": null}
					  
					 */
					
				}
			} catch (Exception err) {
				System.err.println(err);
			}
		}
	}
}