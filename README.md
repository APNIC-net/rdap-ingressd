# Overview
*rdap-ingressd* is a proxy and redirection server for the RDAP protocol.
It recieves RDAP queries from clients and routes the request to the appropriate
RDAP service by either:

1. HTTP 301 redirect; or
2. Proxying the request to another RDAP service and returning the result.

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
