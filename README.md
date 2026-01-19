![Java](https://img.shields.io/badge/java-11+-blue)
![License](https://img.shields.io/github/license/binary-bablu/elastic-job-scheduler)
![Status](https://img.shields.io/badge/status-active-brightgreen)


# 📦 elastic-job-scheduler ⏱️ ⏳ 📅 🧠 🐙
An Elastic , Fault tolerant ,Scalable job scheduler which is easy to use and maintain. Uses shell script as a medium to exceute jobs at scale — enabling Java, SQL, C/C++, and anything executable.

## ℹ️ Overview
A light weight Job scheduler which can scale horizontally,robust,can be deployed on-prem/cloud, api centric ,easy to maintain and enhance, based on open and open source tech like Spring/Spring Boot, Quartz and Java.No requirement for large scale infrastructure elements. 
## 🌟 Highlights
- Light weight ,Fault Tolerant ,Scalable, Easy to Maintain/Enhance, Extensible for Enterprise grade features
- Can be hosted on Cloud , on-prem, scales horizontally
- Better alternative for large clunky open source , cost heavy commercial ones
- API centric , extensible for any required functionality like monitoring, alerting, email, easy to setup and get it up and running
- Shell script(driven) is a medium to execute anything e.g:- Java , SQL, C/C++ binaries, Rust , Go programs, Api calls 

## Why not cron?
- cron doesn't scale
- cron has no retry semantics
- cron has no observability

## Why not Airflow?
- Airflow is heavy
- Airflow assumes Python
- Airflow doesn't embrace shell

Elastic Job Scheduler sits in between.

## Who is this for?
Engineering ,DevOps & SRE teams
Infra engineers
Teams with mixed workloads

## What makes it different
✅ Shell-first execution (Java, SQL, C/C++, Python, anything executable)
✅ Distributed agents
✅ Retry & dead-letter handling
🚧 (under construction) Event-driven triggers (webhooks, messages, files)
✅ API-driven (no UI required)

## 🏗️ Architecture Diagram
   <img width="648" height="724" alt="job-sched" src="https://github.com/user-attachments/assets/90ba811a-3eed-4a03-8d50-2f392c206587" />

   

## 🛠️ Components

### 1. 🧩 **Job Manager** (Port 8080)
- Manages job definitions and schedules (CRUD operations for Job Defs and schedules)
- REST styled API's
- Monitoring (to be developed/in-progress)
### 2. 🧩 **Job Scheduler** (Port 8082)
- Handles Quartz based triggers.
- Publishes job execution requests to queue when triggered
- Processes execution results from agents
### 3. 🧩 **Executor Agent** (Port 8081+)
- Consumes job execution requests from queue
- Execute shell scripts with parameters
- Publish results back to scheduler
- Can run on different machines and Auto-scaling friendly
### 4. 🧩 **Message Queue** (RabbitMQ)
- **Execution Queue**: Job requests from scheduler to agents
- **Result Queue**: Execution results from agents to scheduler
- **Retry Queue**: Failed jobs for retry with delay
- **DLQ**: Dead letter queue for permanently failed jobs
### 5. 🧩 **Storage**
- **PostgreSQL**: Quartz job metadata , Job definitions, Job Execution Information with agent heartbeats

## Benefits of This Architecture

### ✅ **Massive Scalability**
- **Scheduler**: Can handle 10,000+ job definitions
- **Executors**: Add agents horizontally (100+ agents possible)
- **Queue**: RabbitMQ can handle millions of messages
- Each component can be deployed as service that can scale horizontally

   🎯 **Projected Performance Numbers** (Upon Thread Pool optimization, Connection Pool optimization)

| Scenario | Capacity | Configuration |
| :---         |     :---:      |          ---: |
| Small Scale   | 500+ jobs     | 1 scheduler + 5 agents   |
| Medium Scale     | 5,000+ jobs       | 2 schedulers + 20 agents     |
| Large Scale      | 50,000+ jobs       | 3 schedulers + 100 agents     |

### ✅ **Fault Tolerance**
- Jobs survive agent failures (requeued automatically)
- Scheduler and agents are stateless (except job definitions)
- Built-in retry mechanisms with exponential backoff

### ✅ **Monitoring & Observability**
- 💡More to come, ideas are brewing ☕ stay tuned ..  

## 📋 Prerequisites for running  
a) Have a PostgreSQL Database Instance configured  
b) Have a RabbitMQ installation done  
c) Note :- The current yaml files assume local installation of PostgreSQL database and RabbitMQ (with default creds and setup)  
d) Run Quartz table.sql script from Quartz github for postgresql (upto you if you want different schema or in your app schema)

## 🏃 How to Run
a) Have the repo git cloned or forked for your need(s)  
b) Import the repo in your favourite editor (typically Eclipse or IntelliJ)  
c) Run the job-manager module  

- 🧠 **Simple Sample Job Creation Request (success and no re-tries):-**:

curl -X POST http://localhost:8080/api/jobs \
-H "Content-Type: application/json" \
-d '{
    "jobName": "daily-backup-1",
    "jobGroup": "maintenance", 
    "cronExpression": "0/30 * * * * ?",
    "scriptPath": "/Users/mylaptop/test.sh",
    "ownerEmail": "aa@bb.com", 
    "parameters": {"database": "prod"}
}'  

- 🧠 **Sample Job Creation Request with Re-try:-**:

curl -X POST http://localhost:8080/api/jobs \
-H "Content-Type: application/json" \
-d '{
    "jobName": "daily-backup-1",
    "jobGroup": "maintenance", 
    "cronExpression": "0/30 * * * * ?",
    "scriptPath": "/Users/test_laptop/test1.sh",
    "parameters": {"database": "prod"},
    "ownerEmail": "aa@bb.com", 
    "description" :"Daily backup job",
	 "retryConfig": {
        "maxAttempts": 2,
        "backOffStrategy": "FIXED",
        "initialDelayMs": 30000,
        "nonRetryableExitCodes": [2, 126, 130, 143]
    }
}'

d) Agent could be run from command line like below :-  
*mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dexecutor.agent.id=123"*    

Multiple such agents can be run with different agent id's  
e) Similar to above multiple job-scheduler's can be run on different port's


## 🚀 Underlying Tech
Java, Spring/Spring Boot, RabbitMQ, PostgreSQL

### ✍️ Author
Engineer , Management Lead, Works for a bank - 
"Managing minds and machines — all before the next release."

## Project status
🚧 Actively developed
🚀 Early-stage but production-oriented
💬 Feedback welcome
📧 **Email me at:** [darkavenger57@yahoo.co.in](mailto:darkavenger57@yahoo.co.in)
If this project helps you, please consider giving it a ⭐
