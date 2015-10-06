package mylib;

import java.util.ArrayList;
import java.util.List;

public class Point2D { 
	public final int x; 
	public final int y;
	
	public Point2D(int x, int y) { 
		this.x = x; 
		this.y = y; 
	}
	
	public List<Object> serializer(Point2D obj) {
		List<Object> objs = new ArrayList<>();
		objs.add(obj.x);
		objs.add(obj.y);
		return objs; 
	} 
	@Override
	public String toString() {
		return "Point2D [x=" + x + ", y=" + y + "]";
	}

	public Point2D materializer(Point2D obj, Object[]args) { 
		return (Point2D) (args == null? args : new Point2D(((Number) args[0]).intValue(), ((Number)args[1]).intValue())); 
	} 
}
