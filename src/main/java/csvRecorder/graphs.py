import pandas as pd
import matplotlib.pyplot as plt

# Read the Excel file into a DataFrame
csv_file = "results1.csv"  # Replace with the actual file path
df = pd.read_csv(csv_file)

# Initialize a dictionary to store the tasks assigned to each robot
robot_tasks = ["r"] * len(df['RobotID'].unique())
print(robot_tasks)

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
    robot_tasks[robot_id] = task

    # Calculate the proportion of robots with tasks "r" and "b" at the current time
    count_r = robot_tasks.count('r')
    count_b = robot_tasks.count('b')
    total_robots = len(robot_tasks)
    print(robot_tasks)

    proportion_r = count_r / total_robots
    proportion_b = count_b / total_robots

    # Append the time point and proportions to the lists
    time_points.append(time_point)
    proportions_r.append(proportion_r)
    proportions_b.append(proportion_b)

# Create a single figure for both Time vs Proportion of Robots Assigned to Task "r" and "b"
plt.figure(figsize=(10, 6))

# Plot Time vs Proportion of Robots Assigned to Task "r"
plt.plot(time_points, proportions_r, label='Task "r"', color='red')

# Plot Time vs Proportion of Robots Assigned to Task "b"
plt.plot(time_points, proportions_b, label='Task "b"', color='blue')

plt.xlabel('Time')
plt.ylabel('Proportion of Robots')
plt.title('Time vs Proportion of Robots Assigned to Tasks "r" and "b"')
plt.legend()

# Save or display the combined graph
plt.savefig("sample_proportion_graph.png")
plt.show()






