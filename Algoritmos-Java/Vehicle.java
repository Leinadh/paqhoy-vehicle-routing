import java.util.ArrayList;

public class Vehicle {
	public int identifier;
	public int capacity;
	public float velocity;
	public int x;
	public int y;
	public ArrayList<ArrayList<Integer>> path;

	public Vehicle(int identifier_, int capacity_, float velocity_, int x_, int y_) {
		this.identifier = identifier_;
    this.capacity = capacity_;
    this.velocity = velocity_;
    this.x = x_;
    this.y = y_;
    this.path = new ArrayList<ArrayList<Integer>>();
	}

	public void update_capacity(int value) {
		this.capacity = this.capacity - value;
	}
  
  public void update_path(ArrayList<ArrayList<Integer>> new_path) throws Exception {
    if (new_path == null) {
    	throw new Exception("No se encontró un path para el vehículo " + identifier);
    } else {
    	this.path.addAll(new_path);
    }
  }

  public void reset_path(){
    this.path.clear();
  }
}