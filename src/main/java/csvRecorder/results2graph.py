import pandas as pd
import matplotlib.pyplot as plt

# Load the CSV file into a DataFrame
df = pd.read_csv('results2.csv')  # Replace 'your_file.csv' with the actual file path

# Set the style to use a grid
plt.style.use('ggplot')

# Create the four separate graphs
plt.figure(figsize=(12, 8))

# RobotID Vs InitialRT
plt.subplot(2, 2, 1)
plt.plot(df['RobotID'], df['InitialRT'], 'r')
plt.title('RobotID Vs InitialRT')
plt.xlabel('RobotID')
plt.ylabel('InitialRT')

# RobotID Vs FinalRT
plt.subplot(2, 2, 2)
plt.plot(df['RobotID'], df['FinalRT'], 'r')
plt.title('RobotID Vs FinalRT')
plt.xlabel('RobotID')
plt.ylabel('FinalRT')

# RobotID Vs InitialBT
plt.subplot(2, 2, 3)
plt.plot(df['RobotID'], df['InitialBT'], 'b')
plt.title('RobotID Vs InitialBT')
plt.xlabel('RobotID')
plt.ylabel('InitialBT')

# RobotID Vs FinalBT
plt.subplot(2, 2, 4)
plt.plot(df['RobotID'], df['FinalBT'], 'b')
plt.title('RobotID Vs FinalBT')
plt.xlabel('RobotID')
plt.ylabel('FinalBT')

# Adjust spacing between subplots
plt.tight_layout()

# Show the plots
plt.show()
