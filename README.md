# Routing_Packets_within_Structured_P2P_Network_Overlay

## 1 Objectives & Overview
The objective of this assignment is to get you familiar with coding in a distributed setting where you need to manage the underlying communications between nodes. Upon completion of this assignment you will have a set of reusable classes that you will be able to draw upon.
As part of this assignment you will be implementing routing schemes for packets in a structured peer- to-peer (P2P) overlay system. This assignment requires you to: (1) construct a logical overlay over a distributed set of nodes, and then (2) use partial information about nodes within the overlay to route packets. The assignment demonstrates how partial information about nodes comprising a distributed system can be used to route packets while ensuring correctness and convergence.
Nodes within the system are organized into an overlay i.e. you will be imposing a logical structure on the nodes. The overlay encompasses how nodes are organized, how they are located, and how information is maintained at each node. The logical overlay helps with locating nodes and routing content efficiently.
The overlay will contain at least 10 messaging nodes, and each messaging node will be connected to some other messaging nodes.
Once the overlay has been setup, messaging nodes in the system will select a node at random and send a message to that node (also known as the sink or destination node). Rather than send this message directly to the sink node, the source node will use the overlay for communications. Each node consults its routing table, and either routes the packet to its final destination or forwards it to an intermediate node closest (in the node ID space) to the final destination. Depending on the overlay, there may be zero or more intermediate messaging nodes between a particular source and sink that packets must pass through. Such intermediate nodes are said to relay the message. The assignment requires you to verify correctness of packet exchanges between the source and sink by ensuring that: (1) the number of messages that you send and receive within the system match, and (2) these messages have been not corrupted in transit to the intended recipient. Message exchanges happen continually in the system.
All communications in this assignment are based on TCP. The assignment must be implemented in Java and you cannot use any external jar files. You must develop all functionality yourself. This assignment may be modified to clarify any questions (and the version number incremented), but the crux of the assignment and the distribution of points will not change.
 Some context for this assignment:
What you are building is a simplified version of a structured P2P system based on distributed hash tables (DHTs); the routing here is a simplified implementation of the well-known Chord P2P system. In most DHTs node identifiers are 128-bits (when they are based on UUIDs) or 160-bits (when they are generated using SHA-1). In such systems the identifier space ranges from 0 to 2128 or 2160. Structured P2P systems are important because they have demonstrably superior scaling properties

There are two components that you will be building as part of this assignment: a registry and a messaging node. There is exactly one instance of the registry and multiple instances of the messaging nodes.

### 1.1 Registry:
There is exactly one registry in the system. The registry provides the following functions:
A. Allows messaging nodes to register themselves. This is performed when a messaging node
starts up for the first time.
B. Assign random identifiers (between 0-127) to nodes within the system; the registry also has
to ensure that two nodes are not assigned the same IDs i.e., there should be no collisions in
the ID space.
C. Allows messaging nodes to deregister themselves. This is performed when a messaging node
leaves the overlay.
D. Enables the construction of the overlay by populating the routing table at the messaging
nodes. The routing table dictates the connections that a messaging node initiates with other messaging nodes in the system.
The registry maintains information about the registered messaging nodes; you can use any data structure for managing this information but make sure that your choice can support all the operations that you will need.
The registry does not play any role in the routing of data within the overlay. Interactions between the messaging nodes and the registry are via request-response messages. For each request that it receives from the messaging nodes, the registry will send a response back to the messaging node (based on the IP address associated with Socket’s input stream) where the request originated. The contents of this response depend on the type of the request and the outcome of processing this request.

### 1.2 The Messaging node
Unlike the registry, there are multiple messaging nodes (minimum of 10) in the system. A messaging node provides two closely related functions: it initiates and accepts both communications and messages within the system.
Communications that nodes have with each other are based on TCP. Each messaging node needs to automatically configure the port over which it listens for communications i.e. the server-socket port numbers should not be hard-coded or specified at the command line. TCPServerSocket is used to accept incoming TCP communications.
Once the initialization is complete, the node should send a registration request to the registry.
Each node in the system has a routing table that is used to route content along to the sink. This routing table contains information about a small subset of nodes in the system. Nodes should use this routing table to forward packets to the sink specified in the message. Every node makes local decisions based on its routing table to get the packets closer to the sink. Care must be taken to ensure you don’t change directions or overshoot the sink: in such a case, packets may continually traverse the overlay.

## 2 Interactions between the components
This section will describe the interactions between the registry and the messaging nodes. Along with the semantics of these interactions, the prescribed wire-formats have also been included. A good programming practice is to have a separate class for each message type so that you can isolate faults better. The Message Types that have been specified could be part of an interface, say cs455.overlay.wireformats.Protocol and have values specified there. This way you are not hard- coding values in different portions of your code.
  Use of Java serialization is not allowed and the deductions for doing so are very steep; please see the deductions section for additional information. Your classes for the message types SHOULD NOT implement the java.io.Serializable interface.

### 2.1 Registration:
Upon starting up, each messaging node should register its IP address, and port number with the registry. It should be possible to register messaging nodes that are running on the same host but are listening to communications on different ports. There should be 4 fields in this registration request:
byte: Message Type (OVERLAY_NODE_SENDS_REGISTRATION) byte: length of following "IP address" field byte[^^]: IP address; from InetAddress.getAddress() int: Port number
When a registry receives this request, it checks to see if the node had previously registered and ensures that the IP address in the message matches the address where the request originated. The registry issues an error message under two circumstances:
• If the node had previously registered and has a valid entry in its registry.
• If there is a mismatch in the address that is specified in the registration request and the IP
address of the request (the socket’s input stream).
If there is no error, the registry generates a unique identifier (between 0-127) for the node while ensuring that there are no duplicate IDs being assigned.
The contents of the response message generated by the registry are depicted below. The success or failure of the registration request should be indicated in the status field of the response message.
byte: Message type (REGISTRY_REPORTS_REGISTRATION_STATUS)
int: Success status; Assigned ID if successful, -1 in case of a failure byte: Length of following "Information string" field
byte[^^]: Information string; ASCII charset
In the case of successful registration, the registry should include a message that indicates the number of entries currently present in its registry. A sample information string is “Registration request successful. The number of messaging nodes currently constituting the overlay is (5)”. If the registration was unsuccessful, the message from the registry should indicate why the request was unsuccessful.
NOTE: In the rare case that a messaging node fails just after it sends a registration request, the registry will not be able to communicate with it. In this case, the entry for the messaging node should be removed from the data structure maintained at the registry.
### 2.2 Deregistration
When a messaging node exits it should deregister itself. It does so by sending a message to the registry. This deregistration request includes the following fields
byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION) byte: length of following "IP address" field byte[^^]: IP address; from InetAddress.getAddress() int: Port number
The registry should check to see that request is a valid one by checking (1) where the message originated and (2) whether this node was previously registered. Error messages should be returned in case of a mismatch in the addresses or if the messaging node is not registered with the overlay. You should be able to test the error-reporting functionality by de-registering the same messaging node twice. The registry will respond with a REGISTRY_REPORTS_DEREGISTRATION_STATUS that is similar to the REGISTRY_REPORTS_REGISTRATION_STATUS message.
int: assigned Node ID

### 2.3 Peer node manifest
  Once the setup-overlay command (see section 3) is specified at the registry it must perform a series of actions that lead to the creation of the overlay with: (1) a routing table being installed at every node, and (2) messaging nodes initiating connections with each other. Messaging nodes await instructions from the registry regarding the messaging nodes that they must establish connections to – messaging nodes only initiate connections to nodes that are part of its routing table.
The registry is responsible for populating state information at nodes within the system. It does so by propagating state information: this is used to populate the routing table (both the node IDs and the corresponding logical addresses) at individual nodes and also to inform nodes about other nodes (only the node IDs) in the system.
The registry must ensure two properties. First, it must ensure that the size of the routing table at every messaging node in the overlay is identical; this is a configurable metric (with a default value of 3) and is specified as part of the setup-overlay command. Second, the registry must ensure that there is no partition within the overlay i.e. it should be possible to reach any messaging node from any other messaging node in the overlay.
If the routing table size requirement for the overlay is NR, each messaging node will have links to NR other messaging nodes in the overlay. The registry selects these NR messaging nodes that constitute the peer-messaging nodes list for a messaging node such that the first entry is one hop away in the ID space, the second entry is two hops away, and the third entry is 4 hops away. Consider a network overlay comprising nodes with the following identifiers: 10, 21, 32, 43, 54, 61, 77, 87, 99, 101, 103. The routing table at 10 includes information about nodes <21, 32, and 54> while the routing table at node 101 includes information about nodes <103, 10, 32>; notice how the ID space wraps around after 103. A messaging node should initiate connections to all nodes that are part of its routing table. A check should be performed to ensure that the list does not include the targeted messaging node i.e. a messaging node should not have to connect to itself.
The registry also informs each node about the IDs (it should not include IP addresses) of all nodes in the system. This information is used in the testing part of the overlay to randomly select nodes that the messages should be sent to.
The registry includes all this information in a REGISTRY_SENDS_NODE_MANIFEST message. The contents of the manifest message are different for each messaging node (since the routing table at every messaging node would be different). The wire format is shown when NR=3, if NR=4 there will also be
byte: Message type; REGISTRY_SENDS_NODE_MANIFEST
byte: routing table size NR
int: Node ID of node 1 hop away
byte: length of following "IP address" field
byte[^^]: IP address of node 1 hop away; from InetAddress.getAddress() int: Port number of node 1 hop away
int: Node ID of node 2 hops away
byte: length of following "IP address" field
byte[^^]: IP address of node 2 hops away; from InetAddress.getAddress() int: Port number of node 2 hops away
int: Node ID of node 4 hops away
byte: length of following "IP address" field
byte[^^]: IP address of node 4 hops away; from InetAddress.getAddress() int: Port number of node 4 hops away
byte: Number of node IDs in the system
int[^^]: List of all node IDs in the system [Note no IPs are included]
an entry for a node 2(NR−1) hops away.

  Note that the manifest message includes IP addresses only for nodes within a particular node’s routing table. Upon receipt of the manifest from the registry, each messaging node should initiate connections to the nodes that comprise its routing table.

### 2.4 Node overlay setup
Upon receipt of the REGISTRY_SENDS_NODE_MANIFEST from the registry, each messaging node should initiate connections to the nodes that comprise its routing table. Every messaging node must report to the registry on the status of setting up connections to nodes that are part of its routing table.
byte: Message type (NODE_REPORTS_OVERLAY_SETUP_STATUS)
int: Success status; Assigned ID if successful, -1 in case of a failure byte: Length of following "Information string" field
byte[^^]: Information string; ASCII charset

### 2.5 Initiate sending messages
The registry informs nodes in the overlay when they should start sending messages to each other. It does so via the TASK_INITIATE control message. This message also includes the number of packets that must be sent by each messaging node.
byte: Message type; REGISTRY_REQUESTS_TASK_INITIATE int: Number of data packets to send
### 2.6 Send data packets
Data packets can be fed into the overlay from any messaging node within the system. Packets are sent from a source to a sink; it is possible that there might be zero or more intermediate nodes in the system that relay packets en route to the sink. Every node tracks the number of messages that it has relayed during communications within the overlay.
When a packet is ready to be sent from a source to the sink, the source node consults its routing table to identify the best node that it should send the packet to. There are two situations: (1) there is an entry for the sink in the routing table, or (2) the sink does not exist in the routing table and the messaging node must relay the packet to the closest node.
During routing, care must be taken to ensure that you don’t change directionality i.e. your routing decisions should target only nodes that are clockwise successors. You must also ensure that you do not overshoot the sink-node you are trying to reach. Routing errors will result in a packet continuously looping through the overlay and consuming bandwidth.
A key requirement for the dissemination of packets within the overlay is that no messaging node should receive the same packet more than once. This should be achieved without having to rely on duplicate detection and suppression.
byte: Message type; OVERLAY_NODE_SENDS_DATA int: Destination ID
int: Source ID
int: Payload
int: Dissemination trace field length (number of hops)
int[^^]: Dissemination trace comprising nodeIDs that the packet traversed through
  The dissemination trace includes nodes (except the source and sink) that were involved in routing the particular packet. The dissemination traces will help you in your debugging and help you identify any bugs in your implementation.

### 2.7 Inform registry of task completion
Once a node has completed its task of sending a certain number of messages (described in section 4), it informs the registry of its task completion using the OVERLAY_NODE_REPORTS_TASK_FINISHED message. This message should have the following format:
byte: Message type; OVERLAY_NODE_REPORTS_TASK_FINISHED byte: length of following "IP address" field byte[^^]: Node IP address:
int: Node Port number:
int: nodeID

### 2.8 Retrieve traffic summaries from nodes
Once the registry has received TASK_COMPLETE messages from all the registered nodes it will issue a REGISTRY_REQUESTS_TRAFFIC_SUMMARY message. This message is sent to all the registered nodes in the overlay. This message will have the following format.
byte: Message Type; REGISTRY_REQUESTS_TRAFFIC_SUMMARY

### 2.9 Sending traffic summaries from the nodes to the registry
Upon receipt of the REGISTRY_REQUESTS_TRAFFIC_SUMMARY message from the registry, the messaging node will create a response that includes summaries of the traffic that it has participated in. The summary will include information about messages that were sent, received, and relayed by the node. This message will have the following format.
byte: Message type; OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY int: Assigned node ID
int: Total number of packets sent
(only the ones that were started/initiated by the node) int: Total number of packets relayed
(received from a different node and forwarded) long: Sum of packet data sent
(only the ones that were started by the node) int: Total number of packets received
(packets with this node as final destination) long: Sum of packet data received
(only packets that had this node as final destination)
Once the OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY message is sent to the registry, the node must reset the counters associated with traffic relating to the messages it has sent, relayed, and received so far viz. the number of messages sent, summation of sent messages, etc.

###  2.10 Summary of Messages Exchanged between the registry and node
The figure below depicts the exchange of messages between the registry and a particular messaging node in the system.
Registry 

### 2.11 Values for the control messages
Please use the following values for your message types.
OVERLAY_NODE_SENDS_REGISTRATION 2 REGISTRY_REPORTS_REGISTRATION_STATUS 3
OVERLAY_NODE_SENDS_DEREGISTRATION 4 REGISTRY_REPORTS_DEREGISTRATION_STATUS 5
REGISTRY_SENDS_NODE_MANIFEST 6 NODE_REPORTS_OVERLAY_SETUP_STATUS 7
REGISTRY_REQUESTS_TASK_INITIATE 8 OVERLAY_NODE_SENDS_DATA 9 OVERLAY_NODE_REPORTS_TASK_FINISHED 10
REGISTRY_REQUESTS_TRAFFIC_SUMMARY 11 OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY 12

## 3 Specifying commands and interacting with the processes
Both the registry and the messaging nodes should run as foreground processes and allow support for commands to be specified while the processes are running. The commands that should be supported are specific to the two components.

### 3.1 Registry
list-messaging-nodes
This should result in information about the messaging nodes (hostname, port-number, and node ID) being listed. Information for each messaging node should be listed on a separate line.
setup-overlay number-of-routing-table-entries (e.g. setup-overlay 3)
This should result in the registry setting up the overlay. It does so by sending every messaging node the REGISTRY_SENDS_NODE_MANIFEST message that contains information about the routing table
specific to that node and also information about other nodes in the system.
NOTE: You are not required to deal with the case where a messaging node is added or removed after the overlay has been set up. You must however deal with the case where a messaging node registers and deregisters from the registry before the overlay is set up.
list-routing-tables
This should list information about the computed routing tables for each node in the overlay. Each messaging node’s information should be well separated (i.e., have 3-4 blank lines between node listings) and should include the node’s IP address, portnum, and logical-ID. This is useful for debugging.
start number-of-messages (e.g. start 25000)
The start command results in the registry sending the REGISTRY_REQUESTS_TASK_INITIATE to all nodes within the overlay. A command of start 25000 results in each messaging node sending 25000 packets to nodes chosen at random (of course, a node should not send a packet to itself). A detailed description of the sequence of actions that this triggers is provided in section 4.

### 3.2 Messaging node
print-counters-and-diagnostics
This should print information (to the console using System.out) about the number of messages that have been sent, received, and relayed along with the sums for the messages that have been sent from and received at the node.
exit-overlay
This allows a messaging node to exit the overlay. The messaging node should first send a deregistration message (see Section 2.2) to the registry and await a response before exiting and terminating the process.
  For the remainder of the discussion we assume that the setup-overlay command has been specified. Also, nodes will not be added to the system from hereon. Any errors during the overlay setup should be reported back to the registry.
The start command can only be issued after all nodes in the overlay report success in establishing connections to nodes that comprise its routing table. This is reported in the NODE_REPORTS_OVERLAY_SETUP_STATUS message. Only after all nodes report success in setting up connections should the registry print out information on the console saying: “Registry now ready to initiate tasks.”
When the start command is specified at the registry, the registry sends the REGISTRY_REQUESTS_TASK_INITIATE control message to all the registered nodes within the overlay. Upon receiving this information from the registry, a given node will start exchanging messages with other nodes.
Each node participates in a set of rounds. Each round involves a node sending a packet to a randomly chosen node (excluding itself, of course) from the set of all registered nodes advertised in the REGISTRY_SENDS_NODE_MANIFEST. All communications in the system will be based on TCP. To send a data packet the source node consults its routing table to make decisions about the link to send the packet over. During a packet’s routing from the source to the sink there might be zero or more intermediate nodes relaying the packet en route to the destination sink node. The payload of each data packet is a random integer with values that range from 2147483647 to -2147483648. During each round, 1 packet is sent. At the end of each round, the process is repeated by choosing another node at random. The number of rounds that each node will participate in is specified in the REGISTRY_REQUESTS_TASK_INITIATE command. During grading this value will be set anywhere between 25,000 and 100,000 messages.
The number of nodes will be fixed at the start of the experiment. We will likely use around 10 nodes for the test environment during grading.

## 5 Command line arguments for the two components
Your classes should be organized in a package called cs455.overlay. The command-line arguments and the order in which they should be specified for the Messaging node and the Registry are listed below
java cs455.overlay.node.Registry portnum
java cs455.overlay.node.MessagingNode registry-host registry-port
