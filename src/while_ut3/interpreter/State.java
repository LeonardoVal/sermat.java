package while_ut3.interpreter;

import java.util.*;

/** Representación del estado de ejecución de While. */
public class State {
	public final Map<String, Object> values = new HashMap<String, Object>();
	
	/** Chequea si la variable está definida en el estado. */
	public boolean isDefined(String id) {
		return values.containsKey(id);
	}
	
	/** Retorna el valor asignado a la variable. Si no existe levanta una excepción. */
	public Object get(String id) {
		return values.get(id);
	}
	
	/** Asigna una variable y retorna el valor asignado. */
	public Object set(String id, Object value) {
		values.put(id, value);
		return value;
	}
	
	/** Construye un duplicado de este estado de ejecución. */
	public State clone() {
		State result = new State();
		result.values.putAll(values);
		return result;
	}
	
	/** Retorna una representación de texto del estado. */
	public String toString() {
		StringBuilder buffer = new StringBuilder("{");
		int i = 0;
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			buffer.append(i++ > 0 ? ", " : "").append(entry.getKey())
				.append("=").append(entry.getValue());
		}
		return buffer.append("}").toString();
	}
}
