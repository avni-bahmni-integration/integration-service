This module uses the Exotel API and creates the tasks in the Avni system. 
Below is the mapping details which is used while creating a new Task.
- Concept names expected to be present in Avni 
  - Number (Text)
  - State (Coded) -> (Answers -> DL, CG)
  - Program (Coded) -> (Answers -> BoCW, RTE)

- Exotel to Avni Task metadata mapping
  - Sid -> External ID
  - To -> This is used to populate State and Program. Each number is associated to a state and a program
  - From -> Number