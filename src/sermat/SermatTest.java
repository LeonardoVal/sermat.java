package sermat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import junit.framework.TestCase;

public class SermatTest extends TestCase {
	protected Sermat sermat;
	protected void setUp(){
		sermat = new Sermat();
	}
	
	public void testString() throws Exception {		
		String str = "";
		Object strMat = "";
		assertEquals(strMat, sermat.materialize("\"\"") );
		
		str = "hello world";
		strMat = sermat.materialize("\"hello world\"");
		assertEquals(str, strMat);
		str = "\\\"hello world\\\"";
		assertEquals(str, sermat.serialize(strMat));
		//assertEquals(str, sermat.materialize(sermat.serialize(strMat)));
	}
	
	public void testDecimal() throws Exception{
		Double num = 3.3;
		String str = ""+num;
		assertEquals(num, sermat.materialize(str));
		assertEquals(str, sermat.serialize(sermat.materialize(str)));
	}
	
	public void testBoolean() throws Exception{
		Boolean isBool = true;
		String str = ""+isBool;
		assertEquals(isBool, sermat.materialize(str));
		assertEquals(str, sermat.serialize(sermat.materialize(str)));
	}
	
	public void testNull() throws Exception{
		Object isNull = null;
		String str = ""+isNull;
		assertEquals(isNull, sermat.materialize(str));
		assertEquals(str, sermat.serialize(sermat.materialize(str)));
	}
	
	
	public void testList() throws Exception{
		List<Object> list = new ArrayList<Object>();
		String str = "[]";
		assertEquals(list, sermat.materialize(str));
		assertEquals(str, sermat.serialize(sermat.materialize(str)));
		
		str = "[{}]";
		list.add(new HashMap<>());
		assertEquals(list, sermat.materialize(str));
		assertEquals(str, sermat.serialize(sermat.materialize(str)));
		
		str = "[{},Infinity,NaN,null]";
		list.add(Double.POSITIVE_INFINITY);
		list.add(Double.NaN);
		list.add(null);
		assertEquals(list, sermat.materialize(str));
		assertEquals(str, sermat.serialize(sermat.materialize(str)));
		
		// DEFAULT MODE
		list.clear();
		Map<String, Object> obj1 = new HashMap<String, Object>();
		obj1.put("a", 7);
		
		list.add(obj1);
		list.add(obj1);
		
		try {
			sermat.serialize(list);
			fail("Should have thrown an IllegalArgumentException");
		} catch (Exception e) {
			assertEquals("Repeated list!", e.getMessage());
		}
		

		//REPEAT MODE
		str = "[{a:7},{a:7}]";
		assertEquals(str,sermat.serialize(list,Sermat.REPEAT_MODE));
		// DEFAUL MODE
		list.clear();
		obj1.clear();
		obj1.put("Age",Double.POSITIVE_INFINITY);
		obj1.put("Gender", "F");
		list.add(obj1);
		
		str = "[{Age:Infinity,Gender:\\\"F\\\"}]";
		assertEquals(str, sermat.serialize(list));
	}
	
	public void testMap() throws Exception{
		String str = "{}";
		Map<String,Object> map = new HashMap<String,Object>();
		assertEquals(str, sermat.serialize(map));
		assertEquals(str, sermat.serialize(sermat.materialize(str)));
		
		str = "{\"Data\":[],\"Object\":{}}";
		assertEquals("{\"Object\":{},\"Data\":[]}", sermat.serialize(sermat.materialize(str)));
		
		str = "{Data: [], Object: {}}";
		assertEquals("{Data:[],Object:{}}", sermat.serialize(sermat.materialize(str)));
		
		str = "{a:10 /* block comment*/ }";
		assertEquals("{a:10.0}", sermat.serialize(sermat.materialize(str)));		
	}
	
	public void testBinding() throws Exception{
		Map<String, Object> obj1 = new HashMap<String, Object>(); 
		obj1.put("a", 7); 
		List<Object> list1 = new ArrayList<Object>();
		list1.add(obj1);
		list1.add(obj1);
		String str = "$0=[$1={a:7},$1]";
		assertEquals(str, sermat.serialize(list1,sermat.BINDING_MODE));
		
		obj1.put("b", obj1);
		// Binding exception
		
	}
	
	public void testCircular() throws Exception{
		Map<String, Object> obj1 = new HashMap<String, Object>(); 
		obj1.put("a", 7); 
		List<Object> list1 = new ArrayList<Object>();
		list1.add(obj1);
		list1.add(obj1);
		
		String str = "$0=[$1={a:7},$1]";
		assertEquals(str, sermat.serialize(list1,sermat.CIRCULAR_MODE));
		
		obj1.put("b", obj1);
		str = "$0=[$1={b:$1,a:7},$1]";
		assertEquals(str, sermat.serialize(list1,sermat.CIRCULAR_MODE));
	}
	
	// Sermat.serialize(Sermat.materialize("$0={a:$1={x:7},b:$1}", {mode: Sermat.BINDING_MODE}))
	// Sermat.materialize("{a:true, a:$0=[$1=[$2={a:7},$2],$1] , b:$3=[$4=[$5={a:7},$5],$4]}")
	//Sermat.serialize(Sermat.materialize("$0={a:$1={a:7},b:$1}"),{mode: Sermat.BINDING_MODE})
	//Sermat.serialize(new Date());
}

