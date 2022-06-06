[TOC]

# api-notifications

## Explanation

### General

This service provides several ways to send notifications to users.

By default, notifications about the SSE (Server Sent Events) connection are sent to the user.
If no SSE connection is active for the user, the service checks whether an alternative notification method has been activated.
This is purely optional and must have been activated by the user himself. The HTTP interfaces listed below are available for this purpose.
If no alternate notification method has been enabled, the notification will be discarded.

### Plugins

In order to offer as many different ways for notifications as possible in the future, the secondary notification channels were implemented as a plugin.

The server loads all .dll files of the plugins located in the plugin folder and registers them. From this point on, the types of plugins loaded can be queried via an interface and the user can set the required data.

Each plugin must inherit the methods and attributes from the INotificationPlugin interface.

## Endpoints

### SSE
`GET /notifications/messages/subscribe`

This endpoint provides a stream of notifications for the authenticated user.

### Available notifications types

`GET /notification/types`

Returns a list of available types for secondary notification method.

### Switch notification method

`POST /notifications/activate/{type}`

Sets secondary notification method for user. Only one secondary method can be active at once.

`POST /notifications/disable`

Disables notifications via secondary method.

### Configure specific notification type

`GET /notifications/configuration/{type}`

Returns user specific configuration of the plugin. If no configuration was done before, the default config, provided by the plugin is send to the user.
The returned config is a object with simple key value pairs.

`POST /notifications/configure/{type}`

Sets the configuration for specific plugin type.