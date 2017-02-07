#! /usr/bin/python


import dns.query
import dns.resolver
import time

from dns.exception import DNSException


def query_recur_a(domain_name, ns_addr, root_addr):

    print 'Looking up %s on %s' % (domain_name, ns_addr)
    query = dns.message.make_query(domain_name, dns.rdatatype.A)
    response = dns.query.udp(query, ns_addr)

    rcode = response.rcode()
    if rcode != dns.rcode.NOERROR:
        if rcode == dns.rcode.NXDOMAIN:
            raise Exception('%s does not exist.' % (domain_name))
        else:
            raise Exception('Error %s' % (dns.rcode.to_text(rcode)))

    # print '\n\nresponse!!!!!!!1\n'
    # print response

    rrsets_array = []
    ans_a = []
    ans_ns = []
    ans_cname = []
    if_found = False

    if len(response.answer) > 0:
        rrsets = response.answer
        for rrset in rrsets:

            for rr in rrset:
                if rr.rdtype == dns.rdatatype.A:
                    result = rr.to_text()
                    ans_a.append(result)

                elif rr.rdtype == dns.rdatatype.CNAME:
                    result = rr.to_text()
                    ans_cname.append(result)
                elif rr.rdtype == dns.rdatatype.NS:
                    result = rr.to_text()
                    ans_ns.append(result)

        if len(ans_a) > 0:
            # print 'len(a_array)>0:'
            return ans_a
        elif len(ans_cname) > 0:

            return query_recur_a(ans_cname[0], root_addr, root_addr)

        elif len(ans_ns) > 0:

            tmp_a = query_recur_a(ans_ns[0], root_addr, dns, root_addr)
            return query_recur_a(domain_name, tmp_a[0], dns, root_addr)

    else:
        if len(response.answer) > 0:
            rrsets_array.append(response.answer)
        if len(response.authority) > 0:
            rrsets_array.append(response.authority)
        if len(response.additional) > 0:
            rrsets_array.append(response.additional)

    ns_array = []
    a_array = []

    for rrsets in rrsets_array:

        # print 'rrsets!!!'
        # print rrsets

        for rrset in rrsets:
            # print 'rrset'
            # print rrset.to_text()
            # print rrset.to_wire()

            rrset_text = rrset.to_text().split(' ')[0]
            rrset_text = rrset_text[0:len(rrset_text) - 1]
            # if domain_name.lower() == rrset_text.lower() and  rrset.rdtype == dns.rdatatype.A:
            # if_found =  True

            for rr in rrset:
                if rr.rdtype == dns.rdatatype.A:

                    # print rr.to_text()
                    result = rr.to_text()
                    a_array.append(result)

                elif rr.rdtype == dns.rdatatype.NS or rr.rdtype == dns.rdatatype.CNAME:
                    result = rr.to_text()
                    ns_array.append(result)

    # if if_found == True:
    #     return a_array

    if len(a_array) > 0:
        # print 'len(a_array)>0:'
        return query_recur_a(domain_name, a_array[0], root_addr)

    elif len(ns_array) > 0:

        tmp_a = query_recur_a(ns_array[0], root_addr, root_addr)
        return query_recur_a(domain_name, tmp_a[0], root_addr)


import sys


site_array = ['Google.com',
              'Facebook.com',
              'Youtube.com',
              'Baidu.com', 'Yahoo.com',
              'Amazon.com', 'Wikipedia.org', 'qq.com', 'Google.co.in',
              'Twitter.com',
              'Live.com',
              'Taobao.com', 'Sina.com.cn', 'Msn.com',
              'Yahoo.co.jp',
              'Linkedin.com', 'Weibo.com', 'Google.co.jp', 'Vk.com', 'Bing.com', 'Yandex.ru',
              'Hao123.com', 'Ebay.com', 'Instagram.com',
              'Google.de'
              ]

# sd

site_array = ['Google.com',
              'Facebook.com',
              'Youtube.com',
              'Baidu.com',
              'Amazon.com', 'Wikipedia.org', 'Yahoo.com',
              'Twitter.com',
              'Live.com',
              'Msn.com',

              'Linkedin.com', 'Weibo.com', 'Bing.com', 'Yandex.ru',
              'Hao123.com', 'Ebay.com', 'Instagram.com',

              ]

# site_array =
# ['Google.co.jp','Google.co.in','Yahoo.co.jp','Google.de','Vk.com'

# ]


# site_array = ['ns2.google.com']


site_array = ['Google.com',
              'Facebook.com',
              'Youtube.com',
              'Baidu.com',
              'Amazon.com', 'Wikipedia.org', 'Google.co.jp', 'Yahoo.com', 'Yahoo.co.jp', 'Google.co.in', 'Google.de',
              'Twitter.com',
              'Live.com',
              'Msn.com',

              'Linkedin.com', 'Weibo.com', 'Vk.com', 'Bing.com', 'Yandex.ru',
              'Hao123.com', 'Ebay.com', 'Instagram.com',

              ]

# site_array = ['Sina.com.cn','qq.com',
# 'Taobao.com']


# site_array = ['Amazon.com']
# site_array = ['Vk.com']


site_array = []


# for arg in sys.argv:
site_array.append(sys.argv[1])


root_array = ['198.41.0.4', '192.228.79.201', '192.33.4.12', '199.7.91.13', '192.203.230.10', '192.5.5.241',
              '192.112.36.4', '198.97.190.53', '192.36.148.17', '192.58.128.30', '193.0.14.129', '199.7.83.42', '202.12.27.33']
length = len(root_array)


for site in site_array:
    f = open('dns_file.txt', 'r')
    num = int(f.readline())
    f.close()

    root = root_array[num]
    num = (num + 1) % length

    print '\nchoose root server : ', root, '\n'

    f = open('dns_file.txt', 'w')
    f.write(str(num))
    f.close()

    # result = query_recur_ns (site,root,root)
    result = query_recur_a(site, root, root)
    # result = query_recur_mx(site,root,root)

    print '\n\nIP address:  ', result
    print '______________________\n\n'
    # time.sleep(1)
