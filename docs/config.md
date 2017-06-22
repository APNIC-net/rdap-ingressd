# Overview
The following documentation depicts the external configuration that is possible
in *rdap-ingressd*

# Configuration File
*rdap-ingressd* is configured through a yaml file by the name of
*application-rdap.yml* with a static starting point for creating the file
located at ```src/main/resource/application-rdap.yml```

or

```
rdap:
     #If it is known a head of time a list of pre defined rdap authorities can
     #be configured. Each authority takes the form of:
     # name: <primary_authority_name>
     # aliases: # Optional list of aliases this authority can be known as.
     # servers: # List of one or more rdap server URI's for the authority
    authorities:
        - name: apnic
          servers:
              - https://rdap.apnic.net
              - http://rdap.apnic.net

        - name: ripe
          aliases:
              - ripencc
          servers:
              - https://rdap.db.ripe.net/

        - name: afrinic
          servers:
              - https://rdap.afrinic.net/rdap/
              - http://rdap.afrinic.net/rdap/

        - name: arin
          servers:
              - https://rdap.arin.net/registry
              - http://rdap.arin.net/registry

        - name: lacnic
          servers:
              - https://rdap.lacnic.net/rdap/

    scraping:
        scrapers:
            # Individual scrapers available in rdap-ingressd can be enabled and
            # disabled
            iana:
                enabled: true
            nro:
                enabled: true
        config:
            # Order in which scrapers are run
            order:
                - iana
                - nro
```

To configure *rdap-ingressd* at runtime it's necessary to create a config file
that can be given to the application. See [deploy](deploy.md) documentation.
