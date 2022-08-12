### Power

This module uses the Exotel API and creates the tasks in the Avni system. [More on Exotel Voice API.](https://developer.exotel.com/api/make-a-call-api#bulk-call-details)

Below is the mapping details which is used while creating a new Task.
- Concept names expected to be present in Avni 
  - `Number` (Text)
  - `State` (Coded) -> (Answers -> DL, CG)
  - `Program` (Coded) -> (Answers -> BoCW, RTE)
  - For the `Program` and `State` more answers can be added and mappings to the mobile number can be done in `mapping_metadata` table. 

- Exotel to Avni Task metadata mapping
  - `Sid` -> `External ID`
  - `From` -> `Number`
  - `To` -> This is used to populate `State` and `Program` based on the mapping present in `mapping_metadata` table.

### Duplicate task handling
Before creating new task in Avni service checks if there is already a task with the same mobile number in non-terminal state. 
New task is not created if there is any task with non-terminal state with the same mobile number.
However, system will create a new task if older task with the same number has and terminal status.


### Error handling
In case any error occurs during the task save, call `Sid` is saved in the system and is picked by the anther job which calls
the Exotel API using these failed `Sid` and creates the task in Avni. Once `Sid` is processed that entry gets deleted from the system.


### Configuration details
All the configuration details are picked from `power-application.properties` file. Below are the env variable which are required,

```
POWER_EXOTEL_API_KEY : Exotel API key
POWER_EXOTEL_API_TOKEN : Exotel API token
POWER_EXOTEL_ACCOUNT_SID : Exotel Account SID
POWER_EXOTEL_SUBDOMAIN : Exotel Subdomain
POWER_INT_SCHEDULE_CRON : Cron expression to run the call sync job. Default is every day 02:00 am.
POWER_INT_SCHEDULE_CRON_FULL_ERROR : Cron expression to run the error sync job. Default is every day 03:00 am.
```