#scp -r ./* pi@da-piw:~/macpi2/
rsync -a --delete -e ssh . da-piw:~/macpi2/
ssh pi@da-piw ./macpi2/runmacpi.sh
# ssh with -t for interactive shell
