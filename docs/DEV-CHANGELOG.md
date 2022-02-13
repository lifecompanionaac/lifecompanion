## TECH/DEV CHANGELOG

## SERVER

### 1.13.0 - XX/XX/2022

- Added a new file target type "LAUNCHER"
- New application update diff service to include new file target type

### 1.12.0 - 22/12/2021

- File robots.txt configured to "Disallow"
- Added an URL to wakeup server with an image request

### 1.11.0 - 06/12/2021

- Added a service URL to request server version
- Added env config `LC_PLATFORM_URL_PASSWORD` to be able to inject query parameter
- Added `/application-update/delete-update/:id` to delete an update if needed (admin BO)