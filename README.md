# elastic-job-scheduler
An Elastic , Robust, Fault tolerant ,Scalable Java Spring/Spring Boot quartz based scheduler which is easy to use and maintainable.

It has 3 modules :-

job-manager - Has api's to create,update,delete,pause,resume jobs. Job information is stored in custom tables as well as tables used by 
              quartz.
job-scheduler - Gets quartz triggers for different jobs and then publishes it onto a queue.
                Also, receives job execution statuses from job-agent-executor and persists it into a database
job-agent-executor - Listens to a queue for execution requests. Executes the job request (assuming configured in the form of shell      
                     scripts). Publishes execution results to another queue (listened by job-scheduler)
                     Agent Executor can be installed on a machine where the job execution is required
