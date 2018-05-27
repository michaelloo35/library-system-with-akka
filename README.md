# library system based on Akka written in scala (basically akka training)

## Clients may:
- check books price "search:title"
  two database nodes are searched concurrently by worker-actors (one response is enough)
  
- order a book "order:book"
  TODO
  
- stream a book "stream:book"

### Server node can be divided into three parts:
- main actor that defers requests to specialty actors
- three specialty actors that commands concrete tasks to workers
- workers are kept as routers within specialty actors ( their thread pools are specified in .conf files )


#### book parts source:
http://www.fulltextarchive.com/ 
