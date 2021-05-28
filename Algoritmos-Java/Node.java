import java.util.ArrayList;

public class Node {
	public ArrayList<Integer> state;
  public Node parent;
  public String action;
  public float g_scores;
  public float h_scores;
  public float f_scores = 0;
  int depth;

	public Node(ArrayList<Integer> state_, Node parent_, String action_, float h_scores_) {
		this.state = state_;
    this.parent = parent_;
    this.action = action_;
    this.h_scores = h_scores_;

    if (parent != null) {
      this.depth = parent.depth + 1;
    } else {
      this.depth = 0;
    }
	}
}