## TECH/DEV CHANGELOG

## LIFECOMPANION PLUGIN API

To be done later...

## SERVER

### 1.14.12 - 20/06/2022

- New public service "/get-image-url/:imageId" to download an image from its ID
- Removed incorrect plugin stats

### 1.14.9 - 04/03/2022

- Restore plugin service

### 1.14.8 - 01/03/2022

- Old get diff service (get file diff for update) now only get files from API version 1

### 1.14.7 - 28/02/2022

- Fix remove update service

### 1.14.5 - 28/02/2022

- Fix create update service (removed API version filter to create update diff)
- Fix remove update service

### 1.14.4 - 25/02/2022

- New service to download a specific app version (in update diff)

### 1.14.3 - 24/02/2022

- New service to get all plugin updates

### 1.14.2 - 22/02/2022

- Fix insert for application update
- Fix API now check egality

### 1.14.0 - 22/02/2022

- New application update column "api version" to ensure backward compatibility

### 1.13.0 - 18/02/2022

- Added a new file target type "LAUNCHER"
- New application update diff service to include new file target type

### 1.12.0 - 22/12/2021

- File robots.txt configured to "Disallow"
- Added an URL to wakeup server with an image request

### 1.11.0 - 06/12/2021

- Added a service URL to request server version
- Added env config `LC_PLATFORM_URL_PASSWORD` to be able to inject query parameter
- Added `/application-update/delete-update/:id` to delete an update if needed (admin BO)
