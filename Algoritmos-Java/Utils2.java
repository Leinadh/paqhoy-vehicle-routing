import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.Random;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.io.*;

public class Utils2 {
	public static float process_flow(Vehicle n) {
	  return n.capacity * n.velocity;
	}

	public static float process_desirability(int warehouse_x, int warehouse_y, Product n) {
    float estimated_distance = Math.abs(warehouse_x - n.x) + Math.abs(warehouse_y - n.y);
    return 1 / (estimated_distance * n.remaining_time);
	}

	public static float fitness(ArrayList<Vehicle> solution_vehicles, ArrayList<Product> solution_products) {
	  float cumulative_fitness = 0;
	  float estimated_distance;
	  float estimated_arriving_time;
	  float margin_time;
	  float penalty;
	  float abs_margin_time;
	  float factor;

	  Iterator<Vehicle> it1 = solution_vehicles.iterator();
	  Iterator<Product> it2 = solution_products.iterator();

	  while (it1.hasNext() && it2.hasNext()) {
	  	Vehicle vehicle = it1.next();
	  	Product product = it2.next();

	    estimated_distance = Math.abs(vehicle.x - product.x) + Math.abs(vehicle.y - product.y);
	    
	    estimated_arriving_time = estimated_distance / vehicle.velocity;
	    
	    margin_time = product.remaining_time - estimated_arriving_time;
	    
	    if (margin_time < 0) {
	    	penalty = 0.5f;
	    } else {
	    	penalty = 2f;
	    }
	    
	    abs_margin_time = Math.abs(margin_time);

	    if (abs_margin_time > 1) {
	      if (penalty < 1) {
	        penalty = penalty / abs_margin_time;
	      } else {
	        penalty = penalty * abs_margin_time;
	      }
	     }

	    if (vehicle instanceof Motorcycle) {
	      factor = 2f;
	    } else {
	      factor = 0.8f;
	    }

	    cumulative_fitness = cumulative_fitness + (vehicle.capacity * penalty * factor);
	  }
	  
	  return cumulative_fitness;
	}

	public static ArrayList<Object> generate_first_solution(int products_to_assign, ArrayList<Vehicle> list_of_vehicles_by_flow, ArrayList<Product> list_of_products_by_desirability) {
	  ArrayList<Product> solution_products = new ArrayList<Product>();
	  ArrayList<Vehicle> solution_vehicles = new ArrayList<Vehicle>();
	  float solution_fitness;

	  for(int i=0; i<products_to_assign; i++) {
	  	Product product = list_of_products_by_desirability.remove(0);
	  	solution_products.add(product);
	  	solution_vehicles.add(list_of_vehicles_by_flow.get(0));
	  	list_of_vehicles_by_flow.remove(0);
	  }
	  
	  solution_fitness = fitness(solution_vehicles, solution_products);

	  ArrayList<Object> result = new ArrayList<Object>();
	  result.add(solution_vehicles);
	  result.add(solution_products);
	  result.add(solution_fitness);
	  
	  return result;
	}

	public static ArrayList<Vehicle> shuffle_list_of_vehicles(ArrayList<Vehicle> list_of_vehicles) {
	  ArrayList<Vehicle> list_of_vehicles_copy = new ArrayList<Vehicle>(list_of_vehicles);
	  
	  Collections.shuffle(list_of_vehicles_copy);
	  
	  return list_of_vehicles_copy;
	}

	public static ArrayList<Vehicle> sample_list_of_vehicles(ArrayList<Vehicle> list_of_vehicles, int length) {
	  ArrayList<Vehicle> list_of_vehicles_copy = new ArrayList<Vehicle>(list_of_vehicles);
	  ArrayList<Vehicle> list_of_vehicles_sample = new ArrayList<Vehicle>();
	  
	  Collections.shuffle(list_of_vehicles_copy);
	  
	  for(int i=0; i<length; i++) {
	  	list_of_vehicles_sample.add(list_of_vehicles_copy.get(i));
	  }

	  return list_of_vehicles_sample;
	}

	public static ArrayList<ArrayList<Vehicle>> get_neighbors(ArrayList<Vehicle> solution_vehicles, ArrayList<Product> solution_products, float solution_fitness, ArrayList<Vehicle> list_of_vehicles, int neighbors_size) {
	  double half_neighbors_size = Math.ceil(neighbors_size/2f);

	  ArrayList<ArrayList<Vehicle>> neighbors = new ArrayList<ArrayList<Vehicle>>();

	  for(int i=0; i<half_neighbors_size; i++) {
	  	neighbors.add(shuffle_list_of_vehicles(solution_vehicles));
	  }

	  for(int i=0; i<(neighbors_size-half_neighbors_size); i++) {
	  	neighbors.add(sample_list_of_vehicles(list_of_vehicles, solution_vehicles.size()));
	  }

	  return neighbors;
	}

	public static boolean exist_in_tabu_list(ArrayList<Vehicle> candidate, ArrayList<ArrayList<Vehicle>> tabu_list) {
		ArrayList<Vehicle> list_of_vehicles;
		int tabu_list_size = tabu_list.size();
		for(int i=0; i<tabu_list_size; i++) {
			list_of_vehicles = tabu_list.get(i);

			if (list_of_vehicles.equals(candidate)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Object> tabu(int max_iterations, ArrayList<Vehicle> list_of_vehicles, ArrayList<Product> list_of_products, int warehouse_x, int warehouse_y) {
	  int number_of_vehicles = list_of_vehicles.size();
	  int number_of_products = list_of_products.size();
	  int products_to_assign;
	  int max_tabu_size = 15;
	  int neighbors_size = 20;
	  ArrayList<Object> intermediate_result;
	  ArrayList<Vehicle> solution_vehicles;
	  ArrayList<Product> solution_products;
	  float solution_fitness;
	  
	  ArrayList<Vehicle> best_solution_vehicles;
	  ArrayList<Product> best_solution_products;
	  ArrayList<Vehicle> best_candidate_solution_vehicles;
	  ArrayList<Product> best_candidate_solution_products;
	  float best_fitness = -1;

	  ArrayList<ArrayList<Vehicle>> neighborhood;

	  int neighborhood_size;

	  ArrayList<Vehicle> candidate;

	  ArrayList<ArrayList<Vehicle>> tabu_list = new ArrayList<ArrayList<Vehicle>>();
	  
	  ArrayList<Vehicle> list_of_vehicles_by_flow = new ArrayList<Vehicle>(list_of_vehicles);
	  Collections.sort(list_of_vehicles_by_flow, new Comparator<Vehicle>() {
	  	@Override
	  	public int compare(Vehicle o1, Vehicle o2) {
	  		float o1_flow = process_flow(o1);
	  		float o2_flow = process_flow(o2);

	  		return Float.compare(o2_flow, o1_flow);
	  	}
	  });

	  ArrayList<Product> list_of_products_by_desirability = new ArrayList<Product>(list_of_products);
	  Collections.sort(list_of_products_by_desirability, new Comparator<Product>() {
	  	@Override
	  	public int compare(Product o1, Product o2) {
	  		float o1_desirability = process_desirability(warehouse_x, warehouse_y, o1);
	  		float o2_desirability = process_desirability(warehouse_x, warehouse_y, o2);

	  		return Float.compare(o2_desirability, o1_desirability);
	  	}
	  });

	  if (number_of_products <= number_of_vehicles) {
	  	products_to_assign = number_of_products;
	  } else {
	  	products_to_assign = number_of_vehicles;
	  }

	  intermediate_result = generate_first_solution(products_to_assign, list_of_vehicles_by_flow, list_of_products_by_desirability);
	  solution_vehicles = (ArrayList<Vehicle>)intermediate_result.get(0);
    solution_products = (ArrayList<Product>)intermediate_result.get(1);
    solution_fitness = (float)intermediate_result.get(2);

    best_solution_vehicles = solution_vehicles;
    best_solution_products = solution_products;
    best_candidate_solution_vehicles = solution_vehicles;
    best_candidate_solution_products = solution_products;

    tabu_list.add(solution_vehicles);
	  
	  for(int i=0; i<max_iterations; i++) {
	  	best_fitness = fitness(best_candidate_solution_vehicles, best_candidate_solution_products);
	  	neighborhood = get_neighbors(best_candidate_solution_vehicles, best_candidate_solution_products, best_fitness, list_of_vehicles, neighbors_size);
	    
	    neighborhood_size = neighborhood.size();
	    for(int j=0; j<neighborhood_size; j++) {
	    	candidate = neighborhood.get(j);

	    	if (!(exist_in_tabu_list(candidate, tabu_list)) && (fitness(candidate, best_candidate_solution_products) >= fitness(best_candidate_solution_vehicles, best_candidate_solution_products))) {
	    		best_candidate_solution_vehicles = candidate;
		    }

		    if (fitness(best_candidate_solution_vehicles, best_candidate_solution_products) >= fitness(best_solution_vehicles, best_candidate_solution_products)) {
		      best_solution_vehicles = best_candidate_solution_vehicles;
		    	best_solution_products = best_candidate_solution_products;
		    }
		      
		    tabu_list.add(best_solution_vehicles);

		    if (tabu_list.size() > max_tabu_size) {
		    	tabu_list.remove(0);
		    }
	    }
	  }

	  best_fitness = fitness(best_solution_vehicles, best_solution_products);
	  
	  ArrayList<Object> result = new ArrayList<Object>();

	  result.add(best_solution_vehicles);
	  result.add(best_solution_products);
	  result.add(best_fitness);

	  return result;
	}

  public static float manhattan_dist(Node node, SearchProblem problem) {
    int s_x = node.state.get(0);
    int s_y = node.state.get(1);
    int g_x = problem.goal.get(0);
    int g_y = problem.goal.get(1);

    return Math.abs(s_x - g_x) + Math.abs(s_y - g_y);
  }

  public static Node child_node(ArrayList<Integer> next_state, SearchProblem problem, String action, Node node) throws Exception {
    Node result = new Node(next_state, node, action, 0);
    
    return result;
  }

  public static ArrayList<Node> node_path(Node target) {
    ArrayList<Node> path_back = new ArrayList<Node>();

    for(Node node=target; node!=null; node=node.parent) {
    	path_back.add(node);
    }

    Collections.reverse(path_back);

    return path_back;
  }

  public static ArrayList<ArrayList<Integer>> node_solution(Node target) {
    ArrayList<Node> nodes = node_path(target);
    ArrayList<ArrayList<Integer>> solution_list = new ArrayList<ArrayList<Integer>>();
    Node node;

    int nodes_size = nodes.size();

    for(int i=0; i<nodes_size; i++) {
      node = nodes.get(i);

      solution_list.add(node.state);
    }

    return solution_list;
  }

  public static ArrayList<ArrayList<Integer>> astar_search(SearchProblem problem) throws Exception {
  	Node source = new Node(problem.initial, null, null, 0);
  	Node child;
  	float state_cost;
  	float temp_g_scores;
  	float temp_f_scores;

  	HashSet<ArrayList<Integer>> explored = new HashSet<ArrayList<Integer>>();

  	PriorityQueue<Node> queue = new PriorityQueue<Node>(new Comparator<Node>(){
																																					  		public int compare(Node i, Node j){
																																					        if(i.f_scores > j.f_scores) {
																																					  	      return 1;
																																					        } else if (i.f_scores < j.f_scores) {
																																						        return -1;
																																					        } else {
																																					          return 0;
																																					        }
																																					      }
																																					  	});

  	source.g_scores = 0;
  	queue.add(source);

  	while(!(queue.isEmpty())) {
  		Node current = queue.poll();

  		explored.add(current.state);

  		if (problem.goal_test(current.state)) {
  			if (problem.temporal_unblock == true) {
	        problem.remove_temporal_unblock();
  			}
	      
	      return node_solution(current);
  		}

  		for (String action:problem.actions(current.state)) {
  			ArrayList<Integer> next_state = problem.result(current.state, action);
  			
  			child = child_node(next_state, problem, action, current);
  			child.h_scores = manhattan_dist(child, problem);
  			
  			state_cost = problem.city.grid.get(next_state.get(0)).get(next_state.get(1));
  			temp_g_scores = current.g_scores + state_cost;
  			temp_f_scores = temp_g_scores + child.h_scores;

  			if(explored.contains(child.state) && (temp_f_scores >= child.f_scores)) {
  				continue;
  			} else if(!(queue.contains(child)) || (temp_f_scores < child.f_scores)) {
  				child.g_scores = temp_g_scores;
  				child.f_scores = temp_f_scores;

  				if(queue.contains(child)) {
  					queue.remove(child);
  				}

  				queue.add(child);
  			}
  		}
    }

  	return null;
  }

  @SuppressWarnings("unchecked")
  public static ArrayList<Object> algorithm(int max_iterations, ArrayList<Vehicle> list_of_vehicles, ArrayList<Product> list_of_products, int warehouse_x, int warehouse_y, City city) throws Exception {
  	ArrayList<Object> tabu_result = tabu(max_iterations, list_of_vehicles, list_of_products, warehouse_x, warehouse_y);
  	ArrayList<Vehicle> best_solution_vehicles = (ArrayList<Vehicle>)tabu_result.get(0);
	  ArrayList<Product> best_solution_products = (ArrayList<Product>)tabu_result.get(1);
	  float best_fitness = (float)tabu_result.get(2);

	  int flag;

	  Iterator<Vehicle> it1 = best_solution_vehicles.iterator();
	  Iterator<Product> it2 = best_solution_products.iterator();

	  while (it1.hasNext() && it2.hasNext()) {
	  	Vehicle vehicle = it1.next();
	  	Product product = it2.next();

	  	ArrayList<Integer> initial = new ArrayList<Integer>();
	  	initial.add(vehicle.x);
	  	initial.add(vehicle.y);

			ArrayList<Integer> goal = new ArrayList<Integer>();
			goal.add(product.x);
			goal.add(product.y);

			SearchProblem problem = new SearchProblem(city, initial, goal);
			ArrayList<ArrayList<Integer>> path = astar_search(problem);

			if (path == null) {
				path = new ArrayList<ArrayList<Integer>>();
			} else {
				int x = goal.get(0);
				int y = goal.get(1);

				if (problem.city.is_blocked(x, y)) {
					flag = 0;
				} else {
					flag = 1;
				}

				ArrayList<Integer> last_node = new ArrayList<Integer>();

				last_node.add(x);
				last_node.add(y);
				last_node.add(flag);

				path.set(path.size()-1, last_node);
			}

			vehicle.update_path(path);
	  }
	  
	  ArrayList<Object> result = new ArrayList<Object>();

	  result.add(best_solution_vehicles);
	  result.add(best_solution_products);
	  result.add(best_fitness);

	  return result;
  }

  public static void make_solution_report(ArrayList<Vehicle> best_solution_vehicles, ArrayList<Product> best_solution_products) {
  	Iterator<Vehicle> it1 = best_solution_vehicles.iterator();
	  Iterator<Product> it2 = best_solution_products.iterator();

	  String vehicle_type;

	  while (it1.hasNext() && it2.hasNext()) {
	  	Vehicle vehicle = it1.next();
	  	Product product = it2.next();

	  	if (vehicle instanceof Motorcycle) {
	  		vehicle_type = "Motorcycle";
	  	} else {
	  		vehicle_type = "Car";
	  	}

	  	System.out.println("Vehicle " + vehicle_type + " " + vehicle.identifier + " in Product " + product.identifier + " with path " + vehicle.path);
	  }
  }

	public static ArrayList<Vehicle> generate_list_of_vehicles(int x, int y) {
	  ArrayList<Vehicle> list_of_vehicles = new ArrayList<Vehicle>();

	  int number_of_motorcycles = 40;
	  int number_of_cars = 20;
	  int motorcycle_capacity = 4;
	  int motorcycle_velocity = 60;
	  int car_capacity = 25;
	  int car_velocity = 30;

	  for(int i=0; i<number_of_motorcycles; i++) {
	  	Motorcycle vehicle = new Motorcycle(i, motorcycle_capacity, motorcycle_velocity, x, y);

	  	list_of_vehicles.add(vehicle);
	  }

	  for(int i=0; i<number_of_cars; i++) {
	  	Car vehicle = new Car(i, car_capacity, car_velocity, x, y);

	  	list_of_vehicles.add(vehicle);
	  }
	  
	  return list_of_vehicles;
	}

	public static ArrayList<Product> generate_list_of_products(int number_of_products, int warehouse_x, int warehouse_y) {
		int max_x = 70;
		int max_y = 50;
		int max_remaining_time = 24;
		Random random = new Random();
		long seed = 3;
		int x;
		int y;
		int remaining_time;

		random.setSeed(seed);

	  ArrayList<Product> list_of_products = new ArrayList<Product>();

	  for(int i=0; i<number_of_products; i++) {
	  	x = random.nextInt(max_x);
	  	while (x == warehouse_x) {
	      x = random.nextInt(max_x);
	  	}

	  	y = random.nextInt(max_y);
	  	while (y == warehouse_y) {
	      y = random.nextInt(max_y);
	  	}

	  	remaining_time = random.nextInt(max_remaining_time) + 1;

	    Product product = new Product(i, x, y, remaining_time);

	    list_of_products.add(product);
	  }
	  
	  return list_of_products;
	}
}