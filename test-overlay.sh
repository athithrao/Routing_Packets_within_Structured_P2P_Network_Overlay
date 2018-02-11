    test_home=/s/chopin/b/grad/athitha/CS455/CS455_P1/Amarnath_Athith_ASG1
     
    for i in `cat machine_list`
    do
    	echo 'logging into '${i}
    	gnome-terminal -x bash -c "ssh -t ${i} 'cd $test_home; java cs455.overlay.node.MessagingNode spruce 5000;bash;'" &
    done


