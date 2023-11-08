import pandas as pd
import matplotlib.pyplot as plt

# Read the CSV file into a DataFrame
data = pd.read_csv('results3.csv')

# Get a list of unique RobotIDs
robot_ids = data['RobotID'].unique()

# Loop through each RobotID and create separate plots
for robot_id in robot_ids:
    # Filter the data for the current RobotID
    robot_data = data[data['RobotID'] == robot_id]
    print(robot_data)

    # # Create a new figure and axis for each robot
    # fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(8, 6))

    # Plot Time vs. RT
    plt.plot(robot_data['Time'], robot_data['RT'], label='RT', color='red')
    # ax1.set_ylabel('RT')
    # ax1.set_title(f'Robot {robot_id} - Time vs. RT')

    # Plot Time vs. BT
    plt.plot(robot_data['Time'], robot_data['BT'], label='BT', color='blue')
    # ax2.set_xlabel('Time')
    # ax2.set_ylabel('BT')
    plt.xlabel('Time')
    plt.ylabel('Threshold Values')
    plt.title(f'Robot {robot_id} - Time vs. Threshold values')

    # Keep the scales of the two axes the same for comparison
    # ax1.set_aspect('equal')
    # ax2.set_aspect('equal')

    # Add legends to the plots
    plt.legend()
    # ax1.legend()
    # ax2.legend()

    # Save the plot as an image file (optional)
    plt.savefig(f'robot_{robot_id}_plots.png')

    # Show the plots (optional)
    plt.show()
