#!/usr/bin/env python
from mininet.cli import CLI
from mininet.net import Mininet
from mininet.link import Link,TCLink,Intf
  
if '__main__' == __name__:
  net = Mininet(link=TCLink)
  h1 = net.addHost('h1')
  h2 = net.addHost('h2')
  r1 = net.addHost('r1')
  r2 = net.addHost('r2')
  r3 = net.addHost('r3')
  r4 = net.addHost('r4')


  net.addLink(h1, r1)
  net.addLink(r1, r2)
  net.addLink(r2, r4)
 

  net.addLink(r4, h2)


  net.addLink(r1, r3)
  net.addLink(r3, r4)
  net.build()

  # r1.cmd("ifconfig r1-eth0 0")
  # r1.cmd("ifconfig r1-eth1 0")
  r1.cmd('ifconfig r1-eth0 192.168.11.251 netmask 255.255.255.0')
  r1.cmd('ifconfig r1-eth1 192.168.10.1 netmask 255.255.255.0')
  r1.cmd('ifconfig r1-eth2 192.168.20.1 netmask 255.255.255.0')




  r1.cmd("echo 1 > /proc/sys/net/ipv4/ip_forward")
  r1.cmd("ip route add 192.168.30.0/24 via 192.168.10.2")
  r1.cmd("ip route add 192.168.222.0/24 via 192.168.10.2")
  r1.cmd("ip route add 192.168.40.0/24 via 192.168.20.2")
  r1.cmd("ip route add 192.168.222.0/24 via 192.168.10.2")
  
 

  # r2.cmd("ifconfig r2-eth0 0")
  # r2.cmd("ifconfig r2-eth1 0")
  r2.cmd('ifconfig r2-eth0 192.168.10.2 netmask 255.255.255.0')
  r2.cmd('ifconfig r2-eth1 192.168.30.1 netmask 255.255.255.0')
  r2.cmd("echo 1 > /proc/sys/net/ipv4/ip_forward")
  r2.cmd("ip route add 192.168.11.0/24 via 192.168.10.1")
  r2.cmd("ip route add 192.168.222.0/24 via 192.168.30.2")



  # r4.cmd("ifconfig r4-eth0 0")
  # r4.cmd("ifconfig r4-eth1 0")
  r4.cmd('ifconfig r4-eth1 192.168.222.24 netmask 255.255.255.0')
  r4.cmd('ifconfig r4-eth0 192.168.30.2 netmask 255.255.255.0')
  r4.cmd('ifconfig r4-eth2 192.168.40.2 netmask 255.255.255.0')
  r4.cmd("echo 1 > /proc/sys/net/ipv4/ip_forward")
  r4.cmd("ip route add 192.168.10.0/24 via 192.168.30.1")
  r4.cmd("ip route add 192.168.11.0/24 via 192.168.30.1")
  r4.cmd("ip route add 192.168.20.0/24 via 192.168.40.1")
  r4.cmd("ip route add 192.168.11.0/24 via 192.168.40.1")



  r3.cmd('ifconfig r3-eth0 192.168.20.2 netmask 255.255.255.0')
  r3.cmd('ifconfig r-eth1 192.168.40.1 netmask 255.255.255.0')
  r3.cmd("echo 1 > /proc/sys/net/ipv4/ip_forward")
  r3.cmd("ip route add 192.168.222.0/24 via 192.168.40.2")
  r3.cmd("ip route add 192.168.11.0/24 via 192.168.20.1")


  h1.cmd("ifconfig h1-eth0 0")
  h2.cmd("ifconfig h2-eth0 0")
  h1.cmd("ip address add 192.168.11.2/24 dev h1-eth0")
  h1.cmd("ip route add default via 192.168.11.251")
  h2.cmd("ip address add 192.168.222.251/24 dev h2-eth0")
  h2.cmd("ip route add default via 192.168.222.24")
  CLI(net)
  net.stop()
