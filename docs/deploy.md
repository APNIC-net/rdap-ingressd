# Overview
Describes the process for deploying *rdap-ingressd* as a Docker container.

# Getting The Container
The Docker container for *rdap-ingressd* can be obtained from Docker
Hub by running:

```
docker pull apnic/rdap-ingressd
```

or by building from source. See the [building](building.md)
documentation for instructions on building the *rdap-ingressd* Docker
image.

# Running The Container
The Docker container can be run standalone or by supplying a
[config](config.md) file.

```
docker run -p 8080:8080 apnic/rdap-ingressd --name rdap-ingressd
```

To supply a custom configuration file:

```
docker run -v "<absolute_config_file_path>:/app/config/application-rdap.yml" -p 8080:8080 apnic/rdap-ingressd --name rdap-ingressd
```
