# Overview
*rdap-ingressd* is a proxy and redirection server for the RDAP
protocol.  It receives RDAP queries from clients and routes the
requests to the appropriate RDAP service by either:

1. HTTP 301 redirect; or
2. Proxying the request to another RDAP service and returning the result.

# Feature Highlights

* Dynamically scrapes authoritative sources for resource ownership and
  redirects resource requests to the correct authority's RDAP service.
  Supports redirects for the following resource types:
    - IPv4 and IPv6 addresses;
    - AS numbers; and
    - domains (forward and reverse).

* Supports configuring a fall-through RDAP service for resource
  requests that cannot be redirected, such as entities and
  nameservers.

# Licensing
rdap-ingressd is licensed under the BSD licence. Check the [LICENSE
file](LICENSE.txt).

# RDAP Specification
The RDAP protocol is specificed in the following IETF RFCs:

- [RFC7480 - HTTP Usage in the Registration Data Access Protocol](https://tools.ietf.org/html/rfc7480)
- [RFC7481 - Security Services for the Registration Data Access Protocol](https://tools.ietf.org/html/rfc7481)
- [RFC7482 - Registration Data Access protocol (RDAP) Query Format](https://tools.ietf.org/html/rfc7482)
- [RFC7483 - JSON Responses for the Registration Data Access Protocol (RDAP)](https://tools.ietf.org/html/rfc7483)
- [RFC7484 - Finding the Authoritative Registration Data (RDAP) Service](https://tools.ietf.org/html/rfc7484)

# Documentation

Documentation for the project can be found in the *docs/* subdirectory
containing information on how to configure the project.

To quickly get started, please refer to the following documentation:

- [Building The Project](docs/building.md)
- [Deploying](docs/deploy.md)
- [Configuration](docs/config.md)
