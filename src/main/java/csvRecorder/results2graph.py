import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Load the CSV file into a DataFrame
df = pd.read_csv('results2.csv')  # Replace 'your_file.csv' with the actual file path
print(len(df))

# Create the four separate bar graphs
plt.figure(figsize=(12, 8))

# RobotID Vs InitialRT
plt.subplot(2, 2, 1)
plt.bar(df['RobotID'], df['InitialRT'], color='lightcoral', edgecolor='black')
plt.title('RobotID Vs Initial Response Threshold of Red Task')
plt.xlabel('RobotID')
plt.ylabel('Initial Red Threshold')
plt.grid(axis='y', linestyle='--', alpha=0.7)

# RobotID Vs FinalRT
plt.subplot(2, 2, 2)
plt.bar(df['RobotID'], df['FinalRT'], color='lightcoral', edgecolor='black')
plt.title('RobotID Vs Final Response Threshold of Red Task')
plt.xlabel('RobotID')
plt.ylabel('Final Red Threshold')
plt.grid(axis='y', linestyle='--', alpha=0.7)

# RobotID Vs InitialBT
plt.subplot(2, 2, 3)
plt.bar(df['RobotID'], df['InitialBT'], color='skyblue', edgecolor='black')
plt.title('RobotID Vs Initial Response Threshold of Blue Task')
plt.xlabel('RobotID')
plt.ylabel('Initial Blue Threshold')
plt.grid(axis='y', linestyle='--', alpha=0.7)

# RobotID Vs FinalBT
plt.subplot(2, 2, 4)
plt.bar(df['RobotID'], df['FinalBT'], color='skyblue', edgecolor='black')
plt.title('RobotID Vs Final Response Threshold of Blue Task')
plt.xlabel('RobotID')
plt.ylabel('Final Blue Threshold')
plt.grid(axis='y', linestyle='--', alpha=0.7)

# Adjust spacing between subplots
plt.tight_layout()

# Save the plots as image files (optional)
plt.savefig('bar_graphs.png')

# Show the plots
plt.show()
