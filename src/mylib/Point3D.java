package mylib;

import java.util.ArrayList;
import java.util.List;

public class Point3D {
	int x;
	int y;
	boolean z;
	
	public Point3D(int x, int y, boolean z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return "Point3D [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	public List<Object> serializer(Point3D obj) {
		List<Object> objs = new ArrayList<>();
		objs.add(obj.x);
		objs.add(obj.y);
		objs.add(obj.z);
		return objs; 
	} 

	public Point3D materializer(Point3D obj, Object[]args) { 
		return (Point3D) (args == null? args : new Point3D(((Number) args[0]).intValue(), ((Number) args[1]).intValue(), (Boolean)args[2])); 
	}	
}
