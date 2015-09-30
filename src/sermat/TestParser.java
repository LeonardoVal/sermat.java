package sermat;

import java.util.HashMap;
import java.util.Map;

import sermat.ast.Prog;
import sermat.parser.Parser;
import junit.framework.TestCase;
import while_ut3.interpreter.State;
public class TestParser extends TestCase {

	public void testParser() throws Exception {
		Map<String,String> test = new HashMap<String, String>();
		Prog prog;
		State state;
		test.put("{b=1; a=b++;}", "{b=2.0, a=1.0}");
		test.put("{b=1; a=++b;}", "{b=2.0, a=2.0}");
		test.put("{b=1; a=b--;}", "{b=0.0, a=1.0}");
		test.put("{b=1; a=--b;}", "{b=0.0, a=0.0}");
		test.put("{b=1; a=3 & 2;}", "{b=1.0, a=2.0}");
		test.put("{b=1; a=3 | 2;}", "{b=1.0, a=3.0}");
		test.put("{ a = true; if a then a = false; else a = 1; }", "{a=false}");
		test.put("{ a = false; if !a then a = true; }", "{a=true}");
		test.put("{ a = true; if !a then a = 1; }", "{a=true}");
		test.put("{ a = 1; while a < 10 do a++; }", "{a=10.0}");
		test.put("{ a = 1; while false do {} }", "{a=1.0}");
		test.put("{a = 1; b = 10;c = 1;while a < b do{ a = a + a;b = b - 1;c = b/c;c = b * c;}}", 
				"{b=7.0, c=62.015625, a=8.0}");
		test.put("{ a = print(2+2); }", "{a=4.0}");
		
		for(String s: test.keySet()){
			prog = (Prog)(Parser.parse(s).value);
			state = prog.evaluate();
			System.out.println("Test: " + s + " Resultado Esperado: " + test.get(s));
			System.out.println(state + "\n");
			assertEquals(test.get(s), state.toString());
		}
		
		prog = (Prog)(Parser.parse("{ a = random(time(),10); }").value);
		state = prog.evaluate();
		System.out.println("Test: " + "{ a = random(time(),10); }");
		System.out.println(state + "\n");
		
	}
}
