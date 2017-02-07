#! /usr/bin/python


import dns.query
import dns.resolver
import time
import datetime
from time import gmtime, strftime
from dns.exception import DNSException


def query_recur_mx(domain_name, ns_addr, root_addr, if_print, original):

    ns_array = query_recur_ns(domain_name, ns_addr, root_addr, False, original)

    # print ns_array

    # print 'mmm',ns_array,'bbb'
    if not ns_array:
        return None

    if ns_array[0] == 'soa' or ns_array[0] == 'cname':
        return None

    if 'SOA' in ns_array:
        print ns_array
        return None

    ip = query_recur_a(ns_array[0], ns_addr, root_addr, False, original)
    # print ip

    query = dns.message.make_query(domain_name, dns.rdatatype.MX)
    response = dns.query.udp(query, ip[0])

    len_answer = 0
    len_authority = 0
    len_additional = 0

    for rrset in response.answer:
        for rr in rrset:
            len_answer = len_answer + 1

    for rrset in response.authority:
        for rr in rrset:
            len_authority = len_authority + 1

    for rrset in response.additional:
        for rr in rrset:
            len_additional = len_additional + 1

    # print response
    # print '~~~~~~~~~~~'

    ans_a = []
    if len(response.answer) > 0:
        rrsets = response.answer
        for rrset in rrsets:

            for rr in rrset:
                if rr.rdtype == dns.rdatatype.MX:
                    result = rr.to_text()
                    ans_a.append(result)

    print_txt = rrset.to_text()
    output = '\n'
    t = 'QUERY %s, ANSWER %s, AUTHORITY:  %s , ADDITIONAL:  %s    \n' % (
        1, len_answer, len_authority, len_additional)
    output = output + t

    t = ';; QUESTION SECTION:\n'
    output = output + t
    t = '; %s IN %s' % (original, 'MX')
    output = output + t

    t = '\n\n;; ANSWER SECTION:\n'
    output = output + t

    t = print_txt
    output = output + t

    if if_print == True:
        print output
        # print response
        print '\n\n'

    return ans_a


def query_recur_ns(domain_name, ns_addr, root_addr, if_print, original):

    # print 'Looking up %s on %s' % (domain_name, ns_addr)
    query = dns.message.make_query(domain_name, dns.rdatatype.NS)
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

    len_answer = 0
    len_authority = 0
    len_additional = 0

    for rrset in response.answer:
        for rr in rrset:
            len_answer = len_answer + 1

    for rrset in response.authority:
        for rr in rrset:
            len_authority = len_authority + 1

    for rrset in response.additional:
        for rr in rrset:
            len_additional = len_additional + 1

    if len(response.answer) > 0:
        rrsets_array.append(response.answer)
    if len(response.authority) > 0:
        rrsets_array.append(response.authority)
    if len(response.additional) > 0:
        rrsets_array.append(response.additional)

    output = '\n'
    t = 'QUERY %s, ANSWER %s, AUTHORITY:  %s , ADDITIONAL:  %s    \n' % (
        1, len_answer, len_authority, len_additional)
    output = output + t

    t = ';; QUESTION SECTION:\n'
    output = output + t
    t = '; %s IN %s' % (original, 'NS')
    output = output + t

    t = '\n\n;; ANSWER SECTION:\n'
    output = output + t

    final_ns_array = []
    ns_array = []
    a_array = []
    soa_array = []
    cname_array = []

    final_ns_array_text = None
    ns_array_text = None
    a_array_text = None
    soa_array_text = None
    cname_array_text = None

    for rrsets in rrsets_array:

        # print 'rrsets!!!'
        # print rrsets

        for rrset in rrsets:
            # print 'rrset'
            # print rrset.to_text()
            # print rrset.to_wire()

            rrset_text = rrset.to_text().split(' ')[0]
            rrset_text = rrset_text[0:len(rrset_text) - 1]

            for rr in rrset:

                if (domain_name.lower() == rrset_text.lower() and rr.rdtype == dns.rdatatype.NS):
                    result = rr.to_text()
                    final_ns_array.append(result)
                    final_ns_array_text = rrset.to_text()

                elif rr.rdtype == dns.rdatatype.A:

                    # print rr.to_text()
                    result = rr.to_text()
                    a_array.append(result)
                    a_array_text = rrset.to_text()

                elif rr.rdtype == dns.rdatatype.CNAME:
                    result = rr.to_text()
                    cname_array.append(result)
                    cname_array_text = rrset.to_text()

                elif rr.rdtype == dns.rdatatype.SOA:
                    result = rr.to_text()
                    soa_array.append(result)
                    soa_array_text = rrset.to_text()

                elif rr.rdtype == dns.rdatatype.NS:
                    result = rr.to_text()
                    ns_array.append(result)
                    ns_array_text = rrset.to_text()

    if len(final_ns_array) > 0:

        output = output + final_ns_array_text

        if if_print == True:
            print output

        return final_ns_array

    elif len(soa_array) > 0:

        output = output + soa_array_text
        if if_print == True:
            print output
        ns_array = ['soa']

        output = '\n'
        t = 'QUERY %s, ANSWER %s, AUTHORITY:  %s , ADDITIONAL:  %s    \n' % (
            1, len_answer, len_authority, len_additional)
        output = output + t

        t = ';; QUESTION SECTION:\n'
        output = output + t
        t = '; %s IN %s' % (original, 'MX')
        output = output + t

        t = '\n\n;; ANSWER SECTION:\n'
        output = output + t

        output = output + soa_array_text

        # print output

        return output

    elif len(cname_array) > 0:
        # output = output + cname_array_text
        # if if_print == True:
        # 	print output
        # ns_array=['cname']
        # return ns_array

        return query_recur_ns(cname_array[0], root_addr, root_addr, if_print, original)

    elif len(a_array) > 0:
        # print 'len(a_array)>0:'
        return query_recur_ns(domain_name, a_array[0], root_addr, if_print, original)

    elif len(ns_array) > 0:

        tmp_a = query_recur_a(
            ns_array[0], root_addr, root_addr, False, original)
        return query_recur_ns(domain_name, tmp_a[0], root_addr, if_print, original)


def query_recur_a(domain_name, ns_addr, root_addr, if_print, original):

    # print 'Looking up %s on %s' % (domain_name, ns_addr)
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

    len_answer = 0
    len_authority = 0
    len_additional = 0

    for rrset in response.answer:
        for rr in rrset:
            len_answer = len_answer + 1

    for rrset in response.authority:
        for rr in rrset:
            len_authority = len_authority + 1

    for rrset in response.additional:
        for rr in rrset:
            len_additional = len_additional + 1

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

            print_txt = rrset.to_text()
            output = '\n'
            t = 'QUERY %s, ANSWER %s, AUTHORITY:  %s , ADDITIONAL:  %s    \n' % (
                1, len_answer, len_authority, len_additional)
            output = output + t

            t = ';; QUESTION SECTION:\n'
            output = output + t
            t = '; %s IN %s' % (original, 'A')
            output = output + t

            t = '\n\n;; ANSWER SECTION:\n'
            output = output + t

            t = print_txt
            output = output + t

            if if_print == True:
                print output
                # print response
                print '\n\n'

            return ans_a
        elif len(ans_cname) > 0:

            return query_recur_a(ans_cname[0], root_addr, root_addr, if_print, original)

        elif len(ans_ns) > 0:

            tmp_a = query_recur_a(
                ans_ns[0], root_addr, dns, root_addr, False, original)
            return query_recur_a(domain_name, tmp_a[0], dns, root_addr, if_print, original)

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
        return query_recur_a(domain_name, a_array[0], root_addr, if_print, original)

    elif len(ns_array) > 0:

        tmp_a = query_recur_a(
            ns_array[0], root_addr, root_addr, False, original)
        return query_recur_a(domain_name, tmp_a[0], root_addr, if_print, original)


import sys


import socket


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

# site_array = ['Google.com',
# 'Facebook.com',
# 'Youtube.com',
# 'Baidu.com',
# 'Amazon.com','Wikipedia.org','Yahoo.com',
# 'Twitter.com',
# 'Live.com',
# 'Msn.com',

# 'Linkedin.com','Weibo.com','Bing.com','Yandex.ru',
# 'Hao123.com','Ebay.com','Instagram.com',

# ]

site_array = ['Google.co.jp', 'Google.co.in', 'Yahoo.co.jp', 'Google.de', 'Vk.com'

              ]


# site_array = ['Google.co.jp']


# site_array = ['Google.com',
# 'Facebook.com',
# 'Youtube.com',
# 'Baidu.com',
# 'Amazon.com','Wikipedia.org','Google.co.jp','Yahoo.com','Yahoo.co.jp','Google.co.in','Google.de',
# 'Twitter.com',
# 'Live.com',
# 'Msn.com',

# 'Linkedin.com','Weibo.com','Vk.com','Bing.com','Yandex.ru',
# 'Hao123.com','Ebay.com','Instagram.com',

# ]

# site_array = ['Sina.com.cn','qq.com',
# 'Taobao.com']


# site_array = ['Amazon.com']
# site_array = ['Vk.com']


# result = query_recur (site_array[1],root,dns.rdatatype.A,root)
# print result
# print '______________________'


# site = 'google.com'


# # # for arg in sys.argv:


site_array = []

site_array.append(sys.argv[1])

type = sys.argv[2]


root_array = ['198.41.0.4', '192.228.79.201', '192.33.4.12', '199.7.91.13', '192.203.230.10', '192.5.5.241',
              '192.112.36.4', '198.97.190.53', '192.36.148.17', '192.58.128.30', '193.0.14.129', '199.7.83.42', '202.12.27.33']
length = len(root_array)


for site in site_array:

    f = open('dns_file.txt', 'r')
    num = int(f.readline())
    f.close()

    root = root_array[num]
    print 'digging ', site, ' tpye: ', type
    print '\nchoose root server : ', root
    num = (num + 1) % length

    f = open('dns_file.txt', 'w')
    f.write(str(num))
    f.close()

    a = datetime.datetime.now()

    if type == 'a':
        result = query_recur_a(site, root, root, True, site)

    elif type == 'ns':
        result = query_recur_ns(site, root, root, True, site)

    elif type == 'mx':
        result = query_recur_mx(site, root, root, True, site)

    b = datetime.datetime.now()
    c = (b - a).microseconds / 1000

    showtime = strftime("%Y-%m-%d %H:%M:%S", gmtime())
    # print showtime
    ip = socket.gethostbyname(socket.gethostname())

    print ';; Query time: %s msec ' % (c)
    print ';; SERVER: %s ' % (ip)
    print ';; WHEN: %s ' % (showtime)

    # print result
    print '\n\n_______________________________________________\n\n\n\n\n\n\n'
    # time.sleep(1)
