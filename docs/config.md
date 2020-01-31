# Overview
The following documentation depicts the external configuration that is
possible in *rdap-ingressd*.

# Configuration File
*rdap-ingressd* is configured through a YAML file by the name of
*application-rdap.yml*.  The static path of the file is
```src/main/resource/application-rdap.yml```.  Its syntax is like so:

```
rdap:

    # A list of RDAP authorities may be configured ahead of time, if
    # required.  Each authority has a name, an optional list of
    # aliases, a list of RDAP servers used as reference in the IANA bootstrap
    # file and a routing section. The routing section have an optional action 
    # (if no action is defined the defaultAction value will be used), a routing
    # target, an optional internal target (intended for internal direct queries 
    # (e.g. inside the same cluster)) and a optional fallback authority for queries 
    # resulting in 404 responses (not found).
    authorities:
        - name: apnic
          routing:
              action: proxy
              target: https://rdap.apnic.net/
          ianaBootstrapRefServers:
              - https://rdap.apnic.net
              - http://rdap.apnic.net

        - name: ripe
          aliases:
              - ripencc
          routing:
            action: redirect
            target: https://rdap.db.ripe.net
            internalTarget: http://ripe-rdap-mirror:8080/
          ianaBootstrapRefServers:
              - https://rdap.db.ripe.net/

        - name: afrinic
          routing:
            action: redirect
            target: https://rdap.afrinic.net/rdap/
          ianaBootstrapRefServers:
              - http://rdap.afrinic.net/rdap/

        - name: arin
          routing:
            action: redirect
            target: https://rdap.arin.net/registry/
          ianaBootstrapRefServers:
              - https://rdap.arin.net/registry/
              - http://rdap.arin.net/registry/

        - name: lacnic
          routing:
            action: proxy
            target: https://rdap.lacnic.net/rdap/
            notFoundFallbackAuthority: apnic
          ianaBootstrapRefServers:
              - https://rdap.lacnic.net/rdap/
    routing:
        # The default routing action to take for authorities. Accepts either
        # redirect or proxy.
        defaultAction: redirect

        # Allows rdap-ingressd to proxy unroutable requests to a
        # default authority. The name of the authority specificed here
        # can be null or a name specified in the 'authorities' section
        # above. All requests to this authority are proxied.
        defaultAuthority: null

    # List of default notices that are sent with RDAP responses from this 
    # server.
    # Each notice in the list takes on the following form
    # - title: Optional title for the notice
    # - description: # Array of description lines
    #     - description line 1
    #     - description line 2
    # - links: # Array of link object for the notice
    #    - href: Non optional URL ref for the link
    #      rel: Optional relation type
    #      type: link type
    notices: []

    scraping:
        scrapers:
            # Individual scrapers available in rdap-ingressd can be enabled and
            # disabled. It's also possible to specify the property baseURI for
            # each scraper to change the location where data is fetched from.
            # The special "custom" scraper may be used to fetch multiple sources 
            # of delegated stats data.
            iana:
                enabled: true
            nro:
                enabled: true
            custom:
                enabled: true
                entries:
                    - name: idnic
                      uri: https://repository.example/idnic-delegated-stat
                    - name: jpnic
                      uri: https://repository.example/jpnic-delegated-stat
        config:
            # Order in which scrapers are run.
            order:
            - iana
            - nro
            - custom
```

To configure *rdap-ingressd* at runtime, it's necessary to create a
configuration file that can be given to the application. See
[deploy](deploy.md) documentation.
