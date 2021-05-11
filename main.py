import data_manager as dm
import vrp_algorithms.constraint_programming as cp

if __name__ == '__main__':
    # Instantiate the data problem.
    data = dm.create_data_model()

    # Find and print best route
    cp.find_best_routes(data)
