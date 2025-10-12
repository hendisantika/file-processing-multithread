# File Processing with Multithreading

A Spring Boot demonstration project that showcases the performance benefits of multithreaded file processing compared to
single-threaded processing. This project reads and processes multiple NDJSON (Newline Delimited JSON) transaction files
using both approaches.

## Project Description

This application demonstrates how to efficiently process multiple files concurrently using Java's ExecutorService and
thread pools. It compares the execution time and results between:

- **Single-threaded processing**: Files are processed sequentially, one at a time
- **Multi-threaded processing**: Files are processed concurrently using a thread pool of 5 threads

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.6
- **Maven**: Build and dependency management
- **Jackson**: JSON parsing
- **Lombok**: Reduce boilerplate code
- **JUnit 5**: Testing framework

## Project Structure

```
file-processing-multithread/
├── src/
│   ├── main/
│   │   ├── java/com/multithread/
│   │   │   ├── MultithreadApplication.java          # Main Spring Boot application
│   │   │   ├── config/
│   │   │   │   └── ProcessingConfiguration.java     # Thread pool configuration
│   │   │   ├── controller/
│   │   │   │   └── FileProcessingController.java    # REST API endpoints
│   │   │   ├── domain/dto/
│   │   │   │   ├── TransactionDto.java              # Transaction data model
│   │   │   │   ├── LogEntryDto.java                 # Log entry data model
│   │   │   │   └── ProcessingResult.java            # Processing result DTO
│   │   │   └── service/
│   │   │       ├── FileProcessingService.java       # Service interface
│   │   │       ├── TransactionFileProcessorService.java  # Transaction processor
│   │   │       └── LogFileProcessorService.java     # Log file processor
│   │   └── resources/
│   │       ├── application.properties               # Application configuration
│   │       ├── transactions/                        # Transaction data files
│   │       │   ├── transaction_1.ndjson
│   │       │   ├── transaction_2.ndjson
│   │       │   ├── transaction_3.ndjson
│   │       │   ├── transaction_4.ndjson
│   │       │   └── transaction_5.ndjson
│   │       └── logs/                                # Log data files
│   │           ├── service_1.ndjson
│   │           ├── service_2.ndjson
│   │           └── service_3.ndjson
│   └── test/
│       ├── java/com/multithread/
│       │   ├── MultithreadApplicationTests.java     # Basic application test
│       │   └── ProcessFileTest.java                 # File processing tests
│       └── resources/
│           └── transactions/                        # Test transaction files
│               ├── transaction_1.ndjson
│               ├── transaction_2.ndjson
│               ├── transaction_3.ndjson
│               ├── transaction_4.ndjson
│               └── transaction_5.ndjson
├── pom.xml
└── README.md
```

## Transaction Data Model

Each transaction record contains the following fields:

- `transactionId`: Unique transaction identifier
- `userId`: User ID associated with the transaction
- `amount`: Transaction amount in IDR (Indonesian Rupiah)
- `date`: Transaction timestamp (Unix epoch)
- `status`: Transaction status (SUCCESS, FAILED, WAITING)
- `type`: Transaction type (BiFast, RealTime)
- `fromAccountNumber`: Source account number
- `fromBankName`: Source bank name
- `toAccountNumber`: Destination account number
- `toBankName`: Destination bank name

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## How to Build

Build the project using Maven:

```bash
mvn clean install
```

This command will:

1. Clean previous builds
2. Compile the source code
3. Run all tests
4. Package the application as a JAR file

## How to Run Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test

**Single-threaded Processing Test:**

```bash
mvn test -Dtest=ProcessFileTest#processFileWithoutMultithread
```

**Multi-threaded Processing Test:**

```bash
mvn test -Dtest=ProcessFileTest#processFileWithMultithread
```

## Test Results

The test suite includes two main test cases:

### 1. Single-threaded Processing (`processFileWithoutMultithread`)

- Processes 5 NDJSON files sequentially
- Each file takes approximately 1 second to process (simulated)
- **Total expected time**: ~5 seconds
- **Records processed**: 244 transactions

### 2. Multi-threaded Processing (`processFileWithMultithread`)

- Processes 5 NDJSON files concurrently using a thread pool of 5 threads
- Each file takes approximately 1 second to process (simulated)
- **Total expected time**: ~1 second (all files processed in parallel)
- **Records processed**: 244 transactions

### Performance Comparison

| Approach        | Processing Time | Thread Pool Size | Records Processed |
|-----------------|-----------------|------------------|-------------------|
| Single-threaded | ~5 seconds      | 1 (main thread)  | 244               |
| Multi-threaded  | ~1 second       | 5 threads        | 244               |

**Performance Improvement**: ~5x faster with multithreading

## Key Implementation Details

### Thread Safety

The multi-threaded implementation uses:

- `Collections.synchronizedList()`: Thread-safe list for storing transactions
- `AtomicInteger`: Thread-safe counter for transaction count
- `ExecutorService`: Manages thread pool and task execution

### Thread Pool Configuration

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
```

- Fixed thread pool with 5 worker threads
- Optimal for processing 5 files concurrently
- Can be adjusted based on the number of files and system resources

### Graceful Shutdown

```java
executor.shutdown();
executor.

awaitTermination(1,TimeUnit.HOURS);
```

- Prevents new tasks from being submitted
- Waits for all running tasks to complete
- Maximum wait time of 1 hour

## Running the Application

The application provides REST API endpoints to demonstrate file processing with multithreading. Start the application:

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/multithread-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## REST API Endpoints

The application exposes the following REST APIs for testing multithreaded file processing:

### 1. Health Check

**Endpoint**: `GET /api/processing/health`

Check if the application is running.

```bash
curl http://localhost:8080/api/processing/health
```

**Response**:

```json
{
  "status": "UP",
  "service": "File Processing Multithread"
}
```

### 2. Get Configuration

**Endpoint**: `GET /api/processing/config`

Get the current thread pool configuration.

```bash
curl http://localhost:8080/api/processing/config
```

**Response**:

```json
{
  "defaultThreadPoolSize": 5,
  "maxThreadPoolSize": 20,
  "minThreadPoolSize": 1,
  "availableProcessors": 8
}
```

### 3. Process Transactions (Single-Threaded)

**Endpoint**: `POST /api/processing/transactions/single`

Process transaction files using a single thread.

```bash
curl -X POST http://localhost:8080/api/processing/transactions/single
```

**Response**:

```json
{
  "processingType": "Transaction Processing",
  "totalFiles": 5,
  "recordsProcessed": 244,
  "processingTimeMs": 550,
  "startTime": "2025-01-12T10:30:00",
  "endTime": "2025-01-12T10:30:01",
  "useMultithreading": false,
  "threadPoolSize": 1,
  "status": "SUCCESS",
  "message": "Processed 244 transaction records from 5 files"
}
```

### 4. Process Transactions (Multi-Threaded)

**Endpoint**: `POST /api/processing/transactions/multi`

Process transaction files using multiple threads.

**Parameters**:

- `threads` (optional, default: 5): Number of threads to use (1-20)

```bash
# Using default 5 threads
curl -X POST http://localhost:8080/api/processing/transactions/multi

# Using custom thread count
curl -X POST "http://localhost:8080/api/processing/transactions/multi?threads=10"
```

**Response**:

```json
{
  "processingType": "Transaction Processing",
  "totalFiles": 5,
  "recordsProcessed": 244,
  "processingTimeMs": 120,
  "startTime": "2025-01-12T10:30:00",
  "endTime": "2025-01-12T10:30:01",
  "useMultithreading": true,
  "threadPoolSize": 5,
  "status": "SUCCESS",
  "message": "Processed 244 transaction records from 5 files using 5 threads"
}
```

### 5. Process Logs (Single-Threaded)

**Endpoint**: `POST /api/processing/logs/single`

Process log files using a single thread.

```bash
curl -X POST http://localhost:8080/api/processing/logs/single
```

**Response**:

```json
{
  "processingType": "Log Analysis",
  "totalFiles": 3,
  "recordsProcessed": 30,
  "processingTimeMs": 480,
  "startTime": "2025-01-12T10:31:00",
  "endTime": "2025-01-12T10:31:01",
  "useMultithreading": false,
  "threadPoolSize": 1,
  "status": "SUCCESS",
  "message": "Analyzed 30 log entries from 3 files"
}
```

### 6. Process Logs (Multi-Threaded)

**Endpoint**: `POST /api/processing/logs/multi`

Process log files using multiple threads.

**Parameters**:

- `threads` (optional, default: 5): Number of threads to use (1-20)

```bash
# Using default 5 threads
curl -X POST http://localhost:8080/api/processing/logs/multi

# Using custom thread count
curl -X POST "http://localhost:8080/api/processing/logs/multi?threads=3"
```

**Response**:

```json
{
  "processingType": "Log Analysis",
  "totalFiles": 3,
  "recordsProcessed": 30,
  "processingTimeMs": 165,
  "startTime": "2025-01-12T10:31:00",
  "endTime": "2025-01-12T10:31:01",
  "useMultithreading": true,
  "threadPoolSize": 3,
  "status": "SUCCESS",
  "message": "Analyzed 30 log entries from 3 files using 3 threads (4 errors found)"
}
```

### 7. Compare Performance

**Endpoint**: `POST /api/processing/transactions/compare`

Compare single-threaded vs multi-threaded performance side-by-side.

**Parameters**:

- `threads` (optional, default: 5): Number of threads to use for multi-threaded processing

```bash
curl -X POST "http://localhost:8080/api/processing/transactions/compare?threads=5"
```

**Response**:

```json
{
  "singleThreaded": {
    "processingType": "Transaction Processing",
    "totalFiles": 5,
    "recordsProcessed": 244,
    "processingTimeMs": 550,
    "useMultithreading": false,
    "threadPoolSize": 1,
    "status": "SUCCESS"
  },
  "multiThreaded": {
    "processingType": "Transaction Processing",
    "totalFiles": 5,
    "recordsProcessed": 244,
    "processingTimeMs": 120,
    "useMultithreading": true,
    "threadPoolSize": 5,
    "status": "SUCCESS"
  },
  "speedupFactor": "4.58x",
  "timeSaved": "430ms"
}
```

## API Testing with cURL

### Quick Test Sequence

Test the complete functionality with these commands:

```bash
# 1. Check application health
curl http://localhost:8080/api/processing/health

# 2. View configuration
curl http://localhost:8080/api/processing/config

# 3. Process transactions (single-threaded)
curl -X POST http://localhost:8080/api/processing/transactions/single

# 4. Process transactions (multi-threaded with 10 threads)
curl -X POST "http://localhost:8080/api/processing/transactions/multi?threads=10"

# 5. Compare performance
curl -X POST "http://localhost:8080/api/processing/transactions/compare?threads=5"

# 6. Process log files (single-threaded)
curl -X POST http://localhost:8080/api/processing/logs/single

# 7. Process log files (multi-threaded)
curl -X POST "http://localhost:8080/api/processing/logs/multi?threads=3"
```

### Performance Testing Script

Create a bash script to test various thread pool sizes:

```bash
#!/bin/bash
echo "Testing different thread pool sizes..."

for threads in 1 2 5 10 15 20; do
    echo "Testing with $threads threads..."
    curl -s -X POST "http://localhost:8080/api/processing/transactions/multi?threads=$threads" | jq '.processingTimeMs'
    sleep 1
done
```

## API Response Fields

All processing endpoints return a `ProcessingResult` object with the following fields:

| Field               | Type     | Description                                                         |
|---------------------|----------|---------------------------------------------------------------------|
| `processingType`    | String   | Type of processing (e.g., "Transaction Processing", "Log Analysis") |
| `totalFiles`        | Integer  | Number of files processed                                           |
| `recordsProcessed`  | Integer  | Total number of records processed                                   |
| `processingTimeMs`  | Long     | Processing time in milliseconds                                     |
| `startTime`         | DateTime | When processing started                                             |
| `endTime`           | DateTime | When processing ended                                               |
| `useMultithreading` | Boolean  | Whether multithreading was used                                     |
| `threadPoolSize`    | Integer  | Size of the thread pool used                                        |
| `status`            | String   | Processing status ("SUCCESS" or "ERROR")                            |
| `message`           | String   | Descriptive message about the processing                            |

## Configuration

The application can be configured via `application.properties`:

```properties
# Server Configuration
server.port=8080
# Processing Configuration
processing.thread-pool.default-size=5
processing.thread-pool.max-size=20
processing.thread-pool.min-size=1
```

## Sample Transaction Data

Each NDJSON file contains 30-50 transaction records in the following format:

```json
{
  "transactionId": "Z18N5PRAM365CN7",
  "userId": 372,
  "amount": 4800000,
  "date": 1685990400,
  "status": "SUCCESS",
  "type": "BiFast",
  "fromAccountNumber": "1242009285830012",
  "fromBankName": "BCA",
  "toAccountNumber": "23320599833255693",
  "toBankName": "Mandiri"
}
```

## Use Cases

This project demonstrates patterns useful for various real-world scenarios where file processing performance is
critical:

### 1. Financial Transaction Processing

**Scenario**: Banks and payment processors need to process millions of transaction files daily from multiple sources (
branches, ATMs, online banking, mobile apps).

**Benefits**:

- Process end-of-day transaction reconciliation 5-10x faster
- Handle peak transaction periods (salary days, holidays) efficiently
- Reduce processing time from hours to minutes
- Meet regulatory deadlines for transaction reporting

**Example**: A bank receives 1,000 transaction files (each 100MB) at the end of business day. With single-threaded
processing taking 5 hours, multithreading can reduce this to under 1 hour.

### 2. ETL (Extract, Transform, Load) Operations

**Scenario**: Data warehouses need to extract data from multiple source files, transform it, and load it into analytical
databases.

**Benefits**:

- Process multiple data source files simultaneously
- Reduce data pipeline execution time
- Enable more frequent data refreshes (hourly vs daily)
- Improve business intelligence timeliness

**Example**: An e-commerce platform processes sales data, inventory updates, customer behavior logs, and payment records
from 50+ stores simultaneously, reducing ETL window from 4 hours to 30 minutes.

### 3. Log File Analysis and Monitoring

**Scenario**: DevOps teams need to analyze application logs, server logs, security logs, and audit trails from
distributed systems.

**Benefits**:

- Real-time or near-real-time log analysis
- Faster incident detection and response
- Efficient processing of rotated log files
- Better system monitoring and alerting

**Example**: A microservices architecture generates 500 log files per hour across 100 services. Multithreaded processing
enables near real-time anomaly detection instead of delayed batch analysis.

### 4. Data Migration and Synchronization

**Scenario**: Organizations migrating to cloud platforms or synchronizing data between systems need to process large
volumes of export files.

**Benefits**:

- Minimize downtime during migration
- Process data exports from legacy systems faster
- Efficient database dump file processing
- Parallel data validation and transformation

**Example**: Migrating 10TB of customer data split into 10,000 files. Single-threaded processing would take 20 days;
with 20 threads, this reduces to approximately 1 day.

### 5. Media File Processing

**Scenario**: Media companies, content platforms, and social media sites need to process uploaded images, videos, and
documents.

**Benefits**:

- Faster thumbnail generation and image resizing
- Parallel video transcoding and format conversion
- Efficient metadata extraction from media files
- Reduced user waiting time for uploads

**Example**: A photo-sharing platform receives 100,000 image uploads per hour. Multithreaded processing generates
thumbnails and applies filters 10x faster, improving user experience.

### 6. E-commerce Order Processing

**Scenario**: Online retailers process order files, inventory updates, shipping manifests, and returns data from
multiple warehouses and marketplaces.

**Benefits**:

- Faster order fulfillment pipeline
- Real-time inventory synchronization
- Efficient processing of marketplace feeds (Amazon, eBay, etc.)
- Reduced order processing delays

**Example**: A multi-channel retailer imports 50 order files from different marketplaces every 15 minutes. Parallel
processing ensures orders reach fulfillment centers within minutes instead of hours.

### 7. Scientific Data Analysis

**Scenario**: Research institutions process experimental data, sensor readings, genomic sequences, and simulation
results stored in multiple files.

**Benefits**:

- Accelerate research data processing
- Handle large-scale scientific datasets efficiently
- Process multiple experiment results simultaneously
- Enable faster insights and discoveries

**Example**: A genomics lab processes 1,000 DNA sequence files (each 2GB). Multithreading reduces processing time from
48 hours to 6 hours, accelerating research timelines.

### 8. Backup and Archival Operations

**Scenario**: IT departments need to process backup files, verify integrity, compress data, and archive to long-term
storage.

**Benefits**:

- Faster backup verification and restoration
- Efficient compression of multiple files
- Parallel checksum calculation and validation
- Reduced backup window requirements

**Example**: Verifying checksums for 5,000 backup files during disaster recovery. Sequential processing takes 10 hours;
parallel processing completes in 1.5 hours.

### 9. Regulatory Compliance and Reporting

**Scenario**: Financial institutions, healthcare providers, and enterprises must process audit logs, compliance reports,
and regulatory filings from multiple departments.

**Benefits**:

- Meet strict regulatory deadlines
- Process audit data from multiple sources efficiently
- Generate compliance reports faster
- Reduce risk of late submissions and penalties

**Example**: A healthcare provider processes HIPAA audit logs from 200 facilities. Multithreading reduces monthly
compliance report generation from 12 hours to 90 minutes.

### 10. Machine Learning Data Preparation

**Scenario**: Data scientists need to preprocess training datasets, clean data, and prepare features from multiple raw
data files.

**Benefits**:

- Accelerate model training pipeline
- Process multiple feature extraction jobs in parallel
- Efficient data cleaning and normalization
- Faster experimentation and model iteration

**Example**: Preprocessing 10,000 CSV files for model training. Sequential processing takes 8 hours; with
multithreading, preprocessing completes in under 1 hour, enabling more training runs per day.

### When to Use Multithreaded File Processing

**Ideal Scenarios**:

- Processing **multiple independent files** (no dependencies between files)
- **I/O-bound operations** (reading/writing files, network operations)
- Files of **similar size** for balanced workload distribution
- Systems with **multiple CPU cores** available
- **Time-critical** processing requirements

**Consider Alternatives When**:

- Files have dependencies or require sequential processing
- Single file processing (use streaming or chunking instead)
- Memory-constrained environments (thread overhead)
- CPU-bound intensive operations per file (may need different optimization)
- Very small files where thread creation overhead exceeds processing time

## Performance Tuning Tips

1. **Thread Pool Size**: Adjust based on:
    - Number of files to process
    - Available CPU cores
    - I/O vs CPU-bound operations

2. **Memory Management**: For large files, consider:
    - Stream processing instead of loading entire files
    - Chunking large files
    - Using bounded queues

3. **Error Handling**: Implement:
    - Retry mechanisms for failed files
    - Proper exception handling in worker threads
    - Logging for debugging

## License

This project is for educational purposes and demonstration of multithreading concepts in Java.

## Project Information

### Author

- **Name**: Hendi Santika
- **GitHub**: @hendisantika
- **Link**: s.id/hendisantika
- **Email**: hendisantika@yahoo.co.id
- **Telegram**: @hendisantika34

### Project Structure

All Java source files include comprehensive header documentation with:

- Project name and IntelliJ IDEA template
- Author information and contact details
- Creation date and time
- Consistent formatting across all classes

**Main Components**:

- **Controller Layer**: REST API endpoints for file processing
- **Service Layer**: Business logic for transaction and log processing
- **Configuration Layer**: Thread pool and application settings
- **DTO Layer**: Data Transfer Objects for transactions, logs, and results

### Key Features Implemented

1. **REST API Endpoints** (7 endpoints)
    - Health check and configuration viewing
    - Single-threaded and multi-threaded processing
    - Performance comparison tools

2. **Dual Processing Modes**
    - Transaction file processing (financial data)
    - Log file analysis (DevOps monitoring)

3. **Thread Pool Management**
    - Configurable thread pool size (1-20 threads)
    - Dynamic thread allocation
    - Thread-safe collections and atomic operations

4. **Performance Monitoring**
    - Processing time tracking
    - Speedup factor calculation
    - Detailed statistics reporting

## Version History

- **0.0.1-SNAPSHOT**: Initial version with single and multi-threaded file processing
- **Latest**: Spring Boot 3.5.6, Java 21+
- **Features**: REST APIs, dual processing modes, comprehensive documentation

## Contributing

This is an educational project demonstrating multithreading concepts in Java. Contributions and suggestions are welcome!

## Repository

For issues, questions, or contributions, please visit the project repository.

## License

This project is for educational purposes and demonstration of multithreading concepts in Java.
