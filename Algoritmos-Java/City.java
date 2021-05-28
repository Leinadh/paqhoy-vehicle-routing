import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class City {
	public ArrayList<ArrayList<Integer>> grid;
	public int num_rows;
	public int num_cols;

	public City(String file_name_) {
		this.grid = this.read_grid(file_name_);
    this.num_rows = this.grid.size();
    this.num_cols = this.grid.get(0).size();
	}

	public ArrayList<ArrayList<Integer>> read_grid(String file_name) {
		ArrayList<ArrayList<Integer>> grid_content = new ArrayList<ArrayList<Integer>>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file_name));
			String line;
			int elements_size;
			int element_value;

			while ((line = br.readLine()) != null) {
			   String[] elements = line.trim().split(",");

			   ArrayList<Integer> grid_content_line = new ArrayList<Integer>();

			   for (String element:elements) {
			   	element_value = Integer.parseInt(element);

			   	grid_content_line.add(element_value);
			   }

			   grid_content.add(grid_content_line);
			}
			
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
    
    return grid_content;
	}

  public boolean is_blocked(int row, int col) {
    return this.grid.get(row).get(col) == 0;
  }
  
  public boolean is_clear(int row, int col) {
    return this.grid.get(row).get(col) != 0;
  }
  
  public int get_num_rows(){
    return this.num_rows;
  }

  public int get_num_cols(){
    return this.num_cols;
  }
}