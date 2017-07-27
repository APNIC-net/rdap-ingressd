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
    # aliases, and a list of RDAP servers. RDAP servers are choosen from the
    # list based on first server found containing a https connection. If no
    # server is available with https then the first available http server is
    # choosen.
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
    notices:
      - title: Terms and Conditions
        description:
          - This is the APNIC RDAP query service. The objects are in RDAP format.
        links:
          - href: http://www.apnic.net/db/dbcopyright.html
            rel: terms-of-service
            type: text/html

    scraping:
        scrapers:
            # Individual scrapers available in rdap-ingressd can be enabled and
            # disabled.
            iana:
                enabled: true
            nro:
                enabled: true
        config:
            # Order in which scrapers are run.
            order:
                - iana
                - nro
```

To configure *rdap-ingressd* at runtime, it's necessary to create a
configuration file that can be given to the application. See
[deploy](deploy.md) documentation.
