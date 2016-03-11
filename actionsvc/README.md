## To build
./mvnw clean install


## To be able to log to file
sudo mkdir -p /var/log/ctp/responsemanagement/actionsvc 
sudo chmod -R 777 /var/log/ctp


## To run
The app can be started from the command line using : ./mvnw spring-boot:run


## To test action plans
curl http://localhost:8161/actionplans -v -X GET
200 [{"actionPlanId":1,"surveyId":1,"name":"HH","description":"Household Action Plan","createdBy":"SYSTEM",
"createdDatetime":"2016-03-09T11:15:48.002+0000","lastGoodRunDatetime":null},...


curl  -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8161/actionplans/1 -v -X PUT -d "{\"description\":\"philippetesting\"}"
200 {"actionPlanId":1,"surveyId":1,"name":"HH","description":"philippetesting","createdBy":"SYSTEM","createdDatetime":"2016-03-10T15:10:39.494+0000","lastGoodRunDatetime":null}




curl -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8161/actionplans/1 -v -X PUT -d "{\"actionPlanId\":1,\"surveyId\":1,\"name\":\"HH\",\"description\":\"pbrossiertesting\",\"createdBy\":\"SYSTEM\",\"createdDatetime\":\"2016-03-10T15:10:39.494+0000\",\"lastGoodRunDatetime\":\"2016-03-10T15:10:39.494+0000\"}"



