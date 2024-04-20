FROM python:3.8-slim

# Set a working directory
WORKDIR /usr/src/app

# Install Python dependencies
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Copy the Locust script
COPY locustfile.py .

# Expose the port Flask listens on
EXPOSE 8089

# Start the Locust application
CMD ["python", "locustfile.py"]
