## selenium tests 4

# various selenium tests which work with opennms-har-demo docker images

OpennmsSeleniumHarCollectorV1.groovy working collector with no IP address mapping

OpennmsSeleniumHarCollectorV2.groovy moved browser mob proxy initialisation to init method
(didnt work before because of logging in seperate threads)

note 254.0.0.1 is invalid ip address - use for dns lookup