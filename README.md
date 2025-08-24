# 📦 elastic-job-scheduler ⏱️ ⏳ 📅 🧠 🐙
An Elastic , Fault tolerant ,Scalable job scheduler which is easy to use and maintainable.

## ℹ️ Overview
A light weight Job scheduler which can scale horizontally. Architecture follows separation of concerns :- scheduling concerns , execution concerns and management concerns. Incorporates queue based distributed pattern which can scale to thousands of jobs, based on open source tech. Follows an approach where execution agents can be deployed on machines/containers where actual execution should happen. No requirement for lot of infrastructure elements. 
## 🌟 Highlights
- Light weight ,Fault Tolerant ,Scalable, Easy to Maintain/Enhance, Extensible for Enterprise grade features
- Follows separation of concerns architectural pattern, SOA, queue based
- Can be hosted on Cloud , on-prem, scales horizontally
- Better alternative for large clunky open source , cost heavy commercial ones
- API centric , extensible for any required functionality like monitoring, alerting, email, easy to setup and get it running

## 🏗️ Architecture Diagram
   <img width="718" height="457" alt="image" src="https://github.com/user-attachments/assets/99a0aefd-394a-4280-8ca8-efefd176d347" />
   

## 🛠️ Components

### 1. 🧩 **Job Manager** (Port 8080)
- Manages job definitions and schedules (CRUD operations for Job Defs and schedules)
- REST styled API's
- Monitoring (to be developed/in-progress)
### 2. 🧩 **Job Scheduler** (Port 8082)
- Handles Quartz based triggers.
- Publishes job execution requests to queue when triggered
- Processes execution results from agents
### 3. 🧩 **Executor Agents** (Port 8081+)
- Consume job execution requests from queue
- Execute shell scripts with parameters
- Publish results back to scheduler
- Can run on different machines
- Auto-scaling friendly
### 4. 🧩 **Message Queues** (RabbitMQ)
- **Execution Queue**: Job requests from scheduler to agents
- **Result Queue**: Execution results from agents to scheduler
- **Retry Queue**: Failed jobs for retry with delay
- **DLQ**: Dead letter queue for permanently failed jobs
### 5. 🧩 **Storage**
- **PostgreSQL**: Quartz job metadata , Job definition, Job Execution Information with agent heartbeats

## Benefits of This Architecture

### ✅ **Massive Scalability**
- **Scheduler**: Can handle 10,000+ job definitions
- **Executors**: Add agents horizontally (100+ agents possible)
- **Queue**: RabbitMQ can handle millions of messages
- Each component can be deployed as service that can scale horizontally

   🎯 **Projected Performance Numbers** (Upon Thread Pool optimization, Connection Pool optimization)

| Scenario | Capacity | Configuration |
| :---         |     :---:      |          ---: |
| Small Scale   | 1,000 jobs     | 1 scheduler + 5 agents   |
| Medium Scale     | 10,000 jobs       | 2 schedulers + 20 agents     |
| Large Scale      | 100,000 jobs       | 3 schedulers + 100 agents     |

### ✅ **Fault Tolerance**
- Jobs survive agent failures (requeued automatically)
- Scheduler and agents are stateless (except job definitions)
- Built-in retry mechanisms with exponential backoff

### ✅ **Monitoring & Observability**
- 💡More to come, ideas are brewing ☕ stay tuned ...

### ✍️ Authors
Engineer , Management Lead, Works for a bank - 
"Managing minds and machines — all before the next release."

Have questions or feedback? I'd love to hear from you!  
📧 **Email me at:** [darkavenger57@yahoo.co.in](mailto:darkavenger57@yahoo.co.in)

## 🚀 Underlying Tech
Java, Spring/Spring Boot, RabbitMQ, PostgreSQL

## 🚧 UI/UX Still not there...! 🎨

## 💭 Feedback and Contributing
   Not open yet

