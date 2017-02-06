How to run:


for Part A:
python mydnsresolver.py google.com
python mydnsresolver.py facebook.com



for Part B:


python mydig.py google.com mx
python mydig.py google.com ns
python mydig.py google.com a


python mydig.py facebook.com mx
python mydig.py facebook.com ns
python mydig.py facebook.com a


expected output file is called  "mydig_output.txt"

for part3:

python part_c_script.py


The file for graph is called "part_C_graph_report.pdf"


External Library:
python —> dnspython




notice:dns_file.txt is used to record what servers we made qury last time.




High level view of my design:

take 'mydig facebook.com a' as example


First I make a query with type A to the root server,then if the returned type is A, then make recursive query directly. If returned type is NS, then make query to get the ip address of the domain name first. Then make recusive query to the ip address. The program do recusive query until program finds the untimate answer.









PS:

Since for some response ,there are no information in answer section but some info in authority section,  I output these authority info as answer in my program

For example,


Original response:

id 19086
opcode QUERY
rcode NOERROR
flags QR RD
;QUESTION
facebook.com. IN NS
;ANSWER
;AUTHORITY
facebook.com. 172800 IN NS a.ns.facebook.com.
facebook.com. 172800 IN NS b.ns.facebook.com.
;ADDITIONAL
a.ns.facebook.com. 172800 IN AAAA 2a03:2880:fffe:c:face:b00c:0:35
a.ns.facebook.com. 172800 IN A 69.171.239.12
b.ns.facebook.com. 172800 IN AAAA 2a03:2880:ffff:c:face:b00c:0:35
b.ns.facebook.com. 172800 IN A 69.171.255.12



My output:

choose root server :  198.97.190.53

QUERY 1, ANSWER 0, AUTHORITY:  2 , ADDITIONAL:  4    
;; QUESTION SECTION:
; facebook.com IN NS

;; ANSWER SECTION:
facebook.com. 172800 IN NS a.ns.facebook.com.
facebook.com. 172800 IN NS b.ns.facebook.com.
;; Query time: 95 msec 
;; SERVER: 127.0.1.1 
;; WHEN: 2016-02-17 14:38:44 

























part c result explanation


