import pandas as pd
import matplotlib.pyplot as plt

def count_occurrences(dictionary, value_to_count):
    count = 0
    for value in dictionary.values():
        if value == value_to_count:
            count += 1
    return count

# Read the Excel file into a DataFrame
csv_file = "results1_9010.csv"  # Replace with the actual file path
df = pd.read_csv(csv_file)

# Initialize a dictionary to store the tasks assigned to each robot
uniqueRobotIDs = df['RobotID'].unique()
total_robots = len(uniqueRobotIDs)
print(uniqueRobotIDs)
robot_tasks_dictionary = {}
for id in uniqueRobotIDs:
    robot_tasks_dictionary[id] = "r"
print(robot_tasks_dictionary)

# Initialize lists to store the time points and proportions of robots with tasks "r" and "b"
time_points = []
proportions_r = []
proportions_b = []

# Iterate through each row of the DataFrame
for _, row in df.iterrows():
    robot_id = row['RobotID']
    time_point = row['Time']
    task = row['Task']

    # Update the task for the corresponding robot
    robot_tasks_dictionary[robot_id] = task

    # Calculate the proportion of robots with tasks "r" and "b" at the current time
    count_r = count_occurrences(robot_tasks_dictionary,"r")
    count_b = count_occurrences(robot_tasks_dictionary,"b")
    
    proportion_r = count_r / total_robots
    proportion_b = count_b / total_robots

    # Append the time point and proportions to the lists
    time_points.append(time_point)
    proportions_r.append(proportion_r)
    proportions_b.append(proportion_b)

# Create a single figure for both Time vs Proportion of Robots Assigned to Task "r" and "b"
plt.figure(figsize=(10, 6))
plt.grid()
# Plot Time vs Proportion of Robots Assigned to Task "r"
plt.plot(time_points, proportions_r, label='Task "r"', color='red')

# Plot Time vs Proportion of Robots Assigned to Task "b"
plt.plot(time_points, proportions_b, label='Task "b"', color='blue')

plt.xlabel('Time')
plt.ylabel('Proportion of Robots')
plt.title('Time vs Proportion of Robots Assigned to Tasks "r" and "b"')
plt.legend()

# Save or display the combined graph
plt.savefig("proportion_graph_9010.png")
plt.show()







