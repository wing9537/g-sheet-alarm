## Settings
![image](https://github.com/wing9537/g-sheet-alarm/assets/37375762/3c634a69-a377-43cd-9fc8-9d97b70c2e32)


## Gradle 7.5.1
- Kotlin:       1.6.21
- Groovy:       3.0.10
- Ant:          Apache Ant(TM) version 1.10.11 compiled on July 10 2021
- JVM:          1.8.0-332 (Oracle Corporation 25.71-b09)
- OS:           Windows 10 10.0 amd64

------------------------------------------------------------

## Deployment
1. ./gradlew build
2. remote ssh to production server
3. deploy .\build\libs\g-sheet-alarm-1.0.0.jar
4. create script run.sh
5. setup cron job by `crontab -e`

------------------------------------------------------------

## Project Structure
```
g-sheet-alarm
├── g-sheet-alarm-1.0.0.jar
├── debug.log
└── run.sh
```

------------------------------------------------------------

## run.sh
```
0  #!/bin/sh
1  JAR_FILE="~/g-sheet-alarm/g-sheet-alarm-1.0.0.jar"
2  LOG_FILE="~/g-sheet-alarm/debug.log"
3
4  java -jar $JAR_FILE [spreadsheetId] >> $LOG_FILE 2>&1
```
- Note1: replace [spreadsheetId] to your own spreadsheet id
- Note2: duplicate line4 if you want to monitor multiple spreadsheet at the same times
- Note3: make sure run.sh has executable permissions `chmod -x run.sh`
