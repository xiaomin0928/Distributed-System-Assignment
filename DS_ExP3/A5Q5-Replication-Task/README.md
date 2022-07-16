# Run Manual

## execution environment: win 10

## 1.Start gradle
$gradle

## 2.Build the project
$gradle build

# Tasks
## 1. Majority Consensus
In this exercise, you will implement the Majority Consensus algorithm as given in the lecture.  
Furthermore, the following set of classes is provided for implementing the communication:  
* RequestReadVote – Requests a read vote for the client on a replica.
* RequestWriteVote – Requests a write vote for the client on a replica.
* ReleaseReadLock – Releases a read lock for the client on a replica.
* ReleaseWriteLock – Releases a write lock for the client on a replica.
* ReadRequestMessage – Requests the current value from a replica.
* WriteRequestMessage – Requests that a replica updates its value and version to those contained in the message, given that it is a valid update (i.e. given version number > current version number).
* ValueResponseMessage – Sent in response to a ReadRequestMessage. Contains the value stored on this replica.
* Vote – Contains the Vote (“YES”/“NO”) and version number of a replica. In addition, this message is used as a general ACK/NACK message for ReleaseReadLock, ReleaseWriteLock and WriteRequestMessage. The version number is only valid for “YES” votes sent in response to a RequestReadVote/RequestWriteVote message.
