# Overview
*rdap-ingressd* is a proxy and redirection server for the RDAP protocol.
It recieves RDAP queries from clients and routes the request to the appropriate
RDAP service by either:

1. HTTP 301 redirect; or
2. Proxying the request to another RDAP service and returning the result.

# Feature Highlights

* Dynamically scrapes authorative sources for resource ownership and redirects
resource request to the correct authorities rdap service. Supports redirects
for the following resource types.
    - IPv4 and Ipv6 addresses
    - AS numbers
    - Reverse DNS

* Configure a fall through RDAP service for resource requests that are not
  routable such as entities, nameservers.

# Licensing
rdap-ingressd is licensed under the BSD licence. Check the [LICENSE
file](LICENSE.txt).

# RDAP Specification
The RDAP protocol is specificed in the following IETF RFC's:

- [RFC7480 - HTTP Usage in the Registration Data Access Protocol](https://tools.ietf.org/html/rfc7480)
- [RFC7481 - Security Services for the Registration Data Access Protocol](https://tools.ietf.org/html/rfc7481)
- [RFC7482 - Registration Data Access protocol (RDAP) Query Format](https://tools.ietf.org/html/rfc7482)
- [RFC7483 - JSON Responses for the Registration Data Access Protocol (RDAP)](https://tools.ietf.org/html/rfc7483)
- [RFC7484 - Finding the Authoritative Registration Data (RDAP) Service](https://tools.ietf.org/html/rfc7484)

# Documentation

Documentation for the project can be found in the *docs/* sub directory
containing information on how to configure the project.

To quickly get started please refer to the following documentation:

- [Building The Project](docs/building.md)
- [Deploying](docs/deploy.md)
- [Configuration](docs/config.md)
