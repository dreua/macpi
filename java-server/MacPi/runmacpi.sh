# cd to location of this script
cd ${0%/*}

if ! sudo invoke-rc.d isc-dhcp-server status; then 
	sudo ifdown eth0
	sudo ifup eth0
	sudo invoke-rc.d isc-dhcp-server start
fi

echo "Killing all java processes."
killall --signal SIGINT --wait java
echo "Starting JVM"
java -cp bin/ de.dauersolutions.macpi.server.MacPiMain

