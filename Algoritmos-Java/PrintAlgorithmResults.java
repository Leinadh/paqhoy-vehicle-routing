import java.util.ArrayList;
import java.io.*;

public class PrintAlgorithmResults {
	public int identifier;
	public int x;
	public int y;
	public float remaining_time;

	public static void main(String []args) throws Exception {
		PrintWriter writer = new PrintWriter("resultados_algoritmos.txt", "UTF-8");
		for(int i=0; i<40; i++){
            int warehouse_x = 0;
            int warehouse_y = 0;

            int number_of_products = 30;

            ArrayList<Vehicle> list_of_vehicles = Utils.generate_list_of_vehicles(warehouse_x, warehouse_y);
            ArrayList<Product> list_of_products = Utils.generate_list_of_products(number_of_products, warehouse_x, warehouse_y);

            int max_iterations = 50;

            City city = new City("city.txt");

            ArrayList<Object> result = Utils.algorithm(max_iterations, list_of_vehicles, list_of_products, warehouse_x, warehouse_y, city);
            @SuppressWarnings("unchecked")
            ArrayList<Vehicle> best_solution_vehicles = (ArrayList<Vehicle>)result.get(0);
          @SuppressWarnings("unchecked")
          ArrayList<Product> best_solution_products = (ArrayList<Product>)result.get(1);
          @SuppressWarnings("unchecked")
          float best_fitness = (float)result.get(2);

          Utils.make_solution_report(best_solution_vehicles, best_solution_products);
          
          System.out.println(best_fitness);
            //writer.println(String.valueOf(best_fitness));
            
          
          
          
          
          
          ArrayList<Object> result2 = Utils2.algorithm(max_iterations, list_of_vehicles, list_of_products, warehouse_x, warehouse_y, city);
            @SuppressWarnings("unchecked")
            ArrayList<Vehicle> best_solution_vehicles2 = (ArrayList<Vehicle>)result2.get(0);
          @SuppressWarnings("unchecked")
          ArrayList<Product> best_solution_products2 = (ArrayList<Product>)result2.get(1);
          @SuppressWarnings("unchecked")
          float best_fitness2 = (float)result2.get(2);

          Utils2.make_solution_report(best_solution_vehicles2, best_solution_products2);
          System.out.println(best_fitness2);
       
          writer.println(String.valueOf(best_fitness) + " " + String.valueOf(best_fitness2));
      
          
		}
		writer.close();
	}
}