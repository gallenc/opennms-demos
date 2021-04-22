## selenium tests 4

# various selenium tests which work with opennms-har-demo docker images

OpennmsSeleniumHarCollectorV1.groovy working collector with no IP address mapping

OpennmsSeleniumHarCollectorV2.groovy moved browser mob proxy initialisation to init method
(didnt work before because of logging in seperate threads)

note 254.0.0.1 is invalid ip address - use for dns lookup


for ip address substitution see https://webelement.click/en/modify_server_ip_address_in_selenium_java_tests_with_browsermob_proxy

https://www.linkedin.com/pulse/switching-hosts-selenium-pankaj-sukale/  switching host names

setup for chrome https://www.swtestacademy.com/browsermob-proxy-selenium/