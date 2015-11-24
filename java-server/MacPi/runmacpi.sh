# cd to location of this script
#ABSOLUTE_PATH=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)
#~ /`basename "${BASH_SOURCE[0]}"`
#cd $ABSOLUTE_PATH
cd $(dirname $(readlink -m -q $0))
pwd
#~ cd `dirname $0`
#~ pwd
#~ cd `pwd -P`
#~ pwd

if ! sudo invoke-rc.d isc-dhcp-server status; then 
	sudo ifdown eth0
	sudo ifup eth0
	sudo invoke-rc.d isc-dhcp-server start
fi

echo "Killing all java processes."
killall --signal SIGINT --wait java
echo "Starting JVM"
java -cp bin/ de.dauersolutions.macpi.server.MacPiMain

