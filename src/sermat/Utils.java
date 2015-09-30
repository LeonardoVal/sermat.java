package sermat;

public class Utils {

	public static String format(String code, int indentations) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i < indentations; i++) {
			sb.append("\t");
		}
		return sb.append(code + "\n").toString();
	}
}
