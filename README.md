# TrackingNumber 
### TrackingNumber application is a project to generate unique number along with checksum added in the generated number to validate before proceeding with any further action.

The generate number has starting first two digit reserved for region/state/node/service from which we are executing to have better track of number. 

Eight digits are randomply generated alphanumeric number to provide uniqueness and last one digit is checksum.

Generated sequence will have below format.

**<region 2 character> <unique alphanumeric number 8 character> <checksum 1 character>**

#### To generate the number we need to start the application on any IDE(Eclipse / Intellij) and call the below url if you are executing on local machine or provide system ip instead of localhost.

#### We are using default port number for spring-boot. Another way is we can generate jar using maven command and execute java -jar** < generated jar file name >**. Below get call we need to make from either postman or any browser to fetch response.


**http://localhost:8080/trackingnumber**

**--** Output should be a uniquie tracking number with above mentioned format. 

**--** To validate any tracking number is valid we need to follow the Luhn algorith which we have used to add last digit checksum.

**--** We are using BlockingQueue to make system performance efficient as well as thread safe. 

**--** To handle fault tolerance we have checksum implementaion to validate any sequence of given alphanumeric.

**--** Added docker file and since spring-boot has embedded tomcat it can be deployed and scaled at no time. 

