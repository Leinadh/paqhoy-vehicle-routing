import java.util.ArrayList;

public class SearchProblem {
	public City city;
	public ArrayList<Integer> initial;
	public ArrayList<Integer> goal;
	public boolean temporal_unblock;

	public SearchProblem(City city_, ArrayList<Integer> initial_, ArrayList<Integer> goal_) {
		this.city = city_;
    this.initial = initial_;
    this.goal = goal_;
    
    this.temporal_unblock = false;

    int x = this.goal.get(0);
    int y = this.goal.get(1);

    if (this.city.is_blocked(x, y)) {
      this.city.grid.get(x).set(y, 1);
      
      this.temporal_unblock = true;
    }
	}

  public boolean is_valid_state(int row, int col) {
  	if ((row < 0) || (row >= this.city.get_num_rows())) {
      return false;
  	} else if ((col < 0) || (col >= this.city.get_num_cols())) {
      return false;
  	}
    
    return !(this.city.is_blocked(row, col));
  }

  public ArrayList<String> actions(ArrayList<Integer> state){
    int row = state.get(0);
  	int col = state.get(1);
    ArrayList<String> valid_actions = new ArrayList<String>();
    
    if (this.is_valid_state(row-1, col)) {
    	valid_actions.add("N");
    }
    
    if (this.is_valid_state(row+1, col)) {
    	valid_actions.add("S");
    }
    
    if (this.is_valid_state(row, col-1)) {
    	valid_actions.add("W");
    }
    
    if (this.is_valid_state(row, col+1)) {
    	valid_actions.add("E");
    }
    
    return valid_actions;
  }
  
  public ArrayList<Integer> result(ArrayList<Integer> state, String action){
    int row = state.get(0);
    int col = state.get(1);

    ArrayList<Integer> new_state = new ArrayList<Integer>();
    
    if (action.equals("N")) {
      new_state.add(row - 1);
      new_state.add(col);
    } else if (action.equals("S")) {
      new_state.add(row + 1);
      new_state.add(col);
    } else if (action.equals("W")) {
      new_state.add(row);
      new_state.add(col - 1);
    } else if (action.equals("E")) {
      new_state.add(row);
      new_state.add(col + 1);
    }
    
    return new_state;
  }
  
  public boolean goal_test(ArrayList<Integer> state) {
    return this.goal.equals(state);
  }

  public void remove_temporal_unblock() {
    int x = this.goal.get(0);
    int y = this.goal.get(1);

    this.city.grid.get(x).set(y, 0);
      
    this.temporal_unblock = false;
  }
}