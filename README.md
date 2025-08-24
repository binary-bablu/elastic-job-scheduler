# 📦 elastic-job-scheduler
An Elastic , Fault tolerant ,Scalable job scheduler which is easy to use and maintainable.

## 🌟 Highlights

- Fault Tolerant ,Scalable, Easy to Maintain/Enhance, Extensible for Enterprise grade enhancements
- Built using Java, Spring/Spring Boot, Quartz, PostgreSQL, RabbitMQ
- Follows separation of concerns
- Can be hosted on Cloud , on-prem, scales horizontally
- Better alternative for large clunky open source , cost heavy commercial ones
- API centric , extensible for any required functionality like alerting, email, easy to setup and get it running

  ## ℹ️ Overview
A distributed ,elastic, fault tolerant and scalable Job scheduler which can scale horizontally. Architecture follows separation of concerns :- scheduling concerns , execution concerns and management concerns. Has queue based distributed pattern which scales to thousands of jobs.

# Components

### 1. **Job Manager** (Port 8080)
- Manages job definitions and schedules (CRUD operations for Job Defs and schedules)
- REST styled API's
- Monitoring (to be developed/in-progress)

### 2. **Job Scheduler ** (Port 8082)
- Handles Quartz based triggers.
- Publishes job execution requests to queue when triggered
- Processes execution results from agents

### 3. **Executor Agents** (Port 8081+)
- Consume job execution requests from queue
- Execute shell scripts with parameters
- Publish results back to scheduler
- Can run on different machines
- Auto-scaling friendly

### 4. **Message Queues** (RabbitMQ)
- **Execution Queue**: Job requests from scheduler to agents
- **Result Queue**: Execution results from agents to scheduler
- **Retry Queue**: Failed jobs for retry with delay
- **DLQ**: Dead letter queue for permanently failed jobs

### 5. **Storage**
- **PostgreSQL**: Quartz job metadata and schedules and execution status with agent heartbeats

## Benefits of This Architecture

### ✅ **Massive Scalability**
- **Scheduler**: Can handle 10,000+ job definitions
- **Executors**: Add agents horizontally (100+ agents possible)
- **Queue**: RabbitMQ can handle millions of messages
- Each component can be deployed as service that can scale horizontally

### ✅ **Fault Tolerance**
- Jobs survive agent failures (requeued automatically)
- Scheduler and agents are stateless (except job definitions)
- Built-in retry mechanisms with exponential backoff

### ✅ **Monitoring & Observability**
- More to come, stay tuned ....


  🎯 Projected Performance Numbers (Upon Thread Pool optimization, Connection Pool optimization)

| Scenario | Capacity | Configuration |
| :---         |     :---:      |          ---: |
| Small Scale   | 1,000 jobs     | 1 scheduler + 5 agents   |
| Medium Scale     | 10,000 jobs       | 2 schedulers + 20 agents     |
| Large Scale      | 100,000 jobs       | 3 schedulers + 100 agents     |


### ✍️ Authors

Engineer , Management Lead, Works for a bank, based out of India.

## 🚀 Usage

### 1. **Start Job Manager**
```bash
# On scheduler machine
mvn clean package
java -jar target/distributed-quartz-scheduler-0.0.1-SNAPSHOT.jar --spring.profiles.active=scheduler
```

### 2. **Start Scheduler**
```bash
# On scheduler machine
mvn clean package
java -jar target/distributed-quartz-scheduler-0.0.1-SNAPSHOT.jar --spring.profiles.active=scheduler
```

### 3. **Start Executor Agents**
```bash
# On agent machine 1
EXECUTOR_AGENT_ID=agent-1 java -jar target/distributed-quartz-scheduler-0.0.1-SNAPSHOT.jar --spring.profiles.active=executor

# On agent machine 2
EXECUTOR_AGENT_ID=agent-2 java -jar target/distributed-quartz-scheduler-0.0.1-SNAPSHOT.jar --spring.profiles.active=executor
```

## ⬇️ Installation

Simple, understandable installation instructions!

```bash
pip install my-package
```

And be sure to specify any other minimum requirements like Python versions or operating systems.

*You may be inclined to add development instructions here, don't.*


## 💭 Feedback and Contributing

Add a link to the Discussions tab in your repo and invite users to open issues for bugs/feature requests.

This is also a great place to invite others to contribute in any ways that make sense for your project. Point people to your DEVELOPMENT and/or CONTRIBUTING guides if you have them.

It has 3 modules :-

job-manager - Has api's to create,update,delete,pause,resume jobs. Job information is stored in custom tables as well as tables used by 
              quartz.
job-scheduler - Gets quartz triggers for different jobs and then publishes it onto a queue.
                Also, receives job execution statuses from job-agent-executor and persists it into a database
job-agent-executor - Listens to a queue for execution requests. Executes the job request (assuming configured in the form of shell      
                     scripts). Publishes execution results to another queue (listened by job-scheduler)
                     Agent Executor can be installed on a machine where the job execution is required
