# crawler
## description

java crawler (not finished)

## dependences

+ mysql
+ rabbitmq

## runtime

+ jdk1.8+

## structure

+ overview

```
graph TD

    A[injector] --> | different job_id different queue | B(mq)
    B -->|job1| Q1{Q1}
    B -->|job2| Q2{Q2}
    Q1 --> |n consumers| D[crawler]
    Q2 --> |n consumers| D[crawler]
    Q1 --> |n consumers| E[crawler]
    Q2 --> |n consumers| E[crawler]

```

+ crawler

```
graph TD

   A[fetcher] --> B[parser]

   B --> C[sink]


```
