# Run Manual

## execution environment: win 10

## 1.Start gradle
$gradle

## 2.Build the project
$gradle build

# Tasks
## 1. Distributed Debugging Algorithm
Given are the following two worker processes P1 and P2, which can communicate by exchanging messages over reliable FIFO channels.  

***Listing 1: P1***   
```
1  x1 := 5  
2  send ( P2 , x1 )  
3  x1 := x1 * 3  
4  send ( P2 , x1 )  
5  x1 := receive () - x1  
```
***Listing 2: P2***
```
1 x2 := receive ()
2 x2 := x2 + 5
3 x2 := x2 + receive ()
4 send ( P1 , x2 )
```
Here, send(Pn, x) sends the value of x to the process Pn. The channel buffers the message until Pn is ready to receive it. The blocking call receive() is used to receive a message from an arbitrary communication partner. 
It returns the value contained in the received message. 
The local variables xi of both processes are initialized to zero.
In addition, each process Pi sends a state-message to a central monitor process every time the value of its local variable xi changes. A state-message contains the value of the local variable xi and the current vector timestamp of the process. 
