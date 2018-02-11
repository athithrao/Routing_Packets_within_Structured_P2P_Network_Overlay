CS455 - HW1 - Programming Component
Routing Packets within a structured peer-to-peer (P2P) network overlay

Name - Athith Amarnath
CSUID - 830715061

To make the project please use the following command

	make

To clean the project please use the following command 

	make clean

To run the registry, please run the following command 
	
	java cs455.overlay.node.Registry <portno>
	
	The portno arguement is the command line arguement to give the port number for the registry to start its operations.

To run the messaging nodes, I used the following script in test-overlay.sh

    test_home=/s/chopin/b/grad/athitha/CS455/CS455_P1/src
     
    for i in `cat machine_list`
    do
    	echo 'logging into '${i}
    	gnome-terminal -x bash -c "ssh -t ${i} 'cd $test_home; java cs455.overlay.node.MessagingNode spruce 5000;bash;'" &
    done

Assumptions - 
1. The registration and de-registration on messaging nodes are allowed only before the setup-overlay is invoked. After setup-overlay is invoked, the program will not accept any registration and deregistration requests.
2. Once the setup-overlay command is invoked in the registry, th program will not accept another setup-overlay command.
3. The report is formatted using the \t (tab) character. Please increase the size of the terminal window to accomodate all row data in just one line.
4. After running the test-overlay.sh, please wait fofr 4-5 seconds for all the messaging nodes to complete the registration. The script adds some delay to performs the ssh and start the messaging node.
5. I have noticed that 4/5 simulations, all the messages are accounted for. Some simulations results show loss of packets up 12 packets. I am unsure why. 




